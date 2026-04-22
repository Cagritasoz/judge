package com.cagritasoz.validation;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ProjectValidator {

    private final ConfigValidator configValidator;

    public ValidationResult validateName(String projectName) {
        if(projectName == null || projectName.isBlank()) {
            return ValidationResult.error("Project name must exist!");
        }
        else if(projectName.length() > 50) {
            return ValidationResult.error("Project name too long!");
        }
        return ValidationResult.ok();
    }

    public ValidationResult validateDescription(String projectDescription) {
        if(projectDescription.length() > 200) {
            return ValidationResult.error("Project description too long!");
        }
        return ValidationResult.ok();
    }

    public ValidationResult validateProjectDir(String projectDir) {
        if (projectDir == null || projectDir.isBlank()) {
            return ValidationResult.error("Project directory is required!");
        }

        Path projectDirPath;
        try {
            projectDirPath = Paths.get(projectDir);
        } catch (InvalidPathException e) {
            return ValidationResult.error("Invalid path format!");
        }

        if(!Files.exists(projectDirPath)) {
            return ValidationResult.error("Directory does not exist!");
        }

        if(!Files.isDirectory(projectDirPath)) {
            return ValidationResult.error("Selected path is not a directory!");
        }

        if(!Files.isWritable(projectDirPath)) {
            return ValidationResult.error("Directory is not writable!");
        }
        return ValidationResult.ok();
    }

    //TODO: Validate the loaded config with ConfigValidator!

    public ValidationResult validateSubmissionsDir(String submissionsDir) {
        if(submissionsDir == null || submissionsDir.isBlank()) {
            return ValidationResult.error("Submission directory is required!");
        }

        Path submissionsDirPath;
        try {
            submissionsDirPath = Paths.get(submissionsDir);
        } catch (InvalidPathException e) {
            return ValidationResult.error("Invalid path format!");
        }

        if(!Files.exists(submissionsDirPath)) {
            return ValidationResult.error("Directory does not exist!");
        }

        if(!Files.isDirectory(submissionsDirPath)) {
            return ValidationResult.error("Selected path is not a directory!");
        }

        if(!Files.isReadable(submissionsDirPath)) {
            return ValidationResult.error("Directory is not readable!");
        }

        if(!)
    }

    private boolean hasAnyZipFile(Path submissionsDirPath) {
        try(Stream<Path> stream = Files.walk(submissionsDirPath)) {
            stream.a


        } catch (IOException e) {
            return false;
        }
    }





}
