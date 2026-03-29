import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoleSelectPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    // Reference to the main app so we can navigate using CardLayout
    private final MyAdviceApp app;

    public RoleSelectPanel(MyAdviceApp app) {
        this.app = app;

        // BorderLayout: header at top, body in center
        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

   // top header
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(20, 25, 20, 25));
        header.setPreferredSize(new Dimension(1, 110));

        JLabel title = new JLabel("myAdvice");
        title.setForeground(WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 44f));

//        JLabel subtitle = new JLabel("Select your role to continue");
//        subtitle.setForeground(WHITE);
//        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 16f));
//        subtitle.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(title, BorderLayout.WEST);
       // header.add(subtitle, BorderLayout.EAST);

        return header;
    }

    private JComponent buildBody() {

        // Outer panel uses GridBagLayout so we can control where content sits
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(WHITE);
        outer.setBorder(new EmptyBorder(10, 40, 40, 40));

        // holds the buttons vertically
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(WHITE);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));


        JButton studentBtn = makeButton("Student", GRAY);
        JButton staffBtn   = makeButton("Faculty / Staff", BLUE);
        Dimension bigSize = new Dimension(520, 70);
        studentBtn.setPreferredSize(bigSize);
        staffBtn.setPreferredSize(bigSize);

        // BoxLayout uses maximum size when expanding components
        studentBtn.setMaximumSize(bigSize);
        staffBtn.setMaximumSize(bigSize);

        // Align buttons to center
        studentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        staffBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add first button
        buttonsPanel.add(studentBtn);

        // gap between buttons
        buttonsPanel.add(Box.createVerticalStrut(26));

        // Add second button
        buttonsPanel.add(staffBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        outer.add(buttonsPanel, gbc);

        // Button actions: store role then go to login screen
        studentBtn.addActionListener(e -> {
            app.setSelectedRole("STUDENT");
            app.showScreen("login");
        });

        staffBtn.addActionListener(e -> {
            app.setSelectedRole("FACULTY_STAFF");
            app.showScreen("login");
        });

        return outer;
    }

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);

        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 22f));
        btn.setBackground(bg);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(20, 20, 20, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }
}