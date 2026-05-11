package com.cagritasoz.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

/*
-Specific for a programming language.
*/

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    private UUID configId;
    private String configName;
    private String configDescription;

    private boolean requiresEntryPoint;

    private String compilerPath; // If null only interpreted
    private String interpreterPath; // If null only compiled, if both exists config is for Java.
    private List<String> compileCommandTemplate; // List type because of how a ProcessBuilder works!
    private List<String> runCommandTemplate;
    private List<String> checkCommandTemplate;

    private String sourceFilePattern;
    private String compiledOutputName;

}

