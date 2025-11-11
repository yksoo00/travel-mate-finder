package com.multi.ouigo.common.exception.custom;

import org.springframework.http.HttpStatus;

public class InvalidStatusException extends CustomException {

    public InvalidStatusException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
