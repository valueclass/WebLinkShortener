package web.links.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Configuration
public class PasswordEncoderConfig {
    private final Argon2PasswordEncoder argon2Encoder = new Argon2PasswordEncoder();
    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    @Bean
    public PasswordEncoder passwordEncoder() {
        final String defaultId = "argon2";
        final Map<String, PasswordEncoder> encoders = Map.of(
                defaultId, argon2Encoder,
                "bcrypt", bcryptEncoder
        );

        final DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder(defaultId, encoders);
        encoder.setDefaultPasswordEncoderForMatches(argon2Encoder);

        return encoder;
    }
}
