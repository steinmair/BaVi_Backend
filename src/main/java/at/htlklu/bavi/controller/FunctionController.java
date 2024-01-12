package at.htlklu.bavi.controller;

import at.htlklu.bavi.api.ErrorsUtils;
import at.htlklu.bavi.api.LogUtils;
import at.htlklu.bavi.model.Function;
import at.htlklu.bavi.repository.FunctionsRepository;
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
@RequestMapping("functions")
public class FunctionController {

    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "FunctionController";

    @Autowired
    FunctionsRepository functionsRepository;
    @Autowired
    MembersRepository membersRepository;


    //http://localhost:8082/functions
    @GetMapping("")
    public ResponseEntity<?> getAllFunctions() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllFunctions", "Retrieving all instruments"));

        ResponseEntity<?> result;
        try {
            List<Function> functions = functionsRepository.findAll();
            if (!functions.isEmpty()) {
                result = new ResponseEntity<>(functions, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No functions found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    //http://localhost:8082/functions/id
    @GetMapping(value = "{functionId}")
    public ResponseEntity<?> getById(@PathVariable Integer functionId){
        logger.info(LogUtils.info(CLASS_NAME,"getById",String.format("(%d)", functionId)));

        ResponseEntity<?> result;
        Optional<Function> optionalFunction = functionsRepository.findById(functionId);
        if (optionalFunction.isPresent()){

            Function function= optionalFunction.get();
            result = new ResponseEntity<>(function, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Function mit der Id = %d nicht vorhanden", functionId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/functions/id/members

    @GetMapping(value = "{functionId}/members")
    public ResponseEntity<?> getMembersByFunctionId(@PathVariable Integer functionId){

        logger.info(LogUtils.info(CLASS_NAME,"getMembersByFunctionId",String.format("(%d)", functionId)));

        ResponseEntity<?> result;
        Optional<Function> optionalFunction = functionsRepository.findById(functionId);
        if (optionalFunction.isPresent()){

            Function function = optionalFunction.get();
            result =  new ResponseEntity<>(function.getMembers(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Function mit der Id = %d nicht vorhanden", functionId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    // einfügen einer neuen Ressource


    @PostMapping("")
    public ResponseEntity<?> addFunction(@Valid @RequestBody Function function, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addFunction", String.format("(%s)", function)));
        return getResponseEntity(function, bindingResult);
    }

    // ändern einer vorhandenen Ressource
    @PutMapping("")
    public ResponseEntity<?> updateFunction(@Valid @RequestBody Function function, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateFunction", String.format("(%s)", function)));
        return getResponseEntity(function, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Function function, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Function savedFunction = functionsRepository.save(function);
            return new ResponseEntity<>(savedFunction, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //http://localhost:8082/functions/id (delete)

    @DeleteMapping(value = "{functionId}")
    public ResponseEntity<?> deleteFunction(@PathVariable Integer functionId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteFunction", String.format("(%d)", functionId)));
        String errorMessage = "";
        ResponseEntity<?> result;
        Function function = null;

        Optional<Function> optionalFunction = functionsRepository.findById(functionId);
        if (optionalFunction.isPresent()) {
            function = optionalFunction.get();
        } else {
            return new ResponseEntity<>("Function not found", HttpStatus.NOT_FOUND);
        }

        try {
            functionsRepository.delete(function);
            result = new ResponseEntity<>(function, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /*@PostMapping(value = "")
    public ResponseEntity<?> addFunction(@Valid @RequestBody Function function,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "addFunction", String.format("(%s)", function)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                functionsRepository.save(function);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Function>(function, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Function function,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", function)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                functionsRepository.save(function);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Function>(function, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    @DeleteMapping(value = "{functionId}")
    public ResponseEntity<?> deletePV(@PathVariable Integer functionId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", functionId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Function function = null;


        if (!error) {
            Optional<Function> optionalFunction = functionsRepository.findById(functionId);
            if (optionalFunction.isPresent()) {
                function = optionalFunction.get();
            } else {
                error = true;
                errorMessage = "Instrument not found";
            }
        }

        if (!error) {
            try {
                functionsRepository.delete(function);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Function>(function, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }*/




}
