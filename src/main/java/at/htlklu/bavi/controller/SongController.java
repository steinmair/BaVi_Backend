package at.htlklu.bavi.controller;

import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.minio.MinioBucketExistsException;
import at.htlklu.bavi.minio.MinioService;
import at.htlklu.bavi.minio.MinioServiceException;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.repository.SongsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("songs")
public class SongController {

    private static final Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "SongController";

    @Autowired
    SongsRepository songsRepository;

    @Autowired
    MinioService minioService;


    //http://localhost:8082/songs
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("")
    @Operation(summary = "Get All Songs", description = "Retrieve all songs")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved songs",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "404", description = "No songs found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllSongs() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllSongs", "Retrieving all songs"));

        try {
            List<Song> songs = songsRepository.findAll();
            if (!songs.isEmpty()) {
                logger.debug("Retrieved {} songs", songs.size());
                return new ResponseEntity<>(songs, HttpStatus.OK);
            } else {
                logger.info("No songs found");
                return new ResponseEntity<>("No songs found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving songs: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //http://localhost:8082/songs/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{songId}")
    @Operation(summary = "Get Song by ID", description = "Retrieve song by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved song",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "404", description = "Song not found")
    public ResponseEntity<?> getById(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "getById", String.format("(%d)", songId)));

        try {
            Optional<Song> optionalSong = songsRepository.findById(songId);
            if (optionalSong.isPresent()) {
                Song song = optionalSong.get();
                logger.debug("Retrieved song: {}", song);
                return new ResponseEntity<>(song, HttpStatus.OK);
            } else {
                logger.info("Song not found: {}", songId);
                return new ResponseEntity<>(String.format("Song not found (%d)", songId), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving song {}: {}", songId, e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "")
    @Operation(summary = "Add Song", description = "Add a new song")
    @ApiResponse(responseCode = "201", description = "Song added successfully",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "409", description = "Bucket already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Object> add(@Valid @RequestBody Song song, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", song)));

        if (bindingResult.hasErrors()) {
            // Validation errors
            logger.error("Validation errors occurred: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Create a bucket with the name of the archive number
            String archivNumber = song.getArchivNumber();
            minioService.createBucket(archivNumber);

            // Attempt to save the song
            Song savedSong = songsRepository.save(song);
            logger.debug("Saved song: {}", savedSong);
            return new ResponseEntity<>(savedSong, HttpStatus.CREATED);
        } catch (MinioBucketExistsException e) {
            // Handle Minio bucket already exists exception
            logger.error("Bucket already exists for archive number: {}", song.getArchivNumber());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bucket already exists for archive number: " + song.getArchivNumber());
        } catch (MinioServiceException e) {
            // Log the exception
            logger.error("Error saving song or creating bucket", e);

            // Handle specific exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving song or creating bucket: " + e.getMessage());
        } catch (Exception e) {
            // Log the exception
            logger.error("Error saving song", e);

            // Handle specific exceptions if needed
            if (e instanceof DataIntegrityViolationException) {
                // Handle data integrity violations
                return new ResponseEntity<>("Duplicate entry or data integrity violation", HttpStatus.CONFLICT);
            }

            // Generic error response
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "")
    @Operation(summary = "Update Song", description = "Update an existing song")
    @ApiResponse(responseCode = "200", description = "Song updated successfully",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "409", description = "Duplicate entry or data integrity violation")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Object> update(@Valid @RequestBody Song song, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", song)));

        if (bindingResult.hasErrors()) {
            // Validation errors
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Song existingSong = songsRepository.findById(song.getSongId()).orElse(null);
            if (existingSong == null) {
                return new ResponseEntity<>("Song not found", HttpStatus.NOT_FOUND);
            }

            // Check if the archive number has been changed
            String oldArchiveNumber = existingSong.getArchivNumber();
            String newArchiveNumber = song.getArchivNumber();
            if (!oldArchiveNumber.equals(newArchiveNumber)) {

                // Create new bucket with the updated archive number
                minioService.createBucket(newArchiveNumber);
                // Retrieve list of files from old bucket
                List<String> filesInOldBucket = minioService.listFiles(oldArchiveNumber);

                // Iterate through files and copy them to the new bucket
                for (String file : filesInOldBucket) {
                    minioService.copyFile(oldArchiveNumber, file, newArchiveNumber, file);
                }

                // Delete old bucket
                minioService.deleteBucket(oldArchiveNumber);

                // Create new bucket with the updated archive number
                minioService.createBucket(newArchiveNumber);
            }

            // Attempt to update the song
            Song updatedSong = songsRepository.save(song);
            logger.debug("Updated song: {}", updatedSong);
            return new ResponseEntity<>(updatedSong, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception
            logger.error("Error updating song", e);

            // Handle specific exceptions if needed
            if (e instanceof DataIntegrityViolationException) {
                // Handle data integrity violations
                return new ResponseEntity<>("Duplicate entry or data integrity violation", HttpStatus.CONFLICT);
            }

            // Generic error response
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //http://localhost:8082/songs/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{songId}")
    @Operation(summary = "Delete Song", description = "Delete a song by its ID")
    @ApiResponse(responseCode = "200", description = "Song deleted successfully")
    @ApiResponse(responseCode = "404", description = "Song not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<Object> deleteSong(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteSong", String.format("(%d)", songId)));

        try {
            Optional<Song> songOptional = songsRepository.findById(songId);

            if (songOptional.isPresent()) {
                Song song = songOptional.get();

                String bucketName = song.getArchivNumber();
                logger.info("Deleting bucket '{}' associated with song ID: {}", bucketName, songId);

                // Delete the associated Minio bucket
                minioService.deleteBucket(bucketName);
                logger.info("Bucket '{}' deleted successfully", bucketName);

                // Delete the song from the repository
                songsRepository.delete(song);
                logger.info("Song with ID '{}' deleted successfully", songId);

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.info("Song with ID '{}' not found", songId);
                return new ResponseEntity<>("Song not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the exception
            logger.error("Error deleting song or bucket", e);

            // Handle specific exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting song or bucket: " + e.getMessage());
        }
    }


}
