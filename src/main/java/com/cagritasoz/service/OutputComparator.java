package com.cagritasoz.service;

import com.cagritasoz.model.ComparisonMode;
import com.cagritasoz.model.ComparisonResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class OutputComparator {

    public ComparisonResult compareOutput(ComparisonMode comparisonMode,
                                          Path stdoutFilePath,
                                          String expectedOutput) throws IOException{

        log.info("Comparing with comparison mode: {}, Expected output: {}", comparisonMode, expectedOutput);

        ComparisonResult comparisonResult = ComparisonResult.builder()
                .comparisonMode(comparisonMode)
                .expectedLength(expectedOutput.length()) // Should not ever be null because of pre-validation
                .build();

        String actual = readBounded(stdoutFilePath);

        log.info("Run Stdout read as: {}", actual);

        comparisonResult.setActualLength(actual.length());

        boolean passed = switch(comparisonMode) {
            case EXACT -> compareExact(expectedOutput, actual);
            case TRIMMED -> compareTrimmed(expectedOutput, actual);
            case IGNORE_WHITESPACE -> compareIgnoreWhitespace(expectedOutput, actual);
            default -> false;
        };

        comparisonResult.setPassed(passed);

        return comparisonResult;

    }

    /*
        - There should be a limit to the run stdout, what if run stdout is a file with millions of lines due to an infinite loop?
        - If default limit of 3 KB is exceeded, this will likely fail anyway.
        - readBounded method is here for avoiding an OutOfMemory error when trying to read with Files.readString()
        - If file size is too big this causes an OutOfMemory exception to be thrown.
        - Just read some of the file not all of it!
    */

    private boolean compareExact(String expected, String actual) {
        return expected.equals(actual);
    }

    private boolean compareTrimmed(String expected, String actual) {
        return normalizeForTrimmed(expected).equals(normalizeForTrimmed(actual));
    }

    private boolean compareIgnoreWhitespace(String expected, String actual) {
        return normalizeForIgnoreWhitespace(expected).equals(normalizeForIgnoreWhitespace(actual));
    }

    private String normalizeForTrimmed(String text) {
        String normalized = text.replace("\r\n", "\n") // Convert windows line separator to unix
                .replace("\r", "\n"); // Convert old mac line separator to unix.

        String[] lines = normalized.split("\n", -1); // -1 to keep trailing empty strings for split method
        StringBuilder result = new StringBuilder();
        for(String line : lines) {
            result.append(line.stripTrailing()); // Strip trailing whitespace, monitor if just strip() should be used.
            result.append("\n"); // Reconstruct
        }

        return result.toString().strip();
    }

    // Replace all whitespace ([SPACE], \n, \r, \t) with a single space
    private String normalizeForIgnoreWhitespace(String text) {
        return text.replaceAll("\\s+", " ").strip();
    }

    private static final int MAX_OUTPUT_BYTES = 3*1024; // 3 KB, thread safe because read only.

    private String readBounded(Path filePath) throws IOException {
        long size = Files.size(filePath);
        if(size == 0) { // No output
            return "";
        }
        if(size <= MAX_OUTPUT_BYTES) { // Size less than limit, read it.
            return Files.readString(filePath);
        }
        byte[] buffer = new byte[MAX_OUTPUT_BYTES];
        int read;
        try(InputStream is = Files.newInputStream(filePath)) {  // Size bigger than limit, read as much as the limit.
            read = is.read(buffer); // Write read bytes to buffer.
        }

        return new String(buffer, 0, read);

    }
}
