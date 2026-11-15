package com.queuecare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Dipetakan ke 404 supaya data orang lain atau data nonaktif tidak terlihat ada. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TidakDitemukanException extends RuntimeException {

    public TidakDitemukanException(String message) {
        super(message);
    }
}
