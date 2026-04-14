import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import todolist.exception.TaskException;
import todolist.fileio.FileHandler;
import todolist.manager.TaskManager;
import todolist.model.DeadlineTask;
import todolist.model.Priority;
import todolist.model.Status;
import todolist.model.Task;
import todolist.utils.TaskFormatter;
import javafx.scene.control.Label;

import java.time.LocalDateTime;

public class Main extends Application {

    private TaskManager manager;
    private ListView<Task> taskListView;
    private Label statsLabel;

    @Override
    // Initialize TaskManager and try to load saved tasks from file.
    // Sets up the main window and ensures tasks are saved on close.

    public void start(Stage primaryStage) {
        manager = new TaskManager();

        // Try to load previous tasks
        try {
            FileHandler.loadTasks(manager, "taskforge_tasks.dat");
        } catch (TaskException e) {
            System.out.println("No previous data found, starting fresh");
        }

        primaryStage.setTitle("TaskForge - Task Manager");
        primaryStage.setScene(createScene());
        primaryStage.setWidth(900);
        primaryStage.setHeight(700);
        primaryStage.show();

        // Save on close
        primaryStage.setOnCloseRequest(event -> {
            try {
                FileHandler.saveTasks(manager, "taskforge_tasks.dat");
            } catch (TaskException e) {
                System.err.println("Failed to save tasks: " + e.getMessage());
            }
        });
    }

    // Root layout: BorderPane with top bar (inputs), center (task list), bottom bar (stats + controls).
    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: Arial; -fx-font-size: 12;");

        // Top: Title and action buttons
        VBox topBox = createTopBar();
        root.setTop(topBox);

        // Center: Task list
        taskListView = new ListView<>();
        taskListView.setItems(manager.getTasks());
        taskListView.setCellFactory(param -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String emoji = TaskFormatter.getStatusEmoji(task.getStatus());
                    String deadline = "";
                    if (task instanceof DeadlineTask) {
                        DeadlineTask dt = (DeadlineTask) task;
                        deadline = " [" + dt.getDaysRemaining() + " days]";
                    }
                    setText(emoji + " " + task.getTitle() + deadline);

                    // Color by priority
                    String color = task.getPriority().getHexColor();
                    setStyle("-fx-text-fill: " + color + ";");
                }
            }
        });
        root.setCenter(taskListView);

        // Bottom: Stats and controls
        VBox bottomBox = createBottomBar();
        root.setBottom(bottomBox);

        return new Scene(root);
    }


    // Top section: Title label + input fields for new tasks.
    // Includes title, description, priority, optional deadline, and Add button.
    private VBox createTopBar() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");

        Label title1 = new Label("TaskForge - Task Management System");
        title1.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        HBox inputBox = new HBox(10);
        inputBox.setPadding(new Insets(10, 0, 0, 0));

        TextField titleInput = new TextField();
        titleInput.setPromptText("Task title...");
        titleInput.setPrefWidth(200);

        TextArea descInput = new TextArea();
        descInput.setPromptText("Description...");
        descInput.setPrefHeight(60);
        descInput.setWrapText(true);

        ComboBox<Priority> priorityBox = new ComboBox<>();
        priorityBox.setItems(javafx.collections.FXCollections.observableArrayList(Priority.values()));
        priorityBox.setValue(Priority.MEDIUM);

        CheckBox isDeadlineBox = new CheckBox("Has Deadline?");

        DatePicker deadlinePicker = new DatePicker();
        deadlinePicker.setDisable(true);
        isDeadlineBox.selectedProperty().addListener((obs, oldVal, newVal) ->
                deadlinePicker.setDisable(!newVal)
        );

        Button addButton = new Button("Add Task");
        addButton.setStyle("-fx-padding: 8; -fx-font-size: 12;");
        addButton.setOnAction(event -> {
            try {
                String title = titleInput.getText().trim();
                String desc = descInput.getText().trim();
                Priority priority = priorityBox.getValue();

                if (title.isEmpty()) {
                    showAlert("Error", "Title cannot be empty");
                    return;
                }

                Task task;
                if (isDeadlineBox.isSelected() && deadlinePicker.getValue() != null) {
                    LocalDateTime deadline = deadlinePicker.getValue().atTime(23, 59, 59);
                    task = new DeadlineTask(title, desc, priority, deadline);
                } else {
                    task = new Task(title, desc, priority);
                }

                manager.addTask(task);
                titleInput.clear();
                descInput.clear();
                priorityBox.setValue(Priority.MEDIUM);
                isDeadlineBox.setSelected(false);
                deadlinePicker.setValue(null);
                updateStats();
                showAlert("Success", "Task added successfully!");
            } catch (TaskException e) {
                showAlert("Error", e.getMessage());
            }
        });

        VBox descBox = new VBox(5);
        descBox.getChildren().add(new Label("Description:"));
        descBox.getChildren().add(descInput);

        inputBox.getChildren().addAll(
                new VBox(5, new Label("Title:"), titleInput),
                descBox,
                new VBox(5, new Label("Priority:"), priorityBox),
                new VBox(5, isDeadlineBox, deadlinePicker),
                addButton
        );

        box.getChildren().addAll(title1, inputBox);
        return box;
    }

    // Bottom section: Displays task statistics and control buttons.
    // Buttons: Delete, Mark Complete, Save, Refresh.
    private VBox createBottomBar() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0;");

        // Stats
        statsLabel = new Label();
        statsLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        updateStats();

        // Buttons
        HBox buttonBox = new HBox(10);

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(event -> {
            Task selected = taskListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Select a task first");
                return;
            }
            try {
                manager.removeTask(selected.getId());
                updateStats();
                showAlert("Success", "Task deleted");
            } catch (TaskException e) {
                showAlert("Error", e.getMessage());
            }
        });

        Button completeButton = new Button("Mark Complete");
        completeButton.setOnAction(event -> {
            Task selected = taskListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Error", "Select a task first");
                return;
            }
            try {
                manager.updateTask(
                        selected.getId(),
                        selected.getTitle(),
                        selected.getDescription(),
                        selected.getPriority(),
                        Status.COMPLETED
                );
                manager.getStreakTracker().recordCompletion();
                taskListView.refresh();
                updateStats();
                showAlert("Great!", "Task completed! 🎉");
            } catch (TaskException e) {
                showAlert("Error", e.getMessage());
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                FileHandler.saveTasks(manager, "taskforge_tasks.dat");
                showAlert("Success", "Tasks saved to file");
            } catch (TaskException e) {
                showAlert("Error", "Save failed: " + e.getMessage());
            }
        });

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
            taskListView.refresh();
            updateStats();
        });

        buttonBox.getChildren().addAll(deleteButton, completeButton, saveButton, refreshButton);

        box.getChildren().addAll(statsLabel, buttonBox);
        return box;
    }

    // Updates the stats label with total tasks, completed, pending, completion rate, and streak.
    private void updateStats() {
        int total = manager.getTotalTasks();
        int completed = manager.getCompletedCount();
        int pending = manager.getPendingCount();
        double rate = manager.getCompletionRate();
        int streak = manager.getStreakTracker().getCurrentStreak();

        String stats = String.format(
                "📊 Tasks: %d | ✅ Completed: %d | ⏳ Pending: %d | Rate: %.1f%% | 🔥 Streak: %d days",
                total, completed, pending, rate, streak
        );
        statsLabel.setText(stats);
    }

    // Utility method to show information alerts to the user.
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
