# TaskForge - Task Management System

## 📖 Overview
ToDoList is a Java-based task management system with a **JavaFX UI** and a robust **backend**.  
It allows users to create, manage, and track tasks with deadlines, priorities, and streaks.  
Tasks can be saved to disk and reloaded later, ensuring persistence across sessions.

---

## ✨ Features
- Create tasks with title, description, and priority
- Optional deadlines with urgency and overdue detection
- Track task status (Pending, In Progress, Completed, Archived)
- Streak tracking for daily task completions
- Search tasks by title, priority, or status
- Statistics: completion rate, pending count, streaks
- Save and load tasks from file (`FileHandler`)
- JavaFX UI with interactive controls

---

## 📂 Project Structure
- **`todolist.model`**
    - `Task` → base class for tasks
    - `DeadlineTask` → tasks with deadlines
    - `Priority` → enum for task priority
    - `Status` → enum for task status
    - `StrikeManager` → streak tracking
  

- **`todolist.manager`**
    - `TaskManager` → CRUD operations, filtering, statistics


- **`todolist.utils`**
    - `TaskFormatter` → formatting tasks for display
    - `TaskValidator` → validation helpers


- **`todolist.fileio`**
    - `FileHandler` → save/load tasks to file


- **`todolist.exception`**
    - `TaskException` → custom exception handling
  

- **`Main.java`**
    - JavaFX application entry point with UI

---

## 🚀 How to Run
1. Install **JDK 17+** and download **JavaFX SDK** (see [OpenJFX](https://openjfx.io)).
2. In IntelliJ IDEA:
    - Go to **Run → Edit Configurations…**
    - Add VM options:
      ```
      --module-path "C:\path\to\javafx-sdk-17.0.18\lib" --add-modules javafx.controls,javafx.fxml
      ```
3. Run `Main.java`.
4. Tasks will be saved to `taskforge_tasks.dat` on exit and reloaded on startup.

---- Supports both **regular tasks** and **deadline tasks**.
- On startup, tasks are reloaded into the `TaskManager`.

---

## 🔧 Functions & Classes

### **Model**
- `Task` → Base class with ID, title, description, priority, status, timestamps.
- `DeadlineTask` → Extends `Task`, adds deadline and urgency/overdue checks.
- `Priority` → Enum with levels (LOW, MEDIUM, HIGH) and hex colors.
- `Status` → Enum with descriptions (Pending, In Progress, Completed, Archived).
- `StrikeManager` → Tracks streaks of consecutive task completions.

### **Manager**
- `TaskManager`
- `addTask(Task)` → Add new task
- `removeTask(String id)` → Delete task by ID
- `updateTask(...)` → Update task details and status
- `findTaskById(String)` → Find task by ID
- `findTasksByStatus(Status)` → Filter by status
- `findTasksByPriority(Priority)` → Filter by priority
- `findTasksByTitle(String)` → Search by keyword
- `getOverdueTasks()` → List overdue tasks
- `getUrgentTasks()` → List urgent tasks
- `getSortedByPriority()` → Sort tasks by priority
- `getSortedByDeadline()` → Sort tasks by deadline
- `getTotalTasks()` → Count all tasks
- `getCompletedCount()` → Count completed tasks
- `getPendingCount()` → Count pending tasks
- `getCompletionRate()` → Calculate completion %
- `getStreakTracker()` → Access streak manager
- `clear()` → Reset tasks and streaks

### **Utils**
- `TaskFormatter`
- `formatTaskForDisplay(Task)` → Detailed string output
- `formatTaskList(List<Task>)` → List of tasks with deadlines
- `getStatusEmoji(Status)` → Emoji for status
- `TaskValidator`
- `isValidTitle(String)` → Validate title
- `isValidDescription(String)` → Validate description
- `isValidDeadline(LocalDateTime)` → Validate deadline
- `canMarkAsCompleted(Task)` → Check if task can be completed
- `validate(String, String)` → Validate title + description

### **File I/O**
- `FileHandler`
- `saveTasks(TaskManager, String filePath)` → Save tasks to file
- `loadTasks(TaskManager, String filePath)` → Load tasks from file

### **Exception**
- `TaskException` → Custom exception for task errors

---

## 📝 Notes
- Backend logic is fully reusable without the UI.
- JavaFX UI provides an interactive interface but is optional for backend testing.
- JUnit tests can be added for validation but are not included here.

---

## 🗂 Persistence
- Tasks are serialized into a text file (`taskforge_tasks.dat`) using `FileHandler`.
- Supports both regular tasks and deadline tasks.
- On startup, tasks are reloaded into the `TaskManager`.

---

## 📝 Notes
- This project includes both **backend logic** and a **JavaFX UI**.
- Backend classes (`model`, `manager`, `utils`, `fileio`, `exception`) are reusable independently of the UI.
- JUnit tests can be added for validation but are optional.

---

