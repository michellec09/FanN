package edu.tec.azuay.faan.exceptions;

import edu.tec.azuay.faan.persistence.dto.exception.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlerGenericException(Exception exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Internal Server Error, Try again later");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setTime(LocalDateTime.now());
        error.setHttpCode(500);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Error: Unexpected request body or parameters");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setTime(LocalDateTime.now());
        error.setHttpCode(400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DuplicatedObjectFoundException.class)
    public ResponseEntity<?> handlerDuplicatedObjectFoundException(DuplicatedObjectFoundException exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Error: The object already exists");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setTime(LocalDateTime.now());
        error.setHttpCode(409);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<?> handlerObjectNotFoundException(ObjectNotFoundException exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Error: The object doesn't exist");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setTime(LocalDateTime.now());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setHttpCode(404);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handlerIOException(IOException exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Error: Error reading the file");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setTime(LocalDateTime.now());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setHttpCode(500);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handlerInvalidPasswordException(InvalidPasswordException exception, HttpServletRequest request){

        ApiError error = new ApiError();
        error.setMessage("Error: Invalid password");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setTime(LocalDateTime.now());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setHttpCode(400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RecoveryTokenAlreadyExistsException.class)
    public ResponseEntity<?> handlerRecoveryTokenAlreadyExistsException(RecoveryTokenAlreadyExistsException exception, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setMessage("Error: The user already has an active token");
        error.setBackendMessage(exception.getLocalizedMessage());
        error.setTime(LocalDateTime.now());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());
        error.setHttpCode(409);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handlerAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        ApiError error = new ApiError();
        error.setMessage(exception.getLocalizedMessage());
        error.setBackendMessage("Access Denied, you don't have permission to access this resource");
        error.setTime(LocalDateTime.now());
        error.setUrl(request.getRequestURI());
        error.setMethod(request.getMethod());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
