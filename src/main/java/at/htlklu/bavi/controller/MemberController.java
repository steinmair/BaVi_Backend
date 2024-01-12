package at.htlklu.bavi.controller;

import at.htlklu.bavi.api.ErrorsUtils;
import at.htlklu.bavi.api.LogUtils;
import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.MembersRepository;
import at.htlklu.bavi.repository.SongsRepository;
import at.htlklu.bavi.repository.FunctionsRepository;
import at.htlklu.bavi.repository.InstrumentsRepository;
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
@RequestMapping("members")
public class MemberController {

    private static Logger logger = LogManager.getLogger(SongController.class);
    private static final String CLASS_NAME = "MemberController";

    @Autowired
    FunctionsRepository functionsRepository;
    @Autowired
    InstrumentsRepository instrumentsRepository;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/members
    @GetMapping("")
    public ResponseEntity<?> getAllMembers() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllMembers", "Retrieving all members"));

        ResponseEntity<?> result;
        try {
            List<Member> members = membersRepository.findAll();
            if (!members.isEmpty()) {
                result = new ResponseEntity<>(members, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No members found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
    //http://localhost:8082/members/id
    @GetMapping(value = "{memberId}")
    public ResponseEntity<?> getById(@PathVariable Integer memberId){
        logger.info(LogUtils.info(CLASS_NAME,"getById",String.format("(%d)", memberId)));

        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()){

            Member member= optionalMember.get();
            result = new ResponseEntity<>(member, HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Member mit der Id = %d nicht vorhanden", memberId),HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/members/id/songs

    @GetMapping(value = "{memberId}/songs")
    public ResponseEntity<?> getSongsById(@PathVariable Integer memberId){

        logger.info(LogUtils.info(CLASS_NAME,"getSongsById",String.format("(%d)", memberId)));

        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()){

            Member member = optionalMember.get();
            result =  new ResponseEntity<>(member.getSongs(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Member mit der Id = %d nicht vorhanden", memberId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    //http://localhost:8082/members/id/functions

    @GetMapping(value = "{memberId}/functions")
    public ResponseEntity<?> getFunctionsById(@PathVariable Integer memberId){

        logger.info(LogUtils.info(CLASS_NAME,"getFunctionsById",String.format("(%d)", memberId)));

        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()){

            Member member = optionalMember.get();
            result =  new ResponseEntity<>(member.getFunctions(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Member mit der Id = %d nicht vorhanden", memberId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    //http://localhost:8082/members/id/songs

    @GetMapping(value = "{memberId}/instruments")
    public ResponseEntity<?> getInstrumentsById(@PathVariable Integer memberId){

        logger.info(LogUtils.info(CLASS_NAME,"getInstrumentsById",String.format("(%d)", memberId)));

        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()){

            Member member = optionalMember.get();
            result =  new ResponseEntity<>(member.getInstruments(), HttpStatus.OK);
        }else{
            result = new ResponseEntity<>(String.format("Member mit der Id = %d nicht vorhanden", memberId),HttpStatus.NOT_FOUND);
        }
        return result;

    }
    // Einfügen einer neuen Ressource
    @PostMapping("")
    public ResponseEntity<?> addMember(@Valid @RequestBody Member member, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addMember", String.format("(%s)", member)));
        return getResponseEntity(member, bindingResult);
    }

    // Ändern einer vorhandenen Ressource
    @PutMapping("")
    public ResponseEntity<?> updateMember(@Valid @RequestBody Member member, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateMember", String.format("(%s)", member)));
        return getResponseEntity(member, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Member member, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Member savedMember = membersRepository.save(member);
            return new ResponseEntity<>(savedMember, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:8082/members/id (delete)
    @DeleteMapping(value = "{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Integer memberId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteMember", String.format("(%d)", memberId)));
        String errorMessage = "";
        ResponseEntity<?> result;
        Member member;

        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            member = optionalMember.get();
        } else {
            return new ResponseEntity<>("Member not found", HttpStatus.NOT_FOUND);
        }

        try {
            membersRepository.delete(member);
            result = new ResponseEntity<>(member, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /*// einfügen einer neuen Ressource
    @PostMapping(value = "")
    public ResponseEntity<?> add(@Valid @RequestBody Member member,
                                 BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "add", String.format("(%s)", member)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }

        if (!error) {
            try {
                membersRepository.save(member);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }

        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Member>(member, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;

    }

    // ändern einer vorhandenen Ressource
    @PutMapping(value = "")
    public ResponseEntity<?> update(@Valid @RequestBody Member member,
                                    BindingResult bindingResult) {

        logger.info(LogUtils.info(CLASS_NAME, "update", String.format("(%s)", member)));

        boolean error = false;
        String errorMessage = "";

        if (!error) {
            error = bindingResult.hasErrors();
            errorMessage = bindingResult.toString();
        }
        if (!error) {
            try {
                membersRepository.save(member);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
                errorMessage = e.getCause().getCause().getLocalizedMessage();
            }
        }
        ResponseEntity<?> result;
        if (!error) {
            result = new ResponseEntity<Member>(member, HttpStatus.OK);

        } else {
            result = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


    //http://localhost:8082/publishers/id (delete)
    @DeleteMapping(value = "{memberId}")
    public ResponseEntity<?> deletePV(@PathVariable Integer memberId) {
        logger.info(LogUtils.info(CLASS_NAME, "deletePV", String.format("(%d)", memberId)));
        boolean error = false;
        String errorMessage = "";
        ResponseEntity<?> result;
        Member member = null;


        if (!error) {
            Optional<Member> optionalMember = membersRepository.findById(memberId);
            if (optionalMember.isPresent()) {
                member = optionalMember.get();
            } else {
                error = true;
                errorMessage = "Member not found";
            }
        }

        if (!error) {
            try {
                membersRepository.delete(member);
            } catch (Exception e) {
                error = true;
                errorMessage = ErrorsUtils.getErrorMessage(e);
            }
        }
        if (!error) {
            result = new ResponseEntity<Member>(member, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }*/




}
