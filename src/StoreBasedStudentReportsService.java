import java.util.*;
import java.util.stream.Collectors;

/*
 * StoreBasedStudentReportsService
 * -------------------------------
 * StudentReportsService implementation that uses the shared MockBookingStore.
 *
 * Student report requirements:
 * - Students can view faculty/advisor info
 * - Includes advisors’ department, name
 * - Includes most/least appointments (ordered)
 *
 * This service computes appointment counts from MockBookingStore requests.
 * NOTE:
 * - MockBookingStore currently stores advisor as a string ("Computer Science Department", etc.)
 * - We treat that as advisor name AND department label for the report.
 */
public class StoreBasedStudentReportsService implements StudentReportsPanel.StudentReportsService {

    private final MockBookingStore store = MockBookingStore.getInstance();

    /*
     * Maps advisor string -> department label.
     * If not found, we just use the advisor string as the department.
     */
    private final Map<String, String> advisorToDepartment = new HashMap<>();

    public StoreBasedStudentReportsService() {
        advisorToDepartment.put("Curriculum / Schedule Help", "Curriculum / Schedule Help");
        advisorToDepartment.put("Computer Science Department", "Computer Science Department");
        advisorToDepartment.put("Nursing Department", "Nursing Department");
        advisorToDepartment.put("Engineering Department", "Engineering Department");
    }

    @Override
    public List<String> getDepartments() {
        return store.getAllRequests().stream()
                .map(r -> getDepartmentForAdvisor(r.advisor))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentReportsPanel.AdvisorRow> getAdvisorReport(String departmentFilter, boolean sortMostFirst) {

        // Count requests grouped by advisor
        Map<String, Integer> countByAdvisor = new HashMap<>();
        for (MockBookingStore.BookingRequest r : store.getAllRequests()) {

            // If you ONLY want approved appointments counted, uncomment:
            // if (!"APPROVED".equals(r.status)) continue;

            countByAdvisor.put(r.advisor, countByAdvisor.getOrDefault(r.advisor, 0) + 1);
        }

        List<StudentReportsPanel.AdvisorRow> rows = new ArrayList<>();

        for (Map.Entry<String, Integer> e : countByAdvisor.entrySet()) {
            String advisorName = e.getKey();
            String dept = getDepartmentForAdvisor(advisorName);
            int count = e.getValue();

            // Apply department filter
            if (departmentFilter != null && !"All".equals(departmentFilter) && !dept.equals(departmentFilter)) {
                continue;
            }

            rows.add(new StudentReportsPanel.AdvisorRow(advisorName, dept, count));
        }

        // Sort by appointment count
        rows.sort(Comparator.comparingInt(r -> r.appointments));
        if (sortMostFirst) Collections.reverse(rows);

        return rows;
    }

    private String getDepartmentForAdvisor(String advisorName) {
        return advisorToDepartment.getOrDefault(advisorName, advisorName);
    }
}