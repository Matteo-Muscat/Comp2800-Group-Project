import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class FacultyCurriculumPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;
    private final FacultyCurriculumService service;

    private final DefaultListModel<String> completedModel = new DefaultListModel<>();
    private final DefaultListModel<String> suggestedModel = new DefaultListModel<>();
    private final DefaultListModel<String> searchModel    = new DefaultListModel<>();

    private final JList<String> completedList = new JList<>(completedModel);
    private final JList<String> suggestedList = new JList<>(suggestedModel);
    private final JList<String> searchList    = new JList<>(searchModel);

    private final JComboBox<String> studentBox = new JComboBox<>();
    private final JTextField searchField = new JTextField(22);

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

        loadStudents();
        refreshInbox();
    }

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

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(15, 15));
        body.setBackground(WHITE);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(buildTopRow(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setBackground(WHITE);
        center.add(buildLeftColumn());
        center.add(buildRightColumn());

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

    private JComponent buildLeftColumn() {
        JPanel left = new JPanel(new BorderLayout(0, 15));
        left.setBackground(WHITE);

        JPanel topStack = new JPanel(new GridLayout(2, 1, 0, 15));
        topStack.setBackground(WHITE);
        topStack.add(buildListCard("Completed Courses (Selected Student)", completedList));
        topStack.add(buildListCard("Suggested Courses", suggestedList));

        left.add(topStack, BorderLayout.CENTER);
        left.add(buildSearchCard(), BorderLayout.SOUTH);
        return left;
    }

    private JPanel buildSearchCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Search Courses"));

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
        for (String result : results) {
            searchModel.addElement(result);
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No results found.");
        }
    }

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

        inboxList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inboxList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value.summary());
            lbl.setOpaque(true);
            lbl.setBorder(new EmptyBorder(6, 8, 6, 8));
            lbl.setBackground(isSelected ? new Color(230, 230, 230) : WHITE);
            lbl.setForeground(GRAY);
            return lbl;
        });

        inboxList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                InboxMessage msg = inboxList.getSelectedValue();
                if (msg != null) {
                    messageView.setText(msg.fullText());
                    messageView.setCaretPosition(0);
                }
            }
        });

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

        service.sendResponse(selected, currentFacultyName(), reply);
        replyBox.setText("");
        JOptionPane.showMessageDialog(this, "Response sent.");
        refreshInbox();
    }

    private void loadStudents() {
        studentBox.removeAllItems();
        for (String student : service.getStudentList()) {
            studentBox.addItem(student);
        }

        if (studentBox.getItemCount() > 0) {
            studentBox.setSelectedIndex(0);
            loadStudentData((String) studentBox.getSelectedItem());
        }
    }

    private void loadStudentData(String studentIdOrName) {
        completedModel.clear();
        suggestedModel.clear();

        for (String course : service.getCompletedCourses(studentIdOrName)) {
            completedModel.addElement(course);
        }

        for (String course : service.getSuggestedCourses(studentIdOrName)) {
            suggestedModel.addElement(course);
        }
    }

    private void refreshInbox() {
        inboxModel.clear();
        List<InboxMessage> messages = service.getInboxMessages();

        for (InboxMessage message : messages) {
            inboxModel.addElement(message);
        }

        if (!messages.isEmpty()) {
            inboxList.setSelectedIndex(0);
        } else {
            messageView.setText("");
        }
    }

    private String currentFacultyName() {
        UserRecord currentUser = app.getCurrentUser();
        return currentUser == null ? "Faculty/Staff" : currentUser.name;
    }

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

    public static class InboxMessage {
        public final String inquiryId;
        public final String fromStudentId;
        public final String fromStudentName;
        public final String body;
        public final String time;

        public InboxMessage(String inquiryId, String fromStudentId, String fromStudentName, String body, String time) {
            this.inquiryId = inquiryId;
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
            if (s == null) {
                return "";
            }
            return s.length() <= 40 ? s : s.substring(0, 40) + "...";
        }
    }

    public interface FacultyCurriculumService {
        List<String> getStudentList();
        List<String> getCompletedCourses(String studentIdOrName);
        List<String> getSuggestedCourses(String studentIdOrName);
        List<String> searchCourses(String query);
        List<InboxMessage> getInboxMessages();
        void sendResponse(InboxMessage msg, String facultyName, String reply);
    }
}
