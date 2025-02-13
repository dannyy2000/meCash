package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.general.ApiResponse;
import org.springframework.http.HttpStatus;

public class Controller {

    private final HttpServletResponse response;

    public Controller(HttpServletResponse response) {
        this.response = response;
    }


    <T extends ApiResponse> T responseWithUpdatedHttpStatus(T responseDTO) {
        switch (responseDTO.getStatus()) {
            case SUCCESS:
                response.setStatus(HttpStatus.OK.value());
                break;
            case NO_CONTENT:
                response.setStatus(HttpStatus.NO_CONTENT.value());
                break;
            case CREATED:
                response.setStatus(HttpStatus.CREATED.value());
                break;
            case NOT_FOUND:
                response.setStatus(HttpStatus.NOT_FOUND.value());
                break;
            case FORBIDDEN:
                response.setStatus(HttpStatus.FORBIDDEN.value());
                break;
            default:
                response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return responseDTO;
    }
}
