package com.cagritasoz.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Project {
    private String projectName; // Required
    private String projectDescription; // Optional

    private UUID configId; // Reference to the configuration.

    private String projectDir; // Where to save the project. UI ensures that it exists.
    private String submissionsDir; // Where the submissions(ZIP Files) live. UI ensures that it exists.

    private String entryPoint; // Specify main class name, changes from project to project.
    private InputMode inputMode; // Required for each project.
    private String inputData; // Command Arguments, If Input Mode is ARGUMENTS should exist.
    private String expectedOutput; // Required for each project!
    private ComparisonMode comparisonMode; // Required for each project!
    private List<Submission> submissions; // Submissions are created in the zip extraction stage.

    private String lastRunAt;
    private String createdAt;
    private int timeoutSeconds; // Specify how many seconds should be given to a submission to produce an output.

    public void addSubmission(Submission submission) {
        submissions.add(submission);
    }

}
