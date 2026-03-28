//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//
///*
// * BookingsPanel (GUI-only)
// * ------------------------
// * This screen is for booking an advising appointment.
// *
// * What it does (GUI only):
// * - User selects advisor, date, time
// * - User types a reason
// * - When "Submit Booking" is pressed:
// *   - adds a new row to a "Booking History" table
// *   - shows a message popup
// *
// * What it does NOT do:
// * - No real calendar availability checking
// * - No backend/database saving
// */
//public class BookingsPanel extends JPanel {
//
//    private static final Color BLUE   = new Color(0x005A9C);
//    private static final Color GRAY   = new Color(0x555555);
//    private static final Color YELLOW = new Color(0xFFC72C);
//    private static final Color WHITE  = Color.WHITE;
//
//    private final MyAdviceApp app;
//    private final String currentStudent = "Talha Hanif";
//
//    // Model for booking history table
//    private final DefaultTableModel historyModel;
//
//    public BookingsPanel(MyAdviceApp app) {
//        this.app = app;
//
//        setLayout(new BorderLayout());
//        setBackground(WHITE);
//
//        add(buildHeader(), BorderLayout.NORTH);
//
//        // Create model before building body, because body needs it
//        historyModel = new DefaultTableModel(
//                new String[]{"Advisor", "Date", "Time", "Reason", "Status"}, 0
//        ) {
//            @Override
//            public boolean isCellEditable(int r, int c) { return false; }
//        };
//
//        // Add fake history rows (optional)
//        seedHistory();
//
//        add(buildBody(), BorderLayout.CENTER);
//    }
//
//    private JComponent buildHeader() {
//        JPanel header = new JPanel(new BorderLayout());
//        header.setBackground(BLUE);
//        header.setBorder(new EmptyBorder(18, 25, 18, 25));
//
//        JLabel title = new JLabel("Bookings");
//        title.setForeground(Color.WHITE);
//        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
//
//        JButton back = new JButton("Back to Menu");
//        styleSmallButton(back);
//        back.addActionListener(e -> app.showScreen("menu"));
//
//        header.add(title, BorderLayout.WEST);
//        header.add(back, BorderLayout.EAST);
//        return header;
//    }
//
//    private JComponent buildBody() {
//        JPanel body = new JPanel(new BorderLayout(15, 15));
//        body.setBackground(WHITE);
//        body.setBorder(new EmptyBorder(20, 25, 20, 25));
//
//        // -------- Booking form --------
//        JPanel form = new JPanel(new GridLayout(0, 2, 12, 12));
//        form.setBackground(WHITE);
//
//        JLabel studentLbl = new JLabel("Student:");
//        studentLbl.setForeground(GRAY);
//        studentLbl.setFont(studentLbl.getFont().deriveFont(Font.BOLD, 14f));
//
//        JLabel studentVal = new JLabel(currentStudent);
//        studentVal.setForeground(GRAY);
//        studentVal.setFont(studentVal.getFont().deriveFont(Font.BOLD, 14f));
//
//        // Dropdowns for advisor/date/time
//        JComboBox<String> advisorBox = new JComboBox<>(new String[]{"Dr. A", "Dr. B", "Dr. C"});
//        JComboBox<String> dateBox    = new JComboBox<>(new String[]{"Mar 25, 2026", "Mar 26, 2026", "Mar 27, 2026"});
//        JComboBox<String> timeBox    = new JComboBox<>(new String[]{"10:00", "13:00", "15:30"});
//
//        // Text area for reason
//        JTextArea reason = new JTextArea(3, 20);
//        JScrollPane reasonScroll = new JScrollPane(reason);
//
//        // Add label/value pairs into grid
//        form.add(studentLbl);           form.add(studentVal);
//        form.add(new JLabel("Advisor:")); form.add(advisorBox);
//        form.add(new JLabel("Date:"));    form.add(dateBox);
//        form.add(new JLabel("Time:"));    form.add(timeBox);
//        form.add(new JLabel("Reason:"));  form.add(reasonScroll);
//
//        // -------- Buttons (Submit/Clear) --------
//        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
//        actions.setBackground(WHITE);
//
//        JButton submit = new JButton("Submit Booking");
//        styleActionButton(submit, YELLOW);
//        submit.setForeground(Color.BLACK);
//
//        JButton clear = new JButton("Clear");
//        styleActionButton(clear, GRAY);
//
//        // -------- Booking history table --------
//        JTable historyTable = new JTable(historyModel);
//        JPanel historyCard = new JPanel(new BorderLayout());
//        historyCard.setBackground(WHITE);
//        historyCard.setBorder(BorderFactory.createTitledBorder("Booking History"));
//        historyCard.add(new JScrollPane(historyTable), BorderLayout.CENTER);
//
//        // Submit button behavior: add a row to history table (UI only)
//        submit.addActionListener(e -> {
//            String adv  = (String) advisorBox.getSelectedItem();
//            String date = (String) dateBox.getSelectedItem();
//            String time = (String) timeBox.getSelectedItem();
//            String txt  = reason.getText().trim();
//
//            if (txt.isEmpty()) txt = "(No reason provided)";
//
//            // Add row to history table
//            historyModel.addRow(new Object[]{adv, date, time, txt, "Requested"});
//
//            // Clear reason box
//            reason.setText("");
//
//            JOptionPane.showMessageDialog(this, "UI-only: Booking request added to history.");
//        });
//
//        // Clear button behavior: reset form fields
//        clear.addActionListener(e -> {
//            advisorBox.setSelectedIndex(0);
//            dateBox.setSelectedIndex(0);
//            timeBox.setSelectedIndex(0);
//            reason.setText("");
//        });
//
//        actions.add(clear);
//        actions.add(submit);
//
//        // Put form + buttons in a top container
//        JPanel top = new JPanel(new BorderLayout(10, 10));
//        top.setBackground(WHITE);
//        top.add(form, BorderLayout.CENTER);
//        top.add(actions, BorderLayout.SOUTH);
//
//        // Layout: top form then history below
//        body.add(top, BorderLayout.NORTH);
//        body.add(historyCard, BorderLayout.CENTER);
//
//        return body;
//    }
//
//    private void seedHistory() {
//        // Optional initial rows so the history isn't empty
//        historyModel.addRow(new Object[]{"Dr. B", "Mar 18, 2026", "13:00", "Course planning", "Completed"});
//        historyModel.addRow(new Object[]{"Dr. A", "Mar 20, 2026", "10:00", "Schedule conflict", "Completed"});
//    }
//
//    private void styleSmallButton(JButton btn) {
//        btn.setBackground(YELLOW);
//        btn.setForeground(Color.BLACK);
//        btn.setFocusPainted(false);
//        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//    }
//
//    private void styleActionButton(JButton btn, Color bg) {
//        btn.setBackground(bg);
//        btn.setForeground(YELLOW);
//        btn.setFocusPainted(false);
//        btn.setBorder(new EmptyBorder(12, 14, 12, 14));
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
//    }
//}