import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FacultySchedulingPanel extends JPanel {

    private static final Color BLUE   = new Color(0x005A9C);
    private static final Color GRAY   = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE  = Color.WHITE;

    private final MyAdviceApp app;
    private final SchedulingStore schedulingStore;

    private final JComboBox<String> termBox = new JComboBox<>();

    private final DefaultTableModel sectionModel;
    private final DefaultTableModel meetingModel;

    private final JTable sectionTable;
    private final JTable meetingTable;

    private final Map<String, List<SchedulingStore.MeetingRecord>> draftMeetingsBySection = new LinkedHashMap<>();

    public FacultySchedulingPanel(MyAdviceApp app, SchedulingStore schedulingStore) {
        this.app = app;
        this.schedulingStore = schedulingStore;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        String[] sectionCols = {
                "Course ID", "Course Name", "Section", "Professor", "Building", "Room"
        };
        String[] meetingCols = {
                "Day", "Start Time", "End Time"
        };

        sectionModel = new DefaultTableModel(sectionCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }
        };

        meetingModel = new DefaultTableModel(meetingCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }
        };

        sectionTable = new JTable(sectionModel);
        meetingTable = new JTable(meetingModel);

        sectionTable.setRowHeight(22);
        meetingTable.setRowHeight(22);

        add(buildBody(), BorderLayout.CENTER);

        loadTerms();
        loadSectionsForSelectedTerm();

        sectionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                captureCurrentMeetings();
                loadMeetingsForSelectedSection();
            }
        });
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLUE);
        header.setBorder(new EmptyBorder(18, 25, 18, 25));

        JLabel title = new JLabel("Scheduling (Faculty/Staff)");
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

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setBackground(WHITE);

        JLabel termLbl = new JLabel("Term:");
        termLbl.setForeground(GRAY);
        termLbl.setFont(termLbl.getFont().deriveFont(Font.BOLD, 16f));

        termBox.addActionListener(e -> loadSectionsForSelectedTerm());

        left.add(termLbl);
        left.add(termBox);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setBackground(WHITE);

        JButton refresh = new JButton("Reload");
        makeActionButton(refresh, BLUE);
        refresh.addActionListener(e -> loadSectionsForSelectedTerm());

        right.add(refresh);

        topRow.add(left, BorderLayout.WEST);
        topRow.add(right, BorderLayout.EAST);

        JPanel sectionCard = wrapTable("Sections", sectionTable);
        JPanel meetingCard = wrapTable("Meetings for Selected Section", meetingTable);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sectionCard, meetingCard);
        split.setResizeWeight(0.60);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setDividerLocation(260);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setBackground(WHITE);

        JButton addSection = new JButton("Add Section");
        makeActionButton(addSection, BLUE);

        JButton deleteSection = new JButton("Delete Section");
        makeActionButton(deleteSection, GRAY);

        JButton addMeeting = new JButton("Add Meeting");
        makeActionButton(addMeeting, BLUE);

        JButton deleteMeeting = new JButton("Delete Meeting");
        makeActionButton(deleteMeeting, GRAY);

        JButton save = new JButton("Save Changes");
        makeActionButton(save, YELLOW);
        save.setForeground(Color.BLACK);

        addSection.addActionListener(e -> addSectionRow());
        deleteSection.addActionListener(e -> deleteSelectedSection());
        addMeeting.addActionListener(e -> addMeetingRow());
        deleteMeeting.addActionListener(e -> deleteSelectedMeeting());
        save.addActionListener(e -> saveChanges());

        actions.add(addSection);
        actions.add(deleteSection);
        actions.add(addMeeting);
        actions.add(deleteMeeting);
        actions.add(save);

        body.add(topRow, BorderLayout.NORTH);
        body.add(split, BorderLayout.CENTER);
        body.add(actions, BorderLayout.SOUTH);
        return body;
    }

    private JPanel wrapTable(String title, JTable table) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createTitledBorder(title));
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void loadTerms() {
        termBox.removeAllItems();
        for (String term : schedulingStore.getTerms()) {
            termBox.addItem(term);
        }
        if (termBox.getItemCount() > 0) {
            termBox.setSelectedIndex(0);
        }
    }

    private void loadSectionsForSelectedTerm() {
        captureCurrentMeetings();
        draftMeetingsBySection.clear();
        sectionModel.setRowCount(0);
        meetingModel.setRowCount(0);

        List<SchedulingStore.SectionRecord> sections = schedulingStore.getSectionsForTerm(selectedTerm());
        for (SchedulingStore.SectionRecord section : sections) {
            sectionModel.addRow(new Object[]{
                    section.courseId,
                    section.courseName,
                    section.section,
                    section.professor,
                    section.building,
                    section.room
            });
            draftMeetingsBySection.put(section.key(), copyMeetings(section.meetings));
        }

        if (sectionTable.getRowCount() > 0) {
            sectionTable.setRowSelectionInterval(0, 0);
            loadMeetingsForSelectedSection();
        }
    }

    private void loadMeetingsForSelectedSection() {
        meetingModel.setRowCount(0);
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        List<SchedulingStore.MeetingRecord> meetings =
                draftMeetingsBySection.getOrDefault(currentSectionKey(), List.of());
        for (SchedulingStore.MeetingRecord meeting : meetings) {
            meetingModel.addRow(new Object[]{meeting.day, meeting.startTime, meeting.endTime});
        }
    }

    private void addSectionRow() {
        sectionModel.addRow(new Object[]{"COMP-XXXX", "New Course", "000", "TBD", "TBD", "TBD"});
        int last = sectionModel.getRowCount() - 1;
        if (last >= 0) {
            sectionTable.setRowSelectionInterval(last, last);
            draftMeetingsBySection.put(currentSectionKey(), new ArrayList<>(List.of(
                    new SchedulingStore.MeetingRecord("TR", "09:00", "10:20")
            )));
            loadMeetingsForSelectedSection();
        }
    }

    private void deleteSelectedSection() {
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a section to delete.");
            return;
        }

        draftMeetingsBySection.remove(currentSectionKey());
        sectionModel.removeRow(row);
        meetingModel.setRowCount(0);

        if (sectionTable.getRowCount() > 0) {
            int nextRow = Math.min(row, sectionTable.getRowCount() - 1);
            sectionTable.setRowSelectionInterval(nextRow, nextRow);
            loadMeetingsForSelectedSection();
        }
    }

    private void addMeetingRow() {
        if (sectionTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Select a section first.");
            return;
        }
        meetingModel.addRow(new Object[]{"TR", "09:00", "10:20"});
        captureCurrentMeetings();
    }

    private void deleteSelectedMeeting() {
        int row = meetingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a meeting row to delete.");
            return;
        }
        meetingModel.removeRow(row);
        captureCurrentMeetings();
    }

    private void saveChanges() {
        captureCurrentMeetings();

        List<SchedulingStore.SectionRecord> sections = new ArrayList<>();
        for (int row = 0; row < sectionModel.getRowCount(); row++) {
            String courseId = value(sectionModel, row, 0);
            String courseName = value(sectionModel, row, 1);
            String section = value(sectionModel, row, 2);
            String professor = value(sectionModel, row, 3);
            String building = value(sectionModel, row, 4);
            String room = value(sectionModel, row, 5);
            String key = courseId + "::" + section;

            sections.add(new SchedulingStore.SectionRecord(
                    selectedTerm(),
                    courseId,
                    courseName,
                    section,
                    professor,
                    building,
                    room,
                    copyMeetings(draftMeetingsBySection.getOrDefault(key, List.of()))
            ));
        }

        schedulingStore.replaceSectionsForTerm(selectedTerm(), sections);
        loadSectionsForSelectedTerm();
        JOptionPane.showMessageDialog(this, "Scheduling changes saved.");
    }

    private void captureCurrentMeetings() {
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        draftMeetingsBySection.put(currentSectionKey(), meetingsFromTable());
    }

    private List<SchedulingStore.MeetingRecord> meetingsFromTable() {
        List<SchedulingStore.MeetingRecord> meetings = new ArrayList<>();
        for (int row = 0; row < meetingModel.getRowCount(); row++) {
            meetings.add(new SchedulingStore.MeetingRecord(
                    value(meetingModel, row, 0),
                    value(meetingModel, row, 1),
                    value(meetingModel, row, 2)
            ));
        }
        return meetings;
    }

    private List<SchedulingStore.MeetingRecord> copyMeetings(List<SchedulingStore.MeetingRecord> meetings) {
        List<SchedulingStore.MeetingRecord> copy = new ArrayList<>();
        for (SchedulingStore.MeetingRecord meeting : meetings) {
            copy.add(new SchedulingStore.MeetingRecord(meeting.day, meeting.startTime, meeting.endTime));
        }
        return copy;
    }

    private String currentSectionKey() {
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            return "";
        }
        return value(sectionModel, row, 0) + "::" + value(sectionModel, row, 2);
    }

    private String selectedTerm() {
        Object value = termBox.getSelectedItem();
        return value == null ? "" : value.toString();
    }

    private String value(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString();
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
