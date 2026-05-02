package com.cagritasoz.service;

import com.cagritasoz.exception.InternalProcessingException;
import com.cagritasoz.model.Configuration;
import com.cagritasoz.model.Project;
import com.cagritasoz.model.Submission;
import com.cagritasoz.model.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class SubmissionPipeline {

    private final ZipExtractor zipExtractor;

    private final SourceFileFinder sourceFileFinder;

    private final CommandBuilder commandBuilder;

    private final ProcessRunner processRunner;

    private final OutputComparator outputComparator;

    public void processSubmissions(Project project, Configuration config) throws IOException { //Setup submission List and extract zips.

        Path submissionsDirPath = Path.of(project.getSubmissionsDir());
        Path workDirPath = Path.of(project.getProjectDir()).resolve("work");
        Files.createDirectories(workDirPath); // May throw IOException, exit if so.

        try(Stream<Path> stream = Files.list(submissionsDirPath)) {
            List<Path> files = stream
                    .filter(file -> {
                        if(!Files.isRegularFile(file)) { // Skip directories
                            log.info("Skipping non regular file {}", file);
                            return false;
                        }
                        return true;
                    })
                    .toList();

            for(Path file : files) { // For every regular file in the submission directory.
                Submission submission;
                try {
                    submission = processSubmissionFile(file, workDirPath, project, config);
                }
                catch(InternalProcessingException e) {
                    submission = e.getPartial(); // Keep stage information.
                    submission.setSubmissionStatus(SubmissionStatus.INTERNAL_ERROR);
                    log.error("Internal error while processing submission {} at stage {}",
                            file.getFileName(), submission.getStage(), e);
                }
                catch(Exception e) {
                    log.error(" A very unexpected error while processing submission {}",
                            file.getFileName(), e);
                    submission = buildInternalFailure(file);
                }
                project.addSubmission(submission);
            }
        }


    }

    private Submission processSubmissionFile(Path file, Path workDirPath,
                                             Project project, Configuration config) throws InternalProcessingException {
        return null;
    }

    private Submission buildInternalFailure(Path file) {
        String fileName = file.getFileName().toString();

        return Submission.builder()
                .submissionId(getFileNameWithoutExtension(fileName))
                .submissionStatus(SubmissionStatus.INTERNAL_ERROR)
                .processedAt(LocalDateTime.now().toString())
                .build();
    }

    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex); // lastDotIndex == -1 means '.' is not found
    }

}
