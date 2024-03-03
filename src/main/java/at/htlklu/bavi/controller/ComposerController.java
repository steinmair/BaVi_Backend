package at.htlklu.bavi.controller;

import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Composer;
import at.htlklu.bavi.repository.ComposersRepository;
import at.htlklu.bavi.repository.SongsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("composers")
public class ComposerController {


    private static final Logger logger = LogManager.getLogger(ComposerController.class);
    private static final String CLASS_NAME = "ComposerController";

    @Autowired
    ComposersRepository composersRepository;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/composers
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("")
    @Operation(summary = "Get All Composers", description = "Retrieve all composers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved composers",
            content = @Content(schema = @Schema(implementation = Composer.class)))
    @ApiResponse(responseCode = "404", description = "No composers found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllComposers() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllComposers", "Retrieving all composers"));

        ResponseEntity<?> result;
        try {
            List<Composer> composers = composersRepository.findAll();
            if (!composers.isEmpty()) {
                result = new ResponseEntity<>(composers, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No composers found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/composers/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{composerId}")
    @Operation(summary = "Get Composer by ID", description = "Retrieve composer by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved composer",
            content = @Content(schema = @Schema(implementation = Composer.class)))
    @ApiResponse(responseCode = "404", description = "Composer not found")
    public ResponseEntity<?> getById(@PathVariable Integer composerId) {
        logger.info(LogUtils.info(CLASS_NAME, "getComposerById", String.format("(%d)", composerId)));

        ResponseEntity<?> result;
        Optional<Composer> optComposer = composersRepository.findById(composerId);
        if (optComposer.isPresent()) {
            Composer composer = optComposer.get();

            result = new ResponseEntity<>(composer, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Composer mit der Id = %d nicht vorhanden", composerId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/composers/id/songs
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{composerId}/songs")
    @Operation(summary = "Get Songs by Composer ID", description = "Retrieve songs by composer ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved songs",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "404", description = "Composer not found")
    public ResponseEntity<?> getSongsByComposerId(@PathVariable Integer composerId) {

        logger.info(LogUtils.info(CLASS_NAME, "getSongsByComposerId", String.format("(%d)", composerId)));

        ResponseEntity<?> result;
        Optional<Composer> optionalComposer = composersRepository.findById(composerId);
        if (optionalComposer.isPresent()) {

            Composer composer = optionalComposer.get();
            result = new ResponseEntity<>(composer.getSongs(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Song mit der Id = %d nicht vorhanden", composerId), HttpStatus.NOT_FOUND);
        }
        return result;

    }
    // einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @Operation(summary = "Add Composer", description = "Add a new composer")
    @ApiResponse(responseCode = "200", description = "Composer added successfully",
            content = @Content(schema = @Schema(implementation = Composer.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addComposer(@Valid @RequestBody Composer composer, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addComposer", String.format("(%s)", composer)));

        return getResponseEntity(composer, bindingResult);
    }

    // ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    @Operation(summary = "Update Composer", description = "Update an existing composer")
    @ApiResponse(responseCode = "200", description = "Composer updated successfully",
            content = @Content(schema = @Schema(implementation = Composer.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> updateComposer(@Valid @RequestBody Composer composer, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateComposer", String.format("(%s)", composer)));

        return getResponseEntity(composer, bindingResult);
    }


    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Composer composer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Composer savedComposer = composersRepository.save(composer);
            return new ResponseEntity<>(savedComposer, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //http://localhost:8082/composers/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{composerId}")
    @Operation(summary = "Delete Composer", description = "Delete a composer by its ID")
    @ApiResponse(responseCode = "200", description = "Composer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Composer not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteComposer(@PathVariable Integer composerId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteComposer", String.format("(%d)", composerId)));
        String errorMessage;
        ResponseEntity<?> result;
        Composer composer;

        Optional<Composer> optionalComposer = composersRepository.findById(composerId);
        if (optionalComposer.isPresent()) {
            composer = optionalComposer.get();
        } else {
            return new ResponseEntity<>("Composer not found", HttpStatus.NOT_FOUND);
        }

        try {
            composersRepository.delete(composer);
            result = new ResponseEntity<>(composer, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

}
