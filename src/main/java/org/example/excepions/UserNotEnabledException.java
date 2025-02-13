package org.example.excepions;

public class UserNotEnabledException extends MecashException{
    public UserNotEnabledException() {
    }

    public UserNotEnabledException(String message) {
        super(message);
    }

    public UserNotEnabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
