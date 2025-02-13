package org.example.excepions;

public class OtpNotFoundException extends MecashException{
    public OtpNotFoundException() {
    }

    public OtpNotFoundException(String message) {
        super(message);
    }

    public OtpNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
