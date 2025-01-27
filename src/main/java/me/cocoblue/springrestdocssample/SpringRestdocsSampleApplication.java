package me.cocoblue.springrestdocssample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringRestdocsSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRestdocsSampleApplication.class, args);
    }

}
