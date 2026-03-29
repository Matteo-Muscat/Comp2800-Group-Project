import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdminPanel extends JPanel {

    private static final Color BLUE = new Color(0x005A9C);
    private static final Color GRAY = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE = Color.WHITE;

    private final MyAdviceApp app;
    private final AppBackend backend;

    private final JTabbedPane tabs = new JTabbedPane();
    private final JLabel messageLabel = new JLabel("Manage prerequisites and pending sign-up approvals.");

    private final DefaultTableModel prereqModel = new DefaultTableModel(
            new String[]{"Course", "Prerequisite"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable prereqTable = new JTable(prereqModel);
    private final JComboBox<String> courseBox = new JComboBox<>();
    private final JComboBox<String> prereqBox = new JComboBox<>();
    private final Map<String, BackendModels.Course> coursesByCode = new LinkedHashMap<>();

    private final DefaultTableModel signupModel = new DefaultTableModel(
            new String[]{"Request ID", "User ID", "Name", "Email", "Role", "Requested At"}, 0
    ) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable signupTable = new JTable(signupModel);

    public AdminPanel(MyAdviceApp app, AppBackend backend) {
        this.app = app;
        this.backend = backend;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        prereqTable.setRowHeight(22);
        signupTable.setRowHeight(22);

        loadCourses();
        refreshPrereqTable();
        refreshSignupTable();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Administering the System (Faculty/Staff)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        styleSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        messageLabel.setForeground(GRAY);
        messageLabel.setBorder(new EmptyBorder(0, 0, 6, 0));

        tabs.addTab("Prerequisites", buildPrereqTab());
        tabs.addTab("Sign-in Approvals", buildSignupTab());

        body.add(messageLabel, BorderLayout.NORTH);
        body.add(tabs, BorderLayout.CENTER);
        return body;
    }

    private JComponent buildPrereqTab() {
        JPanel tab = new JPanel(new BorderLayout(12, 12));
        tab.setBackground(WHITE);

        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        controls.add(makeLabel("Course:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        controls.add(courseBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        controls.add(makeLabel("Prerequisite:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        controls.add(prereqBox, gbc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(WHITE);

        JButton addBtn = new JButton("Add Prerequisite");
        makeActionButton(addBtn, BLUE);
        addBtn.addActionListener(e -> addPrerequisite());

        JButton removeBtn = new JButton("Remove Selected");
        makeActionButton(removeBtn, GRAY);
        removeBtn.addActionListener(e -> removeSelectedPrerequisite());

        JButton refreshBtn = new JButton("Refresh");
        makeActionButton(refreshBtn, YELLOW);
        refreshBtn.setForeground(Color.BLACK);
        refreshBtn.addActionListener(e -> {
            loadCourses();
            refreshPrereqTable();
            setMessage("Prerequisites refreshed.");
        });

        actions.add(refreshBtn);
        actions.add(removeBtn);
        actions.add(addBtn);

        tab.add(controls, BorderLayout.NORTH);
        tab.add(new JScrollPane(prereqTable), BorderLayout.CENTER);
        tab.add(actions, BorderLayout.SOUTH);
        return tab;
    }

    private JComponent buildSignupTab() {
        JPanel tab = new JPanel(new BorderLayout(12, 12));
        tab.setBackground(WHITE);

        JTextArea noteBox = new JTextArea(4, 30);
        noteBox.setLineWrap(true);
        noteBox.setWrapStyleWord(true);
        noteBox.setFont(noteBox.getFont().deriveFont(14f));
        noteBox.setBorder(new EmptyBorder(8, 8, 8, 8));
        noteBox.setBackground(WHITE);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(WHITE);

        JButton refreshBtn = new JButton("Refresh");
        makeActionButton(refreshBtn, BLUE);
        refreshBtn.addActionListener(e -> refreshSignupTable());

        JButton denyBtn = new JButton("Deny");
        makeActionButton(denyBtn, GRAY);
        denyBtn.addActionListener(e -> denySelectedSignup(noteBox.getText().trim()));

        JButton approveBtn = new JButton("Approve");
        makeActionButton(approveBtn, YELLOW);
        approveBtn.setForeground(Color.BLACK);
        approveBtn.addActionListener(e -> approveSelectedSignup());

        actions.add(refreshBtn);
        actions.add(denyBtn);
        actions.add(approveBtn);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBackground(WHITE);
        bottom.setBorder(BorderFactory.createTitledBorder("Decision Note (used when denying)"));
        bottom.add(new JScrollPane(noteBox), BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.SOUTH);

        tab.add(new JScrollPane(signupTable), BorderLayout.CENTER);
        tab.add(bottom, BorderLayout.SOUTH);
        return tab;
    }

    private void loadCourses() {
        coursesByCode.clear();
        courseBox.removeAllItems();
        prereqBox.removeAllItems();

        for (BackendModels.Course course : backend.getCourses()) {
            coursesByCode.put(course.courseCode(), course);
        }

        for (String code : coursesByCode.keySet()) {
            courseBox.addItem(code);
            prereqBox.addItem(code);
        }
    }

    private void refreshPrereqTable() {
        prereqModel.setRowCount(0);
        for (String courseCode : coursesByCode.keySet()) {
            List<BackendModels.Prerequisite> prerequisites = backend.getPrerequisites(courseCode);
            for (BackendModels.Prerequisite prerequisite : prerequisites) {
                prereqModel.addRow(new Object[]{prerequisite.courseCode(), prerequisite.prereqCourseCode()});
            }
        }
    }

    private void addPrerequisite() {
        String courseCode = (String) courseBox.getSelectedItem();
        String prereqCourseCode = (String) prereqBox.getSelectedItem();

        if (courseCode == null || prereqCourseCode == null) {
            JOptionPane.showMessageDialog(this, "Select both course and prerequisite.");
            return;
        }
        if (courseCode.equals(prereqCourseCode)) {
            JOptionPane.showMessageDialog(this, "A course cannot be its own prerequisite.");
            return;
        }

        backend.addPrerequisite(courseCode, prereqCourseCode);
        refreshPrereqTable();
        setMessage("Added prerequisite " + prereqCourseCode + " to " + courseCode + ".");
    }

    private void removeSelectedPrerequisite() {
        int row = prereqTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a prerequisite row first.");
            return;
        }

        String courseCode = String.valueOf(prereqModel.getValueAt(row, 0));
        String prereqCourseCode = String.valueOf(prereqModel.getValueAt(row, 1));
        backend.removePrerequisite(courseCode, prereqCourseCode);
        refreshPrereqTable();
        setMessage("Removed prerequisite " + prereqCourseCode + " from " + courseCode + ".");
    }

    private void refreshSignupTable() {
        signupModel.setRowCount(0);
        for (BackendModels.PendingSigninRequest request : backend.getPendingSigninRequests()) {
            signupModel.addRow(new Object[]{
                    request.requestId(),
                    request.userId(),
                    request.userName(),
                    request.userEmail(),
                    request.userRole(),
                    request.requestedAt()
            });
        }
        setMessage("Loaded " + signupModel.getRowCount() + " pending sign-up request(s).");
    }

    private void approveSelectedSignup() {
        Integer requestId = selectedSignupRequestId();
        if (requestId == null) {
            JOptionPane.showMessageDialog(this, "Select a sign-up request first.");
            return;
        }

        backend.approveSigninRequest(requestId, currentUserId());
        refreshSignupTable();
        setMessage("Approved sign-up request " + requestId + ".");
    }

    private void denySelectedSignup(String note) {
        Integer requestId = selectedSignupRequestId();
        if (requestId == null) {
            JOptionPane.showMessageDialog(this, "Select a sign-up request first.");
            return;
        }
        if (note == null || note.isBlank()) {
            JOptionPane.showMessageDialog(this, "Please enter a decision note before denying.");
            return;
        }

        backend.denySigninRequest(requestId, currentUserId(), note);
        refreshSignupTable();
        setMessage("Denied sign-up request " + requestId + ".");
    }

    private Integer selectedSignupRequestId() {
        int row = signupTable.getSelectedRow();
        if (row == -1) {
            return null;
        }
        Object value = signupModel.getValueAt(row, 0);
        return value instanceof Number number ? number.intValue() : Integer.parseInt(String.valueOf(value));
    }

    private int currentUserId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? 0 : Integer.parseInt(user.id);
    }

    private void setMessage(String message) {
        messageLabel.setText(message);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(GRAY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        return label;
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
