import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentBookingsPanel extends JPanel {

    private static final Color BLUE = new Color(0x005A9C);
    private static final Color GRAY = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE = Color.WHITE;

    private final MyAdviceApp app;
    private final AppBackend backend;

    private final JComboBox<String> advisorBox = new JComboBox<>();
    private final JLabel advisorDescLabel = new JLabel(" ");
    private final JComboBox<String> dayBox = new JComboBox<>(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"});
    private final JComboBox<String> timeBox = new JComboBox<>(new String[]{
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
    });
    private final JTextArea reasonBox = new JTextArea(4, 30);

    private final DefaultTableModel requestsModel;
    private final JTable requestsTable;

    private final Map<String, BackendModels.Advisor> advisorsByLabel = new LinkedHashMap<>();

    public StudentBookingsPanel(MyAdviceApp app, AppBackend backend) {
        this.app = app;
        this.backend = backend;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        requestsModel = new DefaultTableModel(new String[]{
                "Request #", "Advisor", "Date", "Time", "Reason", "Status", "Staff Message", "Created"
        }, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        requestsTable = new JTable(requestsModel);
        requestsTable.setRowHeight(22);

        add(buildBody(), BorderLayout.CENTER);

        loadAdvisors();
        refreshMyRequests();
    }

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

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(buildBookingFormCard(), BorderLayout.NORTH);
        body.add(buildRequestsTableCard(), BorderLayout.CENTER);
        return body;
    }

    private JComponent buildBookingFormCard() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Book a Meeting"));

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

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Advisor:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(advisorBox, gbc);

        advisorDescLabel.setForeground(GRAY);
        advisorDescLabel.setFont(advisorDescLabel.getFont().deriveFont(Font.PLAIN, 12f));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(advisorDescLabel, gbc);

        gbc.insets = new Insets(8, 0, 8, 10);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Day:"), gbc);

        gbc.gridx = 1;
        form.add(dayBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        form.add(new JLabel("Time:"), gbc);

        gbc.gridx = 1;
        form.add(timeBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        form.add(new JLabel("Reason:"), gbc);

        reasonBox.setLineWrap(true);
        reasonBox.setWrapStyleWord(true);
        reasonBox.setFont(reasonBox.getFont().deriveFont(14f));
        reasonBox.setBorder(new EmptyBorder(8, 8, 8, 8));
        reasonBox.setBackground(WHITE);

        JScrollPane reasonScroll = new JScrollPane(reasonBox);
        reasonScroll.setPreferredSize(new Dimension(1, 90));

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(reasonScroll, gbc);

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

    private JComponent buildRequestsTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("My Booking Requests"));
        card.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        return card;
    }

    private void loadAdvisors() {
        advisorBox.removeAllItems();
        advisorsByLabel.clear();

        for (BackendModels.Advisor advisor : backend.getAdvisors(null)) {
            String label = advisor.displayName() + " - " + advisor.categoryName();
            advisorsByLabel.put(label, advisor);
            advisorBox.addItem(label);
        }

        advisorBox.addActionListener(e -> updateAdvisorDescription());

        if (advisorBox.getItemCount() > 0) {
            advisorBox.setSelectedIndex(0);
            updateAdvisorDescription();
        } else {
            advisorDescLabel.setText("No advisors available from the backend.");
        }
    }

    private void updateAdvisorDescription() {
        String selected = (String) advisorBox.getSelectedItem();
        BackendModels.Advisor advisor = advisorsByLabel.get(selected);
        advisorDescLabel.setText(advisor == null ? " " : "Category: " + advisor.categoryName());
    }

    private void refreshMyRequests() {
        requestsModel.setRowCount(0);

        if (currentStudentId().isEmpty()) {
            return;
        }

        List<BackendModels.Appointment> appointments;
        try {
            appointments = backend.getAppointmentsForStudent(Integer.parseInt(currentStudentId()));
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load booking requests:\n" + friendlyError(ex),
                    "Bookings Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        for (BackendModels.Appointment appointment : appointments) {
            String staffMessage = appointment.suggestionMessage() == null ? "" : appointment.suggestionMessage();
            requestsModel.addRow(new Object[]{
                    appointment.appointmentId(),
                    appointment.advisorName(),
                    appointment.requestedDate(),
                    ApiDataMapper.compactTimeRange(appointment.requestedStartTime(), appointment.requestedEndTime()),
                    appointment.reason(),
                    appointment.status(),
                    staffMessage,
                    appointment.createdAt()
            });
        }
    }

    private void submitRequest() {
        String advisorLabel = (String) advisorBox.getSelectedItem();
        String day = (String) dayBox.getSelectedItem();
        String time = (String) timeBox.getSelectedItem();
        String reason = reasonBox.getText().trim();

        if (currentStudentId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Log in as a student before booking.");
            return;
        }
        if (advisorLabel == null || day == null || time == null) {
            JOptionPane.showMessageDialog(this, "Please select advisor, day, and time.");
            return;
        }
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a reason for booking.");
            reasonBox.requestFocusInWindow();
            return;
        }

        BackendModels.Advisor advisor = advisorsByLabel.get(advisorLabel);
        LocalDate requestedDate = ApiDataMapper.nextDateForShortDay(day);
        LocalTime startTime = ApiDataMapper.parseHourMinute(time);

        try {
            backend.submitAppointmentRequest(
                    Integer.parseInt(currentStudentId()),
                    advisor.advisorId(),
                    requestedDate,
                    startTime,
                    reason
            );
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Booking request failed:\n" + friendlyError(ex),
                    "Bookings Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        reasonBox.setText("");
        refreshMyRequests();
        JOptionPane.showMessageDialog(this, "Booking request submitted.");
    }

    private String currentStudentId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "" : user.id;
    }

    private String currentStudentName() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "Student" : user.name;
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

    private String friendlyError(RuntimeException ex) {
        if (ex instanceof ApiClient.ApiException apiEx) {
            return apiEx.responseBody == null || apiEx.responseBody.isBlank() ? apiEx.getMessage() : apiEx.responseBody;
        }
        return ex.getMessage() == null ? ex.toString() : ex.getMessage();
    }
}
