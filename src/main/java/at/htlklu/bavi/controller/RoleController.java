package at.htlklu.bavi.controller;

import at.htlklu.bavi.model.Member;
import at.htlklu.bavi.model.Role;
import at.htlklu.bavi.repository.MembersRepository;
import at.htlklu.bavi.repository.RoleRepository;
import at.htlklu.bavi.utils.ErrorsUtils;
import at.htlklu.bavi.utils.LogUtils;
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
@RequestMapping("roles")
@CrossOrigin(origins = "*", maxAge = 3600)
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
    @Operation(summary = "Get All Roles", description = "Retrieve all roles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved roles",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @ApiResponse(responseCode = "404", description = "No roles found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getAllRoles() {
        logger.info(LogUtils.info(CLASS_NAME, "getAllRoles", "Retrieving all roles"));

        try {
            List<Role> roles = roleRepository.findAll();
            if (!roles.isEmpty()) {
                logger.debug("Retrieved {} roles", roles.size());
                return new ResponseEntity<>(roles, HttpStatus.OK);
            } else {
                logger.warn("No roles found");
                return new ResponseEntity<>("No roles found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving roles: {}", e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //http://localhost:8082/functions/id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{roleId}")
    @Operation(summary = "Get Role by ID", description = "Retrieve role by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved role",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @ApiResponse(responseCode = "404", description = "Role not found")
    public ResponseEntity<?> getRoleById(@PathVariable Integer roleId) {
        logger.info(LogUtils.info(CLASS_NAME, "getRoleById", String.format("(%d)", roleId)));

        try {
            Optional<Role> optionalRole = roleRepository.findById(roleId);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                logger.debug("Retrieved role: {}", role);
                return new ResponseEntity<>(role, HttpStatus.OK);
            } else {
                logger.warn("Role not found: {}", roleId);
                return new ResponseEntity<>(String.format("Role not found(%d)", roleId), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving role {}: {}", roleId, e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //http://localhost:8082/functions/id/members
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "{roleId}/members")
    @Operation(summary = "Get Members by Role ID", description = "Retrieve members by role ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved members",
            content = @Content(schema = @Schema(implementation = Member.class)))
    @ApiResponse(responseCode = "404", description = "Role not found")
    public ResponseEntity<?> getMembersByRoleId(@PathVariable Integer roleId) {
        logger.info(LogUtils.info(CLASS_NAME, "getMembersByRoleId", String.format("(%d)", roleId)));

        try {
            Optional<Role> optionalRole = roleRepository.findById(roleId);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                logger.debug("Retrieved members for role: {}", role);
                return new ResponseEntity<>(role.getMembers(), HttpStatus.OK);
            } else {
                logger.warn("Role not found: {}", roleId);
                return new ResponseEntity<>(String.format("Role not found(%d)", roleId), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving members for role {}: {}", roleId, e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // einfügen einer neuen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    @Operation(summary = "Add Role", description = "Add a new role")
    @ApiResponse(responseCode = "200", description = "Role added successfully",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> addRole(@Valid @RequestBody Role role, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "addRole", String.format("(%s)", role)));
        return getResponseEntity(role, bindingResult);
    }

    // ändern einer vorhandenen Ressource
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("")
    @Operation(summary = "Update Role", description = "Update an existing role")
    @ApiResponse(responseCode = "200", description = "Role updated successfully",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @ApiResponse(responseCode = "400", description = "Bad request")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> updateRole(@Valid @RequestBody Role role, BindingResult bindingResult) {
        logger.info(LogUtils.info(CLASS_NAME, "updateRole", String.format("(%s)", role)));
        return getResponseEntity(role, bindingResult);
    }

    @NotNull
    private ResponseEntity<?> getResponseEntity(@RequestBody @Valid Role role, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred for role: {}", bindingResult.getAllErrors());
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }

        try {
            Role savedRole = roleRepository.save(role);
            logger.debug("Saved role: {}", savedRole);
            return new ResponseEntity<>(savedRole, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error saving role: {}", e.getMessage());
            String errorMessage = e.getCause().getCause().getLocalizedMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //http://localhost:8082/functions/id (delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "{roleId}")
    @Operation(summary = "Delete Role", description = "Delete a role by its ID")
    @ApiResponse(responseCode = "200", description = "Role deleted successfully")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteRole(@PathVariable Integer roleId) {
        logger.info(LogUtils.info(CLASS_NAME, "deleteRole", String.format("(%d)", roleId)));

        try {
            Optional<Role> optionalRole = roleRepository.findById(roleId);
            if (optionalRole.isPresent()) {
                Role role = optionalRole.get();
                roleRepository.delete(role);
                logger.debug("Deleted role: {}", role);
                return new ResponseEntity<>(role, HttpStatus.OK);
            } else {
                logger.warn("Role not found: {}", roleId);
                return new ResponseEntity<>("Role not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error deleting role {}: {}", roleId, e.getMessage());
            String errorMessage = ErrorsUtils.getErrorMessage(e);
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
