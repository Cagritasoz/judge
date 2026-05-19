package com.cagritasoz.service;

import com.cagritasoz.model.StageResult;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProcessRunner {

    public StageResult run(
            List<String> command,
            Path workingDir,
            int timeoutSeconds,
            Path stdoutFilePath,
            Path stderrFilePath) {

        long startMillis = System.currentTimeMillis(); // To calculate durationMillis.

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        processBuilder.directory(workingDir.toFile()); // Set working directory of the process.

        processBuilder.redirectOutput(stdoutFilePath.toFile()); // Switch with threads.

        processBuilder.redirectError(stderrFilePath.toFile());

        Process process;

        try {

            log.info("Starting subprocess with command: {}", command);

            process = processBuilder.start();

        }

        catch (IOException e) {
            log.error("Error encountered while starting subprocess", e);
            return StageResult.builder()
                    .executed(false)
                    .exitCode(null)
                    .durationMillis(System.currentTimeMillis() - startMillis)
                    .timedOut(false)
                    .stdoutFilePath(stdoutFilePath.toString())
                    .stderrFilePath(stderrFilePath.toString())
                    .build();
        }

        boolean finished;

        try {
            // Wait for timeout seconds. If process is terminated within the specified time, return true, if not return false.
            log.info("Waiting for the process to finish for: {} seconds", timeoutSeconds);
            finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

        }

        catch (InterruptedException e) { // waitFor method is blocking, throws InterruptedException
            log.error("Thread has been interrupted, destroying the process.", e);

            /*
            - Calling thread.interrupt() on a thread primarily sets a boolean status flag inside the thread object.
            - A blocking method inside the thread reacts to this flag and will immediately throw an InterruptedException if interrupted.
            - The "interrupted status" flag is cleared (set back to false) when the exception is thrown.
            - To keep the fact that this thread was interrupted we can set the flag as true with "Thread.currentThread().interrupt();"
            - This line of code only sets the flag, to keep the information. It does not interrupt the thread meaning thread still executes.
             */
            Thread.currentThread().interrupt();
            process.destroyForcibly(); // Destroy the process forcibly.
            log.info("Process destroyed forcibly after thread has been interrupted");

            return StageResult.builder()
                    .executed(true)
                    .exitCode(null)
                    .durationMillis(System.currentTimeMillis() - startMillis)
                    .timedOut(false)
                    .stdoutFilePath(stdoutFilePath.toString())
                    .stderrFilePath(stderrFilePath.toString())
                    .build();
        }

        long duration = System.currentTimeMillis() - startMillis;

        if (!finished) { // If still not finished in timeout seconds.
            log.info("Process is still not finished within {} seconds, attempting to destroy process politely", timeoutSeconds);
            process.destroy(); // Polite destroy.
            try {
                if (!process.waitFor(1, TimeUnit.SECONDS)) { // Still not terminated after 1 second.
                    log.info("Process is still running after polite destroy, attempting to destroy process forcibly");
                    process.destroyForcibly(); // Force destroy
                    process.waitFor(); // Wait until it is terminated after destroying forcibly.
                    log.info("Process destroyed forcibly");
                }
                else {
                    log.info("Process destroyed politely");
                }
            } catch (InterruptedException e) {
                log.error("Thread has been interrupted trying to destroy process forcibly ", e);
                Thread.currentThread().interrupt();
                process.destroyForcibly(); // Would this lead to bugs?
                log.info("Process destroyed forcibly after thread has been interrupted");
            }
            return StageResult.builder()
                    .executed(true)
                    .exitCode(null)
                    .durationMillis(duration)
                    .timedOut(true) // Process timed out.
                    .stdoutFilePath(stdoutFilePath.toString())
                    .stderrFilePath(stderrFilePath.toString())
                    .build();
        }

        int exitCode = process.exitValue(); // We can assure that the process has been terminated at this point.

        log.info("Subprocess finished with exit code: {} for command: {}", exitCode, command);

        return StageResult.builder()
                .executed(true)
                .exitCode(exitCode) // exit code is set.
                .durationMillis(duration)
                .timedOut(false)
                .stdoutFilePath(stdoutFilePath.toString())
                .stderrFilePath(stderrFilePath.toString())
                .build();
    }

}
