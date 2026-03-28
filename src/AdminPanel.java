import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/*
 * AdminPanel (GUI-only)
 * ---------------------
 * Admin is mostly for Staff/Faculty in SAS.
 *
 * For your GUI role, it's still useful to:
 * - Have a screen that exists
 * - Show what will go here later
 *
 * This panel acts as a placeholder with a clear message.
 */
public class AdminPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;

    public AdminPanel(MyAdviceApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Administering the System");
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
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(30, 30, 30, 30));

        // This message explains why it is a placeholder
        JLabel msg = new JLabel(
                "<html><b>UI-only demo:</b> Admin tools are typically Staff/Faculty access.<br>" +
                        "Later: manage prerequisites, timetables, transcripts, and profiles.</html>"
        );
        msg.setForeground(GRAY);
        msg.setFont(msg.getFont().deriveFont(18f));

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
}