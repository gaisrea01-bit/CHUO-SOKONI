package tz.ac.mzumbe.chuosokoni;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Enables automated tasks like holding fee calculations
public class ChuoSokoniApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChuoSokoniApplication.class, args);
    }
}