package com.cagritasoz.service;

import com.cagritasoz.model.Configuration;
import com.cagritasoz.model.Project;
import com.cagritasoz.persistance.ConfigStore;
import com.cagritasoz.persistance.ProjectStore;
import com.cagritasoz.validation.ConfigValidator;
import com.cagritasoz.validation.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
public class ProjectRunner { // Orchestrates the whole system.

    //private final ProjectValidator projectValidator;

    //private final ToolDetector toolDetector;

    //private final ConfigStore configStore;

    private final SubmissionPipeline submissionPipeline;

    //private final ProjectStore projectStore;

    public void run() { //Run specific project with this config

        UUID id = UUID.randomUUID();

        Project testProject = Project.builder()
                .projectName("Test Project")
                .projectDescription("This is a test description")
                .configId(id)
                .projectDir("C:\\Users\\VICTUS\\Desktop\\Project")
                .submissionsDir("C:\\Users\\VICTUS\\Desktop\\Submissions")
                .build();


        Configuration testConfig = new Configuration( // C config for prototype.
                id,
                "C Programming",
                "GCC-based C compilation and execution",
                false,
                "gcc",
                null,
                List.of("{compilerPath}", "{sourceFiles}", "-o", "{compiledOutputName}"),
                List.of("{compiledOutputPath}", "{args}"),
                List.of("{compilerPath}", "--version"),
                "*.c",
                "main.exe"
        );

        //TODO: Pre run validation in a try catch block. If an exception is caught, exit immediately!
        //TODO: Get the config here through ConfigStore, pass it to processSubmissions method as a parameter

        log.info("Running project: {}", testProject.getProjectName());

        try {
            log.info("Starting submission pipeline for project: {}", testProject.getProjectName());
            submissionPipeline.processSubmissions(testProject, testConfig);
        } catch (IOException e) {
            log.error("Unexpected error while processing submissions for project: {}",
                    testProject.getProjectName(), e);
            return;
        }

    }
}
