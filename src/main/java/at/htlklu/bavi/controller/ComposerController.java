package at.htlklu.bavi.controller;

import at.htlklu.bavi.api.ErrorsUtils;
import at.htlklu.bavi.api.HateoasUtils;
import at.htlklu.bavi.api.LogUtils;
import at.htlklu.bavi.model.Composer;
import at.htlklu.bavi.repository.ComposersRepository;
import at.htlklu.bavi.repository.SongsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<?> getByIdPV(@PathVariable Integer composerId){
        logger.info(LogUtils.info(CLASS_NAME,"getByIdPV",String.format("(%d)",composerId)));

        ResponseEntity<?> result;
        Optional<Composer> optComposer = composersRepository.findById(composerId);
        if (optComposer.isPresent()){
            Composer composer = optComposer.get();
           // addLinks(composer);
            result =  new ResponseEntity<>(composer, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Composer mit der Id = %d nicht vorhanden",composerId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/composers/id/songs

    @GetMapping(value = "{composerId}/songs")
    public ResponseEntity<?> getSongsByIdPV(@PathVariable Integer composerId){

        logger.info(LogUtils.info(CLASS_NAME,"getSongsByIdPV",String.format("(%d)",composerId)));

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
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Composer composer,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", composer)));

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

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Composer composer,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", composer)));

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


    //http://localhost:8082/composers/id (delete)
    @DeleteMapping(value = "{composerId}")
    public ResponseEntity<?> deletePV(@PathVariable Integer composerId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", composerId)));
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
    }
    public static void addLinks(Composer composer){
        if (HateoasUtils.enableHateoas){
            composer.add(WebMvcLinkBuilder.linkTo(methodOn(ComposerController.class)
                            .getByIdPV(composer.getComposerId()))
                    .withSelfRel());
            composer.add(WebMvcLinkBuilder.linkTo(methodOn(ComposerController.class)
                            .getSongsByIdPV(composer.getComposerId()))
                    .withRel("songs"));
        }
    }






}
