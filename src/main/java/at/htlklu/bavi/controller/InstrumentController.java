package at.htlklu.bavi.controller;

import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Instrument;
import at.htlklu.bavi.repository.InstrumentsRepository;
import at.htlklu.bavi.repository.MembersRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("instruments")
public class InstrumentController {

    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "InstrumentController";

    @Autowired
    InstrumentsRepository instrumentsRepository;
    @Autowired
    MembersRepository membersRepository;


    //http://localhost:8082/instruments
    @GetMapping("")
    public ResponseEntity<?> getAllInstruments() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllInstruments", "Retrieving all instruments"));

        ResponseEntity<?> result;
        try {
            List<Instrument> instruments = instrumentsRepository.findAll();
            if (!instruments.isEmpty()) {
                result = new ResponseEntity<>(instruments, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No instruments found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    //http://localhost:8082/instruments/id
    @GetMapping(value = "{instrumentId}")
    public ResponseEntity<?> getById(@PathVariable Integer instrumentId){
        logger.info(LogUtils.info(CLASS_NAME,"getById",String.format("(%d)", instrumentId)));

        ResponseEntity<?> result;
        Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
        if (optionalInstrument.isPresent()){

            Instrument instrument= optionalInstrument.get();
            result = new ResponseEntity<>(instrument, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Instrument mit der Id = %d nicht vorhanden", instrumentId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/instruments/id/members

    @GetMapping(value = "{instrumentId}/members")
    public ResponseEntity<?> getMembersByInstrumentsId(@PathVariable Integer instrumentId){

        logger.info(LogUtils.info(CLASS_NAME,"getMembersByInstrumentsId",String.format("(%d)", instrumentId)));

        ResponseEntity<?> result;
        Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
        if (optionalInstrument.isPresent()){

            Instrument instrument = optionalInstrument.get();
            result =  new ResponseEntity<>(instrument.getMembers(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Instrument mit der Id = %d nicht vorhanden", instrumentId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    // einfügen einer neuen Ressource

    @PostMapping("")
    public ResponseEntity<?> addInstrument(@Valid @RequestBody Instrument instrument, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addInstrument", String.format("(%s)", instrument)));
        return getResponseEntity(instrument, bindingResult);
    }

    // ändern einer vorhandenen Ressource
    @PutMapping("")
    public ResponseEntity<?> updateInstrument(@Valid @RequestBody Instrument instrument, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateInstrument", String.format("(%s)", instrument)));
        return getResponseEntity(instrument, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Instrument instrument, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Instrument savedInstrument = instrumentsRepository.save(instrument);
            return new ResponseEntity<>(savedInstrument, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    //http://localhost:8082/instruments/id (delete)

    @DeleteMapping(value = "{instrumentId}")
    public ResponseEntity<?> deleteInstrument(@PathVariable Integer instrumentId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteInstrument", String.format("(%d)", instrumentId)));
        String errorMessage = "";
        ResponseEntity<?> result;
        Instrument instrument;

        Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
        if (optionalInstrument.isPresent()) {
            instrument = optionalInstrument.get();
        } else {
            return new ResponseEntity<>("Instrument not found", HttpStatus.NOT_FOUND);
        }

        try {
            instrumentsRepository.delete(instrument);
            result = new ResponseEntity<>(instrument, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /* @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Instrument instrument,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", instrument)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                instrumentsRepository.save(instrument);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Instrument>(instrument, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Instrument instrument,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", instrument)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                instrumentsRepository.save(instrument);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Instrument>(instrument, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
    @DeleteMapping(value = "{instrumentId}")
    public ResponseEntity<?> deletePV(@PathVariable Integer instrumentId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", instrumentId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Instrument instrument = null;


        if (!error) {
            Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
            if (optionalInstrument.isPresent()) {
                instrument = optionalInstrument.get();
            } else {
                error = true;
                errorMessage = "Instrument not found";
            }
        }

        if (!error) {
            try {
                instrumentsRepository.delete(instrument);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Instrument>(instrument, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }*/





}
