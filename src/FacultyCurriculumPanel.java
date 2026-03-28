import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/*
 * FacultyCurriculumPanel
 * ----------------------
 * Faculty/Staff version of Curriculum Advising.
 *
 * Features (Faculty/Staff):
 * 1) Search courses (catalog search - GUI-only)
 * 2) View a student's completed courses (select student -> load list)
 * 3) Suggested courses (GUI-only list)
 * 4) Advise/Respond:
 *    - Shows incoming student inquiries (notifications/messages)
 *    - Allows faculty/staff to type and send a response
 *
 * NOTE:
 * - This is GUI-first. No backend required.
 * - Uses a mock inbox service (later becomes database + API).
 */
public class FacultyCurriculumPanel extends JPanel {

    // ---- Color scheme (matches your app) ----
    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;

    // Service provides course catalog + student completion + inbox messages
    private final FacultyCurriculumService service;

    // ===== Left-side models =====
    private final DefaultListModel<String> completedModel = new DefaultListModel<>();
    private final DefaultListModel<String> suggestedModel = new DefaultListModel<>();
    private final DefaultListModel<String> searchModel    = new DefaultListModel<>();

    private final JList<String> completedList = new JList<>(completedModel);
    private final JList<String> suggestedList = new JList<>(suggestedModel);
    private final JList<String> searchList    = new JList<>(searchModel);

    // Student selector (faculty chooses which student to view)
    private final JComboBox<String> studentBox = new JComboBox<>();

    // Search controls
    private final JTextField searchField = new JTextField(22);

    // ===== Inbox / Respond controls =====
    private final DefaultListModel<InboxMessage> inboxModel = new DefaultListModel<>();
    private final JList<InboxMessage> inboxList = new JList<>(inboxModel);

    private final JTextArea messageView = new JTextArea();
    private final JTextArea replyBox = new JTextArea(4, 30);

    public FacultyCurriculumPanel(MyAdviceApp app, FacultyCurriculumService service) {
        this.app = app;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        // Load initial data
        loadStudents();
        refreshInbox();
    }

    // =========================
    // Header
    // =========================
    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Curriculum Advising (Faculty/Staff)");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        makeSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    // =========================
    // Body layout
    // =========================
    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top: student selector + refresh inbox
        body.add(buildTopRow(), BorderLayout.NORTH);

        // Center: two columns
        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setBackground(WHITE);

        center.add(buildLeftColumn());   // completed/suggested/search
        center.add(buildRightColumn());  // inbox + respond

