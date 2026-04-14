package todolist.model;

// defines what is the current status of task

public enum Status {
    PENDING("Not started yet"),
    IN_PROGRESS("Currently working on it"),
    COMPLETED("Done"),
    ARCHIVED("Keeping it for later");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == ARCHIVED;
    }
}