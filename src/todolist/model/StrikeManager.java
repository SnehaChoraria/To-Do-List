package todolist.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;//again for date-time calc


// just an extra feature ... kind of like streaks to be maintained
public class StrikeManager {
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastCompletedDate;

    public StrikeManager() {
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.lastCompletedDate = null;
    }

    public void recordCompletion() {
        LocalDate today = LocalDate.now();

        if (lastCompletedDate == null) {
            currentStreak = 1;
        } else if (lastCompletedDate.equals(today)) {
            return; // already counted today
        } else if (lastCompletedDate.equals(today.minusDays(1))) {
            currentStreak++;
        } else {
            currentStreak = 1; // streak broken, restart
        }

        lastCompletedDate = today;
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
    }

    public int getCurrentStreak() {
        // if last completion was before yesterday, streak is broken
        if (lastCompletedDate != null &&
                ChronoUnit.DAYS.between(lastCompletedDate, LocalDate.now()) > 1) {
            return 0;
        }
        return currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public LocalDate getLastCompletedDate() {
        return lastCompletedDate;
    }

    public void reset() {
        currentStreak = 0;
        longestStreak = 0;
        lastCompletedDate = null;
    }

    @Override
    public String toString() {
        return String.format("Current Streak: %d | Longest: %d | Last: %s",
                getCurrentStreak(), longestStreak, lastCompletedDate);
    }
}