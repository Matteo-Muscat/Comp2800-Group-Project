import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
 * FacultyReportsPanel
 * -------------------
 * Faculty/Staff version of Reports.
 *
 * Requirements:
 * - Faculty can view students and faculty info
 * - Faculty/advisor report: department, name, most/least appointments (ordered)
 * - Student report: Name, Major, ID, most/least appointments (ordered)
 *
 * GUI design:
 * - Two tabs:
 *    1) Advisors/Faculty
 *    2) Students
 * - Each tab has:
 *    - Order dropdown (Most / Least appointments)
 *    - Optional filter dropdown (Department for Advisors, Major for Students)
 *    - A table displaying the results
 *
 * NOTE:
 * - GUI-only mock service now; backend later.
 */
public class FacultyReportsPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;
    private final FacultyReportsService service;

    // Tabs
    private final JTabbedPane tabs = new JTabbedPane();

    // ===== Advisors tab controls =====
    private final JComboBox<String> advisorOrderBox = new JComboBox<>(new String[] {
            "Most appointments", "Least appointments"
    });
    private final JComboBox<String> deptBox = new JComboBox<>();

    // Advisors table
    private final DefaultTableModel advisorModel = new DefaultTableModel(
            new String[]{"Advisor Name", "Department", "Appointments"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable advisorTable = new JTable(advisorModel);

    // ===== Students tab controls =====
    private final JComboBox<String> studentOrderBox = new JComboBox<>(new String[] {
            "Most appointments", "Least appointments"
    });
    private final JComboBox<String> majorBox = new JComboBox<>();

    // Students table
    private final DefaultTableModel studentModel = new DefaultTableModel(
            new String[]{"Student Name", "Major", "Student ID", "Appointments"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable studentTable = new JTable(studentModel);

    public FacultyReportsPanel(MyAdviceApp app, FacultyReportsService service) {
        this.app = app;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        // Table readability
        advisorTable.setRowHeight(22);
        studentTable.setRowHeight(22);

        // Load filter dropdowns and initial table data
        loadAdvisorDepartments();
        loadStudentMajors();
        refreshAdvisorReport();
        refreshStudentReport();
    }

    /*
     * Header bar with Back button.
     */
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Reports (Faculty/Staff)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = makeSmallButton("Back to Menu");
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    /*
     * Body: Tabbed interface with two report types.
     */
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        tabs.addTab("Advisors / Faculty", buildAdvisorTab());
        tabs.addTab("Students", buildStudentTab());

        body.add(tabs, BorderLayout.CENTER);
        return body;
    }

    // ============================================================
    // Advisors tab
    // ============================================================

    private JComponent buildAdvisorTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(WHITE);

        tab.add(buildAdvisorControls(), BorderLayout.NORTH);
        tab.add(buildAdvisorTableCard(), BorderLayout.CENTER);

        // Auto-refresh when dropdown changes
        advisorOrderBox.addActionListener(e -> refreshAdvisorReport());
        deptBox.addActionListener(e -> refreshAdvisorReport());

        return tab;
    }

    private JComponent buildAdvisorControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        controls.setBackground(WHITE);

        controls.add(makeLabel("Order:"));
        controls.add(advisorOrderBox);

        controls.add(Box.createHorizontalStrut(20));

        controls.add(makeLabel("Department:"));
        controls.add(deptBox);

        return controls;
    }

    private JComponent buildAdvisorTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Advisor / Faculty Appointment Counts"));

        card.add(new JScrollPane(advisorTable), BorderLayout.CENTER);
        return card;
    }

    private void loadAdvisorDepartments() {
        deptBox.removeAllItems();
        deptBox.addItem("All");

        for (String d : service.getDepartments()) {
            deptBox.addItem(d);
        }

        deptBox.setSelectedIndex(0);
    }

    private void refreshAdvisorReport() {
        advisorModel.setRowCount(0);

        String deptFilter = (String) deptBox.getSelectedItem();
        boolean mostFirst = "Most appointments".equals(advisorOrderBox.getSelectedItem());

        List<AdvisorRow> rows = service.getAdvisorReport(deptFilter, mostFirst);
        for (AdvisorRow r : rows) {
            advisorModel.addRow(new Object[]{r.name, r.department, r.appointments});
        }
    }

    // ============================================================
    // Students tab
    // ============================================================

    private JComponent buildStudentTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(WHITE);

        tab.add(buildStudentControls(), BorderLayout.NORTH);
        tab.add(buildStudentTableCard(), BorderLayout.CENTER);

        // Auto-refresh when dropdown changes
        studentOrderBox.addActionListener(e -> refreshStudentReport());
        majorBox.addActionListener(e -> refreshStudentReport());

        return tab;
    }

    private JComponent buildStudentControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        controls.setBackground(WHITE);

        controls.add(makeLabel("Order:"));
        controls.add(studentOrderBox);

        controls.add(Box.createHorizontalStrut(20));

        controls.add(makeLabel("Major:"));
        controls.add(majorBox);

        return controls;
    }

    private JComponent buildStudentTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Student Appointment Counts"));

        card.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        return card;
    }

    private void loadStudentMajors() {
        majorBox.removeAllItems();
        majorBox.addItem("All");

        for (String m : service.getMajors()) {
            majorBox.addItem(m);
        }

        majorBox.setSelectedIndex(0);
    }

    private void refreshStudentReport() {
        studentModel.setRowCount(0);

        String majorFilter = (String) majorBox.getSelectedItem();
        boolean mostFirst = "Most appointments".equals(studentOrderBox.getSelectedItem());

        List<StudentRow> rows = service.getStudentReport(majorFilter, mostFirst);
        for (StudentRow r : rows) {
            studentModel.addRow(new Object[]{r.name, r.major, r.studentId, r.appointments});
        }
    }

    // ============================================================
    // Styling helpers (your consistency requirement)
    // ============================================================

    private JButton makeSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(BLUE);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
        return btn;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(GRAY);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        return lbl;
    }

    // ============================================================
    // Data types + service interface
    // ============================================================

    public static class AdvisorRow {
        public final String name;
        public final String department;
        public final int appointments;

        public AdvisorRow(String name, String department, int appointments) {
            this.name = name;
            this.department = department;
            this.appointments = appointments;
        }
    }

    public static class StudentRow {
        public final String name;
        public final String major;
        public final String studentId;
        public final int appointments;

        public StudentRow(String name, String major, String studentId, int appointments) {
            this.name = name;
            this.major = major;
            this.studentId = studentId;
            this.appointments = appointments;
        }
    }

    public interface FacultyReportsService {
        List<String> getDepartments();
        List<String> getMajors();

        List<AdvisorRow> getAdvisorReport(String deptFilter, boolean mostFirst);
        List<StudentRow> getStudentReport(String majorFilter, boolean mostFirst);
    }
}