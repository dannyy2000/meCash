package org.example.excepions;

public class OtpGenerationException extends MecashException{
    public OtpGenerationException() {
    }

    public OtpGenerationException(String message) {
        super(message);
    }

    public OtpGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
