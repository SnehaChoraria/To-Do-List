package todolist.utils;

import todolist.model.Task;
import java.time.LocalDateTime;


// just to verify and format the task entered by the user
public class TaskValidator {

    public static boolean isValidTitle(String title) {
        return title != null && !title.trim().isEmpty() && title.length() <= 50;
    }

    public static boolean isValidDescription(String description) {
        return description != null && description.length() <= 200;
    }

    public static boolean isValidDeadline(LocalDateTime deadline) {
        return deadline != null && deadline.isAfter(LocalDateTime.now());
    }

    public static boolean canMarkAsCompleted(Task task) {
        return task != null && !task.getStatus().isTerminal();
    }

    //just both title and description together
    public static String validate(String title, String description) {
        if (!isValidTitle(title)) {
            return "Title must be non-empty and at most 50 characters";
        }
        if (!isValidDescription(description)) {
            return "Description must be at most 200 characters";
        }
        return null; // valid
    }
}