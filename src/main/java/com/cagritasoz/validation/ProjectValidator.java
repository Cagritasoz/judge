package com.cagritasoz.validation;

import com.cagritasoz.model.Configuration;
import com.cagritasoz.model.InputMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


public class ProjectValidator {

    public ValidationResult validateName(String projectName) {
        if(projectName == null || projectName.isBlank()) {
            return ValidationResult.error("Project name must exist!");
        }
        if(projectName.length() > 50) {
            return ValidationResult.error("Project name too long!");
        }
        return ValidationResult.ok();
    }

    public ValidationResult validateDescription(String projectDescription) {
        if(projectDescription == null || projectDescription.isBlank()) {
            return ValidationResult.ok();
        }
        if(projectDescription.length() > 200) {
            return ValidationResult.error("Project description too long!");
        }
        return ValidationResult.ok();
    }

    // Config will be validated separately

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

        if(!hasAnyZipFile(submissionsDirPath)) { // Maybe we can move this check to the preRunValidation method.
            return ValidationResult.error("Directory contains no ZIP files!");
        }
        return ValidationResult.ok();
    }

    public ValidationResult validateEntryPoint(String entryPoint, Configuration config) {
        if(!config.isRequiresEntryPoint()) { // No entry point is required.
            return ValidationResult.ok();
        }
        if(entryPoint == null || entryPoint.isBlank()) {
            return ValidationResult.error("Entry point is required!");
        }
        return ValidationResult.ok();

    }

    public ValidationResult validateInputMode(InputMode inputMode) {
        if(inputMode == null) {
            return ValidationResult.error("Input mode required!");
        }
        return ValidationResult.ok();
    }

    private boolean hasAnyZipFile(Path submissionsDirPath) {
        try(Stream<Path> stream = Files.list(submissionsDirPath)) {
            return stream.filter(Files::isRegularFile) // A zip file is a regular file.
                    .anyMatch(path -> path.getFileName().toString().toLowerCase().endsWith(".zip"));
        } catch (IOException e) {
            return false;
        }
    }





}
