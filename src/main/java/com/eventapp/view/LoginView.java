package com.eventapp.view;

import com.eventapp.AppRouter;
import com.eventapp.model.User;
import com.eventapp.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView extends BorderPane {

    public LoginView(String selectedRole) {
        setPadding(new Insets(28));
        getStyleClass().add("login-root");

        VBox wrapper = new VBox();
        wrapper.setAlignment(Pos.CENTER);

        VBox card = new VBox(18);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(700);
        card.setPadding(new Insets(30));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_RIGHT);

        Button backButton = new Button("Back");
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(e -> AppRouter.showRoleSelection());

        topRow.getChildren().add(backButton);

        StackPane iconCircle = new StackPane();
        iconCircle.getStyleClass().add(
                selectedRole.equals("volunteer") ? "login-icon-circle-green" : "login-icon-circle-blue"
        );
        iconCircle.setPrefSize(70, 70);

        Label iconText = new Label(selectedRole.equals("volunteer") ? "V" : "A");
        iconText.getStyleClass().add("login-icon-letter");
        iconCircle.getChildren().add(iconText);

        Label title = new Label(
                selectedRole.substring(0, 1).toUpperCase() + selectedRole.substring(1) + " Login"
        );
        title.getStyleClass().add("login-title");

        Label subtitle = new Label(
                selectedRole.equals("volunteer")
                        ? "Sign in to view tasks and report issues"
                        : "Sign in to view events and manage your tickets"
        );
        subtitle.getStyleClass().add("login-subtitle");

        Label emailLabel = new Label("Email Address");
        emailLabel.getStyleClass().add("field-label");

        TextField emailField = new TextField();
        emailField.getStyleClass().add("input-field");
        emailField.setPromptText(
                selectedRole.equals("volunteer") ? "volunteer@event.com" : "audience@event.com"
        );

        Label passwordLabel = new Label("Password");
        passwordLabel.getStyleClass().add("field-label");

        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("input-field");
        passwordField.setPromptText("Enter your password");

        HBox optionsRow = new HBox();
        optionsRow.setAlignment(Pos.CENTER_LEFT);
        optionsRow.setSpacing(10);

        CheckBox rememberMe = new CheckBox("Remember me");
        rememberMe.getStyleClass().add("remember-box");

        Region optionSpacer = new Region();
        HBox.setHgrow(optionSpacer, Priority.ALWAYS);

        Hyperlink forgotPassword = new Hyperlink("Forgot password?");
        forgotPassword.getStyleClass().add("forgot-link");

        optionsRow.getChildren().addAll(rememberMe, optionSpacer, forgotPassword);

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");

        Button loginButton = new Button("Sign In");
        loginButton.getStyleClass().addAll(
                "login-button",
                selectedRole.equals("volunteer") ? "green-button" : "blue-button"
        );
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Button registerButton = new Button(
                selectedRole.equals("volunteer") ? "Join as Volunteer" : "Register as Audience"
        );
        registerButton.getStyleClass().add("secondary-button");
        registerButton.setMaxWidth(Double.MAX_VALUE);

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter email and password.");
                messageLabel.getStyleClass().remove("success-message");
                if (!messageLabel.getStyleClass().contains("error-message")) {
                    messageLabel.getStyleClass().add("error-message");
                }
                return;
            }

            User user = AuthService.login(email, password, selectedRole);

            if (user != null) {
                AppRouter.currentUser = user;
                AppRouter.showDashboard();
            } else {
                messageLabel.setText("Invalid credentials for this login.");
                messageLabel.getStyleClass().remove("success-message");
                if (!messageLabel.getStyleClass().contains("error-message")) {
                    messageLabel.getStyleClass().add("error-message");
                }
            }
        });

        registerButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Enter email and password before registering.");
                messageLabel.getStyleClass().remove("success-message");
                if (!messageLabel.getStyleClass().contains("error-message")) {
                    messageLabel.getStyleClass().add("error-message");
                }
                return;
            }

            boolean success = AuthService.register(email, password, selectedRole);

            if (success) {
                messageLabel.setText("Registration successful. You can now sign in.");
                messageLabel.getStyleClass().remove("error-message");
                if (!messageLabel.getStyleClass().contains("success-message")) {
                    messageLabel.getStyleClass().add("success-message");
                }
            } else {
                messageLabel.setText("This email is already registered.");
                messageLabel.getStyleClass().remove("success-message");
                if (!messageLabel.getStyleClass().contains("error-message")) {
                    messageLabel.getStyleClass().add("error-message");
                }
            }
        });

        Label footerText = new Label(
                selectedRole.equals("volunteer")
                        ? "Demo volunteer: volunteer@event.com / 1234"
                        : "Demo audience: audience@event.com / 1234 | Admin: admin@event.com / admin123"
        );
        footerText.getStyleClass().add("footer-text");

        card.getChildren().addAll(
                topRow,
                iconCircle,
                title,
                subtitle,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                optionsRow,
                loginButton,
                registerButton,
                footerText,
                messageLabel
        );

        wrapper.getChildren().add(card);
        setCenter(wrapper);
    }
}