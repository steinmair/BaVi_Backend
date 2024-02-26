package at.htlklu.bavi.controller;

import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Publisher;
import at.htlklu.bavi.repository.PublishersRepository;
import at.htlklu.bavi.repository.SongsRepository;
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
    public ResponseEntity<?> getAllPublishers() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllPublishers", "Retrieving all publishers"));

        ResponseEntity<?> result;
        try {
            List<Publisher> publisher = publishersRepository.findAll();
            if (!publisher.isEmpty()) {
                result = new ResponseEntity<>(publisher, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No publishers found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/publishers/id
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{publisherId}")
    public ResponseEntity<?> getById(@PathVariable Integer publisherId) {
        logger.info(LogUtils.info(CLASS_NAME, "getById", String.format("(%d)", publisherId)));

        ResponseEntity<?> result;
        Optional<Publisher> optionalPublisher = publishersRepository.findById(publisherId);
        if (optionalPublisher.isPresent()) {

            Publisher publisher = optionalPublisher.get();
            result = new ResponseEntity<>(publisher, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Publisher mit der Id = %d nicht vorhanden", publisherId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/publishers/id/songs
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping(value = "{publisherId}/songs")
    public ResponseEntity<?> getSongsById(@PathVariable Integer publisherId) {

        logger.info(LogUtils.info(CLASS_NAME, "getSongsById", String.format("(%d)", publisherId)));

        ResponseEntity<?> result;
        Optional<Publisher> optionalPublisher = publishersRepository.findById(publisherId);
        if (optionalPublisher.isPresent()) {

            Publisher publisher = optionalPublisher.get();
            result = new ResponseEntity<>(publisher.getSongs(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Publisher mit der Id = %d nicht vorhanden", publisherId), HttpStatus.NOT_FOUND);
        }
        return result;

    }

    // Einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> addPublisher(@Valid @RequestBody Publisher publisher, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addPublisher", String.format("(%s)", publisher)));
        return getResponseEntity(publisher, bindingResult);
    }

    // Ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    public ResponseEntity<?> updatePublisher(@Valid @RequestBody Publisher publisher, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updatePublisher", String.format("(%s)", publisher)));
        return getResponseEntity(publisher, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Publisher publisher, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Publisher savedPublisher = publishersRepository.save(publisher);
            return new ResponseEntity<>(savedPublisher, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:8082/publishers/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{publisherId}")
    public ResponseEntity<?> deletePublisher(@PathVariable Integer publisherId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePublisher", String.format("(%d)", publisherId)));
        String errorMessage;
        ResponseEntity<?> result;
        Publisher publisher;

        Optional<Publisher> optionalPublisher = publishersRepository.findById(publisherId);
        if (optionalPublisher.isPresent()) {
            publisher = optionalPublisher.get();
        } else {
            return new ResponseEntity<>("Publisher not found", HttpStatus.NOT_FOUND);
        }

        try {
            publishersRepository.delete(publisher);
            result = new ResponseEntity<>(publisher, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


}
