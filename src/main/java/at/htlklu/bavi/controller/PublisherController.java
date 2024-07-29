package at.htlklu.bavi.controller;

import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Publisher;
import at.htlklu.bavi.repository.PublishersRepository;
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
@RequestMapping("publishers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PublisherController {

    private static final Logger logger = LogManager.getLogger(PublisherController.class);
    private static final String CLASS_NAME = "PublisherController";

    @Autowired
    PublishersRepository publishersRepository;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/publishers
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("")
    @Operation(summary = "Get All Publishers", description = "Retrieve all publishers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved publishers",
            content = @Content(schema = @Schema(implementation = Publisher.class)))
    @ApiResponse(responseCode = "404", description = "No publishers found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllPublishers() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllPublishers", "Retrieving all publishers"));

        try {
            List<Publisher> publishers = publishersRepository.findAll();
            if (!publishers.isEmpty()) {
                logger.debug("Retrieved {} publishers", publishers.size());
                return ResponseEntity.ok().body(publishers);
            } else {
                logger.warn("No publishers found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving publishers: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    //http://localhost:8082/publishers/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{publisherId}")
    @Operation(summary = "Get Publisher by ID", description = "Retrieve publisher by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved publisher",
            content = @Content(schema = @Schema(implementation = Publisher.class)))
    @ApiResponse(responseCode = "404", description = "Publisher not found")
    public ResponseEntity<?> getById(@PathVariable Integer publisherId) {
        logger.info(LogUtils.info(CLASS_NAME, "getById", String.format("(%d)", publisherId)));

        try {
            Optional<Publisher> optionalPublisher = publishersRepository.findById(publisherId);
            if (optionalPublisher.isPresent()) {
                Publisher publisher = optionalPublisher.get();
                logger.debug("Retrieved publisher: {}", publisher);
                return ResponseEntity.ok().body(publisher);
            } else {
                logger.warn("Publisher not found: {}", publisherId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving publisher {}: {}", publisherId, e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    //http://localhost:8082/publishers/id/songs
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{publisherId}/songs")
    @Operation(summary = "Get Songs by Publisher ID", description = "Retrieve songs by publisher ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved songs",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "404", description = "Publisher not found")
    public ResponseEntity<?> getSongsById(@PathVariable Integer publisherId) {
        logger.info(LogUtils.info(CLASS_NAME, "getSongsById", String.format("(%d)", publisherId)));

        try {
            Optional<Publisher> optionalPublisher = publishersRepository.findById(publisherId);
            if (optionalPublisher.isPresent()) {
                Publisher publisher = optionalPublisher.get();
                logger.debug("Retrieved songs for publisher: {}", publisher);
                return ResponseEntity.ok().body(publisher.getSongs());
            } else {
                logger.warn("Publisher not found: {}", publisherId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error retrieving songs for publisher {}: {}", publisherId, e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }

    // Einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @ApiResponse(responseCode = "200", description = "Publisher added successfully",
            content = @Content(schema = @Schema(implementation = Publisher.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addPublisher(@Valid @RequestBody Publisher publisher, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addPublisher", String.format("(%s)", publisher)));
        return getResponseEntity(publisher, bindingResult);
    }

    // Ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    @Operation(summary = "Update Publisher", description = "Update an existing publisher")
    @ApiResponse(responseCode = "200", description = "Publisher updated successfully",
            content = @Content(schema = @Schema(implementation = Publisher.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> updatePublisher(@Valid @RequestBody Publisher publisher, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updatePublisher", String.format("(%s)", publisher)));
        return getResponseEntity(publisher, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Publisher publisher, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Publisher savedPublisher = publishersRepository.save(publisher);
            logger.debug("Saved publisher: {}", savedPublisher);
            return new ResponseEntity<>(savedPublisher, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error saving publisher: {}", e.getMessage());
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:8082/publishers/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{publisherId}")
    @Operation(summary = "Delete Publisher", description = "Delete a publisher by its ID")
    @ApiResponse(responseCode = "200", description = "Publisher deleted successfully")
    @ApiResponse(responseCode = "404", description = "Publisher not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deletePublisher(@PathVariable Integer publisherId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePublisher", String.format("(%d)", publisherId)));
        String errorMessage;
        ResponseEntity<?> result;

        try {
            Optional<Publisher> optionalPublisher = publishersRepository.findById(publisherId);
            if (optionalPublisher.isPresent()) {
                Publisher publisher = optionalPublisher.get();
                publishersRepository.delete(publisher);
                logger.debug("Deleted publisher: {}", publisher);
                result = new ResponseEntity<>(publisher, HttpStatus.OK);
            } else {
                logger.warn("Publisher not found: {}", publisherId);
                result = new ResponseEntity<>("Publisher not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting publisher {}: {}", publisherId, e.getMessage());
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


}
