package todolist.utils;


import todolist.model.DeadlineTask;
import todolist.model.Status;
import todolist.model.Task;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

//Better way of displaying (changes can be made)
public class TaskFormatter {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatTaskForDisplay(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(task.getTitle()).append("\n");
        sb.append("Description: ").append(task.getDescription()).append("\n");
        sb.append("Priority: ").append(task.getPriority().name()).append("\n");
        sb.append("Status: ").append(task.getStatus().getDescription()).append("\n");


        if (task instanceof DeadlineTask) {
            DeadlineTask dt = (DeadlineTask) task;
            sb.append("Deadline: ").append(formatDateTime(dt.getDeadline())).append("\n");
            sb.append("Days Remaining: ").append(dt.getDaysRemaining()).append("\n");
            if (dt.isOverdue()) {
                sb.append("⚠️ OVERDUE");
            }
        }

        return sb.toString();
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    public static String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return "No tasks found.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i).getTitle());
            if (tasks.get(i) instanceof DeadlineTask) {
                DeadlineTask dt = (DeadlineTask) tasks.get(i);
                sb.append(" [").append(dt.getDaysRemaining()).append(" days]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String getStatusEmoji(Status status) {
        return switch (status) {
            case PENDING -> "⏳";
            case IN_PROGRESS -> "🚀";
            case COMPLETED -> "✅";
            case ARCHIVED -> "📦";
        };
    }
}