        body.add(center, BorderLayout.CENTER);
        return body;
    }

    private JComponent buildTopRow() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setBackground(WHITE);

        JLabel lbl = new JLabel("View Student:");
        lbl.setForeground(GRAY);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));

        studentBox.addActionListener(e -> loadStudentData((String) studentBox.getSelectedItem()));

        left.add(lbl);
        left.add(studentBox);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(WHITE);

        JButton refresh = new JButton("Refresh Inbox");
        makeActionButton(refresh, BLUE);
        refresh.addActionListener(e -> refreshInbox());

        right.add(refresh);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    // =========================
    // Left column (student academic view)
    // =========================
    private JComponent buildLeftColumn() {
        JPanel left = new JPanel(new BorderLayout(0, 15));
        left.setBackground(WHITE);

        // Top: Completed + Suggested (stacked)
        JPanel topStack = new JPanel(new GridLayout(2, 1, 0, 15));
        topStack.setBackground(WHITE);
        topStack.add(buildListCard("Completed Courses (Selected Student)", completedList));
        topStack.add(buildListCard("Suggested Courses (GUI-only)", suggestedList));

        // Bottom: Search
        JPanel searchCard = buildSearchCard();

        left.add(topStack, BorderLayout.CENTER);
        left.add(searchCard, BorderLayout.SOUTH);

        return left;
    }

    private JPanel buildSearchCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Search Courses"));

        // Search row
        JPanel row = new JPanel(new GridBagLayout());
        row.setBackground(WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.insets = new Insets(0, 0, 0, 8);
        row.add(new JLabel("Course/Keyword:"), g);

        g.gridx = 1;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        row.add(searchField, g);

        JButton searchBtn = new JButton("Search");
        makeActionButton(searchBtn, BLUE);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 8, 0, 0);
        row.add(searchBtn, g);

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        // Results list
        JScrollPane results = new JScrollPane(searchList);
        results.setPreferredSize(new Dimension(1, 160));

        card.add(row, BorderLayout.NORTH);
        card.add(results, BorderLayout.CENTER);

        return card;
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        searchModel.clear();

        List<String> results = service.searchCourses(q);
        for (String r : results) searchModel.addElement(r);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found.");
        }
    }

    // =========================
    // Right column (inbox + respond)
    // =========================
    private JComponent buildRightColumn() {
        JPanel right = new JPanel(new BorderLayout(0, 15));
        right.setBackground(WHITE);

        right.add(buildInboxCard(), BorderLayout.CENTER);
        right.add(buildRespondCard(), BorderLayout.SOUTH);

        return right;
    }

    private JPanel buildInboxCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Student Messages / Notifications"));

        // Make message objects display nicely in the list
        inboxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inboxList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value.summary());
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(6, 8, 6, 8));
            lbl.setBackground(isSelected ? new Color(230, 230, 230) : WHITE);
            lbl.setForeground(GRAY);
            return lbl;
        });

        // When selecting a message, show full content
        inboxList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                InboxMessage msg = inboxList.getSelectedValue();
                if (msg != null) {
                    messageView.setText(msg.fullText());
                    messageView.setCaretPosition(0);
                }
            }
        });

        // Message view area
        messageView.setEditable(false);
        messageView.setLineWrap(true);
        messageView.setWrapStyleWord(true);
        messageView.setFont(messageView.getFont().deriveFont(14f));
        messageView.setBorder(new EmptyBorder(8, 8, 8, 8));
        messageView.setBackground(WHITE);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(inboxList),
                new JScrollPane(messageView)
        );
        split.setResizeWeight(0.55);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setDividerLocation(220);

        card.add(split, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRespondCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Respond / Advise"));

        JLabel hint = new JLabel("Select a message above, then type a response.");
        hint.setForeground(GRAY);

        replyBox.setEditable(true);
        replyBox.setLineWrap(true);
        replyBox.setWrapStyleWord(true);
        replyBox.setFont(replyBox.getFont().deriveFont(14f));
        replyBox.setBorder(new EmptyBorder(8, 8, 8, 8));
        replyBox.setBackground(WHITE);

        JScrollPane replyScroll = new JScrollPane(replyBox);
        replyScroll.setPreferredSize(new Dimension(1, 110));

        JButton send = new JButton("Send Response");
        makeActionButton(send, YELLOW);
        send.setForeground(Color.BLACK);

        send.addActionListener(e -> sendResponse());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setBackground(WHITE);
        bottom.add(send);

        card.add(hint, BorderLayout.NORTH);
        card.add(replyScroll, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private void sendResponse() {
        InboxMessage selected = inboxList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a student message first.");
            return;
        }

        String reply = replyBox.getText().trim();
        if (reply.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type a response first.");
            replyBox.requestFocusInWindow();
            return;
        }

        // Store faculty response so the student can see it in their notification box
        MockInquiryStore.getInstance().addResponse(
                selected.fromStudentId,      // we use studentId to attach response
                "Faculty/Staff",             // display name for who replied (you can change later)
                reply
        );

// Keep existing behavior (optional): still let service do whatever it currently does
        service.sendResponse(selected, reply);

        replyBox.setText("");
        JOptionPane.showMessageDialog(this, "Response sent (GUI-only).");

        // Refresh inbox to reflect any updates
        refreshInbox();
    }

    // =========================
    // Data loading
    // =========================
    private void loadStudents() {
        studentBox.removeAllItems();
        for (String s : service.getStudentList()) {
            studentBox.addItem(s);
        }

        if (studentBox.getItemCount() > 0) {
            studentBox.setSelectedIndex(0);
            loadStudentData((String) studentBox.getSelectedItem());
        }
    }

    private void loadStudentData(String studentIdOrName) {
        completedModel.clear();
        suggestedModel.clear();

        // Get completed courses
        for (String c : service.getCompletedCourses(studentIdOrName)) {
            completedModel.addElement(c);
        }

        // Suggested courses (GUI-only list)
        for (String s : service.getSuggestedCourses(studentIdOrName)) {
            suggestedModel.addElement(s);
        }
    }

    private void refreshInbox() {
        inboxModel.clear();
        List<InboxMessage> msgs = service.getInboxMessages();

        for (InboxMessage m : msgs) {
            inboxModel.addElement(m);
        }

        // Auto-select first message (if any) so the view isn't blank
        if (!msgs.isEmpty()) {
            inboxList.setSelectedIndex(0);
        } else {
            messageView.setText("");
        }
    }

    // =========================
    // Styling helpers
    // =========================
    private JPanel buildListCard(String title, JList<String> list) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.add(new JScrollPane(list), BorderLayout.CENTER);
        return card;
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

    // ============================================================
    // Data types + service interface
    // ============================================================

    /*
     * InboxMessage
     * ------------
     * Represents a student inquiry/notification that faculty/staff can respond to.
     */
    public static class InboxMessage {
        public final String fromStudentId;
        public final String fromStudentName;
        public final String body;
        public final String time;

        public InboxMessage(String fromStudentId, String fromStudentName, String body, String time) {
            this.fromStudentId = fromStudentId;
            this.fromStudentName = fromStudentName;
            this.body = body;
            this.time = time;
        }

        public String summary() {
            return fromStudentName + " (" + fromStudentId + "): " + preview(body);
        }

        public String fullText() {
            return "From: " + fromStudentName + " (" + fromStudentId + ")\n"
                    + "Time: " + time + "\n\n"
                    + body;
        }

        private String preview(String s) {
            if (s == null) return "";
            return s.length() <= 40 ? s : s.substring(0, 40) + "...";
        }
    }

    /*
     * FacultyCurriculumService
     * ------------------------
     * GUI calls this interface. Mock now, backend later.
     */
    public interface FacultyCurriculumService {
        List<String> getStudentList();
        List<String> getCompletedCourses(String studentIdOrName);
        List<String> getSuggestedCourses(String studentIdOrName);
        List<String> searchCourses(String query);

        List<InboxMessage> getInboxMessages();
        void sendResponse(InboxMessage msg, String reply);
    }
}