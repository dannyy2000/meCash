package org.example.excepions;

public class OtpValidationException extends MecashException{
    public OtpValidationException() {
    }

    public OtpValidationException(String message) {
        super(message);
    }

    public OtpValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtpValidationException(Throwable cause) {
        super(cause);
    }
}
