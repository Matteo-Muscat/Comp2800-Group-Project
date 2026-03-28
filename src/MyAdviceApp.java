import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

 // MyAdviceApp is the main window (JFrame) for the project.

 // It uses CardLayout to switch between screens ("cards").

public class MyAdviceApp extends JFrame {

    // System colors
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    // Card names
    private static final String ROLE_SELECT = "role_select";
    private static final String LOGIN       = "login";
    private static final String MENU        = "menu";
    private static final String CURRICULUM  = "curriculum";
    private static final String SCHEDULING  = "scheduling";
    private static final String BOOKINGS    = "bookings";
    private static final String ADMIN       = "admin";
    private static final String REPORTS     = "reports";

     // Stores what the user chose on the opening screen.
     // "STUDENT" or "FACULTY_STAFF"

    private String selectedRole = null;

    // CardLayout lets us show one panel at a time
    private final CardLayout cards = new CardLayout();

    // root is the container panel holding all screens
    private final JPanel root = new JPanel(cards);

    private LoginPanel loginPanel;

    private JPanel menuPanel;
    private JPanel curriculumPanel;
    private JPanel schedulingPanel;
    private JPanel bookingsPanel;
    private JPanel reportsPanel;

    public MyAdviceApp() {
        super("myAdvice - Student Advising System (Pilot)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Window size
        setSize(950, 600);

        // Center on screen
        setLocationRelativeTo(null);

        // Create mock auth service (acts like database for now)
        AuthService authService = new AuthService();

        // Add screens to CardLayout

        // 1) Opening Role Select Screen
        root.add(new RoleSelectPanel(this), ROLE_SELECT);

        // 2) Login Screen
        // This panel will read app.getSelectedRole() to know which role was chosen.
        loginPanel = new LoginPanel(this, authService);
        root.add(loginPanel, LOGIN);

        // 3) Menu Screen
        menuPanel = buildMenuScreen();
        root.add(menuPanel, MENU);

        // 4) Module Screens
        // Curriculum screen
        curriculumPanel = new StudentCurriculumPanel(this, new MockStudentCurriculumService());
        root.add(curriculumPanel, CURRICULUM);
        // Scheduling Screen
        schedulingPanel = new StudentSchedulingPanel(this);
        root.add(schedulingPanel, SCHEDULING);
        // Bookings Screen
        bookingsPanel = new StudentBookingsPanel(this, new MockStudentBookingsService());
        root.add(bookingsPanel, BOOKINGS);
        // Administrating the System Screen
        MockPrereqAdminService prereqAdminService = new MockPrereqAdminService();
        root.add(new AdminPanel(this, authService, prereqAdminService), ADMIN);
        // Reports Screen
        reportsPanel= new StudentReportsPanel(this, new StoreBasedStudentReportsService());
        root.add(reportsPanel, REPORTS);

        // Put root card panel inside the JFrame
        setContentPane(root);

        // Show the opening screen first
        showScreen(ROLE_SELECT);
    }

    private String currentScreen = null; // add this field at class level

    // Navigate between screens
    public void showScreen(String name) {

        // If we are leaving the login screen, clear it
        if (LOGIN.equals(name) && loginPanel != null) {
            loginPanel.resetFields();
        }

        // If we are going to the menu, rebuild it so it reflects the selected role
        if (MENU.equals(name)) {
            root.remove(menuPanel);
            menuPanel = buildMenuScreen();
            root.add(menuPanel, MENU);
            root.revalidate();
            root.repaint();
        }

        // If we are going to Curriculum page, rebuild it so it reflects the selected role
        if (CURRICULUM.equals(name)) {
            root.remove(curriculumPanel);
            curriculumPanel = buildCurriculumPanel();
            root.add(curriculumPanel, CURRICULUM);
            root.revalidate();
            root.repaint();
        }
        // If we are going to Scheduling page, rebuild it so it reflects the selected role
        if (SCHEDULING.equals(name)) {
            root.remove(schedulingPanel);
            schedulingPanel = buildSchedulingPanel();
            root.add(schedulingPanel, SCHEDULING);
            root.revalidate();
            root.repaint();
        }
        // If we are going to Bookings page, rebuild it so it reflects the selected role
        if (BOOKINGS.equals(name)) {
            root.remove(bookingsPanel);
            bookingsPanel = buildBookingsPanel();
            root.add(bookingsPanel, BOOKINGS);
            root.revalidate();
            root.repaint();
        }
        // If we are going to Reports page, rebuild it so it reflects the selected role
        if (REPORTS.equals(name)) {
            root.remove(reportsPanel);
            reportsPanel = buildReportsPanel();
            root.add(reportsPanel, REPORTS);
            root.revalidate();
            root.repaint();
        }

        cards.show(root, name);
    }
    // Called by RoleSelectPanel when user chooses Student or Faculty/Staff
    public void setSelectedRole(String role) {
        this.selectedRole = role;
    }

    // Used by LoginPanel to know what role was selected
    public String getSelectedRole() {
        return selectedRole;
    }


    // Menu Screen (4/5 buttons)
    private JPanel buildMenuScreen() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);

       // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        header.setPreferredSize(new Dimension(1, 110));

        JLabel title = new JLabel("myAdvice");
        title.setForeground(WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 44f));

        header.add(title, BorderLayout.WEST);

        // Center
        JPanel center = new JPanel(new GridLayout(0, 1, 15, 15));
        center.setBackground(WHITE);
        center.setBorder(new EmptyBorder(25, 25, 25, 25));

        center.add(makeNavButton(" Curriculum Advising", GRAY, CURRICULUM));
        center.add(makeNavButton(" Scheduling", BLUE, SCHEDULING));
        center.add(makeNavButton(" Bookings", GRAY, BOOKINGS));
        // Only show Administering the System for Faculty/Staff (hide for Student)
        String role = getSelectedRole();
        if ("FACULTY_STAFF".equals(role)) {
            center.add(makeNavButton(" Administering the System", BLUE, ADMIN));
            center.add(makeNavButton(" Reports", GRAY, REPORTS));
        }
         else {
            center.add(makeNavButton(" Reports", BLUE, REPORTS));
        }


        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(WHITE);
        footer.setBorder(new EmptyBorder(10, 25, 20, 25));

        JButton logout = new JButton("Log Out");
        makeSmallButton(logout);

        // Log out = clear the selected role, then go back to the role select screen
        logout.addActionListener(e -> {
            setSelectedRole(null); // clears role
            showScreen("role_select"); // back to first screen
        });

        footer.add(logout);

        panel.add(header, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildCurriculumPanel() {
        if ("FACULTY_STAFF".equals(getSelectedRole())) {
            return new FacultyCurriculumPanel(this, new MockFacultyInboxService());
        }
        return new StudentCurriculumPanel(this, new MockStudentCurriculumService());
    }

    private JPanel buildSchedulingPanel() {
        if ("FACULTY_STAFF".equals(getSelectedRole())) {
            return new FacultySchedulingPanel(this);
        }
        return new StudentSchedulingPanel(this);
    }

    private JPanel buildBookingsPanel() {
        if ("FACULTY_STAFF".equals(getSelectedRole())) {
            return new FacultyBookingsPanel(this);
        }
        return new StudentBookingsPanel(this, new MockStudentBookingsService());
    }

    private JPanel buildReportsPanel() {
        if ("FACULTY_STAFF".equals(getSelectedRole())) {
            return new FacultyReportsPanel(this, new StoreBasedFacultyReportsService());
        }
        return new StudentReportsPanel(this, new StoreBasedStudentReportsService());
    }

    // Navigation buttons to traverse through application
    private JButton makeNavButton(String text, Color bg, String targetCard) {
        JButton btn = new JButton(text);

        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 20f));
        btn.setBackground(bg);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(18, 18, 18, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> showScreen(targetCard)); // changes screen when clicked

        return btn;
    }

    // small button for exit
    private void makeSmallButton(JButton btn) {
        btn.setBackground(YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyAdviceApp().setVisible(true));
    }
}