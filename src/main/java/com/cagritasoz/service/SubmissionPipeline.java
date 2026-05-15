package com.cagritasoz.service;

import com.cagritasoz.exception.InternalProcessingException;
import com.cagritasoz.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
public class SubmissionPipeline {

    private final WorkspaceManager workspaceManager;

    private final ZipExtractor zipExtractor;

    private final SourceFileFinder sourceFileFinder;

    private final CommandBuilder commandBuilder;

    private final ProcessRunner processRunner;

    //private final OutputComparator outputComparator;

    public void processSubmissions(Project project, Configuration config) throws IOException {

        Path submissionsDir = Path.of(project.getSubmissionsDir());
        Path workDir = workspaceManager.setupCleanWorkspace(Path.of(project.getProjectDir()));

        try(Stream<Path> stream = Files.list(submissionsDir)) {
            List<Path> files = stream
                    .filter(file -> {
                        if(!Files.isRegularFile(file)) { // Skip directories
                            log.info("Skipping non regular file: {}", file);
                            return false;
                        }
                        return true;
                    })
                    .toList();

            for(Path file : files) { // For every regular file in the submission directory.
                Submission submission;
                try {
                    submission = processSubmissionFile(file, workDir, project, config);
                }
                catch(InternalProcessingException e) {
                    submission = e.getPartial(); // Keep stage information.
                    submission.setSubmissionStatus(SubmissionStatus.INTERNAL_ERROR);
                    log.error("Internal error while processing submission file: {}, at stage: {}",
                            file, submission.getStage(), e);
                }
                catch(Exception e) { // Just in case
                    log.error(" A very unexpected error while processing submission file: {}",
                            file, e);
                    submission = buildInternalFailure(file);
                }
                project.addSubmission(submission);
            }
            for(Submission submission : project.getSubmissions()) {
                System.out.println("===============================");
                System.out.println(submission);
                System.out.println("===============================");
            }
        }
    }

