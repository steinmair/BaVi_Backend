package at.htlklu.bavi.controller;


import at.htlklu.bavi.model.Instrument;
import at.htlklu.bavi.model.Role;
import at.htlklu.bavi.model.Song;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.repository.MembersRepository;
import at.htlklu.bavi.repository.SongsRepository;
import at.htlklu.bavi.repository.RoleRepository;
import at.htlklu.bavi.repository.InstrumentsRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("members")
public class MemberController {

    private static final Logger logger = LogManager.getLogger(MemberController.class);
    private static final String CLASS_NAME = "MemberController";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    InstrumentsRepository instrumentsRepository;
    @Autowired
    MembersRepository membersRepository;
    @Autowired
    SongsRepository songsRepository;


    //http://localhost:8082/members

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Members", description = "Retrieve all members")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved members",
            content = @Content(schema = @Schema(implementation = Member.class)))
    @ApiResponse(responseCode = "404", description = "No members found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllMembers() {
        logger.info("Retrieving all members");
        ResponseEntity<?> result;
        try {
            List<Member> members = membersRepository.findAll();
            if (!members.isEmpty()) {
                logger.debug("Retrieved {} members", members.size());
                result = new ResponseEntity<>(members, HttpStatus.OK);
            } else {
                logger.info("No members found");
                result = new ResponseEntity<>("No members found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving members: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/members/id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{memberId}")
    @Operation(summary = "Get Member by ID", description = "Retrieve member by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved member",
            content = @Content(schema = @Schema(implementation = Member.class)))
    @ApiResponse(responseCode = "404", description = "Member not found")
    public ResponseEntity<?> getById(@PathVariable Integer memberId) {
        logger.info("Retrieving member with ID: {}", memberId);
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isEmpty()) {
            logger.info("Member not found: {}", memberId);
            return new ResponseEntity<>(String.format("Member not found (%d)", memberId), HttpStatus.NOT_FOUND);
        }
        Member member = optionalMember.get();
        logger.debug("Retrieved member: {}", member);
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    //http://localhost:8082/members/id/songs
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{memberId}/songs")
    @Operation(summary = "Get Songs by Member ID", description = "Retrieve songs by member ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved songs",
            content = @Content(schema = @Schema(implementation = Song.class)))
    @ApiResponse(responseCode = "404", description = "Member not found")
    public ResponseEntity<?> getSongsById(@PathVariable Integer memberId) {

        logger.info(LogUtils.info(CLASS_NAME, "getSongsById", String.format("(%d)", memberId)));

        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()) {

            Member member = optionalMember.get();
            logger.debug("Retrieved member: {}", member);
            result = new ResponseEntity<>(member.getSongs(), HttpStatus.OK);

        } else {
            logger.info("Member not found: {}", memberId);
            result = new ResponseEntity<>(String.format("Member not found (%d)", memberId), HttpStatus.NOT_FOUND);
        }
        return result;

    }

    //http://localhost:8082/members/id/functions
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{memberId}/roles")
    @Operation(summary = "Get Roles by Member ID", description = "Retrieve roles by member ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved roles",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @ApiResponse(responseCode = "404", description = "Member not found")
    public ResponseEntity<?> getRolesById(@PathVariable Integer memberId) {
        logger.info(LogUtils.info(CLASS_NAME, "getRolesById", String.format("(%d)", memberId)));
        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            logger.debug("Retrieved member: {}", member);
            result = new ResponseEntity<>(member.getRoles(), HttpStatus.OK);
        } else {
            logger.info("Member not found: {}", memberId);
            result = new ResponseEntity<>(String.format("Member not found (%d)", memberId), HttpStatus.NOT_FOUND);
        }
        return result;

    }

    //http://localhost:8082/members/id/songs
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{memberId}/instruments")
    @Operation(summary = "Get Instruments by Member ID", description = "Retrieve instruments by member ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved instruments",
            content = @Content(schema = @Schema(implementation = Instrument.class)))
    @ApiResponse(responseCode = "404", description = "Member not found")
    public ResponseEntity<?> getInstrumentsById(@PathVariable Integer memberId) {
        logger.info(LogUtils.info(CLASS_NAME, "getInstrumentsById", String.format("(%d)", memberId)));
        ResponseEntity<?> result;
        Optional<Member> optionalMember = membersRepository.findById(memberId);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            logger.debug("Retrieved member: {}", member);
            result = new ResponseEntity<>(member.getInstruments(), HttpStatus.OK);
        } else {
            logger.info("Member not found: {}", memberId);
            result = new ResponseEntity<>(String.format("Member not found (%d)", memberId), HttpStatus.NOT_FOUND);
        }
        return result;

    }

    // Einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @Operation(summary = "Add Member", description = "Add a new member")
    @ApiResponse(responseCode = "200", description = "Member added successfully",
            content = @Content(schema = @Schema(implementation = Member.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addMember(@Valid @RequestBody Member member, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addMember", String.format("(%s)", member)));
        return getResponseEntity(member, bindingResult);
    }

    // Ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    @Operation(summary = "Update Member", description = "Update an existing member")
    @ApiResponse(responseCode = "200", description = "Member updated successfully",
            content = @Content(schema = @Schema(implementation = Member.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
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
            String encodedPassword = passwordEncoder.encode(member.getPassword());
            member.setPassword(encodedPassword);
            Member savedMember = membersRepository.save(member);
            logger.info("Saved member: {}", savedMember);
            return new ResponseEntity<>(savedMember, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error saving member: {}", e.getMessage());
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // http://localhost:8082/members/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{memberId}")
    @Operation(summary = "Delete Member", description = "Delete a member by its ID")
    @ApiResponse(responseCode = "200", description = "Member deleted successfully")
    @ApiResponse(responseCode = "404", description = "Member not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteMember(@PathVariable Integer memberId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteMember", String.format("(%d)", memberId)));
        String errorMessage;
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
            logger.info("Deleted member: {}", member);
            result = new ResponseEntity<>(member, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            logger.error("Error deleting member: {}", e.getMessage());
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


}
