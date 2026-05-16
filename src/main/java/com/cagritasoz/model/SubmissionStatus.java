package com.cagritasoz.model;

public enum SubmissionStatus {
    PASS,
    FAIL,
    IN_PROGRESS,

    COMPARISON_FAILED,
    PENDING_REVIEW,

    RUN_ERROR,
    TIMED_OUT,

    COMPILE_ERROR,

    SOURCE_SEARCH_FAILED,
    NO_SOURCE_FILE,
    ENTRY_POINT_NOT_FOUND,

    EXTRACTION_FAILED,

    SKIPPED,

    INTERNAL_ERROR

}
