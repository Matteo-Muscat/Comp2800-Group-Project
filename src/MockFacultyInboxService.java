import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockFacultyInboxService implements FacultyCurriculumPanel.FacultyCurriculumService {

    private final List<String> catalog = new ArrayList<>();
    private final List<String> students = new ArrayList<>();
    private final Map<String, List<String>> completed = new HashMap<>();
    private final Map<String, List<String>> suggested = new HashMap<>();
    private final List<FacultyCurriculumPanel.InboxMessage> inbox = new ArrayList<>();
    private final List<String> responsesLog = new ArrayList<>();

    public MockFacultyInboxService() {
        seedCatalog();
        seedStudents();
        seedInbox();
    }

    @Override
    public List<String> getStudentList() {
        return new ArrayList<>(students);
    }

    @Override
    public List<String> getCompletedCourses(String studentIdOrName) {
        return new ArrayList<>(completed.getOrDefault(studentIdOrName, List.of()));
    }

    @Override
    public List<String> getSuggestedCourses(String studentIdOrName) {
        return new ArrayList<>(suggested.getOrDefault(studentIdOrName, List.of()));
    }

    @Override
    public List<String> searchCourses(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) {
            return catalog.subList(0, Math.min(8, catalog.size()));
        }

        List<String> out = new ArrayList<>();
        for (String course : catalog) {
            if (course.toLowerCase().contains(q)) {
                out.add(course);
            }
        }
        return out;
    }

    @Override
    public List<FacultyCurriculumPanel.InboxMessage> getInboxMessages() {
        return new ArrayList<>(inbox);
    }

    @Override
    public void sendResponse(FacultyCurriculumPanel.InboxMessage msg, String facultyName, String reply) {
        String time = now();
        responsesLog.add("Reply to " + msg.fromStudentId + " at " + time + ": " + reply);
        MockInquiryStore.getInstance().addResponse(msg.inquiryId, facultyName, reply);
    }

    private void seedCatalog() {
        catalog.add("COMP-1400 - Introduction to Programming");
        catalog.add("COMP-1410 - Introduction to Algorithms and Programming");
        catalog.add("COMP-2540 - Data Structures and Algorithms");
        catalog.add("COMP-2650 - Computer Architecture I");
        catalog.add("COMP-2800 - Software Development");
        catalog.add("COMP-3150 - Database Management Systems");
        catalog.add("COMP-3220 - OO Analysis and Design");
        catalog.add("COMP-3300 - Operating Systems");
        catalog.add("COMP-3670 - Computer Networks");
    }

    private void seedStudents() {
        students.add("1001");
        students.add("1002");
        students.add("1003");

        completed.put("1001", List.of(
                "COMP-1400 - Introduction to Programming",
                "COMP-1410 - Introduction to Algorithms and Programming",
                "MATH-1720 - Differential Calculus"
        ));
        completed.put("1002", List.of("COMP-1400 - Introduction to Programming"));
        completed.put("1003", List.of(
                "COMP-1400 - Introduction to Programming",
                "COMP-1410 - Introduction to Algorithms and Programming"
        ));

        suggested.put("1001", List.of(
                "COMP-2540 - Data Structures and Algorithms",
                "COMP-2800 - Software Development",
                "COMP-2650 - Computer Architecture I"
        ));
        suggested.put("1002", List.of("COMP-1410 - Introduction to Algorithms and Programming"));
        suggested.put("1003", List.of("COMP-2540 - Data Structures and Algorithms"));
    }

    private void seedInbox() {
        inbox.add(new FacultyCurriculumPanel.InboxMessage(
                "Q-0001",
                "1001",
                "Talha Hanif",
                "Hi, I am trying to plan my next semester. Should I take COMP-2540 and COMP-2800 together?",
                now()
        ));
        inbox.add(new FacultyCurriculumPanel.InboxMessage(
                "Q-0002",
                "1002",
                "Student B",
                "I am confused about prerequisites for COMP-3300. What do I need first?",
                now()
        ));
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
