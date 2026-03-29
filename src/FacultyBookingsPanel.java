import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacultyBookingsPanel extends JPanel {

    private static final Color BLUE = new Color(0x005A9C);
    private static final Color GRAY = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE = Color.WHITE;

    private final MyAdviceApp app;
    private final AppBackend backend;

    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{
            "Requested", "All", "Approved", "Denied", "Cancelled"
    });

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Request #", "Student", "Advisor", "Date", "Time", "Reason", "Status", "Staff Message", "Created"},
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JTextArea staffMessageBox = new JTextArea(4, 30);

    public FacultyBookingsPanel(MyAdviceApp app, AppBackend backend) {
        this.app = app;
        this.backend = backend;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        table.setRowHeight(22);
        filterBox.setSelectedItem("Requested");
        filterBox.addActionListener(e -> refreshTable());
        refreshTable();
    }

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

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(buildRequestsCard(), BorderLayout.CENTER);
        body.add(buildDecisionCard(), BorderLayout.SOUTH);
        return body;
    }

    private JComponent buildRequestsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Booking Requests"));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(WHITE);

        JLabel filterLbl = new JLabel("Show:");
        filterLbl.setForeground(GRAY);
        filterLbl.setFont(filterLbl.getFont().deriveFont(Font.BOLD, 14f));

        left.add(filterLbl);
        left.add(filterBox);

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

    private JComponent buildDecisionCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Approve / Deny"));

        JLabel hint = new JLabel("Select a request above. The note is used as the denial suggestion message.");
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

    private void refreshTable() {
        model.setRowCount(0);

        if (currentUserId().isEmpty()) {
            return;
        }

        boolean pendingOnly = "Requested".equals(filterBox.getSelectedItem());
        List<BackendModels.Appointment> appointments =
                backend.getAppointmentsForAdvisorUser(Integer.parseInt(currentUserId()), pendingOnly);

        for (BackendModels.Appointment appointment : appointments) {
            if (!matchesFilter(appointment.status(), String.valueOf(filterBox.getSelectedItem()))) {
                continue;
            }

            String message = appointment.suggestionMessage() == null ? "" : appointment.suggestionMessage();
            model.addRow(new Object[]{
                    appointment.appointmentId(),
                    appointment.studentName() + " (" + appointment.studentId() + ")",
                    appointment.advisorName(),
                    appointment.requestedDate(),
                    ApiDataMapper.compactTimeRange(appointment.requestedStartTime(), appointment.requestedEndTime()),
                    appointment.reason(),
                    appointment.status(),
                    message,
                    appointment.createdAt()
            });
        }
    }

    private boolean matchesFilter(String status, String filter) {
        if ("All".equals(filter)) {
            return true;
        }
        return filter != null && filter.equalsIgnoreCase(status);
    }

    private void approveSelected() {
        Integer appointmentId = selectedAppointmentId();
        if (appointmentId == null) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }

        backend.approveAppointment(appointmentId);
        staffMessageBox.setText("");
        refreshTable();
        JOptionPane.showMessageDialog(this, "Request " + appointmentId + " approved.");
    }

    private void denySelected() {
        Integer appointmentId = selectedAppointmentId();
        if (appointmentId == null) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }

        String msg = staffMessageBox.getText().trim();
        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type an alternate suggestion.");
            staffMessageBox.requestFocusInWindow();
            return;
        }

        backend.denyAppointment(appointmentId, msg);
        staffMessageBox.setText("");
        refreshTable();
        JOptionPane.showMessageDialog(this, "Request " + appointmentId + " denied with suggestion.");
    }

    private Integer selectedAppointmentId() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return null;
        }
        Object value = model.getValueAt(row, 0);
        return value instanceof Number number ? number.intValue() : Integer.parseInt(String.valueOf(value));
    }

    private String currentUserId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "" : user.id;
    }

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
