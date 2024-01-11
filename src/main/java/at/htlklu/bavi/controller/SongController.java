package at.htlklu.bavi.controller;

import at.htlklu.bavi.api.ErrorsUtils;
import at.htlklu.bavi.api.LogUtils;
import at.htlklu.bavi.configs.MinioFileService;
import at.htlklu.bavi.minio.MinioService;
import at.htlklu.bavi.minio.MinioServiceException;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.repository.ComposersRepository;
import at.htlklu.bavi.repository.SongsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("songs")
public class SongController {

    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "SongController";

    @Autowired
    SongsRepository songsRepository;
    @Autowired
    private MinioService minioService;


    //http://localhost:8082/songs
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
    @GetMapping(value = "{songId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer songId){
        logger.info(LogUtils.info(CLASS_NAME,"getByIdPV",String.format("(%d)", songId)));

        ResponseEntity<?> result;
        Optional<Song> optionalSong = songsRepository.findById(songId);
        if (optionalSong.isPresent()){

            Song song= optionalSong.get();
            result =  new ResponseEntity<Song>(song, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Song mit der Id = %d nicht vorhanden", songId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    // einfügen einer neuen Ressource
   /* @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Song song,
                                 BindingResult bindingResult) {


        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", song)));

        boolean error = false;
        String errorMessage = "";


        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                songsRepository.save(song);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Song>(song, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
        }
*/

    @PostMapping(value = "")
    public ResponseEntity<Object> add(@Valid @RequestBody Song song, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", song)));

        if (bindingResult.hasErrors()) {
            // Validation errors
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Attempt to save the song
            Song savedSong = songsRepository.save(song);
            return new ResponseEntity<>(savedSong, HttpStatus.CREATED);
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
   /* @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Song song, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", song)));

        if (bindingResult.hasErrors()) {
            // Validation errors
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            // Attempt to save the song
            Song savedSong = songsRepository.save(song);

            // Attempt to create a Minio bucket
            minioService.createBucket(String.valueOf(savedSong.getSongId()));

            // Bucket created successfully
            return new ResponseEntity<>(savedSong, HttpStatus.CREATED);
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
    }*/



    // ändern einer vorhandenen Ressource
   /* @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Song song,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", song)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                songsRepository.save(song);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Song>(song, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }*/
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
    /*@DeleteMapping(value = "{songId}")
    public ResponseEntity<?> deletePV(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", songId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Song song = null;


        if (!error) {
            Optional<Song> songOptional = songsRepository.findById(songId);
            if (songOptional.isPresent()) {
                song = songOptional.get();
            } else {
                error = true;
                errorMessage = "Song not found";
            }
        }

        if (!error) {
            try {
                songsRepository.delete(song);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Song>(song, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }*/
    @DeleteMapping(value = "{songId}")
    public ResponseEntity<Object> deletePV(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", songId)));

        try {
            Optional<Song> songOptional = songsRepository.findById(songId);

            if (songOptional.isPresent()) {
                Song song = songOptional.get();
                songsRepository.delete(song);
                return new ResponseEntity<>(song, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Song not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the exception
            logger.error("Error deleting song", e);

            // Handle specific exceptions if needed
            if (e instanceof DataIntegrityViolationException) {
                // Handle data integrity violations
                return new ResponseEntity<>("Data integrity violation", HttpStatus.CONFLICT);
            }

            // Generic error response
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /*@DeleteMapping(value = "{songId}")
    public ResponseEntity<Object> deletePV(@PathVariable Integer songId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", songId)));

        try {
            Optional<Song> songOptional = songsRepository.findById(songId);

            if (songOptional.isPresent()) {
                Song song = songOptional.get();

                try {
                    minioService.deleteBucket(String.valueOf(songId));
                } catch (MinioServiceException e) {
                    // Log the exception or perform additional actions
                    logger.error("Error deleting Minio bucket: " + songId, e);
                    return new ResponseEntity<>("Error deleting Minio bucket", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Delete the song from the database
                songsRepository.delete(song);
                return new ResponseEntity<>(song, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Song not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the exception
            logger.error("Error deleting song", e);

            // Handle specific exceptions if needed
            if (e instanceof DataIntegrityViolationException) {
                // Handle data integrity violations
                return new ResponseEntity<>("Data integrity violation", HttpStatus.CONFLICT);
            }

            // Generic error response
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/






}
