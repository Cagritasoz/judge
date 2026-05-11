package com.cagritasoz.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
public class WorkspaceManager {

    public Path setupCleanWorkspace(Path projectDir) throws IOException {
        log.info("Setting up workspace...");
        Path workDir = projectDir.resolve("work");

        cleanIfExists(workDir);

        Files.createDirectories(workDir);

        return workDir;
    }

    public void cleanIfExists(Path workDir) throws IOException {

        if(!Files.exists(workDir)) {
            return; // Do nothing
        }

        log.info("Work directory detected. Deleting existing contents.");

        Files.walkFileTree(workDir, new SimpleFileVisitor<>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file); // On a regular file visit delete the file.
                log.info("Deleted work directory content file: {}", file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if(exc != null) {
                    throw exc; // Technically we handle this in the visitFileFailed method.
                }
                Files.delete(dir); // Delete the directory after its contents have been deleted.
                log.info("Deleted work directory content directory: {}", dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                log.error("Failed to access {} during cleanup", file, exc); // Overridden because useful log info.
                throw exc;  // re-throw to terminate the walk
            }
        });
    }
}
