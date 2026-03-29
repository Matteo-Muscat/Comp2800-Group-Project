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
    private final StudentCurriculumService service;

    private final DefaultListModel<String> completedModel = new DefaultListModel<>();
    private final DefaultListModel<String> suggestedModel = new DefaultListModel<>();
    private final DefaultListModel<String> searchModel    = new DefaultListModel<>();

    private final JList<String> completedList = new JList<>(completedModel);
    private final JList<String> suggestedList = new JList<>(suggestedModel);
    private final JList<String> searchList    = new JList<>(searchModel);

    private final JTextField searchField = new JTextField(22);
    private final JTextArea reachOutBox = new JTextArea(4, 30);

    private final DefaultListModel<String> notifModel = new DefaultListModel<>();
    private final JList<String> notifList = new JList<>(notifModel);
    private final JTextArea notifView = new JTextArea();
    private final JTextArea advisorOutput = new JTextArea();

    public StudentCurriculumPanel(MyAdviceApp app, StudentCurriculumService service) {
        this.app = app;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainSplit(), BorderLayout.CENTER);

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

    private JComponent buildMainSplit() {
        JPanel top = buildTopTools();
        JScrollPane bottom = buildAdvisorOutput();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, bottom);
        split.setResizeWeight(0.72);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setDividerLocation(380);

        return split;
    }

    private JPanel buildTopTools() {
        JPanel top = new JPanel(new BorderLayout(15, 15));
        top.setBackground(WHITE);
        top.setBorder(new EmptyBorder(20, 25, 20, 25));

        top.add(buildStudentLine(), BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 15, 0));
        center.setBackground(WHITE);
        center.add(buildLeftColumn());
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

        JLabel studentVal = new JLabel(currentStudentName() + " (" + currentStudentId() + ")");
        studentVal.setForeground(GRAY);
        studentVal.setFont(studentVal.getFont().deriveFont(Font.BOLD, 16f));

        line.add(studentLbl);
        line.add(studentVal);
        return line;
    }

    private JPanel buildLeftColumn() {
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 15));
        left.setBackground(WHITE);

        left.add(buildListCard("Completed Courses", completedList));
        left.add(buildListCard("Suggested Courses", suggestedList));

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

    private JPanel buildRightColumn() {
        JPanel right = new JPanel(new GridLayout(3, 1, 0, 15));
        right.setBackground(WHITE);

        JPanel searchCard = buildSearchCard();
        JPanel reachCard = buildReachOutCard();
        JPanel notifCard = buildNotificationsCard();

        searchCard.setPreferredSize(new Dimension(1, 240));
        reachCard.setPreferredSize(new Dimension(1, 200));
        notifCard.setPreferredSize(new Dimension(1, 220));

        right.add(searchCard);
        right.add(reachCard);
        right.add(notifCard);

        return right;
    }

    private JPanel buildSearchCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Search Courses"));

        JPanel searchRow = new JPanel(new GridBagLayout());
        searchRow.setBackground(WHITE);

        GridBagConstraints g = new GridBagConstraints();
        g.gridy = 0;
        g.insets = new Insets(0, 0, 0, 8);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        searchRow.add(new JLabel("Course/Keyword:"), g);

        g.gridx = 1;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        searchRow.add(searchField, g);

        JButton searchBtn = new JButton("Search");
        makeActionButton(searchBtn, BLUE);

        g.gridx = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.insets = new Insets(0, 8, 0, 0);
        searchRow.add(searchBtn, g);

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        JScrollPane resultsScroll = new JScrollPane(searchList);
        resultsScroll.setPreferredSize(new Dimension(1, 240));

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

        List<String> results = service.searchCourses(q);
        if (results.isEmpty()) {
            appendAdvisor("Search: no results for \"" + q + "\"");
            return;
        }

        for (String result : results) {
            searchModel.addElement(result);
        }
        appendAdvisor("Search: found " + results.size() + " result(s) for \"" + q + "\"");
    }

    private void checkCoursePrereqs(String courseDisplay) {
        if (currentStudentId().isEmpty()) {
            appendAdvisor("No logged-in student is available for prerequisite checking.");
            return;
        }

        PrereqResult result = service.checkPrereqs(currentStudentId(), courseDisplay);
        appendAdvisor(result.detailMessage);
    }

    private JPanel buildReachOutCard() {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder("Reach Out (Student Inquiry)"));

        JLabel hint = new JLabel("Type your question/inquiry. This will notify faculty/staff.");
        hint.setForeground(GRAY);

        reachOutBox.setEditable(true);
        reachOutBox.setEnabled(true);
        reachOutBox.setFocusable(true);
        reachOutBox.setLineWrap(true);
        reachOutBox.setWrapStyleWord(true);
        reachOutBox.setFont(reachOutBox.getFont().deriveFont(14f));
        reachOutBox.setBackground(WHITE);
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
                reachOutBox.requestFocusInWindow();
                return;
            }

            if (currentStudentId().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Log in as a student before sending an inquiry.");
                return;
            }

            service.sendBroadcastInquiry(currentStudentId(), currentStudentName(), msg);
            appendAdvisor("Inquiry sent to faculty/staff: " + msg);

            reachOutBox.setText("");
            reachOutBox.requestFocusInWindow();
            refreshNotifications();
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

        notifList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
        advisorOutput.append("- " + msg + "\n");
        advisorOutput.setCaretPosition(advisorOutput.getDocument().getLength());
    }

    private void refreshNotifications() {
        notifModel.clear();

        if (currentStudentId().isEmpty()) {
            notifModel.addElement("Log in as a student to view faculty responses.");
            notifView.setText("");
            return;
        }

        List<MockInquiryStore.Response> responses =
                MockInquiryStore.getInstance().getResponsesForStudent(currentStudentId());

        if (responses.isEmpty()) {
            notifModel.addElement("No faculty responses yet.");
            notifView.setText("");
            return;
        }

        for (MockInquiryStore.Response response : responses) {
            String line =
                    "From: " + response.facultyName + "\n" +
                    "Time: " + response.createdAt + "\n\n" +
                    response.body;
            notifModel.addElement(line);
        }

        notifList.setSelectedIndex(0);
    }

    private void loadStudentData() {
        completedModel.clear();
        suggestedModel.clear();
        searchModel.clear();
        advisorOutput.setText("");

        for (String course : service.getCompletedCourses(currentStudentId())) {
            completedModel.addElement(course);
        }

        for (String course : service.getSuggestedCourses(currentStudentId())) {
            suggestedModel.addElement(course);
        }

        appendAdvisor("Loaded student curriculum data.");
    }

    private String currentStudentId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "" : user.id;
    }

    private String currentStudentName() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "Student" : user.name;
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

    public interface StudentCurriculumService {
        List<String> getCompletedCourses(String studentId);
        List<String> getSuggestedCourses(String studentId);
        List<String> searchCourses(String query);
        PrereqResult checkPrereqs(String studentId, String courseCode);
        void sendBroadcastInquiry(String studentId, String studentName, String message);
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
