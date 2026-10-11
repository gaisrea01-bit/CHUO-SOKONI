package tz.ac.mzumbe.chuosokoni.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component; // 1. Added import

@Configuration
@Component("customPasswordEncoderClass") // 2. Forces Spring to use a unique name for the class component
public class PasswordEncoder {

    @Bean(name = "passwordEncoder") // 3. Your method safely keeps the name you want
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // Sets BCrypt strength (work factor / log rounds) to 12
        return new BCryptPasswordEncoder(12);
    }
}
