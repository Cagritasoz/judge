package com.cagritasoz.model;

import lombok.*;

import java.nio.file.Path;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StageResult {
    private boolean executed;
    private Integer exitCode;
    private long durationMillis;
    private boolean timedOut;
    private String stdoutFilePath;
    private String stderrFilePath;
}
