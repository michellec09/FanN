package edu.tec.azuay.faan.exceptions;

public class RecoveryTokenAlreadyExistsException extends RuntimeException {
    public RecoveryTokenAlreadyExistsException() {
        super();
    }

    public RecoveryTokenAlreadyExistsException(String message) {
        super(message);
    }

    public RecoveryTokenAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
