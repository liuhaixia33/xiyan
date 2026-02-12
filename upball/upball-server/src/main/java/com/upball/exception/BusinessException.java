package com.upball.exception;

import com.upball.enums.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final Integer code;
    private final String message;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
