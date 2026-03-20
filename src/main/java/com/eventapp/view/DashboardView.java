package com.eventapp.view;

import com.eventapp.AppRouter;
import com.eventapp.model.Event;
import com.eventapp.model.Task;
import com.eventapp.model.User;
import com.eventapp.service.AuthService;
import com.eventapp.service.EventService;
import com.eventapp.service.TaskService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class DashboardView extends BorderPane {

    private final User currentUser;

    public DashboardView(User user) {
        this.currentUser = user;
        setPadding(new Insets(18));
        getStyleClass().add("dashboard-root");

        setLeft(buildSidebar());

        if ("admin".equalsIgnoreCase(user.role)) {
            setCenter(buildAdminDashboard());
        } else if ("volunteer".equalsIgnoreCase(user.role)) {
            setCenter(buildVolunteerDashboard());
        } else {
            setCenter(buildAudienceDashboard());
        }
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(18);
        sidebar.setPadding(new Insets(22));
        sidebar.setPrefWidth(250);
        sidebar.getStyleClass().add("dashboard-sidebar");

        Label appTitle = new Label("Event System");
        appTitle.getStyleClass().add("sidebar-title");

        Label roleLabel = new Label("Role: " + currentUser.role.toUpperCase());
        roleLabel.getStyleClass().add("sidebar-text");

        Label emailLabel = new Label("Email: " + currentUser.email);
        emailLabel.getStyleClass().add("sidebar-text");
        emailLabel.setWrapText(true);

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setOnAction(e -> AppRouter.logout());

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        sidebar.getChildren().addAll(appTitle, roleLabel, emailLabel, spacer, logoutButton);
        return sidebar;
    }

    private VBox buildAudienceDashboard() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(0, 0, 0, 18));

        Label title = new Label("Audience Dashboard");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Browse all available events.");
        subtitle.getStyleClass().add("dashboard-subtitle");

        VBox eventCard = new VBox(14);
        eventCard.getStyleClass().add("dashboard-card");

        Label sectionTitle = new Label("Event List");
        sectionTitle.getStyleClass().add("section-title");

        ListView<String> eventList = new ListView<>();
        eventList.setPrefHeight(500);

        List<Event> events = EventService.getAllEvents();
        if (events.isEmpty()) {
            eventList.getItems().add("No events available yet.");
        } else {
            for (Event event : events) {
                eventList.getItems().add("ID: " + event.id + " | " + event.name + " | Date: " + event.date);
            }
        }

        eventCard.getChildren().addAll(sectionTitle, eventList);
        root.getChildren().addAll(title, subtitle, eventCard);
        return root;
    }

    private VBox buildVolunteerDashboard() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(0, 0, 0, 18));

        Label title = new Label("Volunteer Dashboard");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("View your tasks and update their status.");
        subtitle.getStyleClass().add("dashboard-subtitle");

        VBox taskCard = new VBox(12);
        taskCard.getStyleClass().add("dashboard-card");

        Label sectionTitle = new Label("My Tasks");
        sectionTitle.getStyleClass().add("section-title");

        ListView<String> taskList = new ListView<>();
        taskList.setPrefHeight(420);

        TextField selectedTaskIdField = new TextField();
        selectedTaskIdField.setPromptText("Selected task ID");
        selectedTaskIdField.setEditable(false);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Button completeButton = new Button("Mark Completed");
        completeButton.getStyleClass().add("green-button");
        completeButton.setMaxWidth(Double.MAX_VALUE);

        Button skipButton = new Button("Skip Task");
        skipButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 12;");
        skipButton.setMaxWidth(Double.MAX_VALUE);

        refreshVolunteerTaskList(taskList);

        taskList.setOnMouseClicked(e -> {
            String selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null && selected.startsWith("ID:")) {
                int id = Integer.parseInt(selected.split("\\|")[0].replace("ID:", "").trim());
                selectedTaskIdField.setText(String.valueOf(id));
                messageLabel.setText("");
            }
        });

        completeButton.setOnAction(e -> {
            String idText = selectedTaskIdField.getText().trim();
            if (idText.isEmpty()) {
                messageLabel.setText("Select a task first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            int taskId = Integer.parseInt(idText);
            TaskService.updateTaskStatus(taskId, "Completed");
            AppRouter.showDashboard();
        });

        skipButton.setOnAction(e -> {
            String idText = selectedTaskIdField.getText().trim();
            if (idText.isEmpty()) {
                messageLabel.setText("Select a task first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            int taskId = Integer.parseInt(idText);
            TaskService.updateTaskStatus(taskId, "Skipped");
            AppRouter.showDashboard();
        });

        taskCard.getChildren().addAll(
                sectionTitle,
                taskList,
                selectedTaskIdField,
                completeButton,
                skipButton,
                messageLabel
        );

        root.getChildren().addAll(title, subtitle, taskCard);
        return root;
    }

    private void refreshVolunteerTaskList(ListView<String> taskList) {
        taskList.getItems().clear();

        List<Task> tasks = TaskService.getTasksForVolunteer(currentUser.email);

        if (tasks.isEmpty()) {
            taskList.getItems().add("No tasks assigned yet.");
        } else {
            for (Task task : tasks) {
                taskList.getItems().add(
                        "ID: " + task.id + " | " + task.title + " | Status: " + task.status
                );
            }
        }
    }

    private VBox buildAdminDashboard() {
        VBox root = new VBox(18);
        root.setPadding(new Insets(0, 0, 0, 18));

        Label title = new Label("Admin Dashboard");
        title.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("Manage events, assign tasks, remove tasks, and reassign skipped work.");
        subtitle.getStyleClass().add("dashboard-subtitle");

        HBox contentRow = new HBox(16);

        VBox eventCard = buildAdminEventCard();
        VBox taskCard = buildAdminTaskCard();

        HBox.setHgrow(eventCard, Priority.ALWAYS);
        HBox.setHgrow(taskCard, Priority.ALWAYS);

        contentRow.getChildren().addAll(eventCard, taskCard);
        root.getChildren().addAll(title, subtitle, contentRow);
        return root;
    }

    private VBox buildAdminEventCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(450);

        Label cardTitle = new Label("Event CRUD");
        cardTitle.getStyleClass().add("section-title");

        TextField nameField = new TextField();
        nameField.setPromptText("Event name");

        TextField dateField = new TextField();
        dateField.setPromptText("Event date");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        ListView<String> eventList = new ListView<>();
        eventList.setPrefHeight(340);
        refreshAdminEventList(eventList);

        Button addButton = new Button("Add Event");
        Button updateButton = new Button("Update Event");
        Button deleteButton = new Button("Delete Event");

        addButton.getStyleClass().add("blue-button");
        updateButton.getStyleClass().add("green-button");
        deleteButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 12;");

        addButton.setMaxWidth(Double.MAX_VALUE);
        updateButton.setMaxWidth(Double.MAX_VALUE);
        deleteButton.setMaxWidth(Double.MAX_VALUE);

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();

            if (name.isEmpty() || date.isEmpty()) {
                messageLabel.setText("Enter event name and date.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            EventService.addEvent(name, date);
            AppRouter.showDashboard();
        });

        updateButton.setOnAction(e -> {
            String selected = eventList.getSelectionModel().getSelectedItem();

            if (selected == null || !selected.startsWith("ID:")) {
                messageLabel.setText("Select an event first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            String name = nameField.getText().trim();
            String date = dateField.getText().trim();

            if (name.isEmpty() || date.isEmpty()) {
                messageLabel.setText("Enter event name and date.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            int id = Integer.parseInt(selected.split("\\|")[0].replace("ID:", "").trim());
            EventService.updateEvent(id, name, date);
            AppRouter.showDashboard();
        });

        deleteButton.setOnAction(e -> {
            String selected = eventList.getSelectionModel().getSelectedItem();

            if (selected == null || !selected.startsWith("ID:")) {
                messageLabel.setText("Select an event first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            int id = Integer.parseInt(selected.split("\\|")[0].replace("ID:", "").trim());
            EventService.deleteEvent(id);
            AppRouter.showDashboard();
        });

        eventList.setOnMouseClicked(e -> {
            String selected = eventList.getSelectionModel().getSelectedItem();
            if (selected != null && selected.startsWith("ID:")) {
                int id = Integer.parseInt(selected.split("\\|")[0].replace("ID:", "").trim());
                List<Event> events = EventService.getAllEvents();
                for (Event event : events) {
                    if (event.id == id) {
                        nameField.setText(event.name);
                        dateField.setText(event.date);
                        break;
                    }
                }
                messageLabel.setText("");
            }
        });

        card.getChildren().addAll(
                cardTitle,
                nameField,
                dateField,
                addButton,
                updateButton,
                deleteButton,
                messageLabel,
                eventList
        );

        return card;
    }

    private void refreshAdminEventList(ListView<String> eventList) {
        eventList.getItems().clear();

        List<Event> allEvents = EventService.getAllEvents();
        if (allEvents.isEmpty()) {
            eventList.getItems().add("No events created yet.");
        } else {
            for (Event event : allEvents) {
                eventList.getItems().add("ID: " + event.id + " | " + event.name + " | " + event.date);
            }
        }
    }

    private VBox buildAdminTaskCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("dashboard-card");
        card.setPrefWidth(500);

        Label cardTitle = new Label("Task Management");
        cardTitle.getStyleClass().add("section-title");

        TextField taskField = new TextField();
        taskField.setPromptText("Task title");

        ComboBox<String> volunteerBox = new ComboBox<>();
        volunteerBox.setPromptText("Select volunteer");

        List<User> volunteers = AuthService.getVolunteers();
        for (User volunteer : volunteers) {
            volunteerBox.getItems().add(volunteer.email);
        }

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        ListView<String> taskList = new ListView<>();
        taskList.setPrefHeight(260);
        refreshAdminTaskList(taskList);

        TextField selectedTaskIdField = new TextField();
        selectedTaskIdField.setPromptText("Selected task ID");
        selectedTaskIdField.setEditable(false);

        ComboBox<String> reassignVolunteerBox = new ComboBox<>();
        reassignVolunteerBox.setPromptText("Reassign skipped task to volunteer");

        for (User volunteer : volunteers) {
            reassignVolunteerBox.getItems().add(volunteer.email);
        }

        Button assignButton = new Button("Assign Task");
        assignButton.getStyleClass().add("green-button");
        assignButton.setMaxWidth(Double.MAX_VALUE);

        Button deleteTaskButton = new Button("Delete Task");
        deleteTaskButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-background-radius: 12;");
        deleteTaskButton.setMaxWidth(Double.MAX_VALUE);

        Button reassignButton = new Button("Reassign Skipped Task");
        reassignButton.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-background-radius: 12;");
        reassignButton.setMaxWidth(Double.MAX_VALUE);

        taskList.setOnMouseClicked(e -> {
            String selected = taskList.getSelectionModel().getSelectedItem();
            if (selected != null && selected.startsWith("ID:")) {
                int taskId = Integer.parseInt(selected.split("\\|")[0].replace("ID:", "").trim());
                selectedTaskIdField.setText(String.valueOf(taskId));
                messageLabel.setText("");
            }
        });

        assignButton.setOnAction(e -> {
            String taskTitle = taskField.getText().trim();
            String volunteerEmail = volunteerBox.getValue();

            if (taskTitle.isEmpty()) {
                messageLabel.setText("Enter a task title.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            if (volunteerEmail == null || volunteerEmail.isEmpty()) {
                messageLabel.setText("Select a volunteer first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            boolean success = TaskService.assignTask(taskTitle, volunteerEmail);

            if (success) {
                AppRouter.showDashboard();
            } else {
                messageLabel.setText("Task assignment failed.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
            }
        });

        deleteTaskButton.setOnAction(e -> {
            String idText = selectedTaskIdField.getText().trim();

            if (idText.isEmpty()) {
                messageLabel.setText("Select a task first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            int taskId = Integer.parseInt(idText);
            TaskService.deleteTask(taskId);
            AppRouter.showDashboard();
        });

        reassignButton.setOnAction(e -> {
            String idText = selectedTaskIdField.getText().trim();
            String newVolunteer = reassignVolunteerBox.getValue();

            if (idText.isEmpty()) {
                messageLabel.setText("Select a task first.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            if (newVolunteer == null || newVolunteer.isEmpty()) {
                messageLabel.setText("Select a volunteer to reassign.");
                messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                return;
            }

            int taskId = Integer.parseInt(idText);
            List<Task> tasks = TaskService.getAllTasks();

            for (Task task : tasks) {
                if (task.id == taskId) {
                    if (!"Skipped".equalsIgnoreCase(task.status)) {
                        messageLabel.setText("Only skipped tasks can be reassigned.");
                        messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
                        return;
                    }

                    TaskService.reassignTask(taskId, newVolunteer);
                    AppRouter.showDashboard();
                    return;
                }
            }

            messageLabel.setText("Task not found.");
            messageLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-font-weight: bold;");
        });

        card.getChildren().addAll(
                cardTitle,
                taskField,
                volunteerBox,
                assignButton,
                taskList,
                selectedTaskIdField,
                reassignVolunteerBox,
                reassignButton,
                deleteTaskButton,
                messageLabel
        );

        return card;
    }

    private void refreshAdminTaskList(ListView<String> taskList) {
        taskList.getItems().clear();

        List<Task> allTasks = TaskService.getAllTasks();
        if (allTasks.isEmpty()) {
            taskList.getItems().add("No tasks assigned yet.");
        } else {
            for (Task task : allTasks) {
                taskList.getItems().add(
                        "ID: " + task.id + " | " + task.title + " | " + task.assignedTo + " | Status: " + task.status
                );
            }
        }
    }
}