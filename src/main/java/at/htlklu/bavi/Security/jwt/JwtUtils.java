package at.htlklu.bavi.Security.jwt;

import at.htlklu.bavi.Security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
  public static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  @Value("${BAVI.app.jwtSecret}")
  private String jwtSecret;

  @Value("${BAVI.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    // Log the user email for which the token is being generated
    logger.info("Generating JWT token for user: {}", userPrincipal.geteMail());

    try {
      // Build the JWT token
      String jwtToken = Jwts.builder()
              .setSubject(userPrincipal.geteMail())
              .setIssuedAt(new Date())
              .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
              .signWith(key(), SignatureAlgorithm.HS256)
              .compact();

      // Log successful token generation
      logger.debug("JWT token generated successfully for user: {}", userPrincipal.geteMail());

      return jwtToken;
    } catch (Exception e) {
      // Log any exceptions that occur during token generation
      logger.error("Error generating JWT token for user: {}", userPrincipal.geteMail(), e);
      throw e; // Rethrow the exception after logging
    }
  }
  
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key()).build()
               .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateJwtToken(String authToken) {

    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

}
