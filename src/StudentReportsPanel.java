import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentReportsPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    // Reference to main app so we can navigate back to menu
    private final MyAdviceApp app;

    // Mock service that provides advisor report data (replace with backend later)
    private final StudentReportsService service;

    // Sort dropdown: most/least
    private final JComboBox<String> sortBox = new JComboBox<>(
            new String[]{"Most appointments", "Least appointments"}
    );

    // Department filter dropdown
    private final JComboBox<String> deptBox = new JComboBox<>();

    // Table model for advisor report
    private final DefaultTableModel advisorModel = new DefaultTableModel(
            new String[]{"Advisor Name", "Department", "Appointments"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false; // report table is display-only
        }
    };

    private final JTable advisorTable = new JTable(advisorModel);

    public StudentReportsPanel(MyAdviceApp app, StudentReportsService service) {
        this.app = app;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        // Make table a bit easier to read
        advisorTable.setRowHeight(22);

        // Load departments + initial report
        loadDepartments();
        refreshAdvisorReport();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Reports (Student)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = makeSmallButton("Back to Menu");
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(buildControlsRow(), BorderLayout.NORTH);
        body.add(buildAdvisorTableCard(), BorderLayout.CENTER);

        return body;
    }

    /*
     * Top controls:
     * - Sort dropdown
     * - Department filter dropdown
     */
    private JComponent buildControlsRow() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.anchor = GridBagConstraints.WEST;

        // Sort label
        gbc.gridx = 0;
        controls.add(makeLabel("Order:"), gbc);

        // Sort dropdown
        gbc.gridx = 1;
        controls.add(sortBox, gbc);

        // Department label
        gbc.gridx = 2;
        controls.add(makeLabel("Department:"), gbc);

        // Department dropdown
        gbc.gridx = 3;
        controls.add(deptBox, gbc);

        // If user changes sort or department, auto-refresh
        sortBox.addActionListener(e -> refreshAdvisorReport());
        deptBox.addActionListener(e -> refreshAdvisorReport());

        return controls;
    }

    private JComponent buildAdvisorTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Faculty / Advisor Information"));

        card.add(new JScrollPane(advisorTable), BorderLayout.CENTER);
        return card;
    }

    /*
     * Loads department options into the filter dropdown.
     * Adds an "All" option at the top.
     */
    private void loadDepartments() {
        deptBox.removeAllItems();

        // First item = All departments
        deptBox.addItem("All");

        // Get departments from service
        for (String dept : service.getDepartments()) {
            deptBox.addItem(dept);
        }

        // Default selection
        deptBox.setSelectedIndex(0);
    }

    /*
     * Refreshes the advisor report table using current filters:
     * - selected department
     * - selected ordering (most/least)
     */
    private void refreshAdvisorReport() {
        advisorModel.setRowCount(0); // clear table

        String selectedDept = (String) deptBox.getSelectedItem();
        String selectedSort = (String) sortBox.getSelectedItem();

        boolean sortMostFirst = "Most appointments".equals(selectedSort);

        // Ask service for advisor report rows
        List<AdvisorRow> rows = service.getAdvisorReport(selectedDept, sortMostFirst);

        // Fill table
        for (AdvisorRow r : rows) {
            advisorModel.addRow(new Object[]{r.name, r.department, r.appointments});
        }
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(GRAY);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        return lbl;
    }

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

    // ============================================================
    // Service + data types
    // ============================================================

    /*
     * AdvisorRow
     * ----------
     * One row of report output: advisor name, department, appointment count.
     */
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

    /*
     * StudentReportsService
     * ---------------------
     * Service interface so GUI doesn't depend on backend/database.
     * We'll implement this with a mock service for now.
     */
    public interface StudentReportsService {
        List<String> getDepartments();
        List<AdvisorRow> getAdvisorReport(String departmentFilter, boolean sortMostFirst);
    }
}