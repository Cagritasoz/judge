package com.cagritasoz.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor //Needed for Jackson serialization!
public class Submission {
    private String submissionId;
    private String filePath;
    private String fileName;
    private String extractionPath;

    private SubmissionStatus submissionStatus;
    private Stage stage;

    private List<String> sourceFilesFound;

    private StageResult compileResult;
    private StageResult runResult;
    private ComparisonResult comparisonResult;

    private LocalDateTime processedAt;

}
