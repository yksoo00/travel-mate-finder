package com.multi.ouigo.common.response;


import org.springframework.http.HttpStatus;


public class ResponseNoDateDto {

    private int status;
    private String message;

    public ResponseNoDateDto(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "ResponseDto{" +
            "status=" + status +
            ", message='" + message + '\'' +
            '}';
    }
}
