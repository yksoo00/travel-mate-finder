package com.multi.ouigo.common.exception.custom;

import org.springframework.http.HttpStatus;

public class DuplicateUsernameException extends CustomException {

    public DuplicateUsernameException(String message) {
        super(HttpStatus.CONFLICT, message);
    }


}
