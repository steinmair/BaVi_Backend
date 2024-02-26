package at.htlklu.bavi.controller;

import at.htlklu.bavi.model.Role;
import at.htlklu.bavi.repository.MembersRepository;
import at.htlklu.bavi.repository.RoleRepository;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
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
@RequestMapping("roles")
public class RoleController {

    private static final Logger logger = LogManager.getLogger(RoleController.class);
    private static final String CLASS_NAME = "RoleController";

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MembersRepository membersRepository;


    //http://localhost:8082/functions
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getAllRoles() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllRoles", "Retrieving all roles"));

        ResponseEntity<?> result;
        try {
            List<Role> roles = roleRepository.findAll();
            if (!roles.isEmpty()) {
                result = new ResponseEntity<>(roles, HttpStatus.OK);
            } else {
                result = new ResponseEntity<>("No roles found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    //http://localhost:8082/functions/id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{roleId}")
    public ResponseEntity<?> getRoleById(@PathVariable Integer roleId) {
        logger.info(LogUtils.info(CLASS_NAME, "getRoleById", String.format("(%d)", roleId)));

        ResponseEntity<?> result;
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isPresent()) {

            Role role = optionalRole.get();
            result = new ResponseEntity<>(role, HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Role mit der Id = %d nicht vorhanden", roleId), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    //http://localhost:8082/functions/id/members
    @PreAuthorize("hasRole('ADMIN')")

    @GetMapping(value = "{roleId}/members")
    public ResponseEntity<?> getMembersByRoleId(@PathVariable Integer roleId) {

        logger.info(LogUtils.info(CLASS_NAME, "getMembersByRoleId", String.format("(%d)", roleId)));

        ResponseEntity<?> result;
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isPresent()) {

            Role role = optionalRole.get();
            result = new ResponseEntity<>(role.getMembers(), HttpStatus.OK);
        } else {
            result = new ResponseEntity<>(String.format("Function mit der Id = %d nicht vorhanden", roleId), HttpStatus.NOT_FOUND);
        }
        return result;

    }

    // einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> addRole(@Valid @RequestBody Role role, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addRole", String.format("(%s)", role)));
        return getResponseEntity(role, bindingResult);
    }

    // ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    public ResponseEntity<?> updateRole(@Valid @RequestBody Role role, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateRole", String.format("(%s)", role)));
        return getResponseEntity(role, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Role role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Role savedRole = roleRepository.save(role);
            return new ResponseEntity<>(savedRole, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Consider logging the exception
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //http://localhost:8082/functions/id (delete)
    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping(value = "{roleId}")
    public ResponseEntity<?> deleteFunction(@PathVariable Integer roleId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteRole", String.format("(%d)", roleId)));
        String errorMessage;
        ResponseEntity<?> result;
        Role role;

        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if (optionalRole.isPresent()) {
            role = optionalRole.get();
        } else {
            return new ResponseEntity<>("Role not found", HttpStatus.NOT_FOUND);
        }

        try {
            roleRepository.delete(role);
            result = new ResponseEntity<>(role, HttpStatus.OK);
        } catch (Exception e) {
            errorMessage = ErrorsUtils.getErrorMessage(e);
            result = new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


}
