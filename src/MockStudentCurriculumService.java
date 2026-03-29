import java.util.*;

/*
 * MockStudentCurriculumService
 * ----------------------------
 * GUI-only mock data source for CurriculumStudentPanel (Student version).
 *
 * What it provides:
 * - Completed courses for a student
 * - Suggested courses for a student
 * - Course catalog search
 * - Mock prerequisite checking (based on a simple prereq map + completed list)
 * - Mock "broadcast inquiry" storage (pretends to notify faculty/staff)
 *
 * Later:
 * - Backend dev can replace this with a real implementation that calls APIs/DB.
 */
public class MockStudentCurriculumService implements StudentCurriculumPanel.StudentCurriculumService {

    // ===== Mock course catalog =====
    // We store courses as strings in a consistent display format:
    // "COMP-2540 - Data Structures"
    private final List<String> catalog = new ArrayList<>();

    // ===== Completed courses per studentId =====
    private final Map<String, List<String>> completedByStudent = new HashMap<>();

    // ===== Suggested courses per studentId =====
    private final Map<String, List<String>> suggestedByStudent = new HashMap<>();

    // ===== Prerequisites (key = courseCode, value = required course codes) =====
    private final Map<String, List<String>> prereqMap = new HashMap<>();

    // ===== Stored "broadcast inquiries" (mock notifications) =====
    private final List<String> broadcastInquiries = new ArrayList<>();

    public MockStudentCurriculumService() {
        seedCatalog();
        seedStudentData();
        seedPrereqs();
    }

    // -------------------------------
    // Completed courses
    // -------------------------------
    @Override
    public List<String> getCompletedCourses(String studentId) {
        return new ArrayList<>(completedByStudent.getOrDefault(studentId, List.of()));
    }

    // -------------------------------
    // Suggested courses
    // -------------------------------
    @Override
    public List<String> getSuggestedCourses(String studentId) {
        return new ArrayList<>(suggestedByStudent.getOrDefault(studentId, List.of()));
    }

    // -------------------------------
    // Search courses (simple contains)
    // -------------------------------
    @Override
    public List<String> searchCourses(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();

        // If query is empty, return a small default list (or empty).
        if (q.isEmpty()) {
            return catalog.subList(0, Math.min(6, catalog.size()));
        }

        List<String> results = new ArrayList<>();
        for (String courseDisplay : catalog) {
            if (courseDisplay.toLowerCase().contains(q)) {
                results.add(courseDisplay);
            }
        }
        return results;
    }

    // -------------------------------
    // Prerequisite check (mock logic)
    // -------------------------------
    @Override
    public StudentCurriculumPanel.PrereqResult checkPrereqs(String studentId, String courseDisplay) {

        // Extract the course code from "COMP-2540 - Data Structures"
        String courseCode = extractCode(courseDisplay);

        // Student completed courses are stored as course displays too,
        // but we only need the codes for comparison.
        Set<String> completedCodes = new HashSet<>();
        for (String c : getCompletedCourses(studentId)) {
            completedCodes.add(extractCode(c));
        }

        // Get prereqs for the course
        List<String> reqs = prereqMap.getOrDefault(courseCode, List.of());

        // If there are no prereqs, it’s automatically "met"
        if (reqs.isEmpty()) {
            return new StudentCurriculumPanel.PrereqResult(
                    "MET",
                    courseCode + ": No prerequisites required."
            );
        }

        // Check what is missing
        List<String> missing = new ArrayList<>();
        for (String r : reqs) {
            if (!completedCodes.contains(r)) {
                missing.add(r);
            }
        }

        if (missing.isEmpty()) {
            return new StudentCurriculumPanel.PrereqResult(
                    "MET",
                    courseCode + ": All prerequisites are met."
            );
        } else {
            return new StudentCurriculumPanel.PrereqResult(
                    "NOT MET",
                    courseCode + ": Missing prerequisite(s): " + String.join(", ", missing)
            );
        }
    }

    // -------------------------------
    // Reach out / broadcast inquiry
    // -------------------------------
    @Override
    public void sendBroadcastInquiry(String studentId, String studentName, String message) {
        // Store inquiry so faculty can see it and respond
        MockInquiryStore.getInstance().addInquiry(studentId, studentName, message);
    }

    @Override
    public List<StudentCurriculumPanel.FacultyResponse> getResponses(String studentId) {
        List<StudentCurriculumPanel.FacultyResponse> responses = new ArrayList<>();
        for (MockInquiryStore.Response response : MockInquiryStore.getInstance().getResponsesForStudent(studentId)) {
            responses.add(new StudentCurriculumPanel.FacultyResponse(
                    response.inquiryId,
                    response.facultyName,
                    response.body,
                    response.createdAt
            ));
        }
        return responses;
    }

    // ============================================
    // Helper: extract course code from display text
    // ============================================
    private String extractCode(String courseDisplay) {
        if (courseDisplay == null) return "";
        // Expected format: "COMP-2540 - Something"
        int dash = courseDisplay.indexOf(" - ");
        if (dash == -1) return courseDisplay.trim();
        return courseDisplay.substring(0, dash).trim();
    }

    // ============================================
    // Seed data (mock)
    // ============================================
    private void seedCatalog() {
        catalog.add("COMP-1000 - Key Concepts in Computer Science");
        catalog.add("COMP-1400 - Introduction to Programming");
        catalog.add("COMP-1410 - Introduction to Algorithms and Programming");
        catalog.add("MATH-1720 - Differential Calculus");
        catalog.add("COMP-2540 - Data Structures and Algorithms");
        catalog.add("COMP-2650 - Computer Architecture I");
        catalog.add("COMP-2800 - Software Development");
        catalog.add("COMP-3150 - Database Management Systems");
        catalog.add("COMP-3220 - Object-Oriented Software Analysis and Design");
        catalog.add("COMP-3300 - Operating Systems");
        catalog.add("COMP-3670 - Computer Networks");
        catalog.add("COMP-4000 - Capstone / Senior Project");
    }

    private void seedStudentData() {
        // Completed courses for student 1001 (Talha mock)
        completedByStudent.put("1001", List.of(
                "COMP-1400 - Introduction to Programming",
                "COMP-1410 - Introduction to Algorithms and Programming",
                "MATH-1720 - Differential Calculus"
        ));

        // Suggested courses for student 1001
        suggestedByStudent.put("1001", List.of(
                "COMP-2540 - Data Structures and Algorithms",
                "COMP-2800 - Software Development",
                "COMP-2650 - Computer Architecture I",
                "COMP-3150 - Database Management Systems"
        ));
    }

    private void seedPrereqs() {
        // COMP-2540 requires COMP-1410
        prereqMap.put("COMP-2540", List.of("COMP-1410"));

        // COMP-2800 requires COMP-2540
        prereqMap.put("COMP-2800", List.of("COMP-2540"));

        // COMP-3220 requires COMP-2800
        prereqMap.put("COMP-3220", List.of("COMP-2800"));

        // COMP-3300 requires COMP-2540
        prereqMap.put("COMP-3300", List.of("COMP-2540"));

        // COMP-4000 requires COMP-3220 and COMP-3300 (example)
        prereqMap.put("COMP-4000", List.of("COMP-3220", "COMP-3300"));
    }
}
