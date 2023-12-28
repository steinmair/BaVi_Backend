package at.htlklu.bavi.controller;

import at.htlklu.bavi.api.ErrorsUtils;
import at.htlklu.bavi.api.LogUtils;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.repository.SongsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("songs")
public class SongController {

    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "GenreController";

    @Autowired
    SongsRepository songsRepository;


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
    @PostMapping(value = "")
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

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
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
    }


    //http://localhost:8082/songs/id (delete)
    @DeleteMapping(value = "{songId}")
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
    }




}
