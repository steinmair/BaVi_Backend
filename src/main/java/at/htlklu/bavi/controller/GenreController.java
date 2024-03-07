package at.htlklu.bavi.controller;

import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Genre;
import at.htlklu.bavi.repository.GenresRepository;
import at.htlklu.bavi.repository.SongsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("genres")
public class GenreController {

    private static final Logger logger = LogManager.getLogger(GenreController.class);
    private static final String CLASS_NAME = "GenreController";
    @Autowired
    GenresRepository genresRepository;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/genres
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("")
    @Operation(summary = "Get All Genres", description = "Retrieve all genres")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved genres",
            content = @Content(schema = @Schema(implementation = Genre.class)))
    @ApiResponse(responseCode = "404", description = "No genres found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllGenres() {
        logger.info("Retrieving all genres");
        ResponseEntity<?> result;
        try {
            List<Genre> genres = genresRepository.findAll();
            if (!genres.isEmpty()) {
                logger.debug("Retrieved {} genres", genres.size());
                result = new ResponseEntity<>(genres, HttpStatus.OK);
            } else {
                logger.info("No genres found");
                result = new ResponseEntity<>("No genres found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving genres: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/genres/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{genreId}")
    @Operation(summary = "Get Genre by ID", description = "Retrieve genre by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved genre",
            content = @Content(schema = @Schema(implementation = Genre.class)))
    @ApiResponse(responseCode = "404", description = "Genre not found")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer genreId) {
        logger.info("Retrieving genre with ID: {}", genreId);

        ResponseEntity<?> result;
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();
            logger.debug("Retrieved genre: {}", genre);
            result = new ResponseEntity<>(genre, HttpStatus.OK);
        } else {
            logger.info("Genre not found: {}", genreId);
            result = new ResponseEntity<>(String.format("Genre not found (%d)", genreId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/genres/id/songs
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{genreId}/songs")
    @Operation(summary = "Get Songs by Genre ID", description = "Retrieve songs by genre ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved songs",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "404", description = "Genre not found")
    public ResponseEntity<?> getSongsById(@PathVariable Integer genreId) {
        logger.info("Retrieving songs for genre with ID: {}", genreId);
        ResponseEntity<?> result;
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();
            logger.debug("Retrieved genre: {}", genre);
            result = new ResponseEntity<>(genre.getSongs(), HttpStatus.OK);
        } else {
            logger.info("Genre not found: {}", genreId);
            result = new ResponseEntity<>(String.format("Genre not found (%d)", genreId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    // Einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @Operation(summary = "Add Genre", description = "Add a new genre")
    @ApiResponse(responseCode = "200", description = "Genre added successfully",
            content = @Content(schema = @Schema(implementation = Genre.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addGenre(@Valid @RequestBody Genre genre, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addGenre", String.format("(%s)", genre)));
        return getResponseEntity(genre, bindingResult);
    }

    // Ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    @Operation(summary = "Update Genre", description = "Update an existing genre")
    @ApiResponse(responseCode = "200", description = "Genre updated successfully",
            content = @Content(schema = @Schema(implementation = Genre.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> updateGenre(@Valid @RequestBody Genre genre, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateGenre", String.format("(%s)", genre)));
        return getResponseEntity(genre, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Genre genre, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            Genre savedGenre = genresRepository.save(genre);
            logger.info("Saved genre: {}", savedGenre);
            return new ResponseEntity<>(savedGenre, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error saving genre: {}", e.getMessage());
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:8082/genres/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{genreId}")
    @Operation(summary = "Delete Genre", description = "Delete a genre by its ID")
    @ApiResponse(responseCode = "200", description = "Genre deleted successfully")
    @ApiResponse(responseCode = "404", description = "Genre not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteGenre(@PathVariable Integer genreId) {
        logger.info("Deleting genre with ID: {}", genreId);
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isEmpty()) {
            logger.info("Genre not found: {}", genreId);
            return new ResponseEntity<>("Genre not found", HttpStatus.NOT_FOUND);
        }
        Genre genre = optionalGenre.get();
        try {
            genresRepository.delete(genre);
            logger.info("Deleted genre: {}", genre);
            return new ResponseEntity<>(genre, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting genre: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
