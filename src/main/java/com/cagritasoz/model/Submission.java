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
    private String extractionDir; // Will be project\work\submissionId\extracted, this is the working directory!

    private SubmissionStatus submissionStatus;
    private Stage stage;

    private List<String> sourceFilesFound;

    private StageResult compileResult;
    private StageResult runResult;
    private ComparisonResult comparisonResult;

    private String processedAt;

    @Override
    public String toString() {
        return "Submission Id: " + submissionId + "\n"
                + "Zip File Path: " + zipFilePath + "\n"
                + "Zip File Name: " + zipFileName + "\n"
                + "Extraction Directory: " + extractionDir + "\n"
                + "Submission Status: " + submissionStatus + "\n"
                + "Stage: " + stage + "\n"
                + "Source Files Found: " + sourceFilesFound + "\n"
                + "Compile Result: " + compileResult + "\n"
                + "Run Result: " + runResult + "\n"
                + "Comparison Result: " + comparisonResult + "\n";
    }

}
