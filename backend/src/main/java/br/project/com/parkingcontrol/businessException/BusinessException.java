package br.project.com.parkingcontrol.businessException;

import br.project.com.parkingcontrol.util.errorResponse.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BusinessException extends RuntimeException{
    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(Exception ex) {
        super(ex);
    }

    public static ResponseEntity<Object> handleBusinessException(BusinessException message, Integer httpStatus) throws IllegalArgumentException {
        ErrorResponse errorResponse = setErrorResponse(message.getMessage(), httpStatus);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    public static ErrorResponse setErrorResponse(String message, Integer codeError) {
        ErrorResponse errorResponse = new ErrorResponse.Builder()
                .setMessageError(message)
                .setCodeError(codeError)
                .build();

        return errorResponse;
    }
}
