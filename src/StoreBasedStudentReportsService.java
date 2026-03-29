import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreBasedStudentReportsService implements StudentReportsPanel.StudentReportsService {

    private final MockBookingStore store;
    private final AdvisorDirectory advisorDirectory;

    public StoreBasedStudentReportsService() {
        this(MockBookingStore.getInstance(), new AdvisorDirectory());
    }

    public StoreBasedStudentReportsService(MockBookingStore store, AdvisorDirectory advisorDirectory) {
        this.store = store;
        this.advisorDirectory = advisorDirectory;
    }

    @Override
    public List<String> getDepartments() {
        List<String> departments = new ArrayList<>(advisorDirectory.getDepartments());
        departments.sort(String::compareTo);
        return departments;
    }

    @Override
    public List<StudentReportsPanel.AdvisorRow> getAdvisorReport(String departmentFilter, boolean sortMostFirst) {
        Map<String, Integer> countByAdvisor = new HashMap<>();
        for (AdvisorDirectory.AdvisorInfo info : advisorDirectory.getAdvisors()) {
            countByAdvisor.put(info.name, 0);
        }

        for (MockBookingStore.BookingRequest request : store.getAllRequests()) {
            countByAdvisor.put(request.advisor, countByAdvisor.getOrDefault(request.advisor, 0) + 1);
        }

        List<StudentReportsPanel.AdvisorRow> rows = new ArrayList<>();
        for (AdvisorDirectory.AdvisorInfo info : advisorDirectory.getAdvisors()) {
            if (departmentFilter != null
                    && !"All".equals(departmentFilter)
                    && !info.department.equals(departmentFilter)) {
                continue;
            }

            rows.add(new StudentReportsPanel.AdvisorRow(
                    info.name,
                    info.department,
                    countByAdvisor.getOrDefault(info.name, 0)
            ));
        }

        rows.sort(Comparator.comparingInt(r -> r.appointments));
        if (sortMostFirst) {
            rows.sort(Comparator.comparingInt((StudentReportsPanel.AdvisorRow r) -> r.appointments).reversed()
                    .thenComparing(r -> r.name));
        } else {
            rows.sort(Comparator.comparingInt((StudentReportsPanel.AdvisorRow r) -> r.appointments)
                    .thenComparing(r -> r.name));
        }

        return rows;
    }
}
