//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.util.ArrayList;
//import java.util.List;
//
///*
// * CurriculumPanel is the GUI screen for "Curriculum Advising".
// *
// * FIXES INCLUDED:
// * 1) Advisor Output no longer steals the whole screen:
// *    - We use JSplitPane so Output stays at the bottom and scrolls.
// * 2) Single-student perspective:
// *    - No dropdown; we show one "logged-in" student only.
// */
//public class CurriculumPanel extends JPanel {
//
//    // ---- Color scheme (same as MyAdviceApp) ----
//    private static final Color BLUE   = new Color(0x005A9C);
//    private static final Color GRAY   = new Color(0x555555);
//    private static final Color YELLOW = new Color(0xFFC72C);
//    private static final Color WHITE  = Color.WHITE;
//
//    // Reference to main app (so we can navigate back to menu)
//    private final MyAdviceApp app;
//
//    // Reference to service that provides data (mock now, real later)
//    private final AdvisingService service;
//
//    /*
//     * Single student only (student perspective).
//     * Later, you can pass this in from a login screen.
//     */
//    private final String currentStudent = "Talha Hanif";
//
//    // Models store the data for each list
//    private final DefaultListModel<String> completedModel = new DefaultListModel<>();
//    private final DefaultListModel<String> availableModel = new DefaultListModel<>();
//    private final DefaultListModel<String> plannedModel   = new DefaultListModel<>();
//
//    // Lists display the models
//    private final JList<String> completedList = new JList<>(completedModel);
//    private final JList<String> availableList = new JList<>(availableModel);
//    private final JList<String> plannedList   = new JList<>(plannedModel);
//
//    // Output message box at the bottom
//    private final JTextArea output = new JTextArea();
//
//    /*
//     * Constructor: creates the panel UI.
//     */
//    public CurriculumPanel(MyAdviceApp app, AdvisingService service) {
//        this.app = app;
//        this.service = service;
//
//        setLayout(new BorderLayout());
//        setBackground(WHITE);
//
//        // Header always at top
//        add(buildHeader(), BorderLayout.NORTH);
//
//        /*
//         * Center now contains BOTH:
//         * - main UI (lists/buttons)
//         * - advisor output (scrolling)
//         * via JSplitPane so the output doesn't take over the whole screen.
//         */
//        add(buildMainWithOutputSplit(), BorderLayout.CENTER);
//
//        // Load data once for the "logged-in" student
//        loadStudentData(currentStudent);
//    }
//
//    /*
//     * Builds the top header bar: Title + Back button
//     */
//    private JPanel buildHeader() {
//        JPanel header = new JPanel(new BorderLayout());
//        header.setBackground(BLUE);
//        header.setBorder(new EmptyBorder(18, 25, 18, 25));
//
//        JLabel title = new JLabel("Curriculum Advising");
//        title.setForeground(Color.WHITE);
//        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
//
//        JButton back = new JButton("Back to Menu");
//        styleSmallButton(back);
//        back.addActionListener(e -> app.showScreen("menu"));
//
//        header.add(title, BorderLayout.WEST);
//        header.add(back, BorderLayout.EAST);
//
//        return header;
//    }
//
//    /*
//     * Creates a split view:
//     * - TOP: main UI (student label + lists + buttons)
//     * - BOTTOM: advisor output (scroll pane)
//     *
//     * The bottom does NOT expand as text grows; it scrolls.
//     */
//    private JComponent buildMainWithOutputSplit() {
//
//        // Main UI panel (top)
//        JPanel mainUI = buildMainContent();
//
//        // Output panel (bottom)
//        JScrollPane outputScroll = buildOutputBox();
//
//        // Split vertically: top then bottom
//        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainUI, outputScroll);
//
//        // 0.70 means top gets 70% of space by default, bottom 30%
//        split.setResizeWeight(0.70);
//
//        // smoother resizing
//        split.setContinuousLayout(true);
//
//        // smaller divider bar
//        split.setDividerSize(8);
//
//        // Optional: set initial divider location (pixels)
//        split.setDividerLocation(320);
//
//        return split;
//    }
//
//    /*
//     * Builds the main content area:
//     * - shows ONE student (label)
//     * - 3 list columns: Completed, Available, Planned
//     * - Buttons for Add/Remove/Submit
//     */
//    private JPanel buildMainContent() {
//
//        JPanel main = new JPanel(new BorderLayout(15, 15));
//        main.setBackground(WHITE);
//        main.setBorder(new EmptyBorder(20, 25, 20, 25));
//
//        // ---------- Top row: show current student (no dropdown) ----------
//        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
//        topRow.setBackground(WHITE);
//
//        JLabel studentLabel = new JLabel("Student:");
//        studentLabel.setForeground(GRAY);
//        studentLabel.setFont(studentLabel.getFont().deriveFont(Font.BOLD, 16f));
//
//        // Display the logged-in student name
//        JLabel studentValue = new JLabel(currentStudent);
//        studentValue.setForeground(GRAY);
//        studentValue.setFont(studentValue.getFont().deriveFont(Font.BOLD, 16f));
//
//        topRow.add(studentLabel);
//        topRow.add(studentValue);
//
//        // ---------- Center row: Lists + Buttons ----------
//        JPanel centerRow = new JPanel(new GridLayout(1, 3, 15, 0));
//        centerRow.setBackground(WHITE);
//
//        // Completed list panel
//        centerRow.add(buildListCard("Completed Courses", completedList));
//
//        // Middle panel for buttons
//        JPanel midButtons = new JPanel(new GridLayout(0, 1, 10, 10));
//        midButtons.setBackground(WHITE);
//
//        JButton add = new JButton("Add →");
//        styleActionButton(add, BLUE);
//        add.addActionListener(e -> addSelectedCourse());
//
//        JButton remove = new JButton("← Remove");
//        styleActionButton(remove, GRAY);
//        remove.addActionListener(e -> removeSelectedPlannedCourse());
//
//        JButton submit = new JButton("Submit Plan");
//        styleActionButton(submit, YELLOW);
//        submit.setForeground(Color.BLACK);
//        submit.addActionListener(e -> submitPlan());
//
//        midButtons.add(new JLabel(""));
//        midButtons.add(add);
//        midButtons.add(remove);
//        midButtons.add(submit);
//        midButtons.add(new JLabel(""));
//
//        centerRow.add(midButtons);
//
//        // Right side has two list cards stacked: Available and Planned
//        JPanel rightColumn = new JPanel(new GridLayout(2, 1, 0, 15));
//        rightColumn.setBackground(WHITE);
//
//        rightColumn.add(buildListCard("Available Courses", availableList));
//        rightColumn.add(buildListCard("Planned Courses", plannedList));
//
//        centerRow.add(rightColumn);
//
//        main.add(topRow, BorderLayout.NORTH);
//        main.add(centerRow, BorderLayout.CENTER);
//
//        return main;
//    }
//
//    /*
//     * Builds the output area that shows messages (like a log).
//     * This scroll pane will stay fixed in size due to the JSplitPane.
//     */
//    private JScrollPane buildOutputBox() {
//        output.setEditable(false);
//        output.setLineWrap(true);
//        output.setWrapStyleWord(true);
//        output.setFont(output.getFont().deriveFont(14f));
//        output.setBackground(WHITE);
//        output.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        JScrollPane scroll = new JScrollPane(output);
//        scroll.setBorder(BorderFactory.createTitledBorder("Advisor Output"));
//
//        // Always show vertical scroll bar when needed
//        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//
//        return scroll;
//    }
//
//    /*
//     * Loads completed + available courses for the current student and clears planned.
//     */
//    private void loadStudentData(String student) {
//
//        completedModel.clear();
//        availableModel.clear();
//        plannedModel.clear();
//
//        for (String c : service.getCompletedCourses(student)) {
//            completedModel.addElement(c);
//        }
//
//        for (String c : service.getAvailableCourses(student)) {
//            availableModel.addElement(c);
//        }
//
//        appendMessage("Loaded course data for: " + student);
//    }
//
//    /*
//     * Adds selected course from Available -> Planned.
//     * GUI-only behavior (no prereq checking).
//     */
//    private void addSelectedCourse() {
//        String selected = availableList.getSelectedValue();
//
//        if (selected == null) {
//            appendMessage("Please select an available course to add.");
//            return;
//        }
//
//        if (!plannedModel.contains(selected)) {
//            plannedModel.addElement(selected);
//            appendMessage("Added to planned: " + selected);
//        } else {
//            appendMessage("That course is already planned: " + selected);
//        }
//    }
//
//    /*
//     * Removes selected course from Planned list.
//     */
//    private void removeSelectedPlannedCourse() {
//        String selected = plannedList.getSelectedValue();
//
//        if (selected == null) {
//            appendMessage("Please select a planned course to remove.");
//            return;
//        }
//
//        plannedModel.removeElement(selected);
//        appendMessage("Removed from planned: " + selected);
//    }
//
//    /*
//     * Submits planned courses to the service (mock returns confirmation).
//     */
//    private void submitPlan() {
//
//        // Student is always the logged-in student
//        String student = currentStudent;
//
//        if (plannedModel.isEmpty()) {
//            appendMessage("No planned courses to submit.");
//            return;
//        }
//
//        // Convert plannedModel -> List<String> for the service call
//        List<String> planned = new ArrayList<>();
//        for (int i = 0; i < plannedModel.size(); i++) {
//            planned.add(plannedModel.get(i));
//        }
//
//        String response = service.submitPlannedCourses(student, planned);
//        appendMessage(response);
//    }
//
//    /*
//     * Adds a bullet-style message to the output box and scrolls to bottom.
//     */
//    private void appendMessage(String msg) {
//        output.append("• " + msg + "\n");
//        output.setCaretPosition(output.getDocument().getLength());
//    }
//
//    /*
//     * Styles small buttons like "Back to Menu".
//     */
//    private void styleSmallButton(JButton btn) {
//        btn.setBackground(YELLOW);
//        btn.setForeground(Color.BLACK);
//        btn.setFocusPainted(false);
//        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//    }
//
//    /*
//     * Styles action buttons like Add / Remove / Submit.
//     */
//    private void styleActionButton(JButton btn, Color bg) {
//        btn.setBackground(bg);
//        btn.setForeground(YELLOW);
//        btn.setFocusPainted(false);
//        btn.setBorder(new EmptyBorder(12, 14, 12, 14));
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 14f));
//    }
//
//    /*
//     * Wraps a JList in a titled border and a scroll pane.
//     */
//    private JPanel buildListCard(String title, JList<String> list) {
//        JPanel card = new JPanel(new BorderLayout());
//        card.setBackground(WHITE);
//        card.setBorder(BorderFactory.createTitledBorder(title));
//        card.add(new JScrollPane(list), BorderLayout.CENTER);
//        return card;
//    }
//}