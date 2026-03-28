import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
 * AdminPanel
 * ----------
 * Faculty-only Admin page.
 *
 * Contains:
 * 1) Prerequisite structures (add/edit/delete)
 * 2) Approve/Deny Sign-ins (from AuthService pending requests)
 *
 * Note:
 * - Timetable management is already handled in FacultySchedulingPanel, so we don't repeat it here.
 */
public class AdminPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;

    // Uses your existing mock auth service
    private final AuthService auth;

    // Mock prereq service for GUI-only prereq CRUD
    private final MockPrereqAdminService prereqService;

    // Tabs
    private final JTabbedPane tabs = new JTabbedPane();

    // ===== Prereq tab components =====
    private final DefaultTableModel prereqModel = new DefaultTableModel(
            new String[]{"Course", "Prerequisite"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable prereqTable = new JTable(prereqModel);

    private final JComboBox<String> courseBox = new JComboBox<>();
    private final JComboBox<String> prereqBox = new JComboBox<>();

    // ===== Sign-in approvals components =====
    private final DefaultTableModel signupModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Role", "Approved?"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable signupTable = new JTable(signupModel);

    public AdminPanel(MyAdviceApp app, AuthService auth, MockPrereqAdminService prereqService) {
        this.app = app;
        this.auth = auth;
        this.prereqService = prereqService;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        prereqTable.setRowHeight(22);
        signupTable.setRowHeight(22);

        // Load initial data into both tabs
        loadCourseDropdowns();
        refreshPrereqTable();
        refreshSignupTable();
    }

    // ================= Header =================
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Administering the System (Faculty/Staff)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        makeSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    // ================= Body =================
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        tabs.addTab("Prerequisites", buildPrereqTab());
        tabs.addTab("Sign-in Approvals", buildSignupTab());

        body.add(tabs, BorderLayout.CENTER);
        return body;
    }

    // ============================================================
    // TAB 1: Prerequisites CRUD
    // ============================================================
    private JComponent buildPrereqTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(WHITE);

        tab.add(buildPrereqControls(), BorderLayout.NORTH);
        tab.add(wrapTable("Course Prerequisite Structures", prereqTable), BorderLayout.CENTER);

        return tab;
    }

    private JComponent buildPrereqControls() {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        controls.add(makeLabel("Course:"), gbc);

        gbc.gridx = 1;
        controls.add(courseBox, gbc);

        gbc.gridx = 2;
        controls.add(makeLabel("Prerequisite:"), gbc);

        gbc.gridx = 3;
        controls.add(prereqBox, gbc);

        JButton add = new JButton("Add");
        makeActionButton(add, BLUE);

        JButton edit = new JButton("Edit Selected");
        makeActionButton(edit, GRAY);

        JButton delete = new JButton("Delete Selected");
        makeActionButton(delete, GRAY);

        JButton refresh = new JButton("Refresh");
        makeActionButton(refresh, BLUE);

        add.addActionListener(e -> addPrereq());
        edit.addActionListener(e -> editSelectedPrereq());
        delete.addActionListener(e -> deleteSelectedPrereq());
        refresh.addActionListener(e -> {
            loadCourseDropdowns();
            refreshPrereqTable();
        });

        gbc.gridx = 4;
        gbc.insets = new Insets(0, 10, 0, 0);
        controls.add(add, gbc);

        gbc.gridx = 5;
        controls.add(edit, gbc);

        gbc.gridx = 6;
        controls.add(delete, gbc);

        gbc.gridx = 7;
        controls.add(refresh, gbc);

        return controls;
    }

    private void loadCourseDropdowns() {
        courseBox.removeAllItems();
        prereqBox.removeAllItems();

        for (String c : prereqService.getAllCourses()) {
            courseBox.addItem(c);
            prereqBox.addItem(c);
        }
    }

    private void refreshPrereqTable() {
        prereqModel.setRowCount(0);
        List<String[]> rows = prereqService.getAllPrereqPairs();

        for (String[] r : rows) {
            prereqModel.addRow(new Object[]{r[0], r[1]});
        }
    }

    private void addPrereq() {
        String course = (String) courseBox.getSelectedItem();
        String pre = (String) prereqBox.getSelectedItem();

        if (course == null || pre == null) {
            JOptionPane.showMessageDialog(this, "Select both Course and Prerequisite.");
            return;
        }
        if (course.equals(pre)) {
            JOptionPane.showMessageDialog(this, "A course cannot be a prerequisite of itself.");
            return;
        }

        prereqService.addPrereq(course, pre);
        refreshPrereqTable();
    }

    private void deleteSelectedPrereq() {
        int row = prereqTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a prerequisite row to delete.");
            return;
        }

        String course = String.valueOf(prereqModel.getValueAt(row, 0));
        String pre = String.valueOf(prereqModel.getValueAt(row, 1));

        prereqService.removePrereq(course, pre);
        refreshPrereqTable();
    }

    private void editSelectedPrereq() {
        int row = prereqTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a prerequisite row to edit.");
            return;
        }

        String oldCourse = String.valueOf(prereqModel.getValueAt(row, 0));
        String oldPre = String.valueOf(prereqModel.getValueAt(row, 1));

        String newCourse = (String) courseBox.getSelectedItem();
        String newPre = (String) prereqBox.getSelectedItem();

        if (newCourse == null || newPre == null) return;

        prereqService.replacePair(oldCourse, oldPre, newCourse, newPre);
        refreshPrereqTable();
    }

    // ============================================================
    // TAB 2: Sign-in Approvals (AuthService pendingRequests)
    // ============================================================
    private JComponent buildSignupTab() {
        JPanel tab = new JPanel(new BorderLayout(15, 15));
        tab.setBackground(WHITE);

        tab.add(buildSignupControls(), BorderLayout.NORTH);
        tab.add(wrapTable("Pending Sign-in Requests", signupTable), BorderLayout.CENTER);

        return tab;
    }

    private JComponent buildSignupControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setBackground(WHITE);

        JButton approve = new JButton("Approve Selected");
        makeActionButton(approve, YELLOW);
        approve.setForeground(Color.BLACK);

        JButton deny = new JButton("Deny Selected");
        makeActionButton(deny, GRAY);

        JButton refresh = new JButton("Refresh");
        makeActionButton(refresh, BLUE);

        approve.addActionListener(e -> approveSelected());
        deny.addActionListener(e -> denySelected());
        refresh.addActionListener(e -> refreshSignupTable());

        controls.add(refresh);
        controls.add(deny);
        controls.add(approve);

        return controls;
    }

    private void refreshSignupTable() {
        signupModel.setRowCount(0);

        // AuthService pending requests
        for (UserRecord u : auth.getPendingRequests()) {
            signupModel.addRow(new Object[]{
                    u.id,
                    u.name,
                    u.role,
                    u.approved ? "YES" : "NO"
            });
        }
    }

    private void approveSelected() {
        int row = signupTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sign-in request to approve.");
            return;
        }

        String id = String.valueOf(signupModel.getValueAt(row, 0));
        auth.approve(id);

        refreshSignupTable();
        JOptionPane.showMessageDialog(this, "Approved sign-in for ID: " + id);
    }

    private void denySelected() {
        int row = signupTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sign-in request to deny.");
            return;
        }

        String id = String.valueOf(signupModel.getValueAt(row, 0));
        auth.deny(id);

        refreshSignupTable();
        JOptionPane.showMessageDialog(this, "Denied sign-in for ID: " + id);
    }

    // ============================================================
    // Helpers
    // ============================================================
    private JPanel wrapTable(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(GRAY);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));
        return lbl;
    }

    // For consistency with your naming style:
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