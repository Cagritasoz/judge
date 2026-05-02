package com.cagritasoz.exception;

import com.cagritasoz.model.Submission;
import lombok.Getter;

@Getter
public class InternalProcessingException extends RuntimeException {

    private final Submission partial;

    public InternalProcessingException(Submission partial, String message, Throwable cause) {
        super(message, cause);
        this.partial = partial;
    }
}
