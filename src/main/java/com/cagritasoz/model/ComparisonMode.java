package com.cagritasoz.model;

public enum ComparisonMode {
    NO_COMPARISON, // Do not make any comparisons, the output can be reviewed, user sets pass or fail.
    EXACT, // Stdout of run should match expected output exactly.
    TRIMMED,
    IGNORE_WHITESPACE
}
