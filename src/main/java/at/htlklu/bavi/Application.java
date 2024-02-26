package at.htlklu.bavi;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing

@OpenAPIDefinition(info = @Info(title = "BAVI APIs", version = "1.0", description = "Musicsheet Management APIs"))
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    //http://localhost:8082/swagger-ui/#/

}

//https://github.com/NrktSLL/spring-boot-minio/tree/master
//https://github.com/Rapter1990/SpringBootMinio


//https://www.baeldung.com/spring-security-jdbc-authentication
//https://medium.com/@barbieri.santiago/implementing-user-authentication-in-java-apis-using-spring-boot-spring-security-and-spring-data-cb9eac2361f6
