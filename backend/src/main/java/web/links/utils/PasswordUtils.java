package web.links.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private static final Argon2PasswordEncoder ARGON2 = new Argon2PasswordEncoder();

    public static BCryptPasswordEncoder bcryptEncoder() {
        return BCRYPT;
    }

    public static Argon2PasswordEncoder argon2Encoder() {
        return ARGON2;
    }
}
