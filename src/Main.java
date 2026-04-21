import todolist.exception.TaskException;
import todolist.fileio.FileHandler;
import todolist.manager.TaskManager;
import todolist.model.DeadlineTask;
import todolist.model.Priority;
import todolist.model.Status;
import todolist.model.Task;
import todolist.utils.TaskFormatter;
import todolist.utils.TaskValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Main extends JFrame {

    private TaskManager manager;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JLabel statsLabel;

    // Input fields
    private JTextField titleInput;
    private JTextArea descInput;
    private JComboBox<Priority> priorityBox;
    private JCheckBox hasDeadlineCheck;
    private JTextField deadlineField;

    public Main() {
        manager = new TaskManager();

        try {
            FileHandler.loadTasks(manager, "taskforge_tasks.dat");
        } catch (TaskException e) {
            System.out.println("No previous data found, starting fresh");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setTitle("TaskForge - Beautiful Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 820);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        content.setBackground(new Color(42, 42, 69));

        JPanel topPanel = createTopPanel();
        content.add(topPanel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        refreshTaskList();

        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        taskList.setBackground(new Color(35, 35, 55));
        taskList.setFixedCellHeight(58);

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(120, 130, 255), 3),
                " Your Tasks ",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 17),
                new Color(180, 190, 255)
        ));
        content.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        content.add(bottomPanel, BorderLayout.SOUTH);

        add(content);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    FileHandler.saveTasks(manager, "taskforge_tasks.dat");
                } catch (TaskException ex) {
                    System.err.println("Failed to save tasks: " + ex.getMessage());
                }
            }
        });

        updateStats();
        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(232, 245, 233));   // Light Green
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 125, 50), 4),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel titleLabel = new JLabel("TaskForge");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(27, 94, 32));

        JLabel subtitle = new JLabel("Add New Task");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(46, 125, 50));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(232, 245, 233));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        titleInput = new JTextField(26);
        titleInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        descInput = new JTextArea(3, 30);
        descInput.setLineWrap(true);
        descInput.setWrapStyleWord(true);
        descInput.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        priorityBox = new JComboBox<>(Priority.values());
        priorityBox.setSelectedItem(Priority.MEDIUM);
        priorityBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Improved Checkbox Style
        hasDeadlineCheck = new JCheckBox("Has Deadline?");
        hasDeadlineCheck.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hasDeadlineCheck.setForeground(new Color(27, 94, 32));

        // Custom styling for checkbox
        hasDeadlineCheck.setIcon(new ImageIcon(createCheckboxIcon(false)));
        hasDeadlineCheck.setSelectedIcon(new ImageIcon(createCheckboxIcon(true)));

        deadlineField = new JTextField(15);
        deadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        deadlineField.setEnabled(false);

        hasDeadlineCheck.addActionListener(e -> deadlineField.setEnabled(hasDeadlineCheck.isSelected()));

        JButton addButton = createStyledButton("Add Task", new Color(46, 125, 50));

        // Layout
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Task Title:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(titleInput, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(new JScrollPane(descInput), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(priorityBox, gbc);

        // Deadline side by side
        JPanel deadlinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        deadlinePanel.setBackground(new Color(232, 245, 233));
        deadlinePanel.add(hasDeadlineCheck);
        deadlinePanel.add(deadlineField);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(deadlinePanel, gbc);

        JLabel hint = new JLabel("(Format: yyyy-MM-dd)");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(new Color(80, 140, 80));
        gbc.gridy = 4;
        inputPanel.add(hint, gbc);

        // Add Task Button at bottom
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(18, 10, 8, 10);
        inputPanel.add(addButton, gbc);

        addButton.addActionListener(e -> addNewTask());

        panel.add(titleLabel);
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(inputPanel);

        return panel;
    }

    // Custom Checkbox Icon Creator
    private Image createCheckboxIcon(boolean selected) {
        int size = 22;
        Image image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(selected ? new Color(46, 125, 50) : Color.WHITE);
        g2.fillRoundRect(1, 1, size-2, size-2, 6, 6);

        // Border
        g2.setColor(new Color(27, 94, 32));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(1, 1, size-2, size-2, 6, 6);

        // Tick mark when selected
        if (selected) {
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3f));
            g2.drawLine(5, 11, 9, 16);
            g2.drawLine(9, 16, 17, 6);
        }

        g2.dispose();
        return image;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(35, 35, 60));

        statsLabel = new JLabel("Statistics");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsLabel.setForeground(new Color(200, 210, 255));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 12));
        buttonPanel.setBackground(new Color(35, 35, 60));

        JButton deleteBtn    = createStyledButton("Delete Selected", new Color(220, 50, 50));
        JButton completeBtn  = createStyledButton("Mark Complete", new Color(40, 180, 120));
        JButton saveBtn      = createStyledButton("Save Tasks", new Color(140, 80, 220));
        JButton refreshBtn   = createStyledButton("Refresh", new Color(0, 180, 220));

        deleteBtn.addActionListener(e -> deleteSelectedTask());
        completeBtn.addActionListener(e -> markTaskComplete());
        saveBtn.addActionListener(e -> saveTasks());
        refreshBtn.addActionListener(e -> refreshTaskList());

        buttonPanel.add(deleteBtn);
        buttonPanel.add(completeBtn);
        buttonPanel.add(saveBtn);
        buttonPanel.add(refreshBtn);

        panel.add(statsLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(baseColor);
        btn.setForeground(new Color(27, 94, 32));     // Dark Green Text as requested
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 30, 14, 30));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }

    // ==================== Core Methods ====================

    private void addNewTask() {
        String title = titleInput.getText().trim();
        String desc = descInput.getText().trim();
        Priority priority = (Priority) priorityBox.getSelectedItem();

        String error = TaskValidator.validate(title, desc);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Task task;
            if (hasDeadlineCheck.isSelected() && !deadlineField.getText().trim().isEmpty()) {
                LocalDate date = LocalDate.parse(deadlineField.getText().trim());
                LocalDateTime deadline = date.atTime(23, 59, 59);
                task = new DeadlineTask(title, desc, priority, deadline);
            } else {
                task = new Task(title, desc, priority);
            }

            manager.addTask(task);
            refreshTaskList();

            titleInput.setText("");
            descInput.setText("");
            priorityBox.setSelectedItem(Priority.MEDIUM);
            hasDeadlineCheck.setSelected(false);
            deadlineField.setText("");
            deadlineField.setEnabled(false);

            updateStats();
            JOptionPane.showMessageDialog(this, "Task added successfully! ✅", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedTask() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            manager.removeTask(selected.getId());
            refreshTaskList();
            updateStats();
        } catch (TaskException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markTaskComplete() {
        Task selected = taskList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a task.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            manager.updateTask(selected.getId(), selected.getTitle(), selected.getDescription(),
                    selected.getPriority(), Status.COMPLETED);
            manager.getStreakTracker().recordCompletion();
            refreshTaskList();
            updateStats();
            JOptionPane.showMessageDialog(this, "Task marked as completed! 🎉", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (TaskException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveTasks() {
        try {
            FileHandler.saveTasks(manager, "taskforge_tasks.dat");
            JOptionPane.showMessageDialog(this, "All tasks saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (TaskException e) {
            JOptionPane.showMessageDialog(this, "Save failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTaskList() {
        taskListModel.clear();
        manager.getTasks().forEach(taskListModel::addElement);
    }

    private void updateStats() {
        int total = manager.getTotalTasks();
        int completed = manager.getCompletedCount();
        int pending = manager.getPendingCount();
        double rate = manager.getCompletionRate();
        int streak = manager.getStreakTracker().getCurrentStreak();

        statsLabel.setText(String.format(
                "📊 Total Tasks: %d   |   ✅ Completed: %d   |   ⏳ Pending: %d   |   Rate: %.1f%%   |   🔥 Streak: %d day(s)",
                total, completed, pending, rate, streak));
    }

    private static class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Task task) {
                String emoji = TaskFormatter.getStatusEmoji(task.getStatus());
                String deadlineInfo = (task instanceof DeadlineTask dt) ? "   [" + dt.getDaysRemaining() + " days left]" : "";
                setText(emoji + "  " + task.getTitle() + deadlineInfo);
                setForeground(Color.decode(task.getPriority().getHexColor()));
                setFont(new Font("Segoe UI", Font.PLAIN, 16));
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
