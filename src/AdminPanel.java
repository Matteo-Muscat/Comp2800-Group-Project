import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;
    private final AuthService authService;

    private final DefaultTableModel pendingModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Role", "Approval Status"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable pendingTable = new JTable(pendingModel);

    public AdminPanel(MyAdviceApp app, AuthService authService) {
        this.app = app;
        this.authService = authService;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        pendingTable.setRowHeight(22);
        refreshPendingRequests();
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
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel msg = new JLabel(
                "<html>Approve or deny sign-up requests so new users can access the advising system.</html>"
        );
        msg.setForeground(GRAY);
        msg.setFont(msg.getFont().deriveFont(16f));

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(WHITE);
        tableCard.setBorder(BorderFactory.createTitledBorder("Pending Sign-Up Requests"));
        tableCard.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setBackground(WHITE);

        JButton refresh = new JButton("Refresh");
        styleActionButton(refresh, BLUE);
        refresh.addActionListener(e -> refreshPendingRequests());

        JButton deny = new JButton("Deny");
        styleActionButton(deny, GRAY);
        deny.addActionListener(e -> denySelected());

        JButton approve = new JButton("Approve");
        styleActionButton(approve, YELLOW);
        approve.setForeground(Color.BLACK);
        approve.addActionListener(e -> approveSelected());

        actions.add(refresh);
        actions.add(deny);
        actions.add(approve);

        body.add(msg, BorderLayout.NORTH);
        body.add(tableCard, BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);

        return body;
    }

    private void refreshPendingRequests() {
        pendingModel.setRowCount(0);

        List<UserRecord> pending = authService.getPendingRequests();
        for (UserRecord record : pending) {
            pendingModel.addRow(new Object[]{
                    record.id,
                    record.name,
                    record.role == UserRole.STUDENT ? "Student" : "Faculty/Staff",
                    record.approved ? "Approved" : "Pending"
            });
        }
    }

    private void approveSelected() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }

        String id = String.valueOf(pendingModel.getValueAt(row, 0));
        authService.approve(id);
        refreshPendingRequests();
        JOptionPane.showMessageDialog(this, "Approved user " + id + ".");
    }

    private void denySelected() {
        int row = pendingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }

        String id = String.valueOf(pendingModel.getValueAt(row, 0));
        authService.deny(id);
        refreshPendingRequests();
        JOptionPane.showMessageDialog(this, "Denied user " + id + ".");
    }

    private void styleSmallButton(JButton btn) {
        btn.setBackground(YELLOW);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleActionButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
    }
}
