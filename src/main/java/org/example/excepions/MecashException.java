package org.example.excepions;

public class MecashException extends RuntimeException{
    public MecashException() {
    }

    public MecashException(String message) {
        super(message);
    }

    public MecashException(String message, Throwable cause) {
        super(message, cause);
    }

    public MecashException(Throwable cause) {
        super(cause);
    }


}
