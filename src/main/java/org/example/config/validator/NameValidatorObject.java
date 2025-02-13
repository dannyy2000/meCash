package org.example.config.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.general.ErrorMessages;

import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class NameValidatorObject {

    public static void validateName(String name) {
        GeneralValidatorObject.validate(name);
        boolean isNameValid;
        isNameValid = Pattern.matches("^[a-zA-Z0-9_-]*$", name);
        if (!isNameValid){
            throw new IllegalArgumentException(String.format(ErrorMessages.INVALID_FORMAT_NAME));
        }
    }
}
