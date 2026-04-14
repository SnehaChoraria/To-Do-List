package todolist.model;

import java.time.LocalDateTime;
import java.util.UUID; //universally unique identifier

public class Task {
    protected String id;
    protected String title;
    protected String description;
    protected Priority priority;
    protected Status status;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public Task(String title, String description, Priority priority) {
        this.id = UUID.randomUUID().toString();// unique id is generated for each task
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = Status.PENDING;//default
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();//default
    }

    // getters for all
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // setters (if anything is altered , updatedAt changes )
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    //
    public boolean isOverdue() {
        return false;
    }


    // [PENDING] Project-semester 2 (Priority : MEDIUM)
    // can format it better

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (Priority: %s)",
                status, title, description, priority);
    }
}