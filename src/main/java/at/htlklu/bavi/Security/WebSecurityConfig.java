package at.htlklu.bavi.Security;

import at.htlklu.bavi.Security.jwt.AuthEntryPointJwt;
import at.htlklu.bavi.Security.jwt.AuthTokenFilter;
import at.htlklu.bavi.Security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static at.htlklu.bavi.Security.jwt.JwtUtils.logger;


@Configuration
@EnableWebSecurity

@EnableMethodSecurity()
class WebSecurityConfig {//extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    private static final String[] AUTH_WHITELIST = {
            "login",
            "api/v1/auth/**",
            "v3/api-docs/**",
            "v3/api-docs/yaml",
            "api-docs/**",
            "swagger-ui/**",
            "swagger-resources",
            "swagger-resources/**",
            "webjars/**",
            "configuration/**",
            "configuration/ui",
            "csrf",
            "swagger-ui.html"
    };

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        // Log the creation of the authentication provider
        logger.debug("Authentication provider bean created");

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        AuthenticationManager authenticationManager = authConfig.getAuthenticationManager();

        // Log the creation of the authentication manager
        logger.debug("Authentication manager bean created");

        return authenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Log the creation of the password encoder
        logger.debug("Password encoder bean created");

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Configure security filters
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                );

        // Set up authentication provider
        http.authenticationProvider(authenticationProvider());

        // Add JWT token filter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Log the setup of security filter chain
        logger.debug("Security filter chain configured");

        return http.build();
    }


}

