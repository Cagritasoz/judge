package com.cagritasoz.model;

import lombok.*;

import java.nio.file.Path;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageResult {
    private boolean executed;
    private Integer exitCode; // Object because nullable.
    private long durationMillis;
    private boolean timedOut;
    private String stdoutFilePath;
    private String stderrFilePath;
}
