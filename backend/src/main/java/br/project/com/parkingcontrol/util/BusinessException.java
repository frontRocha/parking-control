package br.project.com.parkingcontrol.util;

public class BusinessException extends RuntimeException {
    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(Exception ex) {
        super(ex);
    }
}
