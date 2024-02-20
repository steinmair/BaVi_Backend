package at.htlklu.bavi.controller;

import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Composer;
import at.htlklu.bavi.repository.ComposersRepository;
import at.htlklu.bavi.repository.SongsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("composers")
public class ComposerController {



    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "ComposerController";

    @Autowired
    ComposersRepository composersRepository ;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/composers
    @GetMapping("")
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
    @GetMapping(value = "{composerId}")
    public ResponseEntity<?> getById(@PathVariable Integer composerId){
        logger.info(LogUtils.info(CLASS_NAME,"getComposerById",String.format("(%d)",composerId)));

        ResponseEntity<?> result;
        Optional<Composer> optComposer = composersRepository.findById(composerId);
        if (optComposer.isPresent()){
            Composer composer = optComposer.get();

            result =  new ResponseEntity<>(composer, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Composer mit der Id = %d nicht vorhanden",composerId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/composers/id/songs

    @GetMapping(value = "{composerId}/songs")
    public ResponseEntity<?> getSongsByComposerId(@PathVariable Integer composerId){

        logger.info(LogUtils.info(CLASS_NAME,"getSongsByComposerId",String.format("(%d)",composerId)));

        ResponseEntity<?> result;
        Optional<Composer> optionalComposer = composersRepository.findById(composerId);
        if (optionalComposer.isPresent()){

            Composer composer = optionalComposer.get();
            result =  new ResponseEntity<>(composer.getSongs(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Song mit der Id = %d nicht vorhanden",composerId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    // einfügen einer neuen Ressource

    @PostMapping("")
    public ResponseEntity<?> addComposer(@Valid @RequestBody Composer composer, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addComposer", String.format("(%s)", composer)));

        return getResponseEntity(composer, bindingResult);
    }

    // ändern einer vorhandenen Ressource

    @PutMapping("")
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

    @DeleteMapping(value = "{composerId}")
    public ResponseEntity<?> deleteComposer(@PathVariable Integer composerId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteComposer", String.format("(%d)", composerId)));
        String errorMessage = "";
        ResponseEntity<?> result;
        Composer composer = null;

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
     /* @PostMapping(value = "")
    public ResponseEntity<?> addComposer(@Valid @RequestBody Composer composer,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "addComposer", String.format("(%s)", composer)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                composersRepository.save(composer);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Composer>(composer, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }
    @PutMapping(value = "")
    public ResponseEntity<?> updateComposer(@Valid @RequestBody Composer composer,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "updateComposer", String.format("(%s)", composer)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                composersRepository.save(composer);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Composer>(composer, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
     @DeleteMapping(value = "{composerId}")
    public ResponseEntity<?> deleteComposer(@PathVariable Integer composerId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteComposer", String.format("(%d)", composerId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Composer composer = null;


        if (!error) {
            Optional<Composer> optionalComposer = composersRepository.findById(composerId);
            if (optionalComposer.isPresent()) {
                composer = optionalComposer.get();
            } else {
                error = true;
                errorMessage = "Composer not found";
            }
        }

        if (!error) {
            try {
                composersRepository.delete(composer);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Composer>(composer, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }*/







}
