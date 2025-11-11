package com.multi.ouigo.common.exception.custom;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends CustomException {

    public NotAuthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message); // 401
    }
}