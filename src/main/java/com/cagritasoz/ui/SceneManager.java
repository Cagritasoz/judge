package com.cagritasoz.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class SceneManager { //

    public static final String MAIN_MENU = "/com/cagritasoz/ui/views/main-menu.fxml";

    // Caching already loaded roots is a good idea.

    private final Scene scene;

    public void switchTo(String fxmlPath) {
        try {
            URL fxmlUrl = Objects.requireNonNull(getClass().getResource(fxmlPath),
                    "Fxml not found on classpath"); // Fail loudly
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            Object controller = loader.getController();
            if(controller instanceof SceneAware aware) {
                aware.setSceneManager(this);
            }
            scene.setRoot(root);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load fxml " + fxmlPath, e);
        }
    }


}
