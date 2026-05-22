package com.cagritasoz.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class MainMenuController {

    @FXML
    private Button projectsButton;

    @FXML
    private Button configsButton;

    @FXML
    private Button settingsButton;

    @FXML
    private void initialize() {
        System.out.println("Main menu loaded!");

        
    }

    @FXML
    private void handleProjectsClick(ActionEvent event) {
        System.out.println("Projects clicked");
        // TODO: navigate to projects view
    }

    @FXML
    private void handleConfigsClick(ActionEvent event) {
        System.out.println("Configs clicked");
        // TODO: navigate to configs view
    }

    @FXML
    private void handleSettingsClick(ActionEvent event) {
        System.out.println("Settings clicked");
        // TODO: navigate to settings view
    }

}
