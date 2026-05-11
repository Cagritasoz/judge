package com.cagritasoz.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // Needed for Jackson serialization!
public class Submission {
    private String submissionId; // What is written before ".zip"
    private String zipFilePath; // Store the absolute path to the zip file.
    private String zipFileName;
    private String extractionDir; // Will be project\work\submissionId\extracted

    private SubmissionStatus submissionStatus;
    private Stage stage;

    private List<String> sourceFilesFound;

    private StageResult compileResult;
    private StageResult runResult;
    private ComparisonResult comparisonResult;

    private String processedAt;

}
