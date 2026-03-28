import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentCurriculumPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;

    // ===== Mock “current student” (later comes from login/currentUser) =====
    private final String currentStudentName = "Talha Hanif";
    private final String currentStudentId   = "1001";

    // ====== Service (GUI calls this; backend later replaces it) ======
    private final StudentCurriculumService service;

    private final DefaultListModel<String> completedModel = new DefaultListModel<>();
    private final DefaultListModel<String> suggestedModel = new DefaultListModel<>();
    private final DefaultListModel<String> searchModel    = new DefaultListModel<>();

    private final JList<String> completedList = new JList<>(completedModel);
    private final JList<String> suggestedList = new JList<>(suggestedModel);
    private final JList<String> searchList    = new JList<>(searchModel);

    // Search field + buttons
    private final JTextField searchField = new JTextField(22);

    // Reach out area
    private final JTextArea reachOutBox = new JTextArea(4, 30);

    // Notifications from faculty responses
    private final DefaultListModel<String> notifModel = new DefaultListModel<>();
    private final JList<String> notifList = new JList<>(notifModel);
    private final JTextArea notifView = new JTextArea();

    // General “Advisor Output” (messages, status, etc.)
    private final JTextArea advisorOutput = new JTextArea();

    /*
     * Constructor:
     * app: main CardLayout controller
     * service: mock service for now (later backend)
     */
    public StudentCurriculumPanel(MyAdviceApp app, StudentCurriculumService service) {
        this.app = app;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainSplit(), BorderLayout.CENTER);

        // Load initial data into lists
        loadStudentData();
        refreshNotifications();
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Curriculum Advising (Student)");
        title.setForeground(WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton back = new JButton("Back to Menu");
        makeSmallButton(back);
        back.addActionListener(e -> app.showScreen("menu"));

        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    // Main Split:
    // TOP: main tools (completed, suggested, search, reach out)
    // BOTTOM: advisor output (scrolls, never takes whole screen)
    private JComponent buildMainSplit() {

        JPanel top = buildTopTools();          // completed/suggested/search/reach out
        JScrollPane bottom = buildAdvisorOutput(); // scrolling output

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        split.setResizeWeight(0.72);       // top gets more space than bottom
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setDividerLocation(380);

        return split;
    }

    // Top Tools Area Layout
    private JPanel buildTopTools() {
        JPanel top = new JPanel(new BorderLayout(15, 15));
        top.setBackground(WHITE);
        top.setBorder(new EmptyBorder(20, 25, 20, 25));

        // Top row: Student identity line
        top.add(buildStudentLine(), BorderLayout.NORTH);

        // Center: 2 columns
        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setBackground(WHITE);

        // Left column: Completed + Suggested
        center.add(buildLeftColumn());

        // Right column: Search + Reach Out
        center.add(buildRightColumn());

        top.add(center, BorderLayout.CENTER);
        return top;
    }

    private JComponent buildStudentLine() {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        line.setBackground(WHITE);

        JLabel studentLbl = new JLabel("Student:");
        studentLbl.setForeground(GRAY);
        studentLbl.setFont(studentLbl.getFont().deriveFont(Font.BOLD, 16f));

        JLabel studentVal = new JLabel(currentStudentName + " (" + currentStudentId + ")");
        studentVal.setForeground(GRAY);
        studentVal.setFont(studentVal.getFont().deriveFont(Font.BOLD, 16f));

        line.add(studentLbl);
        line.add(studentVal);
        return line;
    }

    // Left Column: Completed + Suggested
    private JPanel buildLeftColumn() {
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 15));
        left.setBackground(WHITE);

        // Completed Courses card
        left.add(buildListCard("Completed Courses", completedList));

        // Suggested Courses card
        left.add(buildListCard("Suggested Courses", suggestedList));

        // When student clicks a suggested course, check prereq status
        suggestedList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String course = suggestedList.getSelectedValue();
                if (course != null) {
                    checkCoursePrereqs(course);
                }
            }
        });

        return left;
    }

    // Right Column: Search + Reach Out + Notifs
    private JPanel buildRightColumn() {

        // 3 rows: Search + Reach Out + Faculty Responses
        JPanel right = new JPanel(new GridLayout(3, 1, 0, 15));
        right.setBackground(WHITE);

        JPanel searchCard = buildSearchCard();
        JPanel reachCard  = buildReachOutCard();
        JPanel notifCard  = buildNotificationsCard();

        // Give reasonable heights so nothing gets squashed
        searchCard.setPreferredSize(new Dimension(1, 240));
        reachCard.setPreferredSize(new Dimension(1, 200));
        notifCard.setPreferredSize(new Dimension(1, 220));

        right.add(searchCard);
        right.add(reachCard);
        right.add(notifCard);

        return right;
    }

    // Search section
    private JPanel buildSearchCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Search Courses"));

        // Top row: search field + button (GridBagLayout so button never clips)
        JPanel searchRow = new JPanel(new GridBagLayout());
        searchRow.setBackground(WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.insets = new Insets(0, 0, 0, 8);
        g.anchor = GridBagConstraints.WEST;

        // Label
        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        searchRow.add(new JLabel("Course/Keyword:"), g);

        // Text field expands
        g.gridx = 1;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        searchRow.add(searchField, g);

        // Button stays visible at the end
        JButton searchBtn = new JButton("Search");
        makeActionButton(searchBtn, BLUE);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 8, 0, 0);
        searchRow.add(searchBtn, g);

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        // Results list
        JScrollPane resultsScroll = new JScrollPane(searchList);
        resultsScroll.setPreferredSize(new Dimension(1, 240));

        // When student clicks a search result, check prereqs
        searchList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String course = searchList.getSelectedValue();
                if (course != null) {
                    checkCoursePrereqs(course);
                }
            }
        });

        card.add(searchRow, BorderLayout.NORTH);
        card.add(resultsScroll, BorderLayout.CENTER);

        return card;
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        searchModel.clear();

        // Ask service for results (GUI does NOT implement heavy logic)
        List<String> results = service.searchCourses(q);

        if (results.isEmpty()) {
            appendAdvisor("Search: no results for \"" + q + "\"");
            return;
        }

        for (String r : results) searchModel.addElement(r);
        appendAdvisor("Search: found " + results.size() + " result(s) for \"" + q + "\"");
    }

    private void checkCoursePrereqs(String courseCode) {
        // Ask the service if prereqs are met (mock now, backend later)
        PrereqResult result = service.checkPrereqs(currentStudentId, courseCode);

        // Also write to advisor output so student sees history in one place
        appendAdvisor(result.detailMessage);
    }

    // Reach Out section
    private JPanel buildReachOutCard() {
        // BorderLayout with gaps so components don't feel cramped
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Reach Out (Student Inquiry)"));

        JLabel hint = new JLabel("Type your question/inquiry. This will notify faculty/staff.");
        hint.setForeground(GRAY);

        // Ensure text area can be typed in
        reachOutBox.setEditable(true);
        reachOutBox.setEnabled(true);
        reachOutBox.setFocusable(true);
        reachOutBox.setLineWrap(true);
        reachOutBox.setWrapStyleWord(true);
        reachOutBox.setFont(reachOutBox.getFont().deriveFont(14f));
        reachOutBox.setBackground(WHITE);

        // Inner padding so text isn't glued to the edge
        reachOutBox.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane reachScroll = new JScrollPane(reachOutBox);
        reachScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        reachScroll.setPreferredSize(new Dimension(1, 110));

        JButton send = new JButton("Send Inquiry");
        makeActionButton(send, YELLOW);
        send.setForeground(Color.BLACK);

        send.addActionListener(e -> {
            String msg = reachOutBox.getText().trim();
            if (msg.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please type a message before sending.");
                // Put cursor back in the box so user can type immediately
                reachOutBox.requestFocusInWindow();
                return;
            }

            // Store inquiry so faculty/staff can respond (shared store)
            MockInquiryStore.getInstance().addInquiry(currentStudentId, currentStudentName, msg);

            appendAdvisor("Inquiry sent to faculty/staff: " + msg);

            // Clear and refocus so user can type another message easily
            reachOutBox.setText("");
            reachOutBox.requestFocusInWindow();

            // Refresh notifications (in case responses exist already)
            refreshNotifications();

            appendAdvisor("Inquiry sent to faculty/staff: " + msg);

            // Clear and refocus so user can type another message easily
            reachOutBox.setText("");
            reachOutBox.requestFocusInWindow();
        });

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomRow.setBackground(WHITE);
        bottomRow.add(send);

        card.add(hint, BorderLayout.NORTH);
        card.add(reachScroll, BorderLayout.CENTER);
        card.add(bottomRow, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildNotificationsCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Faculty Responses"));

        // Left: list of notifications
        notifList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Right: full message viewer
        notifView.setEditable(false);
        notifView.setLineWrap(true);
        notifView.setWrapStyleWord(true);
        notifView.setFont(notifView.getFont().deriveFont(14f));
        notifView.setBackground(WHITE);
        notifView.setBorder(new EmptyBorder(8, 8, 8, 8));

        notifList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = notifList.getSelectedValue();
                if (selected != null) {
                    notifView.setText(selected);
                    notifView.setCaretPosition(0);
                }
            }
        });

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(notifList),
                new JScrollPane(notifView)
        );
        split.setResizeWeight(0.55);
        split.setDividerSize(8);
        split.setContinuousLayout(true);

        // Top-right refresh button
        JButton refresh = new JButton("Refresh");
        makeActionButton(refresh, BLUE);
        refresh.addActionListener(e -> refreshNotifications());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        top.setBackground(WHITE);
        top.add(refresh);

        card.add(top, BorderLayout.NORTH);
        card.add(split, BorderLayout.CENTER);

        return card;
    }

    // Bottom: Advisor Output
    private JScrollPane buildAdvisorOutput() {
        advisorOutput.setEditable(false);
        advisorOutput.setLineWrap(true);
        advisorOutput.setWrapStyleWord(true);
        advisorOutput.setFont(advisorOutput.getFont().deriveFont(14f));
        advisorOutput.setBackground(WHITE);
        advisorOutput.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(advisorOutput);
        scroll.setBorder(BorderFactory.createTitledBorder("Advisor Output"));
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        return scroll;
    }

    private void appendAdvisor(String msg) {
        advisorOutput.append("• " + msg + "\n");
        advisorOutput.setCaretPosition(advisorOutput.getDocument().getLength());
    }

    private void refreshNotifications() {
        notifModel.clear();

        // Pull responses for this student from the shared store
        var responses = MockInquiryStore.getInstance().getResponsesForStudent(currentStudentId);

        if (responses.isEmpty()) {
            notifModel.addElement("No faculty responses yet.");
            return;
        }

        for (var r : responses) {
            String line =
                    "From: " + r.facultyName + "\n" +
                            "Time: " + r.createdAt + "\n\n" +
                            r.body;
            notifModel.addElement(line);
        }

        notifList.setSelectedIndex(0);
    }

    // ============================================
    // Data loading (GUI-only)
    // ============================================
    private void loadStudentData() {
        completedModel.clear();
        suggestedModel.clear();
        searchModel.clear();
        advisorOutput.setText("");

        for (String c : service.getCompletedCourses(currentStudentId)) {
            completedModel.addElement(c);
        }

        for (String s : service.getSuggestedCourses(currentStudentId)) {
            suggestedModel.addElement(s);
        }

        appendAdvisor("Loaded student curriculum data.");
    }

    // ============================================
    // Styling helpers
    // ============================================
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

    /*
     * StudentCurriculumService
     * ------------------------
     * This is the "bridge" between GUI and backend.
     * GUI uses this interface. Right now you can use a mock implementation.
     * Later, backend dev can implement it using APIs/DB.
     */
    public interface StudentCurriculumService {
        List<String> getCompletedCourses(String studentId);
        List<String> getSuggestedCourses(String studentId);
        List<String> searchCourses(String query);
        PrereqResult checkPrereqs(String studentId, String courseCode);
        void sendBroadcastInquiry(String studentId, String message);
    }

    public static class PrereqResult {
        public final String statusText;
        public final String detailMessage;

        public PrereqResult(String statusText, String detailMessage) {
            this.statusText = statusText;
            this.detailMessage = detailMessage;
        }
    }
}