package com.multi.ouigo.common.exception.custom;

import org.springframework.http.HttpStatus;

public class TokenException extends CustomException {

    public TokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message); // 401
    }
}