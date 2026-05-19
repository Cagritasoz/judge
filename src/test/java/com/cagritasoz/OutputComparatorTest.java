package com.cagritasoz;


import com.cagritasoz.model.ComparisonMode;
import com.cagritasoz.model.ComparisonResult;
import com.cagritasoz.service.OutputComparator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OutputComparatorTest {

    OutputComparator outputComparator = new OutputComparator();

    @TempDir // Create an actual existing directory in the filesystem. It is created and deleted after each test.
    private Path tempDir;

    private Path writeStdout(String content) throws IOException {
        Path stdout = tempDir.resolve("stdout.txt");
        Files.writeString(stdout, content);
        return stdout;
    }

    // EXACT Mode tests.

    @Test
    void exact_identicalStrings_passes() throws IOException {
        Path stdout = writeStdout("Hello");
        ComparisonResult comparisonResult = outputComparator.compareOutput(
                ComparisonMode.EXACT,
                stdout,
                "Hello");
        assertTrue(comparisonResult.isPassed());
    }

    @Test
    void exact_differentStrings_fails() throws IOException {
        Path stdout = writeStdout("Hello");
        ComparisonResult comparisonResult = outputComparator.compareOutput(
                ComparisonMode.EXACT,
                stdout,
                "hello");
        assertFalse(comparisonResult.isPassed());
    }

    @Test
    void exact_crlfVsLf_fails() throws IOException {
        Path stdout = writeStdout("Hello World\r\n");
        ComparisonResult comparisonResult = outputComparator.compareOutput(
                ComparisonMode.EXACT,
                stdout,
                "Hello World\n"
        );
        assertFalse(comparisonResult.isPassed());
    }


}