    private Submission processSubmissionFile(Path file, Path workDirPath,
                                             Project project, Configuration config) throws InternalProcessingException {

        log.info("Processing submission file: {}", file);

        String fileName = file.getFileName().toString();
        String submissionId = getFileNameWithoutExtension(fileName);

        Submission submission = Submission.builder()
                .submissionId(submissionId)
                .zipFilePath(file.toString()) // Naming could be better here.
                .zipFileName(fileName)
                .stage(Stage.DISCOVERY)
                .processedAt(LocalDateTime.now().toString())
                .build();

        try { //Pipeline starts, can be thinned out a bit.

            // Start Stage = DISCOVERY
            log.info("Submission file: {}, at stage: {}", file, submission.getStage());

            if(!isZipFile(fileName)) { // Skip non zip files
                submission.setSubmissionStatus(SubmissionStatus.SKIPPED);

                log.warn("Skipping non zip file {} at stage {}", file, submission.getStage());

                return submission;
            }

            log.info("Submission file: {} passes stage: {}", file, submission.getStage());

            // End Stage = DISCOVERY

            // Start Stage = EXTRACTION
            submission.setStage(Stage.EXTRACTION);

            Path safeExtractionDir;

            log.info("Submission file: {} at stage: {}", file, submission.getStage());

            try {

                safeExtractionDir = workDirPath.resolve(submissionId).resolve("extracted").toAbsolutePath().normalize();
                submission.setExtractionDir(safeExtractionDir.toString());
                Files.createDirectories(safeExtractionDir);

                log.info("Setup extraction directory: {} for submission file: {} at stage: {}",
                        safeExtractionDir, file, submission.getStage());

                zipExtractor.extractSafely(file, safeExtractionDir);

                submission.setSubmissionStatus(SubmissionStatus.IN_PROGRESS);

            }
            catch(IOException e) {
                submission.setSubmissionStatus(SubmissionStatus.EXTRACTION_FAILED);

                log.error("Extraction failed for submission file: {} ", file, e);

                return submission;
            }

            log.info("Submission file: {} passes stage: {}", file, submission.getStage());

            // End Stage = EXTRACTION

            // Start Stage = SOURCE_SEARCH
            submission.setStage(Stage.SOURCE_SEARCH);

            List<Path> sourceFilesFound;

            log.info("Submission file: {}, at stage: {}", file, submission.getStage());

            try {

                sourceFilesFound = sourceFileFinder
                        .findSourceFiles(safeExtractionDir, config.getSourceFilePattern());

                if(sourceFilesFound.isEmpty()) {
                    log.warn("No source files are found for submission file: {}", file);
                    submission.setSubmissionStatus(SubmissionStatus.NO_SOURCE_FILE);
                    return submission;
                }

                log.info("Found source files: {} ", sourceFilesFound);

                submission.setSourceFilesFound(sourceFilesFound.stream()
                        .map(Path::toString)
                        .toList());
            }
            catch(IOException e) {
                submission.setSubmissionStatus(SubmissionStatus.SOURCE_SEARCH_FAILED);

                log.error("Source file search failed for submission file: {}", file, e);

                return submission;
            }

            log.info("Submission file: {} passes stage: {}", file, submission.getStage());

            // End Stage = SOURCE_SEARCH

            int timeoutSeconds = project.getTimeoutSeconds();

            // Start Stage = COMPILE (If required)
            if(config.getCompilerPath() != null) { // If Compilation is required (For example compilerPath will be null for python)
                submission.setStage(Stage.COMPILE);

                log.info("Submission file: {}, at stage: {}", file, submission.getStage());

                List<String> compileCommand = commandBuilder.buildCompileCommand(config, sourceFilesFound, safeExtractionDir);

                Path parent = safeExtractionDir.getParent();
                Path stdoutFilePath = parent.resolve("compile-stdout.txt");
                Path stderrFilePath = parent.resolve("compile-stderr.txt");

                StageResult compileResult = processRunner.run(
                        compileCommand,
                        safeExtractionDir,
                        timeoutSeconds, // I might make it so that project has compile/run timeout seconds.
                        stdoutFilePath,
                        stderrFilePath
                );

                submission.setCompileResult(compileResult);

                if(compileResult.isExecuted()) {
                    String stdout = readBounded(Path.of(compileResult.getStdoutFilePath()));
                    String stderr = readBounded(Path.of(compileResult.getStderrFilePath()));
                    if(!stdout.isBlank()) log.info("Compile stdout for [{}]:\n{}", submissionId, stdout.stripTrailing());
                    if(!stderr.isBlank()) log.info("Compile stderr for [{}]:\n{}", submissionId, stderr.stripTrailing());
                }

                if(compileResult.isTimedOut()) {
                    log.warn("Submission [{}] timed out at stage: {}", submissionId, submission.getStage());
                    submission.setSubmissionStatus(SubmissionStatus.TIMED_OUT);
                    return submission;
                }

                if(!compileResult.isExecuted() || compileResult.getExitCode() == null || compileResult.getExitCode() != 0) {
                    log.warn("Submission [{}] failed at stage: {} with exit code: {}", submissionId, submission.getStage(), compileResult.getExitCode());
                    submission.setSubmissionStatus(SubmissionStatus.COMPILE_ERROR);
                    return submission;
                }

                log.info("Submission file: {} passes stage: {}", file, submission.getStage());
            }

            // End Stage = COMPILE

            // Start Stage = RUN
            submission.setStage(Stage.RUN); // Every project will have to be run, no need for a check here.

            log.info("Submission file: {}, at stage: {}", file, submission.getStage());

            List<String> runCommand = commandBuilder.buildRunCommand(project, config, safeExtractionDir);

            Path parent = safeExtractionDir.getParent();
            Path stdoutFilePath = parent.resolve("run-stdout.txt");
            Path stderrFilePath = parent.resolve("run-stderr.txt");

            StageResult runResult = processRunner.run(
                    runCommand,
                    safeExtractionDir,
                    timeoutSeconds,
                    stdoutFilePath,
                    stderrFilePath
            );

            submission.setRunResult(runResult);

            if(runResult.isExecuted()) {
                String stdout = readBounded(Path.of(runResult.getStdoutFilePath()));
                String stderr = readBounded(Path.of(runResult.getStderrFilePath()));
                if(!stdout.isBlank()) log.info("Run stdout for [{}]:\n{}", submissionId, stdout.stripTrailing());
                if(!stderr.isBlank()) log.info("Run stderr for [{}]:\n{}", submissionId, stderr.stripTrailing());
            }

            if(runResult.isTimedOut()) {
                log.warn("Submission [{}] timed out at stage: {}", submissionId, submission.getStage());
                submission.setSubmissionStatus(SubmissionStatus.TIMED_OUT);
                return submission;
            }

            if(!runResult.isExecuted() || runResult.getExitCode() == null || runResult.getExitCode() != 0) {
                log.warn("Submission [{}] failed at stage: {} with exit code: {}", submissionId, submission.getStage(), runResult.getExitCode());
                submission.setSubmissionStatus(SubmissionStatus.RUNTIME_ERROR);
                return submission;
            }

            log.info("Submission file: {} passes stage: {}", file, submission.getStage());

            // End Stage = RUN

            // Start Stage = COMPARISON



        }
        catch(Exception e) { // Catch any unexpected exception
            throw new InternalProcessingException(submission, e);
        }

        return submission;

    }

    private Submission buildInternalFailure(Path file) {
        String fileName = file.getFileName().toString();

        return Submission.builder()
                .submissionId(getFileNameWithoutExtension(fileName))
                .zipFilePath(file.toString())
                .zipFileName(fileName)
                .submissionStatus(SubmissionStatus.INTERNAL_ERROR)
                .processedAt(LocalDateTime.now().toString())
                .build();
    }

    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? fileName : fileName.substring(0, lastDotIndex); // lastDotIndex == -1 means '.' is not found
    }

    private boolean isZipFile(String fileName) {
        return fileName.toLowerCase().endsWith(".zip");
    }

    /*
    - For avoiding an OutOfMemory error when trying to read with Files.readString()
    - If file size is too big this causes an OutOfMemory exception to be thrown.
    - Just read some of the file not all of it!
     */

    private static final int MAX_OUTPUT_BYTES = 8 * 1024; // 8 KB

    private String readBounded(Path filePath) throws IOException {
        long fileSize = Files.size(filePath);
        if (fileSize == 0) return "";
        if (fileSize <= MAX_OUTPUT_BYTES) {
            return Files.readString(filePath);
        }
        byte[] buf = new byte[MAX_OUTPUT_BYTES];
        int read;
        try (InputStream is = Files.newInputStream(filePath)) {
            read = is.read(buf);
        }
        return new String(buf, 0, read) + "\n... [output truncated — " + fileSize + " bytes total]";
    }

}
