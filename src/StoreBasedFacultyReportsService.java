import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreBasedFacultyReportsService implements FacultyReportsPanel.FacultyReportsService {

    private final MockBookingStore store;
    private final AdvisorDirectory advisorDirectory;
    private final AcademicDataStore academicDataStore;

    public StoreBasedFacultyReportsService() {
        this(MockBookingStore.getInstance(), new AdvisorDirectory(), new AcademicDataStore());
    }

    public StoreBasedFacultyReportsService(MockBookingStore store,
                                           AdvisorDirectory advisorDirectory,
                                           AcademicDataStore academicDataStore) {
        this.store = store;
        this.advisorDirectory = advisorDirectory;
        this.academicDataStore = academicDataStore;
    }

    @Override
    public List<String> getDepartments() {
        List<String> departments = new ArrayList<>(advisorDirectory.getDepartments());
        departments.sort(String::compareTo);
        return departments;
    }

    @Override
    public List<String> getMajors() {
        return academicDataStore.getAllMajors();
    }

    @Override
    public List<FacultyReportsPanel.AdvisorRow> getAdvisorReport(String deptFilter, boolean mostFirst) {
        Map<String, Integer> countByAdvisor = new HashMap<>();
        for (AdvisorDirectory.AdvisorInfo info : advisorDirectory.getAdvisors()) {
            countByAdvisor.put(info.name, 0);
        }

        for (MockBookingStore.BookingRequest request : store.getAllRequests()) {
            countByAdvisor.put(request.advisor, countByAdvisor.getOrDefault(request.advisor, 0) + 1);
        }

        List<FacultyReportsPanel.AdvisorRow> rows = new ArrayList<>();
        for (AdvisorDirectory.AdvisorInfo info : advisorDirectory.getAdvisors()) {
            if (deptFilter != null && !"All".equals(deptFilter) && !info.department.equals(deptFilter)) {
                continue;
            }

            rows.add(new FacultyReportsPanel.AdvisorRow(
                    info.name,
                    info.department,
                    countByAdvisor.getOrDefault(info.name, 0)
            ));
        }

        if (mostFirst) {
            rows.sort(Comparator.comparingInt((FacultyReportsPanel.AdvisorRow row) -> row.appointments).reversed()
                    .thenComparing(row -> row.name));
        } else {
            rows.sort(Comparator.comparingInt((FacultyReportsPanel.AdvisorRow row) -> row.appointments)
                    .thenComparing(row -> row.name));
        }

        return rows;
    }

    @Override
    public List<FacultyReportsPanel.StudentRow> getStudentReport(String majorFilter, boolean mostFirst) {
        Map<String, Integer> countByStudent = new HashMap<>();
        Map<String, String> nameByStudent = new HashMap<>();

        for (MockBookingStore.BookingRequest request : store.getAllRequests()) {
            countByStudent.put(request.studentId, countByStudent.getOrDefault(request.studentId, 0) + 1);
            nameByStudent.put(request.studentId, request.studentName);
        }

        List<FacultyReportsPanel.StudentRow> rows = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countByStudent.entrySet()) {
            String studentId = entry.getKey();
            String major = academicDataStore.getMajorForStudent(studentId);

            if (majorFilter != null && !"All".equals(majorFilter) && !major.equals(majorFilter)) {
                continue;
            }

            rows.add(new FacultyReportsPanel.StudentRow(
                    nameByStudent.getOrDefault(studentId, "Unknown Student"),
                    major,
                    studentId,
                    entry.getValue()
            ));
        }

        if (mostFirst) {
            rows.sort(Comparator.comparingInt((FacultyReportsPanel.StudentRow row) -> row.appointments).reversed()
                    .thenComparing(row -> row.studentId));
        } else {
            rows.sort(Comparator.comparingInt((FacultyReportsPanel.StudentRow row) -> row.appointments)
                    .thenComparing(row -> row.studentId));
        }

        return rows;
    }
}
