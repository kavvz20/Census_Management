package com.census.management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RegionAccessDeniedException extends RuntimeException {

    public RegionAccessDeniedException(String message) {
        super(message);
    }
}
