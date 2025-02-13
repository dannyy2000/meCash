package org.example.config.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.example.general.ErrorMessages;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EmailValidatorObj {

    public static void validateEmail(String email) {
        String emailRegex = "^[\\p{Alnum}._%+-]+@[\\p{Alnum}.+-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (StringUtils.isEmpty(email)) {
            log.info("Email: {} is not a valid email address.", email);
            throw new IllegalArgumentException(ErrorMessages.EMAIL_EMPTY);
        }
        if (!EmailValidator.getInstance().isValid(email.trim())){
            throw new IllegalArgumentException(ErrorMessages.INVALID_EMAIL_ADDRESS);
        }

        if (!pattern.matcher(email).matches()) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_EMAIL_ADDRESS);
        }
    }
}
