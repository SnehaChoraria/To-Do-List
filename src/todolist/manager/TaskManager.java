package todolist.manager;

import todolist.exception.TaskException;
import todolist.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskManager {
    private final List<Task> tasks;
    private final StrikeManager streakTracker;

    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.streakTracker = new StrikeManager();
    }

    // Core CRUD operations
    public void addTask(Task task) throws TaskException {
        if (task == null) {
            throw new TaskException("Task cannot be null");
        }
        if (task.getTitle().trim().isEmpty()) {
            throw new TaskException("Task title cannot be empty");
        }
        tasks.add(task);
    }

    public void removeTask(String taskId) throws TaskException {
        Optional<Task> task = findTaskById(taskId);
        if (task.isEmpty()) {
            throw new TaskException("Task not found with id: " + taskId);
        }
        tasks.remove(task.get());
    }

    public void updateTask(String taskId, String newTitle, String newDescription,
                           Priority newPriority, Status newStatus) throws TaskException {
        Task task = findTaskById(taskId)
                .orElseThrow(() -> new TaskException("Task not found with id: " + taskId));

        task.setTitle(newTitle);
        task.setDescription(newDescription);
        task.setPriority(newPriority);

        if (newStatus == Status.COMPLETED) {
            streakTracker.recordCompletion();
        }
        task.setStatus(newStatus);
    }

    // Finder methods
    public Optional<Task> findTaskById(String taskId) {
        return tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst();
    }

    public List<Task> findTasksByStatus(Status status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Task> findTasksByPriority(Priority priority) {
        return tasks.stream()
                .filter(t -> t.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<Task> findTasksByTitle(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    // Filtering and sorting
    public List<Task> getOverdueTasks() {
        return tasks.stream()
                .filter(Task::isOverdue)
                .sorted(Comparator.comparing(t -> {
                    if (t instanceof DeadlineTask) {
                        return ((DeadlineTask) t).getDeadline();
                    }
                    return LocalDateTime.MAX;
                }))
                .collect(Collectors.toList());
    }

    public List<Task> getUrgentTasks() {
        return tasks.stream()
                .filter(t -> t instanceof DeadlineTask)
                .map(t -> (DeadlineTask) t)
                .filter(DeadlineTask::isUrgent)
                .sorted(Comparator.comparing(DeadlineTask::getDeadline))
                .collect(Collectors.toList());
    }

    public List<Task> getSortedByPriority() {
        return tasks.stream()
                .sorted(Comparator.comparing((Task t) -> t.getPriority().getLevel()).reversed())
                .collect(Collectors.toList());
    }

    public List<Task> getSortedByDeadline() {
        return tasks.stream()
                .filter(t -> t instanceof DeadlineTask)
                .sorted(Comparator.comparing(t -> ((DeadlineTask) t).getDeadline()))
                .collect(Collectors.toList());
    }

    // Statistics
    public int getTotalTasks() {
        return tasks.size();
    }

    public int getCompletedCount() {
        return (int) tasks.stream()
                .filter(t -> t.getStatus() == Status.COMPLETED)
                .count();
    }

    public int getPendingCount() {
        return (int) tasks.stream()
                .filter(t -> t.getStatus() == Status.PENDING)
                .count();
    }

    public double getCompletionRate() {
        if (getTotalTasks() == 0) return 0.0;
        return (double) getCompletedCount() / getTotalTasks() * 100;
    }

    public StrikeManager getStreakTracker() {
        return streakTracker;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void clear() {
        tasks.clear();
        streakTracker.reset();
    }
}
