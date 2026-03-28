import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/*
 * FacultySchedulingPanel
 * ----------------------
 * Faculty/Staff version of Scheduling (timetable administration).
 *
 * Matches your Admin/Scheduling design:
 * - Create/manage course timetables (change course time, professor, etc.)
 *
 * UI behavior (GUI-only):
 * - Term dropdown
 * - Sections table (editable fields like professor/building/room)
 * - Meetings table (days + start/end) for the selected section
 * - Add/Delete section
 * - Add/Delete meeting rows
 *
 * NOTE:
 * - This is GUI-only mock data. Later this will load/save to backend/database.
 */
public class FacultySchedulingPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;

    // Term selection (timeframe)
    private final JComboBox<String> termBox = new JComboBox<>(new String[]{"2026W", "2026S", "2026F"});

    // Sections table model
    private final DefaultTableModel sectionModel;

    // Meetings table model (depends on selected section)
    private final DefaultTableModel meetingModel;

    // Tables
    private final JTable sectionTable;
    private final JTable meetingTable;

    public FacultySchedulingPanel(MyAdviceApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        // Sections columns (faculty/staff can edit some fields)
        String[] sectionCols = {
                "Course ID", "Course Name", "Section", "Professor", "Building", "Room"
        };

        // Meetings columns (editable)
        String[] meetingCols = {
                "Day", "Start Time", "End Time"
        };

        // Sections model: allow editing for Professor/Building/Room
        sectionModel = new DefaultTableModel(sectionCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Allow editing: Professor, Building, Room
                return col == 3 || col == 4 || col == 5;
            }
        };

        // Meetings model: allow editing all columns
        meetingModel = new DefaultTableModel(meetingCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }
        };

        sectionTable = new JTable(sectionModel);
        meetingTable = new JTable(meetingModel);

        sectionTable.setRowHeight(22);
        meetingTable.setRowHeight(22);

        add(buildBody(), BorderLayout.CENTER);

        // Load initial mock data
        loadSectionsForSelectedTerm();

        // When a section row is selected, load meetings for that section (mock)
        sectionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadMeetingsForSelectedSection();
            }
        });
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Scheduling (Faculty/Staff)");
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

        // Top row: Term dropdown + refresh button
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setBackground(WHITE);

        JLabel termLbl = new JLabel("Term:");
        termLbl.setForeground(GRAY);
        termLbl.setFont(termLbl.getFont().deriveFont(Font.BOLD, 16f));

        termBox.addActionListener(e -> loadSectionsForSelectedTerm());

        left.add(termLbl);
        left.add(termBox);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(WHITE);

        JButton refresh = new JButton("Reload (Mock)");
        makeActionButton(refresh, BLUE);
        refresh.addActionListener(e -> loadSectionsForSelectedTerm());

        right.add(refresh);

        topRow.add(left, BorderLayout.WEST);
        topRow.add(right, BorderLayout.EAST);

        // Center: two tables stacked using split pane
        JPanel sectionCard = wrapTable("Sections (Edit professor/building/room)", sectionTable);
        JPanel meetingCard = wrapTable("Meetings for Selected Section (Edit day/time)", meetingTable);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sectionCard, meetingCard);
        split.setResizeWeight(0.60);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setDividerLocation(260);

        // Bottom: actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setBackground(WHITE);

        JButton addSection = new JButton("Add Section");
        makeActionButton(addSection, BLUE);

        JButton deleteSection = new JButton("Delete Section");
        makeActionButton(deleteSection, GRAY);

        JButton addMeeting = new JButton("Add Meeting");
        makeActionButton(addMeeting, BLUE);

        JButton deleteMeeting = new JButton("Delete Meeting");
        makeActionButton(deleteMeeting, GRAY);

        JButton save = new JButton("Save Changes (UI Only)");
        makeActionButton(save, YELLOW);
        save.setForeground(Color.BLACK);

        addSection.addActionListener(e -> addSectionRow());
        deleteSection.addActionListener(e -> deleteSelectedSection());
        addMeeting.addActionListener(e -> addMeetingRow());
        deleteMeeting.addActionListener(e -> deleteSelectedMeeting());
        save.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "GUI-only: In the real system, changes would be saved to the database."));

        actions.add(addSection);
        actions.add(deleteSection);
        actions.add(addMeeting);
        actions.add(deleteMeeting);
        actions.add(save);

        // Assemble body
        body.add(topRow, BorderLayout.NORTH);
        body.add(split, BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);

        return body;
    }

    private JPanel wrapTable(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    // ==========================
    // Mock data loading
    // ==========================

    private void loadSectionsForSelectedTerm() {
        String term = (String) termBox.getSelectedItem();

        sectionModel.setRowCount(0);
        meetingModel.setRowCount(0);

        // Mock section offerings per term
        if ("2026W".equals(term)) {
            sectionModel.addRow(new Object[]{"COMP-2540", "Data Structures", "001", "Dr. X", "Erie", "101"});
            sectionModel.addRow(new Object[]{"COMP-2800", "Software Development", "002", "Dr. Y", "Erie", "202"});
            sectionModel.addRow(new Object[]{"COMP-3300", "Operating Systems", "003", "Dr. A", "Erie", "210"});
        } else if ("2026S".equals(term)) {
            sectionModel.addRow(new Object[]{"COMP-3220", "OO Analysis & Design", "001", "Dr. C", "Erie", "120"});
            sectionModel.addRow(new Object[]{"COMP-3670", "Computer Networks", "002", "Dr. D", "Leddy", "210"});
        } else { // 2026F
            sectionModel.addRow(new Object[]{"COMP-2540", "Data Structures", "002", "Dr. X", "Erie", "101"});
            sectionModel.addRow(new Object[]{"COMP-2800", "Software Development", "001", "Dr. Y", "Erie", "202"});
        }

        // Auto-select first section so meetings load
        if (sectionTable.getRowCount() > 0) {
            sectionTable.setRowSelectionInterval(0, 0);
            loadMeetingsForSelectedSection();
        }
    }

    private void loadMeetingsForSelectedSection() {
        meetingModel.setRowCount(0);

        int row = sectionTable.getSelectedRow();
        if (row == -1) return;

        // Read the course/section to decide meeting mock
        String course = String.valueOf(sectionModel.getValueAt(row, 0));
        String sec = String.valueOf(sectionModel.getValueAt(row, 2));

        // Mock meeting patterns
        if (course.equals("COMP-2540") && sec.equals("001")) {
            meetingModel.addRow(new Object[]{"MWF", "10:00", "10:50"});
        } else if (course.equals("COMP-2800")) {
            meetingModel.addRow(new Object[]{"TR", "11:30", "12:50"});
        } else if (course.equals("COMP-3300")) {
            meetingModel.addRow(new Object[]{"MW", "16:00", "17:20"});
        } else {
            meetingModel.addRow(new Object[]{"TR", "14:30", "15:50"});
        }
    }

    // ==========================
    // Actions (GUI-only)
    // ==========================

    private void addSectionRow() {
        // Add a blank-ish row for staff to edit
        sectionModel.addRow(new Object[]{"COMP-XXXX", "New Course", "000", "TBD", "TBD", "TBD"});

        int last = sectionModel.getRowCount() - 1;
        if (last >= 0) {
            sectionTable.setRowSelectionInterval(last, last);
        }
    }

    private void deleteSelectedSection() {
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a section to delete.");
            return;
        }
        sectionModel.removeRow(row);
        meetingModel.setRowCount(0);
    }

    private void addMeetingRow() {
        if (sectionTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Select a section first.");
            return;
        }
        meetingModel.addRow(new Object[]{"TR", "09:00", "10:20"});
    }

    private void deleteSelectedMeeting() {
        int row = meetingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a meeting row to delete.");
            return;
        }
        meetingModel.removeRow(row);
    }

    // ==========================
    // Button styling (match app)
    // ==========================

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