import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class LoginPanel extends JPanel {

    private static final Color BLUE = new Color(0x005A9C);
    private static final Color GRAY = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE = Color.WHITE;

    private final MyAdviceApp app;
    private final AppBackend backend;

    private final JTextField nameField = new JTextField(22);
    private final JTextField emailField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);

    public LoginPanel(MyAdviceApp app, AppBackend backend) {
        this.app = app;
        this.backend = backend;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenterForm(), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

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
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(WHITE);

        JPanel content = new JPanel();
        content.setBackground(WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel heading = new JLabel("Log in / Sign up", SwingConstants.CENTER);
        heading.setForeground(GRAY);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 30f));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(heading);
        content.add(Box.createVerticalStrut(25));
        content.add(buildForm());
        content.add(Box.createVerticalStrut(18));
        content.add(buildActionButtons());

        outer.add(content);
        return outer;
    }

    private JComponent buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;

        Dimension fieldSize = new Dimension(420, 34);
        nameField.setPreferredSize(fieldSize);
        emailField.setPreferredSize(fieldSize);
        passwordField.setPreferredSize(fieldSize);
        nameField.setFont(nameField.getFont().deriveFont(15f));
        emailField.setFont(emailField.getFont().deriveFont(15f));
        passwordField.setFont(passwordField.getFont().deriveFont(15f));

        addRow(form, gbc, 0, "Full Name:", nameField);
        addRow(form, gbc, 1, "Email:", emailField);
        addRow(form, gbc, 2, "Password:", passwordField);

        JLabel hint = new JLabel("Login uses email + password. Name is only needed for new sign-ups.");
        hint.setForeground(GRAY);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 12f));

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(hint, gbc);

        return form;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        JLabel rowLabel = new JLabel(label);
        rowLabel.setForeground(GRAY);
        rowLabel.setFont(rowLabel.getFont().deriveFont(Font.BOLD, 14f));

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(8, 0, 8, 10);
        form.add(rowLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        form.add(field, gbc);
    }

    private JComponent buildActionButtons() {
        JPanel actions = new JPanel();
        actions.setBackground(WHITE);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));

        Dimension authBtnSize = new Dimension(160, 44);

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        loginRow.setBackground(WHITE);
        JButton loginBtn = new JButton("Log In");
        makeAuthButton(loginBtn, BLUE);
        loginBtn.setPreferredSize(authBtnSize);
        loginBtn.addActionListener(e -> handleLogin());
        loginRow.add(loginBtn);

        JPanel hintRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        hintRow.setBackground(WHITE);
        JLabel signupHint = new JLabel("Not in the system yet?");
        signupHint.setForeground(GRAY);
        signupHint.setFont(signupHint.getFont().deriveFont(Font.PLAIN, 14f));
        hintRow.add(signupHint);

        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        signupRow.setBackground(WHITE);
        JButton signupBtn = new JButton("Sign Up");
        makeAuthButton(signupBtn, GRAY);
        signupBtn.setPreferredSize(authBtnSize);
        signupBtn.addActionListener(e -> handleSignup());
        signupRow.add(signupBtn);

        actions.add(loginRow);
        actions.add(Box.createVerticalStrut(28));
        actions.add(hintRow);
        actions.add(Box.createVerticalStrut(10));
        actions.add(signupRow);
        return actions;
    }

    private JComponent buildBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(WHITE);
        bottom.setBorder(new EmptyBorder(10, 25, 20, 25));

        JButton back = new JButton("Back");
        makeSmallButton(back);
        back.addActionListener(e -> app.showScreen("role_select"));
        bottom.add(back, BorderLayout.EAST);
        return bottom;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both email and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

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

        try {
            UserRecord user = backend.login(email, password, roleStr);
            app.setCurrentUser(user);
            app.showScreen("menu");
        } catch (ApiClient.ApiException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed: " + parseApiError(ex),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Login failed: " + ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter name, email, and password before signing up.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String roleStr = app.getSelectedRole();
        if (roleStr == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select Student or Faculty/Staff first.",
                    "Role Missing",
                    JOptionPane.WARNING_MESSAGE
            );
            app.showScreen("role_select");
            return;
        }

        try {
            backend.register(name, email, password, roleStr);
            JOptionPane.showMessageDialog(
                    this,
                    "Sign-up request created. An admin will need to approve the account before login.",
                    "Sign Up Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (ApiClient.ApiException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sign-up failed: " + parseApiError(ex),
                    "Sign Up Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Sign-up failed: " + ex.getMessage(),
                    "Sign Up Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String parseApiError(ApiClient.ApiException ex) {
        try {
            Object parsed = SimpleJson.parse(ex.responseBody);
            if (parsed instanceof Map<?, ?> map && map.get("error") != null) {
                return String.valueOf(map.get("error"));
            }
        } catch (RuntimeException ignored) {
        }
        return ex.responseBody == null || ex.responseBody.isBlank() ? ex.getMessage() : ex.responseBody;
    }

    private void makeSmallButton(JButton btn) {
        btn.setBackground(YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void makeAuthButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 18, 12, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
    }

    public void resetFields() {
        nameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        emailField.requestFocusInWindow();
    }
}
