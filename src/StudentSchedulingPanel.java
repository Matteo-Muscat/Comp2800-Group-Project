import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentSchedulingPanel extends JPanel {

    private static final Color BLUE = new Color(0x005A9C);
    private static final Color GRAY = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE = Color.WHITE;

    private final MyAdviceApp app;
    private final AppBackend backend;

    private final DefaultTableModel availableModel;
    private final DefaultTableModel scheduleModel;

    private final JTable availableTable;
    private final JTable scheduleTable;
    private final JComboBox<String> termBox = new JComboBox<>();

    private final Map<String, BackendModels.Term> termsByName = new LinkedHashMap<>();

    public StudentSchedulingPanel(MyAdviceApp app, AppBackend backend) {
        this.app = app;
        this.backend = backend;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        String[] cols = {
                "Section ID", "Course ID", "Course Name", "Section", "Days", "Time", "Professor", "Building", "Room"
        };

        availableModel = createTableModel(cols);
        scheduleModel = createTableModel(cols);

        availableTable = new JTable(availableModel);
        scheduleTable = new JTable(scheduleModel);
        availableTable.setRowHeight(22);
        scheduleTable.setRowHeight(22);

        add(buildBody(), BorderLayout.CENTER);

        loadTerms();
        refreshTables();
    }

    private DefaultTableModel createTableModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topRow.setBackground(WHITE);

        JLabel studentLbl = makeLabel("Student:");
        JLabel studentVal = makeLabel(currentStudentDisplay());
        JLabel termLbl = makeLabel("Term:");

        termBox.addActionListener(e -> refreshTables());

        topRow.add(studentLbl);
        topRow.add(studentVal);
        topRow.add(Box.createHorizontalStrut(25));
        topRow.add(termLbl);
        topRow.add(termBox);

        JPanel tables = new JPanel(new GridLayout(1, 2, 15, 0));
        tables.setBackground(WHITE);
        tables.add(wrapTable("Available Sections", availableTable));
        tables.add(wrapTable("Current Schedule", scheduleTable));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setBackground(WHITE);

        JButton addBtn = new JButton("Add ->");
        makeActionButton(addBtn, BLUE);

        JButton removeBtn = new JButton("<- Remove");
        makeActionButton(removeBtn, GRAY);

        JButton clearBtn = new JButton("Clear Schedule");
        makeActionButton(clearBtn, YELLOW);
        clearBtn.setForeground(Color.BLACK);

        addBtn.addActionListener(e -> addSelectedAvailableToSchedule());
        removeBtn.addActionListener(e -> removeSelectedFromSchedule());
        clearBtn.addActionListener(e -> clearSchedule());

        actions.add(addBtn);
        actions.add(removeBtn);
        actions.add(clearBtn);

        body.add(topRow, BorderLayout.NORTH);
        body.add(tables, BorderLayout.CENTER);
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

    private void loadTerms() {
        termBox.removeAllItems();
        termsByName.clear();

        for (BackendModels.Term term : backend.getTerms()) {
            termsByName.put(term.termName(), term);
            termBox.addItem(term.termName());
        }

        if (termBox.getItemCount() > 0) {
            termBox.setSelectedIndex(0);
        }
    }

    private void refreshTables() {
        loadAvailableSectionsForSelectedTerm();
        loadCurrentScheduleForSelectedTerm();
    }

    private void loadAvailableSectionsForSelectedTerm() {
        availableModel.setRowCount(0);
        BackendModels.Term term = selectedTerm();
        if (term == null) {
            return;
        }

        for (BackendModels.Section section : backend.getSectionsForTerm(term.termId())) {
            availableModel.addRow(toRow(section));
        }
    }

    private void loadCurrentScheduleForSelectedTerm() {
        scheduleModel.setRowCount(0);
        if (currentStudentId().isEmpty()) {
            return;
        }

        BackendModels.Term term = selectedTerm();
        if (term == null) {
            return;
        }

        List<BackendModels.Section> sections =
                backend.getStudentSchedule(Integer.parseInt(currentStudentId()), term.termId());
        for (BackendModels.Section section : sections) {
            scheduleModel.addRow(toRow(section));
        }
    }

    private void addSelectedAvailableToSchedule() {
        if (currentStudentId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Log in as a student before building a schedule.");
            return;
        }

        int row = availableTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a section to add.");
            return;
        }

        BackendModels.Term term = selectedTerm();
        int sectionId = Integer.parseInt(String.valueOf(availableModel.getValueAt(row, 0)));
        backend.addSectionToSchedule(Integer.parseInt(currentStudentId()), term.termId(), sectionId);
        loadCurrentScheduleForSelectedTerm();
    }

    private void removeSelectedFromSchedule() {
        int row = scheduleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a scheduled section to remove.");
            return;
        }

        BackendModels.Term term = selectedTerm();
        int sectionId = Integer.parseInt(String.valueOf(scheduleModel.getValueAt(row, 0)));
        backend.removeSectionFromSchedule(Integer.parseInt(currentStudentId()), term.termId(), sectionId);
        loadCurrentScheduleForSelectedTerm();
    }

    private void clearSchedule() {
        if (currentStudentId().isEmpty()) {
            return;
        }
        BackendModels.Term term = selectedTerm();
        if (term == null) {
            return;
        }
        backend.clearSchedule(Integer.parseInt(currentStudentId()), term.termId());
        loadCurrentScheduleForSelectedTerm();
    }

    private Object[] toRow(BackendModels.Section section) {
        StringBuilder days = new StringBuilder();
        StringBuilder times = new StringBuilder();

        for (int i = 0; i < section.meetings().size(); i++) {
            BackendModels.SectionMeeting meeting = section.meetings().get(i);
            if (i > 0) {
                days.append(", ");
                times.append(", ");
            }
            days.append(ApiDataMapper.shortDay(meeting.dayOfWeek()));
            times.append(ApiDataMapper.compactTimeRange(meeting.startTime(), meeting.endTime()));
        }

        return new Object[]{
                section.sectionId(),
                section.courseCode(),
                section.courseName(),
                section.sectionNumber(),
                days.toString(),
                times.toString(),
                section.instructorName(),
                section.building(),
                section.room()
        };
    }

    private String currentStudentId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "" : user.id;
    }

    private String currentStudentDisplay() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "Student" : user.name + " (" + user.id + ")";
    }

    private BackendModels.Term selectedTerm() {
        Object value = termBox.getSelectedItem();
        return value == null ? null : termsByName.get(value.toString());
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(GRAY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
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
