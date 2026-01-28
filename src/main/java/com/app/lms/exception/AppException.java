package com.app.lms.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException{
    private ErroCode erroCode;

    public AppException(ErroCode erroCode) {
        super(erroCode.getMessage());
        this.erroCode = erroCode;
    }

}
