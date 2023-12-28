package at.htlklu.bavi.controller;

import at.htlklu.bavi.api.ErrorsUtils;
import at.htlklu.bavi.api.LogUtils;
import at.htlklu.bavi.model.Genre;
import at.htlklu.bavi.repository.GenresRepository;
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
@RequestMapping("genres")
public class GenreController {

    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "GenreController";

    @Autowired
    GenresRepository genresRepository;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/genres
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
    @GetMapping(value = "{genreId}")
    public ResponseEntity<?> getByIdPV(@PathVariable Integer genreId){
        logger.info(LogUtils.info(CLASS_NAME,"getByIdPV",String.format("(%d)", genreId)));

        ResponseEntity<?> result;
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()){

            Genre genre = optionalGenre.get();
            result =  new ResponseEntity<Genre>(genre, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Genre mit der Id = %d nicht vorhanden", genreId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/genres/id/songs

    @GetMapping(value = "{genreId}/songs")
    public ResponseEntity<?> getSongsByIdPV(@PathVariable Integer genreId){

        logger.info(LogUtils.info(CLASS_NAME,"getSongsByIdPV",String.format("(%d)", genreId)));

        ResponseEntity<?> result;
        Optional<Genre> optionalGenre = genresRepository.findById(genreId);
        if (optionalGenre.isPresent()){

            Genre genre = optionalGenre.get();
            result =  new ResponseEntity<>(genre.getSongs(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Genre mit der Id = %d nicht vorhanden", genreId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    // einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Genre genre,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", genre)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                genresRepository.save(genre);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Genre>(genre, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Genre genre,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", genre)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                genresRepository.save(genre);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Genre>(genre, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


    //http://localhost:8082/genres/id (delete)
    @DeleteMapping(value = "{genreId}")
    public ResponseEntity<?> deletePV(@PathVariable Integer genreId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", genreId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Genre genre = null;


        if (!error) {
            Optional<Genre> optionalGenre = genresRepository.findById(genreId);
            if (optionalGenre.isPresent()) {
                genre = optionalGenre.get();
            } else {
                error = true;
                errorMessage = "Genre not found";
            }
        }

        if (!error) {
            try {
                genresRepository.delete(genre);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Genre>(genre, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }




}
