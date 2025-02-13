package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailNotificationRequest {

    @NotNull(message = "This field cannot be null")
    @NotBlank(message = "This field cannot be blank")
    private String recipients;
    private final String subject = "welcome to mecash";
    @NotNull(message = "This field cannot be null")
    @NotBlank(message = "This field cannot be blank")
    private String text;

}
