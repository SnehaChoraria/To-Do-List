package todolist.fileio;

import todolist.exception.TaskException;
import todolist.manager.TaskManager;
import todolist.model.DeadlineTask;
import todolist.model.Priority;
import todolist.model.Status;
import todolist.model.Task;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//saving tasks to a file and loading them back later

public class FileHandler {
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;//decides on how date and time are written
    private static final String FIELD_DELIMITER = "||"; // separate pieces fo info

    //writing on file
    public static void saveTasks(TaskManager manager, String filePath) throws TaskException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            for (Task task : manager.getTasks()) {
                String line = serializeTask(task);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new TaskException("Failed to save tasks to file: " + filePath, e);
        }
    }

    //reads from file puts into task manager

    public static void loadTasks(TaskManager manager, String filePath) throws TaskException {
        File file = new File(filePath);
        if (!file.exists()) {
            return; // file doesn't exist yet, that's fine
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Task task = deserializeTask(line);
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new TaskException("Failed to load tasks from file: " + filePath, e);
        }
    }

    // task into single line of text
    private static String serializeTask(Task task) {
        StringBuilder sb = new StringBuilder();

        sb.append(task.getId()).append(FIELD_DELIMITER);
        sb.append(task.getTitle()).append(FIELD_DELIMITER);
        sb.append(task.getDescription()).append(FIELD_DELIMITER);
        sb.append(task.getPriority().name()).append(FIELD_DELIMITER);
        sb.append(task.getStatus().name()).append(FIELD_DELIMITER);
        sb.append(task.getCreatedAt().format(DATETIME_FORMATTER)).append(FIELD_DELIMITER);
        sb.append(task.getUpdatedAt().format(DATETIME_FORMATTER)).append(FIELD_DELIMITER);

        //ID||Title||Description||Priority||Status||CreatedAt||UpdatedAt||TaskType||Deadline

        //if task has deadline date it adds it too else "REGULAR"
        if (task instanceof DeadlineTask) {
            DeadlineTask dt = (DeadlineTask) task;
            sb.append("DEADLINE").append(FIELD_DELIMITER);
            sb.append(dt.getDeadline().format(DATETIME_FORMATTER));
        } else {
            sb.append("REGULAR");
        }

        return sb.toString();
    }


    // converts line task to object again

    private static Task deserializeTask(String line) throws TaskException {
        String[] parts = line.split("\\|\\|", -1);
        //seperate by ||

        if (parts.length < 8) {
            throw new TaskException("Invalid task format in file");
        }

        try {
            String id = parts[0];
            String title = parts[1];
            String description = parts[2];
            Priority priority = Priority.valueOf(parts[3]);
            Status status = Status.valueOf(parts[4]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[5], DATETIME_FORMATTER);
            LocalDateTime updatedAt = LocalDateTime.parse(parts[6], DATETIME_FORMATTER);
            String taskType = parts[7];

            Task task;
            if ("DEADLINE".equals(taskType) && parts.length > 8) {
                LocalDateTime deadline = LocalDateTime.parse(parts[8], DATETIME_FORMATTER);
                task = new DeadlineTask(title, description, priority, deadline);
            } else {
                task = new Task(title, description, priority);
            }

            // restore original timestamps and status
            // the id update time and restore one
            task.setStatus(status);

            return task;
        } catch (Exception e) {
            throw new TaskException("Error deserializing task: " + e.getMessage(), e);
        }
    }
}
