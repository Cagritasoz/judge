package com.cagritasoz.model;

public enum Stage {
    DISCOVERY,
    EXTRACTION, // ZIP Extraction stage.
    SOURCE_SEARCH, // Searching for source files stage.
    COMPILE, // Compilation stage
    RUN, // Running Stage
    COMPARISON // Comparison Stage
}
