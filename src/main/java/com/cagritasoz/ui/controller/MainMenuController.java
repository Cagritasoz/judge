package com.cagritasoz.ui.controller;

import com.cagritasoz.ui.SceneAware;
import com.cagritasoz.ui.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class MainMenuController implements SceneAware {

    private SceneManager sceneManager;

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

    @Override
    public void setSceneManager(SceneManager sceneManager) {
        System.out.println("Setting scene manager for " + this.getClass());
        this.sceneManager = sceneManager;
    }
}
