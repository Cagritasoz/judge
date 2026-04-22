package com.cagritasoz.model;

public enum SubmissionStatus {
    PASS,
    FAIL,

    RUNTIME_ERROR,
    TIMED_OUT,

    COMPILE_ERROR,

    NO_SOURCE_FILE,
    ENTRY_POINT_NOT_FOUND,

    EXTRACTION_FAILED,

    SKIPPED,

    INTERNAL_ERROR

}
