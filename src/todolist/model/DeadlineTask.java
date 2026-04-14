package todolist.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;// in short to manipulate date-time.

public class DeadlineTask extends Task
{
    private LocalDateTime deadline;
    private long daysRemaining; //long in case of no deadline

    public DeadlineTask(String title, String description, Priority priority, LocalDateTime deadline) {
        super(title, description, priority);
        this.deadline = deadline;
        calculateDaysRemaining();
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        this.updatedAt = LocalDateTime.now();
        calculateDaysRemaining();
    }

    private void calculateDaysRemaining() {
        if (deadline != null) {
            this.daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);
        }
    }

    public long getDaysRemaining() {
        calculateDaysRemaining();
        return daysRemaining;
    }

    @Override
    public boolean isOverdue() {
        return deadline != null && LocalDateTime.now().isAfter(deadline) &&
                !status.isTerminal();
    }

    public boolean isUrgent() {
        calculateDaysRemaining();
        return daysRemaining <= 2 && daysRemaining >= 0 && !status.isTerminal();
    }


    //
    @Override
    public String toString() {
        String overdueTag = isOverdue() ? " [OVERDUE]" : "";
        String urgentTag = isUrgent() ? " [URGENT]" : "";
        return super.toString() + String.format(" | Deadline: %s%s%s",
                deadline, overdueTag, urgentTag);
    }
}
