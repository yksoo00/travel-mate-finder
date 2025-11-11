package com.multi.ouigo.common.exception.custom;

import org.springframework.http.HttpStatus;

public class NotFindException extends CustomException {

    public NotFindException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
