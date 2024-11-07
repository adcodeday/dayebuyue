package org.lu.zhaodazi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ZhaodaziApplication {
       public static void main(String[] args) {
        SpringApplication.run(ZhaodaziApplication.class, args);
    }

}
