import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

 // Uses AuthService (mock) to validate login for now.

public class LoginPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    // Reference to main page so we can navigate screens using CardLayout
    private final MyAdviceApp app;

    // Mock authentication service (implement backend and database later)
    private final AuthService auth;

    // Input login fields
    private final JTextField nameField = new JTextField(22);
    private final JTextField idField   = new JTextField(22);

    public LoginPanel(MyAdviceApp app, AuthService auth) {
        this.app = app;
        this.auth = auth;

        // BorderLayout:
        // NORTH  = header bar
        // CENTER = login content
        // SOUTH  = bottom bar (Back button)
        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenterForm(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

     // Top blue header bar with "myAdvice".
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        header.setPreferredSize(new Dimension(1, 110));

        JLabel title = new JLabel("myAdvice");
        title.setForeground(WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 44f));

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JComponent buildCenterForm() {

        // Outer panel centers the whole content block
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(WHITE);

        // Content stack (vertical)
        JPanel content = new JPanel();
        content.setBackground(WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 20, 40));

        // Centered "Log in" heading
        JLabel heading = new JLabel("Log in", SwingConstants.CENTER);
        heading.setForeground(GRAY);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 30f));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(heading);
        content.add(Box.createVerticalStrut(25));

        // Input form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        Dimension fieldSize = new Dimension(420, 34);
        nameField.setPreferredSize(fieldSize);
        idField.setPreferredSize(fieldSize);
        nameField.setFont(nameField.getFont().deriveFont(15f));
        idField.setFont(idField.getFont().deriveFont(15f));

        // Row spacing
        Insets rowInsets = new Insets(8, 0, 8, 0);

        // Row 1: Name
        JLabel nameLbl = new JLabel("Name:");
        nameLbl.setForeground(GRAY);
        nameLbl.setFont(nameLbl.getFont().deriveFont(Font.BOLD, 14f));

        gbc.gridy = 0;

        // label cell
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(rowInsets.top, 0, rowInsets.bottom, 10); // small label->field gap
        form.add(nameLbl, gbc);

        // field cell
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = rowInsets;
        form.add(nameField, gbc);

        // Row 2: ID
        JLabel idLbl = new JLabel("ID:");
        idLbl.setForeground(GRAY);
        idLbl.setFont(idLbl.getFont().deriveFont(Font.BOLD, 14f));

        gbc.gridy = 1;

        // label cell
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(rowInsets.top, 0, rowInsets.bottom, 10);
        form.add(idLbl, gbc);

        // field cell
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = rowInsets;
        form.add(idField, gbc);

        content.add(form);
        content.add(Box.createVerticalStrut(18));

        // Centered Login button
        JPanel loginButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginButtonRow.setBackground(WHITE);

        JButton loginBtn = new JButton("Log In");
        makeAuthButton(loginBtn, BLUE);

        // size for authencation buttons
        Dimension authBtnSize = new Dimension(140, 44);
        loginBtn.setPreferredSize(authBtnSize);

        loginBtn.addActionListener(e -> handleLogin());

        loginButtonRow.add(loginBtn);
        content.add(loginButtonRow);

        // Spacing before Sign Up section
        content.add(Box.createVerticalStrut(28));

        JPanel signupTextRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupTextRow.setBackground(WHITE);

        JLabel signupHint = new JLabel("Not in the system?");
        signupHint.setForeground(GRAY);
        signupHint.setFont(signupHint.getFont().deriveFont(Font.PLAIN, 14f));

        signupTextRow.add(signupHint);
        content.add(signupTextRow);

        content.add(Box.createVerticalStrut(10));

        JPanel signupButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupButtonRow.setBackground(WHITE);

        JButton signupBtn = new JButton("Sign Up");
        makeAuthButton(signupBtn, GRAY);

        // Same size as Login button
        signupBtn.setPreferredSize(authBtnSize);

        signupBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Sign up request submitted (UI-only).\nIn the real system, this would be saved as PENDING for Admin approval.",
                    "Sign Up",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        signupButtonRow.add(signupBtn);
        content.add(signupButtonRow);

        // Add the content block to the centered outer panel
        outer.add(content);
        return outer;
    }

     // Bottom bar with Back button on bottom right.

    private JComponent buildBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(WHITE);
        bottom.setBorder(new EmptyBorder(10, 25, 20, 25));

        JButton back = new JButton("Back");
        makeSmallButton(back);

        // Back goes to role select page
        back.addActionListener(e -> app.showScreen("role_select"));
        bottom.add(back, BorderLayout.EAST);
        return bottom;
    }

    /*
     * Attempts login using AuthService (mock).
     * If successful -> go to main menu.
     * If failed -> popup telling user to sign up.
     */
    private void handleLogin() {
        String name = nameField.getText().trim();
        String id   = idField.getText().trim();

        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both Name and ID.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Role comes from your role select page
        String roleStr = app.getSelectedRole();
        if (roleStr == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Role not selected. Please choose Student or Faculty/Staff first.",
                    "Role Missing",
                    JOptionPane.WARNING_MESSAGE
            );
            app.showScreen("role_select");
            return;
        }

        UserRole role = roleStr.equals("STUDENT") ? UserRole.STUDENT : UserRole.FACULTY_STAFF;

        String result = auth.login(name, id, role);

        if (!"OK".equals(result)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed: " + result + "\n\nPlease Sign Up if you are not in the system.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Success -> main menu
        app.showScreen("menu");
    }

    // small button for back button
    private void makeSmallButton(JButton btn) {
        btn.setBackground(YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // for Auth buttons
    private void makeAuthButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 18, 12, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
    }

    public void resetFields() {
        // Clear inputs
        nameField.setText("");
        idField.setText("");

        // Put cursor back in Name field for convenience
        nameField.requestFocusInWindow();
    }
}