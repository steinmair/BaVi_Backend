package at.htlklu.bavi.controller;

import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.minio.MinioBucketExistsException;
import at.htlklu.bavi.minio.MinioService;
import at.htlklu.bavi.minio.MinioServiceException;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.repository.SongsRepository;
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
    public ResponseEntity<?> getAllSongs() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllSongs", "Retrieving all songs"));

        ResponseEntity<?> result;
        try {
            List<Song> songs = songsRepository.findAll();
            if (!songs.isEmpty()) {
                result = new ResponseEntity<>(songs, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No songs found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/songs/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{songId}")
    public ResponseEntity<?> getById(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "getById", String.format("(%d)", songId)));

        ResponseEntity<?> result;
        Optional<Song> optionalSong = songsRepository.findById(songId);
        if (optionalSong.isPresent()) {

            Song song = optionalSong.get();
            result = new ResponseEntity<>(song, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Song mit der Id = %d nicht vorhanden", songId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "")
    public ResponseEntity<Object> add(@Valid @RequestBody Song song, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", song)));

        if (bindingResult.hasErrors()) {
            // Validation errors
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Create a bucket with the name of the archive number
            String archivNumber = song.getArchivNumber();
            minioService.createBucket(archivNumber);

            // Attempt to save the song
            Song savedSong = songsRepository.save(song);
            return new ResponseEntity<>(savedSong, HttpStatus.CREATED);
        } catch (MinioBucketExistsException e) {
            // Handle Minio bucket already exists exception
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
    public ResponseEntity<Object> update(@Valid @RequestBody Song song, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", song)));

        if (bindingResult.hasErrors()) {
            // Validation errors
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Attempt to update the song
            Song updatedSong = songsRepository.save(song);
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
    public ResponseEntity<Object> deletePV(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", songId)));

        try {
            Optional<Song> songOptional = songsRepository.findById(songId);

            if (songOptional.isPresent()) {
                Song song = songOptional.get();

                // Delete the associated Minio bucket
                String bucketName = song.getArchivNumber();
                minioService.deleteBucket(bucketName);

                // Delete the song from the repository
                songsRepository.delete(song);

                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Song not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the exception
            logger.error("Error deleting song or bucket", e);

            // Handle specific exceptions if needed
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting song or bucket: " + e.getMessage());
        }
    }


}
