import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
 * StudentBookingsPanel
 * --------------------
 * Student version of Bookings connected to the SAME shared MockBookingStore
 * used by FacultyBookingsPanel.
 *
 * What changed:
 * - On Submit: creates a MockBookingStore.BookingRequest and stores it in MockBookingStore
 * - "My Booking Requests" table is populated by reading from MockBookingStore
 * - When faculty approves/denies in their panel, student will see updated status/message
 *   when this table refreshes.
 */
public class StudentBookingsPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    // Reference to main app for navigation
    private final MyAdviceApp app;

    // Shared mock store (acts like the database)
    private final MockBookingStore store = MockBookingStore.getInstance();

    // Service for advisor list + descriptions (still fine to keep as mock)
    private final StudentBookingsService service;

    // UI components for booking form
    private final JComboBox<String> advisorBox = new JComboBox<>();
    private final JLabel advisorDescLabel = new JLabel(" ");

    private final JComboBox<String> dayBox = new JComboBox<>(new String[] {
            "Mon", "Tue", "Wed", "Thu", "Fri"
    });

    private final JComboBox<String> timeBox = new JComboBox<>(new String[] {
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
    });

    private final JTextArea reasonBox = new JTextArea(4, 30);

    // Table model for "My Booking Requests"
    private final DefaultTableModel requestsModel;

    // Table showing requests
    private final JTable requestsTable;

    public StudentBookingsPanel(MyAdviceApp app, StudentBookingsService service) {
        this.app = app;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        requestsModel = new DefaultTableModel(new String[] {
                "Request #", "Advisor", "Day", "Time", "Reason", "Status", "Staff Message", "Created"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        requestsTable = new JTable(requestsModel);
        requestsTable.setRowHeight(22);

        add(buildBody(), BorderLayout.CENTER);

        // Load advisors into dropdown
        loadAdvisors();

        // Load this student's requests from the shared store
        refreshMyRequests();
    }

    /*
     * Header bar: title + Back button.
     */
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Bookings (Student)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        styleSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    /*
     * Body layout:
     * - Top: booking form
     * - Bottom: request history table
     */
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(buildBookingFormCard(), BorderLayout.NORTH);
        body.add(buildRequestsTableCard(), BorderLayout.CENTER);

        return body;
    }

    /*
     * Booking form card
     */
    private JComponent buildBookingFormCard() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Book a Meeting"));

        // Student line
        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topLine.setBackground(WHITE);

        JLabel studentLbl = new JLabel("Student:");
        studentLbl.setForeground(GRAY);
        studentLbl.setFont(studentLbl.getFont().deriveFont(Font.BOLD, 14f));

        JLabel studentVal = new JLabel(currentStudentName() + " (" + currentStudentId() + ")");
        studentVal.setForeground(GRAY);
        studentVal.setFont(studentVal.getFont().deriveFont(Font.BOLD, 14f));

        topLine.add(studentLbl);
        topLine.add(studentVal);

        // Form grid
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Advisor label
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Advisor:"), gbc);

        // Advisor dropdown
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(advisorBox, gbc);

        // Advisor description
        advisorDescLabel.setForeground(GRAY);
        advisorDescLabel.setFont(advisorDescLabel.getFont().deriveFont(Font.PLAIN, 12f));

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(advisorDescLabel, gbc);

        // Day
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Day:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        form.add(dayBox, gbc);

        // Time
        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Time:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        form.add(timeBox, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Reason:"), gbc);

        reasonBox.setLineWrap(true);
        reasonBox.setWrapStyleWord(true);
        reasonBox.setFont(reasonBox.getFont().deriveFont(14f));
        reasonBox.setBorder(new EmptyBorder(8, 8, 8, 8));
        reasonBox.setBackground(WHITE);

        JScrollPane reasonScroll = new JScrollPane(reasonBox);
        reasonScroll.setPreferredSize(new Dimension(1, 90));
        reasonScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(reasonScroll, gbc);

        // Buttons row
        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsRow.setBackground(WHITE);

        JButton refresh = new JButton("Refresh");
        styleActionButton(refresh, BLUE);
        refresh.addActionListener(e -> refreshMyRequests());

        JButton submit = new JButton("Submit Request");
        styleActionButton(submit, YELLOW);
        submit.setForeground(Color.BLACK);
        submit.addActionListener(e -> submitRequest());

        buttonsRow.add(refresh);
        buttonsRow.add(submit);

        card.add(topLine, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttonsRow, BorderLayout.SOUTH);

        return card;
    }

    /*
     * Requests table card
     */
    private JComponent buildRequestsTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("My Booking Requests"));

        card.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        return card;
    }

    // ============================================================
    // Advisors (dropdown + description)
    // ============================================================

    private void loadAdvisors() {
        advisorBox.removeAllItems();

        for (AdvisorOption a : service.getAdvisorOptions()) {
            advisorBox.addItem(a.displayName);
        }

        advisorBox.addActionListener(e -> updateAdvisorDescription());

        if (advisorBox.getItemCount() > 0) {
            advisorBox.setSelectedIndex(0);
            updateAdvisorDescription();
        }
    }

    private void updateAdvisorDescription() {
        String selected = (String) advisorBox.getSelectedItem();
        if (selected == null) return;

        AdvisorOption option = service.findAdvisorByDisplayName(selected);
        if (option == null) return;

        advisorDescLabel.setText(option.description);
    }

    // ============================================================
    // Shared store integration
    // ============================================================

    /*
     * Pull this student's requests from MockBookingStore and show them in the table.
     * This is what allows the student to see faculty decisions (approve/deny).
     */
    private void refreshMyRequests() {
        requestsModel.setRowCount(0);

        List<MockBookingStore.BookingRequest> all = store.getAllRequests();
        for (MockBookingStore.BookingRequest r : all) {

            // Only show requests that belong to this student
            if (!currentStudentId().equals(r.studentId)) continue;

            requestsModel.addRow(new Object[] {
                    r.requestId,
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
     * Submit booking request:
     * - Validates form
     * - Creates a BookingRequest
     * - Stores it in MockBookingStore
     * - Refreshes the student's table
     */
    private void submitRequest() {
        String advisor = (String) advisorBox.getSelectedItem();
        String day = (String) dayBox.getSelectedItem();
        String time = (String) timeBox.getSelectedItem();
        String reason = reasonBox.getText().trim();

        if (currentStudentId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Log in as a student before booking.");
            return;
        }

        if (advisor == null || day == null || time == null) {
            JOptionPane.showMessageDialog(this, "Please select advisor, day, and time.");
            return;
        }
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a reason for booking.");
            reasonBox.requestFocusInWindow();
            return;
        }

        // Get a shared request id from the store
        String requestId = store.nextId();

        // Create and store the request in the shared "database"
        MockBookingStore.BookingRequest req =
                new MockBookingStore.BookingRequest(requestId, currentStudentId(), currentStudentName(),
                        advisor, day, time, reason);

        store.addRequest(req);

        // Clear input and refresh table
        reasonBox.setText("");
        reasonBox.requestFocusInWindow();

        refreshMyRequests();

        JOptionPane.showMessageDialog(this, "Booking request submitted. Status: REQUESTED");
    }

    private String currentStudentId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "" : user.id;
    }

    private String currentStudentName() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "Student" : user.name;
    }

    // ============================================================
    // Styling helpers
    // ============================================================

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

    // ============================================================
    // Small model + service interface (same as before)
    // ============================================================

    public static class AdvisorOption {
        public final String displayName;
        public final String description;

        public AdvisorOption(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
    }

    public interface StudentBookingsService {
        List<AdvisorOption> getAdvisorOptions();
        AdvisorOption findAdvisorByDisplayName(String displayName);
    }
}
