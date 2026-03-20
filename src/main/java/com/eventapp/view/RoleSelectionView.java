package com.eventapp.view;

import com.eventapp.AppRouter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class RoleSelectionView extends BorderPane {

    public RoleSelectionView() {
        setPadding(new Insets(40));
        getStyleClass().add("role-root");

        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label title = new Label("Event Management System");
        title.getStyleClass().add("main-title");

        Label subtitle = new Label("Select your role to continue");
        subtitle.getStyleClass().add("sub-title");

        headerBox.getChildren().addAll(title, subtitle);
        setTop(headerBox);
        BorderPane.setAlignment(headerBox, Pos.CENTER);

        HBox cardsBox = new HBox(30);
        cardsBox.setAlignment(Pos.CENTER);
        cardsBox.setPadding(new Insets(40, 0, 0, 0));

        VBox audienceCard = createRoleCard(
                "Audience",
                "Attendees and event participants",
                new String[]{
                        "View upcoming events",
                        "Manage your tickets",
                        "Check event schedules",
                        "Register for events"
                },
                "Continue as Audience",
                "blue-button",
                "feature-marker-blue",
                () -> AppRouter.showLogin("audience")
        );

        VBox volunteerCard = createRoleCard(
                "Volunteer",
                "Event helpers and support staff",
                new String[]{
                        "View assigned tasks",
                        "Update task status",
                        "Report issues",
                        "Track your contributions"
                },
                "Continue as Volunteer",
                "green-button",
                "feature-marker-green",
                () -> AppRouter.showLogin("volunteer")
        );

        cardsBox.getChildren().addAll(audienceCard, volunteerCard);
        setCenter(cardsBox);
    }

    private VBox createRoleCard(
            String titleText,
            String subtitleText,
            String[] features,
            String buttonText,
            String buttonStyleClass,
            String markerStyleClass,
            Runnable action
    ) {
        VBox card = new VBox(18);
        card.getStyleClass().add("role-card");
        card.setPrefWidth(360);
        card.setMinHeight(480);
        card.setPadding(new Insets(28));
        card.setAlignment(Pos.TOP_CENTER);

        StackPane iconCircle = new StackPane();
        iconCircle.getStyleClass().add("icon-circle");

        Label iconText = new Label(titleText.equals("Audience") ? "A" : "V");
        iconText.getStyleClass().add("icon-letter");
        iconCircle.getChildren().add(iconText);

        Label roleTitle = new Label(titleText);
        roleTitle.getStyleClass().add("card-title");

        Label roleSubtitle = new Label(subtitleText);
        roleSubtitle.getStyleClass().add("card-subtitle");
        roleSubtitle.setWrapText(true);

        VBox featureBox = new VBox(12);
        featureBox.setAlignment(Pos.TOP_LEFT);
        featureBox.setPadding(new Insets(10, 0, 10, 0));

        for (String feature : features) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Region marker = new Region();
            marker.getStyleClass().add(markerStyleClass);
            marker.setMinSize(8, 8);
            marker.setPrefSize(8, 8);
            marker.setMaxSize(8, 8);

            Label text = new Label(feature);
            text.getStyleClass().add("feature-text");

            row.getChildren().addAll(marker, text);
            featureBox.getChildren().add(row);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button button = new Button(buttonText);
        button.getStyleClass().addAll("role-action-button", buttonStyleClass);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e -> action.run());

        card.getChildren().addAll(iconCircle, roleTitle, roleSubtitle, featureBox, spacer, button);
        return card;
    }
}