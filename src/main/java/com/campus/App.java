package com.campus;

import com.campus.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX de l'application Smart Campus.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.init(primaryStage);
        SceneManager.showDashboard();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

