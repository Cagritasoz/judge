package com.cagritasoz.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InputMode {
    NO_ARGUMENTS("No Arguments"),
    ARGUMENTS("Arguments"),
    STDIN("STDIN"), // Might not include this one.
    FILE("File"); // And this one.

    private final String displayName;
}
