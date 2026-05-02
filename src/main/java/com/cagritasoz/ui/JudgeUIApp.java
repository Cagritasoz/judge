package com.cagritasoz.ui;

import com.cagritasoz.model.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JudgeUIApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Judge");
        primaryStage.show();
    }

    public static void main(String[] args) {
        // launch(args);
        File file = new File("D:\\Projects\\Judge\\sample-configs\\c.json");

        UUID id = UUID.randomUUID();

        Configuration configuration = new Configuration(
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

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, configuration);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
