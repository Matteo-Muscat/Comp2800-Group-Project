import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
 * FacultyBookingsPanel
 * --------------------
 * Faculty/Staff version of Bookings.
 *
 * Updated:
 * - Adds a status filter dropdown:
 *      - Requested (default)
 *      - All
 *      - Approved
 *      - Denied
 * - Table refresh respects the selected filter
 *
 * Shared data:
 * - Uses MockBookingStore (same store student uses) so decisions update the student view.
 */
public class FacultyBookingsPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;

    // Shared mock store (acts like DB)
    private final MockBookingStore store = MockBookingStore.getInstance();

    // Filter dropdown
    private final JComboBox<String> filterBox = new JComboBox<>(new String[] {
            "Requested", "All", "Approved", "Denied"
    });

    // Table model showing booking requests
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Request #", "Student", "Advisor", "Day", "Time", "Reason", "Status", "Staff Message", "Created"},
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private final JTable table = new JTable(model);

    // Staff message box for deny/suggestion or approve note
    private final JTextArea staffMessageBox = new JTextArea(4, 30);

    public FacultyBookingsPanel(MyAdviceApp app) {
        this.app = app;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        table.setRowHeight(22);

        // Default view: Requested
        filterBox.setSelectedItem("Requested");

        // Auto-refresh whenever filter changes
        filterBox.addActionListener(e -> refreshTable());

        // Load requests into table initially
        refreshTable();
    }

    /*
     * Header bar: title + Back to Menu
     */
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Bookings (Faculty/Staff)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        makeSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    /*
     * Body:
     * - Requests table card (with filter + refresh)
     * - Decision card (approve/deny + suggestion message)
     */
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(buildRequestsCard(), BorderLayout.CENTER);
        body.add(buildDecisionCard(), BorderLayout.SOUTH);

        return body;
    }

    /*
     * Table card with filter dropdown and refresh button.
     */
    private JComponent buildRequestsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Booking Requests"));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);

        // Left: filter controls
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(WHITE);

        JLabel filterLbl = new JLabel("Show:");
        filterLbl.setForeground(GRAY);
        filterLbl.setFont(filterLbl.getFont().deriveFont(Font.BOLD, 14f));

        left.add(filterLbl);
        left.add(filterBox);

        // Right: refresh button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(WHITE);

        JButton refresh = new JButton("Refresh");
        makeActionButton(refresh, BLUE);
        refresh.addActionListener(e -> refreshTable());

        right.add(refresh);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        return card;
    }

    /*
     * Approve/Deny card:
     * - Staff types a message
     * - Approve uses message as optional note
     * - Deny requires message (alternate suggestion)
     */
    private JComponent buildDecisionCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Approve / Deny"));

        JLabel hint = new JLabel("Select a request above. Add a message (required for Deny).");
        hint.setForeground(GRAY);

        staffMessageBox.setLineWrap(true);
        staffMessageBox.setWrapStyleWord(true);
        staffMessageBox.setFont(staffMessageBox.getFont().deriveFont(14f));
        staffMessageBox.setBorder(new EmptyBorder(8, 8, 8, 8));
        staffMessageBox.setBackground(WHITE);

        JScrollPane msgScroll = new JScrollPane(staffMessageBox);
        msgScroll.setPreferredSize(new Dimension(1, 95));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setBackground(WHITE);

        JButton approve = new JButton("Approve");
        makeActionButton(approve, YELLOW);
        approve.setForeground(Color.BLACK);

        JButton deny = new JButton("Deny + Suggest");
        makeActionButton(deny, GRAY);

        approve.addActionListener(e -> approveSelected());
        deny.addActionListener(e -> denySelected());

        actions.add(deny);
        actions.add(approve);

        card.add(hint, BorderLayout.NORTH);
        card.add(msgScroll, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    /*
     * Refresh table with current filter.
     */
    private void refreshTable() {
        model.setRowCount(0);

        String filter = (String) filterBox.getSelectedItem(); // Requested/All/Approved/Denied
        List<MockBookingStore.BookingRequest> requests = store.getAllRequests();

        for (MockBookingStore.BookingRequest r : requests) {

            // Apply filter
            if (!matchesFilter(r.status, filter)) continue;

            model.addRow(new Object[]{
                    r.requestId,
                    r.studentName + " (" + r.studentId + ")",
                    r.advisor,
                    r.day,
                    r.time,
                    r.reason,
                    r.status,
                    r.staffMessage,
                    r.createdAt
            });
        }
    }

    /*
     * Returns true if a request status matches the selected filter.
     */
    private boolean matchesFilter(String status, String filter) {
        if ("All".equals(filter)) return true;
        if ("Requested".equals(filter)) return "REQUESTED".equals(status);
        if ("Approved".equals(filter)) return "APPROVED".equals(status);
        if ("Denied".equals(filter)) return "DENIED".equals(status);
        return true;
    }

    /*
     * Approve selected request.
     */
    private void approveSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }

        String requestId = (String) model.getValueAt(row, 0);
        MockBookingStore.BookingRequest req = store.findById(requestId);
        if (req == null) return;

        String note = staffMessageBox.getText().trim();
        if (note.isEmpty()) note = "Approved. See you then.";

        req.status = "APPROVED";
        req.staffMessage = note;

        staffMessageBox.setText("");
        refreshTable();

        JOptionPane.showMessageDialog(this, "Request " + requestId + " approved.");
    }

    /*
     * Deny selected request and require suggestion message.
     */
    private void denySelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }

        String requestId = (String) model.getValueAt(row, 0);
        MockBookingStore.BookingRequest req = store.findById(requestId);
        if (req == null) return;

        String msg = staffMessageBox.getText().trim();
        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please type an alternate suggestion (e.g., \"Try Wed at 2:00pm\").");
            staffMessageBox.requestFocusInWindow();
            return;
        }

        req.status = "DENIED";
        req.staffMessage = msg;

        staffMessageBox.setText("");
        refreshTable();

        JOptionPane.showMessageDialog(this, "Request " + requestId + " denied with suggestion.");
    }

    // ============================================================
    // Button styling helpers
    // ============================================================

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