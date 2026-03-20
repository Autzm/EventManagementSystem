package com.eventapp;

import com.eventapp.model.User;
import com.eventapp.view.DashboardView;
import com.eventapp.view.LoginView;
import com.eventapp.view.RoleSelectionView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppRouter {

    private static Stage stage;
    public static User currentUser;

    public static void setStage(Stage s) {
        stage = s;
    }

    public static void showRoleSelection() {
        stage.setScene(createScene(new RoleSelectionView(), 980, 700));
        stage.setTitle("Event Management");
        stage.show();
    }

    public static void showLogin(String role) {
        stage.setScene(createScene(new LoginView(role), 780, 620));
        stage.setTitle("Login");
    }

    public static void showDashboard() {
        stage.setScene(createScene(new DashboardView(currentUser), 1200, 760));
        stage.setTitle("Dashboard");
    }

    public static void logout() {
        currentUser = null;
        showRoleSelection();
    }

    private static Scene createScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        String css = AppRouter.class.getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        return scene;
    }
}