package com.multi.ouigo.common.exception.custom;

import org.springframework.http.HttpStatus;

public class AlreadyAppliedException extends CustomException {

    public AlreadyAppliedException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
