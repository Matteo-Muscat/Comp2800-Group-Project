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
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        tabs.addTab("Prerequisites", buildPrereqTab());
        tabs.addTab("Sign-in Approvals", buildSignupTab());

        body.add(msg, BorderLayout.NORTH);

        return body;
    }

    private void styleSmallButton(JButton btn) {
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