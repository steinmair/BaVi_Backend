package at.htlklu.bavi.controller;

import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Instrument;
import at.htlklu.bavi.repository.InstrumentsRepository;
import at.htlklu.bavi.repository.MembersRepository;
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
@RequestMapping("instruments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InstrumentController {

    private static final Logger logger = LogManager.getLogger(InstrumentController.class);
    private static final String CLASS_NAME = "InstrumentController";

    @Autowired
    InstrumentsRepository instrumentsRepository;
    @Autowired
    MembersRepository membersRepository;


    //http://localhost:8082/instruments
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("")
    @Operation(summary = "Get All Instruments", description = "Retrieve all instruments")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved instruments",
            content = @Content(schema = @Schema(implementation = Instrument.class)))
    @ApiResponse(responseCode = "404", description = "No instruments found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllInstruments() {
        logger.info("Retrieving all instruments");
        ResponseEntity<?> result;
        try {
            List<Instrument> instruments = instrumentsRepository.findAll();
            if (!instruments.isEmpty()) {
                logger.debug("Retrieved {} instruments", instruments.size());
                result = new ResponseEntity<>(instruments, HttpStatus.OK);
            } else {
                logger.warn("No instruments found");
                result = new ResponseEntity<>("No instruments found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving instruments: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/instruments/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{instrumentId}")
    @Operation(summary = "Get Instrument by ID", description = "Retrieve instrument by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved instrument",
            content = @Content(schema = @Schema(implementation = Instrument.class)))
    @ApiResponse(responseCode = "404", description = "Instrument not found")
    public ResponseEntity<?> getById(@PathVariable Integer instrumentId) {
        logger.info("Retrieving instrument with ID: {}", instrumentId);

        ResponseEntity<?> result;
        Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
        if (optionalInstrument.isPresent()) {
            Instrument instrument = optionalInstrument.get();
            logger.debug("Retrieved instrument: {}", instrument);
            result = new ResponseEntity<>(instrument, HttpStatus.OK);
        } else {
            logger.warn("Instrument not found: {}", instrumentId);
            result = new ResponseEntity<>(String.format("Instrument not found(%d)", instrumentId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/instruments/id/members
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{instrumentId}/members")
    @Operation(summary = "Get Members by Instrument ID", description = "Retrieve members by instrument ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved members",
            content = @Content(schema = @Schema(implementation = Member.class)))
    @ApiResponse(responseCode = "404", description = "Instrument not found")
    public ResponseEntity<?> getMembersByInstrumentsId(@PathVariable Integer instrumentId) {
        logger.info("Retrieving members for instrument with ID: {}", instrumentId);
        ResponseEntity<?> result;
        Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
        if (optionalInstrument.isPresent()) {
            Instrument instrument = optionalInstrument.get();
            logger.debug("Retrieved instrument: {}", instrument);
            result = new ResponseEntity<>(instrument.getMembers(), HttpStatus.OK);
        } else {
            logger.warn("Instrument not found: {}", instrumentId);
            result = new ResponseEntity<>(String.format("Instrument not found (%d)", instrumentId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    // einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @Operation(summary = "Add Instrument", description = "Add a new instrument")
    @ApiResponse(responseCode = "200", description = "Instrument added successfully",
            content = @Content(schema = @Schema(implementation = Instrument.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addInstrument(@Valid @RequestBody Instrument instrument, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addInstrument", String.format("(%s)", instrument)));
        return getResponseEntity(instrument, bindingResult);
    }

    // ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    @Operation(summary = "Update Instrument", description = "Update an existing instrument")
    @ApiResponse(responseCode = "200", description = "Instrument updated successfully",
            content = @Content(schema = @Schema(implementation = Instrument.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> updateInstrument(@Valid @RequestBody Instrument instrument, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateInstrument", String.format("(%s)", instrument)));
        return getResponseEntity(instrument, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Instrument instrument, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        try {
            Instrument savedInstrument = instrumentsRepository.save(instrument);
            logger.debug("Saved instrument: {}", savedInstrument);
            return new ResponseEntity<>(savedInstrument, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error saving instrument: {}", e.getMessage());
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //http://localhost:8082/instruments/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{instrumentId}")
    @Operation(summary = "Delete Instrument", description = "Delete an instrument by its ID")
    @ApiResponse(responseCode = "200", description = "Instrument deleted successfully")
    @ApiResponse(responseCode = "404", description = "Instrument not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteInstrument(@PathVariable Integer instrumentId) {
        logger.info("Deleting instrument with ID: {}", instrumentId);
        Optional<Instrument> optionalInstrument = instrumentsRepository.findById(instrumentId);
        if (optionalInstrument.isEmpty()) {
            logger.warn("Instrument not found: {}", instrumentId);
            return new ResponseEntity<>("Instrument not found", HttpStatus.NOT_FOUND);
        }
        Instrument instrument = optionalInstrument.get();
        try {
            instrumentsRepository.delete(instrument);
            logger.debug("Deleted instrument: {}", instrument);
            return new ResponseEntity<>(instrument, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting instrument: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
