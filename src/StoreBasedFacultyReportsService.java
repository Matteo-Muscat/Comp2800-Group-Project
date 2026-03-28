import java.util.*;
import java.util.stream.Collectors;

/*
 * StoreBasedFacultyReportsService
 * -------------------------------
 * FacultyReportsService implementation that uses the shared MockBookingStore.
 *
 * What it does:
 * - Computes "appointments" counts from booking requests in MockBookingStore.
 * - Advisors report:
 *    - group by advisor name (advisor field in booking request)
 *    - count requests
 *    - attach a department label (derived from advisor name)
 * - Students report:
 *    - group by studentId
 *    - count requests
 *    - attach a major (mock map, since MockBookingStore doesn't store majors yet)
 *
 * IMPORTANT:
 * - This is still GUI-only, but now reports reflect real activity during demo.
 * - Later, backend/database will replace MockBookingStore and this logic becomes SQL queries.
 */
public class StoreBasedFacultyReportsService implements FacultyReportsPanel.FacultyReportsService {

    // Shared store used by both StudentBookingsPanel and FacultyBookingsPanel
    private final MockBookingStore store = MockBookingStore.getInstance();

    /*
     * Since MockBookingStore doesn't contain majors, we keep a small mock lookup.
     * This matches your planned database column: students.major.
     */
    private final Map<String, String> studentMajor = new HashMap<>();

    /*
     * Advisor "department" mapping.
     * In your UI, advisors already represent department/service (CS Dept, Nursing Dept, etc.)
     * So we can treat advisor name as department, or map it cleanly here.
     */
    private final Map<String, String> advisorToDepartment = new HashMap<>();

    public StoreBasedFacultyReportsService() {
        // Mock majors (edit these freely)
        studentMajor.put("1001", "Computer Science");
        studentMajor.put("1002", "Computer Science");
        studentMajor.put("1003", "Nursing");
        studentMajor.put("1004", "Engineering");
        studentMajor.put("1005", "Engineering");

        // Advisor → Department mapping (adjust as needed)
        advisorToDepartment.put("Curriculum / Schedule Help", "Curriculum / Schedule Help");
        advisorToDepartment.put("Computer Science Department", "Computer Science Department");
        advisorToDepartment.put("Nursing Department", "Nursing Department");
        advisorToDepartment.put("Engineering Department", "Engineering Department");
    }

    // ============================================
    // Filters list for the UI dropdowns
    // ============================================

    @Override
    public List<String> getDepartments() {
        // Departments are based on the advisor categories that exist in requests
        return getAllRequests().stream()
                .map(r -> getDepartmentForAdvisor(r.advisor))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getMajors() {
        // Majors based on known student IDs (from requests)
        return getAllRequests().stream()
                .map(r -> studentMajor.getOrDefault(r.studentId, "Unknown"))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ============================================
    // Advisor report
    // ============================================

    @Override
    public List<FacultyReportsPanel.AdvisorRow> getAdvisorReport(String deptFilter, boolean mostFirst) {

        // Count requests grouped by advisor name
        Map<String, Integer> countByAdvisor = new HashMap<>();
        for (MockBookingStore.BookingRequest r : getAllRequests()) {
            // If you ONLY want approved appointments counted, uncomment this:
            // if (!"APPROVED".equals(r.status)) continue;

            countByAdvisor.put(r.advisor, countByAdvisor.getOrDefault(r.advisor, 0) + 1);
        }

        // Convert to rows
        List<FacultyReportsPanel.AdvisorRow> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> e : countByAdvisor.entrySet()) {
            String advisorName = e.getKey();
            String dept = getDepartmentForAdvisor(advisorName);
            int count = e.getValue();

            // Apply department filter if not "All"
            if (deptFilter != null && !"All".equals(deptFilter) && !dept.equals(deptFilter)) {
                continue;
            }

            // For "Advisor Name" we can show the advisor label itself (department/service)
            rows.add(new FacultyReportsPanel.AdvisorRow(advisorName, dept, count));
        }

        // Sort by count
        rows.sort(Comparator.comparingInt(a -> a.appointments));
        if (mostFirst) Collections.reverse(rows);

        return rows;
    }

    // ============================================
    // Student report
    // ============================================

    @Override
    public List<FacultyReportsPanel.StudentRow> getStudentReport(String majorFilter, boolean mostFirst) {

        // Count requests grouped by studentId
        Map<String, Integer> countByStudent = new HashMap<>();
        Map<String, String> nameByStudent = new HashMap<>();

        for (MockBookingStore.BookingRequest r : getAllRequests()) {
            // If you ONLY want approved appointments counted, uncomment this:
            // if (!"APPROVED".equals(r.status)) continue;

            countByStudent.put(r.studentId, countByStudent.getOrDefault(r.studentId, 0) + 1);
            nameByStudent.put(r.studentId, r.studentName);
        }

        // Convert to rows
        List<FacultyReportsPanel.StudentRow> rows = new ArrayList<>();
        for (String studentId : countByStudent.keySet()) {
            String name = nameByStudent.getOrDefault(studentId, "Unknown Student");
            String major = studentMajor.getOrDefault(studentId, "Unknown");
            int count = countByStudent.get(studentId);

            // Apply major filter if not "All"
            if (majorFilter != null && !"All".equals(majorFilter) && !major.equals(majorFilter)) {
                continue;
            }

            rows.add(new FacultyReportsPanel.StudentRow(name, major, studentId, count));
        }

        // Sort by count
        rows.sort(Comparator.comparingInt(s -> s.appointments));
        if (mostFirst) Collections.reverse(rows);

        return rows;
    }

    // ============================================
    // Helpers
    // ============================================

    private List<MockBookingStore.BookingRequest> getAllRequests() {
        return store.getAllRequests();
    }

    private String getDepartmentForAdvisor(String advisorName) {
        // Default: if not found in map, treat advisorName as department itself
        return advisorToDepartment.getOrDefault(advisorName, advisorName);
    }
}