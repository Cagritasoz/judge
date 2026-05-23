package com.cagritasoz.ui;

import com.cagritasoz.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;


public class JudgeUIApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = Objects.requireNonNull(getClass().getResource(SceneManager.MAIN_MENU));
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/com/cagritasoz/ui/styles/dark-theme.css"))
                        .toExternalForm()
        );

        SceneManager sceneManager = new SceneManager(scene);
        Object controller = loader.getController();
        if(controller instanceof SceneAware aware) {
            aware.setSceneManager(sceneManager);
        }
        primaryStage.setTitle("Judge");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);




    }

    public static SubmissionPipeline setUp() {
        WorkspaceManager workspaceManager = new WorkspaceManager();
        ZipExtractor zipExtractor = new ZipExtractor();
        SourceFileFinder sourceFileFinder = new SourceFileFinder();
        CommandBuilder commandBuilder = new CommandBuilder();
        ProcessRunner processRunner = new ProcessRunner();
        OutputComparator outputComparator = new OutputComparator();

        return new SubmissionPipeline(workspaceManager,
                zipExtractor,
                sourceFileFinder,
                commandBuilder,
                processRunner,
                outputComparator);

    }
}
