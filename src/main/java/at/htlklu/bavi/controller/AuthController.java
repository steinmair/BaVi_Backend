package at.htlklu.bavi.controller;

import java.util.List;
import java.util.stream.Collectors;


import at.htlklu.bavi.repository.RoleRepository;
import at.htlklu.bavi.repository.MembersRepository;
import at.htlklu.bavi.utils.LogUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.htlklu.bavi.payload.request.LoginRequest;
import at.htlklu.bavi.payload.response.JwtResponse;
import at.htlklu.bavi.Security.jwt.JwtUtils;
import at.htlklu.bavi.Security.services.UserDetailsImpl;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);
    private static final String CLASS_NAME = "AuthController";
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MembersRepository membersRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("login")
    @Operation(summary = "Authenticate User", description = "Endpoint to authenticate a user.")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully",
            content = @Content(schema = @Schema(implementation = JwtResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")

    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        logger.info(LogUtils.info(CLASS_NAME, "authenticateUser", String.format("loginRequest (%s)",loginRequest.getEmail())));


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        logger.info(LogUtils.info(CLASS_NAME, "authenticateUser", String.format("User authenticated successfully (%s)",jwt)));

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.memberId(),
                userDetails.eMail(),
                roles));

    }
}
