package com.cagritasoz.model;

import lombok.*;

import java.util.List;
import java.util.UUID;

/*
-Specific for each programming language.
-
*/

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Configuration {
    private UUID id;
    private String name;
    private String description;

    private String compilerPath;
    private String interpreterPath;
    private List<String> compileCommandTemplate;
    private List<String> runCommandTemplate;
    private List<String> checkCommandTemplate;

    private String sourceFilePattern;
    private String compiledOutputName;

}

