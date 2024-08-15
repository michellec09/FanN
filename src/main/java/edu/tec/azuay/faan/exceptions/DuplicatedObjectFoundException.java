package edu.tec.azuay.faan.exceptions;

public class DuplicatedObjectFoundException extends RuntimeException {

        public DuplicatedObjectFoundException() {
        }

        public DuplicatedObjectFoundException(String message) {
            super(message);
        }

        public DuplicatedObjectFoundException(String message, Throwable cause) {
            super(message, cause);
        }
}
