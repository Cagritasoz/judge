package com.cagritasoz.ui;

import com.cagritasoz.service.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;




public class JudgeUIApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Judge");
        primaryStage.show();
    }

    public static void main(String[] args) {
        // launch(args);


        ProjectRunner pr = new ProjectRunner(setUp());

        pr.run();
        Platform.exit();


    }

    public static SubmissionPipeline setUp() {
        WorkspaceManager workspaceManager = new WorkspaceManager();
        ZipExtractor zipExtractor = new ZipExtractor();
        SourceFileFinder sourceFileFinder = new SourceFileFinder();
        CommandBuilder commandBuilder = new CommandBuilder();
        ProcessRunner processRunner = new ProcessRunner();

        return new SubmissionPipeline(workspaceManager,
                zipExtractor,
                sourceFileFinder,
                commandBuilder,
                processRunner);

    }
}
