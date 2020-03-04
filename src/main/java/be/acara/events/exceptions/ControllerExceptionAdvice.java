package be.acara.events.exceptions;

import be.acara.events.controller.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionAdvice extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiError> handleCustomException(CustomException ex) {
        if (ex.getStatus().is5xxServerError()) {
            logger.error(ex, ex);
        } else {
            logger.warn(ex, ex);
        }
        
        return new ResponseEntity<>(
                ApiError.builder()
                        .title(ex.getTitle())
                        .message(ex.getMessage())
                        .status(ex.getStatus().getReasonPhrase())
                        .code(ex.getStatus().value())
                        .build(),
                ex.getStatus()
        );
    }
    
    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
