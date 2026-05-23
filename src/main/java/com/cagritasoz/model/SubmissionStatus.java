package com.cagritasoz.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionStatus {
    PASS("Pass"),
    FAIL("Fail"),
    IN_PROGRESS("In Progress"),

    COMPARISON_FAILED("Comparison failed"),
    PENDING_REVIEW("Pending Review"),

    RUN_ERROR("Run Error"),
    TIMED_OUT("Timed Out"),

    COMPILE_ERROR("Compile Error"),

    SOURCE_SEARCH_FAILED("Source File Search Failed"),
    NO_SOURCE_FILE("No Source File Found"),
    ENTRY_POINT_NOT_FOUND("Entry Point Not Found"),

    EXTRACTION_FAILED("Zip Extraction Failed"),

    SKIPPED("Skipped"),

    INTERNAL_ERROR("Internal Error");

    private final String displayName;

}
