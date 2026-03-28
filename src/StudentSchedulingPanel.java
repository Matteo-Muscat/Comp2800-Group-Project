import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StudentSchedulingPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    // Reference to the main app so we can navigate back to menu
    private final MyAdviceApp app;

    // Mock current student display (later you can pull this from login/currentUser)
    private final String currentStudent = "Talha Hanif (1001)";

    // Table models (the data behind each table)
    private final DefaultTableModel availableModel;
    private final DefaultTableModel scheduleModel;

    // Tables
    private final JTable availableTable;
    private final JTable scheduleTable;

    // Term selection
    private final JComboBox<String> termBox = new JComboBox<>(new String[] { "2026W", "2026S", "2026F" });

    public StudentSchedulingPanel(MyAdviceApp app) {
        this.app = app;

        // BorderLayout:
        // NORTH  = header
        // CENTER = main content
        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        String[] cols = {
                "Course ID",
                "Course Name",
                "Section",
                "Days",
                "Time",
                "Professor",
                "Building",
                "Room"
        };

        // Available sections table model
        availableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // no editing in the table directly
            }
        };

        // Current schedule table model
        scheduleModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        availableTable = new JTable(availableModel);
        scheduleTable  = new JTable(scheduleModel);

        // Improve readability
        availableTable.setRowHeight(22);
        scheduleTable.setRowHeight(22);

        add(buildBody(), BorderLayout.CENTER);

        // Load initial mock data
        loadAvailableSectionsForSelectedTerm();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Scheduling (Student)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        makeSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);

        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top row: student + term
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topRow.setBackground(WHITE);

        JLabel studentLbl = new JLabel("Student:");
        studentLbl.setForeground(GRAY);
        studentLbl.setFont(studentLbl.getFont().deriveFont(Font.BOLD, 16f));

        JLabel studentVal = new JLabel(currentStudent);
        studentVal.setForeground(GRAY);
        studentVal.setFont(studentVal.getFont().deriveFont(Font.BOLD, 16f));

        JLabel termLbl = new JLabel("Term:");
        termLbl.setForeground(GRAY);
        termLbl.setFont(termLbl.getFont().deriveFont(Font.BOLD, 16f));

        // When the term changes, reload available sections (mock)
        termBox.addActionListener(e -> loadAvailableSectionsForSelectedTerm());

        topRow.add(studentLbl);
        topRow.add(studentVal);
        topRow.add(Box.createHorizontalStrut(25));
        topRow.add(termLbl);
        topRow.add(termBox);

        // Center: two tables
        JPanel tables = new JPanel(new GridLayout(1, 2, 15, 0));
        tables.setBackground(WHITE);

        tables.add(wrapTable("Available Sections", availableTable));
        tables.add(wrapTable("Current Schedule", scheduleTable));

        // Bottom: action buttons
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setBackground(WHITE);

        JButton addBtn = new JButton("Add →");
        makeActionButton(addBtn, BLUE);

        JButton removeBtn = new JButton("← Remove");
        makeActionButton(removeBtn, GRAY);

        JButton clearBtn = new JButton("Clear Schedule");
        makeActionButton(clearBtn, YELLOW);
        clearBtn.setForeground(Color.BLACK);

        // Add selected available row into schedule
        addBtn.addActionListener(e -> addSelectedAvailableToSchedule());

        // Remove selected schedule row
        removeBtn.addActionListener(e -> removeSelectedFromSchedule());

        // Clear schedule table
        clearBtn.addActionListener(e -> scheduleModel.setRowCount(0));

        actions.add(addBtn);
        actions.add(removeBtn);
        actions.add(clearBtn);


        body.add(topRow, BorderLayout.NORTH);
        body.add(tables, BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);

        return body;
    }

     // Wraps a table in a titled border and scroll pane

    private JPanel wrapTable(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    /*
     * Adds the selected row from Available Sections into Current Schedule.
     * (GUI-only: we do not check conflicts yet.)
     */
    private void addSelectedAvailableToSchedule() {
        int row = availableTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to add.");
            return;
        }

        // Copy all columns from available to schedule
        Object[] data = new Object[availableModel.getColumnCount()];
        for (int c = 0; c < availableModel.getColumnCount(); c++) {
            data[c] = availableModel.getValueAt(row, c);
        }

        // Prevent duplicate exact rows
        if (isDuplicateInSchedule(data)) {
            JOptionPane.showMessageDialog(this, "That section is already in your schedule.");
            return;
        }

        scheduleModel.addRow(data);
    }

    private void removeSelectedFromSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a scheduled section to remove.");
            return;
        }
        scheduleModel.removeRow(row);
    }

    private boolean isDuplicateInSchedule(Object[] rowData) {
        for (int r = 0; r < scheduleModel.getRowCount(); r++) {
            boolean same = true;
            for (int c = 0; c < scheduleModel.getColumnCount(); c++) {
                Object a = scheduleModel.getValueAt(r, c);
                Object b = rowData[c];
                if (a == null && b == null) continue;
                if (a == null || b == null || !a.equals(b)) {
                    same = false;
                    break;
                }
            }
            if (same) return true;
        }
        return false;
    }

    // ============================================
    // Mock data loading (term-based)
    // ============================================

    /*
     * Loads mock available sections depending on the selected term.
     * Later, this becomes a backend call like:
     * GET /api/sections?term=2026W
     */
    private void loadAvailableSectionsForSelectedTerm() {
        String term = (String) termBox.getSelectedItem();

        // Clear existing rows
        availableModel.setRowCount(0);

        // Mock data: different term -> different section offerings
        if ("2026W".equals(term)) {
            availableModel.addRow(new Object[]{"COMP-2540", "Data Structures and Algorithms", "001", "MWF", "10:00-10:50", "Dr. X", "Erie", "101"});
            availableModel.addRow(new Object[]{"COMP-2800", "Software Development",          "002", "TR",  "11:30-12:50", "Dr. Y", "Erie", "202"});
            availableModel.addRow(new Object[]{"COMP-3150", "Database Management Systems",  "001", "TR",  "14:30-15:50", "Dr. Z", "Leddy", "110"});
            availableModel.addRow(new Object[]{"COMP-3300", "Operating Systems",            "003", "MW",  "16:00-17:20", "Dr. A", "Erie", "210"});
        } else if ("2026S".equals(term)) {
            availableModel.addRow(new Object[]{"COMP-2650", "Computer Architecture I",      "001", "TR",  "09:00-10:20", "Dr. B", "Erie", "105"});
            availableModel.addRow(new Object[]{"COMP-3220", "OO Analysis and Design",       "001", "MW",  "13:00-14:20", "Dr. C", "Erie", "120"});
            availableModel.addRow(new Object[]{"COMP-3670", "Computer Networks",            "002", "TR",  "15:00-16:20", "Dr. D", "Leddy", "210"});
        } else { // 2026F
            availableModel.addRow(new Object[]{"COMP-2540", "Data Structures and Algorithms", "002", "MWF", "11:00-11:50", "Dr. X", "Erie", "101"});
            availableModel.addRow(new Object[]{"COMP-2800", "Software Development",          "001", "TR",  "10:00-11:20", "Dr. Y", "Erie", "202"});
            availableModel.addRow(new Object[]{"COMP-3300", "Operating Systems",            "001", "TR",  "12:30-13:50", "Dr. A", "Erie", "210"});
        }
    }

    private void makeSmallButton(JButton btn) {
        btn.setBackground(YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void makeActionButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
    }
}