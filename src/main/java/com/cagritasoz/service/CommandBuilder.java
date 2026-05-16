package com.cagritasoz.service;

import com.cagritasoz.model.Configuration;
import com.cagritasoz.model.InputMode;
import com.cagritasoz.model.Project;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandBuilder {

    public List<String> buildCompileCommand(
            Configuration config,
            List<Path> sourceFilesFound,
            Path workingDir // Working dir is the extracted dir of a submission.
    ) throws IllegalArgumentException{

        log.info("Building compile command...");

        Map<String, Object> subs = new HashMap<>();
        subs.put("compilerPath", config.getCompilerPath());
        subs.put("sourceFiles", relativizeAll(sourceFilesFound, workingDir));
        subs.put("compiledOutputPath", config.getCompiledOutputPath());

        List<String> compileCommand = substitute(config.getCompileCommandTemplate(), subs);

        log.info("Built compile command: {} ", compileCommand);

        return compileCommand;
    }

    public List<String> buildRunCommand(
            Project project,
            Configuration config,
            Path executableDir
    ) throws IllegalArgumentException {
        log.info("Building run command...");

        Map<String, Object> subs = new HashMap<>();

        subs.put("interpreterPath", config.getInterpreterPath()); // Null for C. Fine if null since C config run template
                                                                  // Does not have an {interpreterPath} placeholder.
        String compiledOutputPath = config.getCompiledOutputPath(); // Has to be an absolute path because it was leading to errors.
        subs.put("compiledOutputPath", compiledOutputPath != null
                ? executableDir.resolve(compiledOutputPath).toAbsolutePath().toString()
                : null);
        subs.put("entryPoint", project.getEntryPoint());
        subs.put("args", parseArgs(project));

        List<String> runCommand = substitute(config.getRunCommandTemplate(), subs);

        log.info("Built run command: {} ", runCommand);

        return runCommand;

    }

    private List<String> parseArgs(Project project) { // Parse arguments String inputData.
        if(project.getInputMode() == null || project.getInputMode() != InputMode.ARGUMENTS) { // Null for now.
            log.info("No arguments for project: {} ", project.getProjectName());
            return List.of(); // Return unmodifiable empty list.
        }
        List<String> arguments = List.of(project.getInputData().trim().split("\\s+"));

        log.info("Arguments parsed as {}", arguments); // I sense bugs will occur here.

        return arguments;

    }

    private List<String> relativizeAll(List<Path> sourceFilesFound, Path workingDir) {

        log.info("Relativizing...");

        List<String> relativizedList = sourceFilesFound.stream()
                .map(workingDir::relativize) // Parent is the working directory. Map the paths to relative paths.
                .map(Path::toString)
                .toList();
        log.info("Relativized paths list: {}", relativizedList);

        return relativizedList;

    }

    private List<String> substitute(List<String> template, Map<String, Object> subs) {
        if(template == null) { //Config will be validated in the UI. this should never execute, defensive check??
            log.warn("Template is null for substitution! How?");
            return List.of();
        }

        List<String> result = new ArrayList<>();

        log.info("Substituting values into the template...");

        for(String token : template) { // For every token in the config template (compile or run)
            if(isPlaceHolder(token)) { // Format of token is "{text}"
                String key = token.substring(1, token.length()-1); // Remove the "{}"
                Object value = subs.get(key);
                log.info("Processing token: {}, key: {}, value: {}", token, key, value);
                if(value == null) { // Values come from the config, values should never be in null in theory because of pre-validation.
                    throw new IllegalArgumentException("Template references {" + key + "} but no value provided for this language");
                } // This exception(if thrown) should kill the whole pipeline process if encountered. Keep this in mind.

                if(value instanceof List<?> list) { // Value is of type list. (like source files list)
                    list.forEach(item -> result.add(item.toString()));
                }
                else { // Value is not a list. Only possible one here is type String.
                    result.add(value.toString());
                }

            }
            else { // If not a placeholder, probably a flag like -o. Add it as is.
                result.add(token);
            }
        }

        return result;

    }

    private boolean isPlaceHolder(String token) {
        return token.startsWith("{") && token.endsWith("}") && token.length()>2;
    }

}
