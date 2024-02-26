package at.htlklu.bavi.controller;

import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Genre;
import at.htlklu.bavi.repository.GenresRepository;
import at.htlklu.bavi.repository.SongsRepository;
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
    public ResponseEntity<?> getAllGenres() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllGenres", "Retrieving all genres"));

        ResponseEntity<?> result;
        try {
            List<Genre> genres = genresRepository.findAll();
            if (!genres.isEmpty()) {
                result = new ResponseEntity<>(genres, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No genres found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/genres/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{genreId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer genreId) {
        logger.info(LogUtils.info(CLASS_NAME, "getByIdPV", String.format("(%d)", genreId)));

        ResponseEntity<?> result;
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()) {

            Genre genre = optionalGenre.get();
            result = new ResponseEntity<>(genre, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Genre mit der Id = %d nicht vorhanden", genreId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/genres/id/songs
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{genreId}/songs")
    public ResponseEntity<?> getSongsById(@PathVariable Integer genreId) {

        logger.info(LogUtils.info(CLASS_NAME, "getSongsById", String.format("(%d)", genreId)));

        ResponseEntity<?> result;
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()) {

            Genre genre = optionalGenre.get();
            result = new ResponseEntity<>(genre.getSongs(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Genre mit der Id = %d nicht vorhanden", genreId), HttpStatus.NOT_FOUND);
        }
        return result;

    }

    // Einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> addGenre(@Valid @RequestBody Genre genre, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addGenre", String.format("(%s)", genre)));
        return getResponseEntity(genre, bindingResult);
    }

    // Ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    public ResponseEntity<?> updateGenre(@Valid @RequestBody Genre genre, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateGenre", String.format("(%s)", genre)));
        return getResponseEntity(genre, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Genre genre, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Genre savedGenre = genresRepository.save(genre);
            return new ResponseEntity<>(savedGenre, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:8082/genres/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{genreId}")
    public ResponseEntity<?> deleteGenre(@PathVariable Integer genreId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteGenre", String.format("(%d)", genreId)));
        String errorMessage;
        ResponseEntity<?> result;
        Genre genre;

        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()) {
            genre = optionalGenre.get();
        } else {
            return new ResponseEntity<>("Genre not found", HttpStatus.NOT_FOUND);
        }

        try {
            genresRepository.delete(genre);
            result = new ResponseEntity<>(genre, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


}
