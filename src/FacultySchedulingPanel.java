import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FacultySchedulingPanel extends JPanel {

    private static final Color BLUE = new Color(0x005A9C);
    private static final Color GRAY = new Color(0x555555);
    private static final Color YELLOW = new Color(0xFFC72C);
    private static final Color WHITE = Color.WHITE;

    private final MyAdviceApp app;
    private final AppBackend backend;

    private final JComboBox<String> termBox = new JComboBox<>();
    private final DefaultTableModel sectionModel;
    private final DefaultTableModel meetingModel;
    private final JTable sectionTable;
    private final JTable meetingTable;

    private final Map<String, BackendModels.Term> termsByName = new LinkedHashMap<>();
    private final Map<String, List<BackendModels.SectionMeeting>> draftMeetingsBySection = new LinkedHashMap<>();
    private final Set<Integer> loadedSectionIds = new LinkedHashSet<>();

    public FacultySchedulingPanel(MyAdviceApp app, AppBackend backend) {
        this.app = app;
        this.backend = backend;

        setLayout(new BorderLayout());
        setBackground(WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        String[] sectionCols = {
                "Section ID", "Course ID", "Course Name", "Section", "Professor", "Building", "Room"
        };
        String[] meetingCols = {"Day", "Start Time", "End Time"};

        sectionModel = createEditableModel(sectionCols);
        meetingModel = createEditableModel(meetingCols);

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

    private DefaultTableModel createEditableModel(String[] cols) {
        return new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return true;
            }
        };
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

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                wrapTable("Sections", sectionTable),
                wrapTable("Meetings for Selected Section", meetingTable)
        );
        split.setResizeWeight(0.60);
        split.setDividerSize(8);
        split.setContinuousLayout(true);
        split.setDividerLocation(260);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actions.setBackground(WHITE);

        JButton addSection = new JButton("Add Section");
        makeActionButton(addSection, BLUE);
        addSection.addActionListener(e -> addSectionRow());

        JButton deleteSection = new JButton("Delete Section");
        makeActionButton(deleteSection, GRAY);
        deleteSection.addActionListener(e -> deleteSelectedSection());

        JButton addMeeting = new JButton("Add Meeting");
        makeActionButton(addMeeting, BLUE);
        addMeeting.addActionListener(e -> addMeetingRow());

        JButton deleteMeeting = new JButton("Delete Meeting");
        makeActionButton(deleteMeeting, GRAY);
        deleteMeeting.addActionListener(e -> deleteSelectedMeeting());

        JButton save = new JButton("Save Changes");
        makeActionButton(save, YELLOW);
        save.setForeground(Color.BLACK);
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
        termsByName.clear();
        for (BackendModels.Term term : backend.getTerms()) {
            termsByName.put(term.termName(), term);
            termBox.addItem(term.termName());
        }
        if (termBox.getItemCount() > 0) {
            termBox.setSelectedIndex(0);
        }
    }

    private void loadSectionsForSelectedTerm() {
        captureCurrentMeetings();
        draftMeetingsBySection.clear();
        loadedSectionIds.clear();
        sectionModel.setRowCount(0);
        meetingModel.setRowCount(0);

        BackendModels.Term term = selectedTerm();
        if (term == null) {
            return;
        }

        List<BackendModels.Section> sections = backend.getSectionsForTerm(term.termId());
        for (BackendModels.Section section : sections) {
            sectionModel.addRow(new Object[]{
                    section.sectionId(),
                    section.courseCode(),
                    section.courseName(),
                    section.sectionNumber(),
                    section.instructorName(),
                    section.building(),
                    section.room()
            });
            loadedSectionIds.add(section.sectionId());
            draftMeetingsBySection.put(sectionKey(section.sectionId(), section.courseCode(), section.sectionNumber()),
                    copyMeetings(section.meetings()));
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

        for (BackendModels.SectionMeeting meeting : draftMeetingsBySection.getOrDefault(currentSectionKey(), List.of())) {
            meetingModel.addRow(new Object[]{
                    ApiDataMapper.shortDay(meeting.dayOfWeek()),
                    ApiDataMapper.trimSeconds(meeting.startTime()),
                    ApiDataMapper.trimSeconds(meeting.endTime())
            });
        }
    }

    private void addSectionRow() {
        sectionModel.addRow(new Object[]{"", "COMP-XXXX", "New Course", "01", currentFacultyName(), "TBD", "TBD"});
        int last = sectionModel.getRowCount() - 1;
        if (last >= 0) {
            sectionTable.setRowSelectionInterval(last, last);
            draftMeetingsBySection.put(currentSectionKey(), new ArrayList<>(List.of(
                    new BackendModels.SectionMeeting(null, null, "MON", "09:00", "10:20")
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
        meetingModel.addRow(new Object[]{"MON", "09:00", "10:20"});
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
        BackendModels.Term term = selectedTerm();
        if (term == null) {
            return;
        }

        Set<Integer> currentIds = new LinkedHashSet<>();

        for (int row = 0; row < sectionModel.getRowCount(); row++) {
            Integer sectionId = nullableInt(sectionModel.getValueAt(row, 0));
            String courseCode = value(sectionModel, row, 1);
            String sectionNumber = value(sectionModel, row, 3);
            String professor = value(sectionModel, row, 4);
            String building = value(sectionModel, row, 5);
            String room = value(sectionModel, row, 6);
            int instructorUserId = backend.resolveInstructorUserId(professor, currentFacultyUserId());

            if (sectionId == null) {
                BackendModels.Section created = backend.createSection(
                        courseCode, term.termId(), sectionNumber, instructorUserId, building, room);
                backend.replaceMeetings(created.sectionId(), draftMeetingsBySection.getOrDefault(
                        sectionKey(null, courseCode, sectionNumber), List.of()));
                currentIds.add(created.sectionId());
            } else {
                backend.updateSection(sectionId, courseCode, term.termId(), sectionNumber, instructorUserId, building, room);
                backend.replaceMeetings(sectionId, draftMeetingsBySection.getOrDefault(
                        sectionKey(sectionId, courseCode, sectionNumber), List.of()));
                currentIds.add(sectionId);
            }
        }

        for (Integer loadedId : loadedSectionIds) {
            if (!currentIds.contains(loadedId)) {
                backend.deleteSection(loadedId);
            }
        }

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

    private List<BackendModels.SectionMeeting> meetingsFromTable() {
        List<BackendModels.SectionMeeting> meetings = new ArrayList<>();
        for (int row = 0; row < meetingModel.getRowCount(); row++) {
            meetings.add(new BackendModels.SectionMeeting(
                    null,
                    null,
                    value(meetingModel, row, 0).toUpperCase(),
                    value(meetingModel, row, 1),
                    value(meetingModel, row, 2)
            ));
        }
        return meetings;
    }

    private List<BackendModels.SectionMeeting> copyMeetings(List<BackendModels.SectionMeeting> meetings) {
        List<BackendModels.SectionMeeting> copy = new ArrayList<>();
        for (BackendModels.SectionMeeting meeting : meetings) {
            copy.add(new BackendModels.SectionMeeting(
                    meeting.meetingId(),
                    meeting.sectionId(),
                    meeting.dayOfWeek(),
                    ApiDataMapper.trimSeconds(meeting.startTime()),
                    ApiDataMapper.trimSeconds(meeting.endTime())
            ));
        }
        return copy;
    }

    private String currentSectionKey() {
        int row = sectionTable.getSelectedRow();
        if (row == -1) {
            return "";
        }
        return sectionKey(nullableInt(sectionModel.getValueAt(row, 0)), value(sectionModel, row, 1), value(sectionModel, row, 3));
    }

    private String sectionKey(Integer sectionId, String courseCode, String sectionNumber) {
        return (sectionId == null ? "new" : sectionId) + "::" + courseCode + "::" + sectionNumber;
    }

    private BackendModels.Term selectedTerm() {
        Object value = termBox.getSelectedItem();
        return value == null ? null : termsByName.get(value.toString());
    }

    private String currentFacultyName() {
        UserRecord user = app.getCurrentUser();
        return user == null ? "Faculty/Staff" : user.name;
    }

    private int currentFacultyUserId() {
        UserRecord user = app.getCurrentUser();
        return user == null ? 0 : Integer.parseInt(user.id);
    }

    private Integer nullableInt(Object value) {
        String text = value == null ? "" : value.toString().trim();
        return text.isEmpty() ? null : Integer.parseInt(text);
    }

    private String value(DefaultTableModel model, int row, int column) {
        Object value = model.getValueAt(row, column);
        return value == null ? "" : value.toString().trim();
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
