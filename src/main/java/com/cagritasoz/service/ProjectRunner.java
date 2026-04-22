package com.cagritasoz.service;

import com.cagritasoz.model.Project;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectRunner { // Orchestrates the whole system.

    private final ToolDetector toolDetector;

    private final ZipExtractor zipExtractor;

    private final SourceFileFinder sourceFileFinder;

    private final CommandBuilder commandBuilder;

    private final ProcessRunner processRunner;

    private final OutputComparator outputComparator;

    public void run(Project project) {

        //TODO: Pre run validation in a try catch block. If an exception is caught, exit immediately!

    }

    private void extract(Project project) { //Setup submission List and extract zips.



    }
}
