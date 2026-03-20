package com.eventapp;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        AppRouter.setStage(stage);
        AppRouter.showRoleSelection();
    }

    public static void main(String[] args) {
        launch();
    }
}