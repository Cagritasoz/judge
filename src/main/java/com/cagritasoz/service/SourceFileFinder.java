package com.cagritasoz.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class SourceFileFinder {

    private static final int MAX_DEPTH = 5; // For Files.walk()

    public List<Path> findSourceFiles(Path extractionDir, String sourceFilePattern) throws IOException {

        log.info("Finding source files in directory: {}", extractionDir);

        if (!Files.isDirectory(extractionDir)) { // In theory this should never execute, but just in case.
            throw new IOException("Search directory does not exist: " + extractionDir);
        }

        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + sourceFilePattern);

        try (Stream<Path> stream = Files.walk(extractionDir, MAX_DEPTH)) {
            return stream
                    .filter(Files::isRegularFile) // We only care about files.
                    .filter(path -> pathMatcher.matches(path.getFileName())) // Apply the glob to the filename only.
                    .toList(); // Return absolute paths of source files found, relativization will happen later.
        }
    }
}
