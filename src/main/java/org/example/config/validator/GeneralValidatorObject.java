package org.example.config.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.general.ErrorMessages;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneralValidatorObject {
    public static void validate(String name)  {
        if (StringUtils.isEmpty(name) || StringUtils.isBlank(name) || StringUtils.isEmpty(name.trim())) {
            throw new IllegalArgumentException(String.format(ErrorMessages.CANNOT_BE_EMPTY_OR_NULL));
        }
    }

}
