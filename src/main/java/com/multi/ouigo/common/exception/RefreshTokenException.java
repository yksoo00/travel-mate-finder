package com.multi.ouigo.common.exception;

import com.multi.ouigo.common.exception.custom.CustomException;
import org.springframework.http.HttpStatus;

public class RefreshTokenException extends CustomException {

    public RefreshTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message); // 401
    }
}


