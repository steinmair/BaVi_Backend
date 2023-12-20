package at.htlklu.bavi;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaAuditing
@EnableSwagger2
public class Application
{
	public static void main(String[] args)
	{		
		SpringApplication.run(Application.class, args);

	}
}
