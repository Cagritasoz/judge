package com.cagritasoz.exception;

import com.cagritasoz.model.Submission;
import lombok.Getter;

@Getter
public class InternalProcessingException extends RuntimeException {

    private final Submission partial;

    public InternalProcessingException(Submission partial, Throwable cause) {
        super(cause);
        this.partial = partial;
    }
}
