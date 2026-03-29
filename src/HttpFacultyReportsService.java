import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class HttpFacultyReportsService implements FacultyReportsPanel.FacultyReportsService {

    private final ApiClient apiClient;

    public HttpFacultyReportsService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<String> getDepartments() {
        TreeSet<String> departments = new TreeSet<>();
        for (Map<String, Object> faculty : ApiDataMapper.objectList(apiClient.get("/reports/faculty"))) {
            String department = ApiDataMapper.string(faculty, "department");
            if (department != null && !department.isBlank()) {
                departments.add(department);
            }
        }
        return new ArrayList<>(departments);
    }

    @Override
    public List<String> getMajors() {
        TreeSet<String> majors = new TreeSet<>();
        for (Map<String, Object> student : ApiDataMapper.objectList(apiClient.get("/reports/students"))) {
            String major = ApiDataMapper.string(student, "major");
            if (major != null && !major.isBlank()) {
                majors.add(major);
            }
        }
        return new ArrayList<>(majors);
    }

    @Override
    public List<FacultyReportsPanel.AdvisorRow> getAdvisorReport(String deptFilter, boolean mostFirst) {
        Map<Integer, BackendModels.FacultyProfile> facultyByUserId = new HashMap<>();
        for (Map<String, Object> faculty : ApiDataMapper.objectList(apiClient.get("/reports/faculty"))) {
            BackendModels.FacultyProfile profile = ApiDataMapper.toFacultyProfile(faculty);
            facultyByUserId.put(profile.facultyId(), profile);
        }

        Map<Integer, Long> countByAdvisorId = countMap(apiClient.get("/reports/advisors/most-appointments"));
        Map<String, FacultyReportsPanel.AdvisorRow> rowsByDisplayName = new LinkedHashMap<>();

        for (Map<String, Object> advisor : ApiDataMapper.objectList(apiClient.get("/bookings/advisors"))) {
            BackendModels.Advisor info = ApiDataMapper.toAdvisor(advisor);
            BackendModels.FacultyProfile faculty = facultyByUserId.get(info.userId());
            String department = faculty == null ? "Staff / Advising" : faculty.department();

            if (deptFilter != null && !"All".equals(deptFilter) && !deptFilter.equals(department)) {
                continue;
            }

            int appointments = countByAdvisorId.getOrDefault(info.advisorId(), 0L).intValue();
            FacultyReportsPanel.AdvisorRow existing = rowsByDisplayName.get(info.displayName());
            if (existing == null) {
                rowsByDisplayName.put(info.displayName(),
                        new FacultyReportsPanel.AdvisorRow(info.displayName(), department, appointments));
            } else {
                rowsByDisplayName.put(info.displayName(),
                        new FacultyReportsPanel.AdvisorRow(existing.name, existing.department,
                                existing.appointments + appointments));
            }
        }

        List<FacultyReportsPanel.AdvisorRow> rows = new ArrayList<>(rowsByDisplayName.values());
        rows.sort(advisorComparator(mostFirst));
        return rows;
    }

    @Override
    public List<FacultyReportsPanel.StudentRow> getStudentReport(String majorFilter, boolean mostFirst) {
        Map<Integer, Long> countByStudentId = countMap(apiClient.get("/reports/students/most-appointments"));
        List<FacultyReportsPanel.StudentRow> rows = new ArrayList<>();

        for (Map<String, Object> student : ApiDataMapper.objectList(apiClient.get("/reports/students"))) {
            BackendModels.StudentProfile profile = ApiDataMapper.toStudentProfile(student);
            if (majorFilter != null && !"All".equals(majorFilter) && !majorFilter.equals(profile.major())) {
                continue;
            }

            rows.add(new FacultyReportsPanel.StudentRow(
                    profile.name(),
                    profile.major(),
                    String.valueOf(profile.studentId()),
                    countByStudentId.getOrDefault(profile.studentId(), 0L).intValue()
            ));
        }

        rows.sort(studentComparator(mostFirst));
        return rows;
    }

    private Map<Integer, Long> countMap(Object response) {
        Map<Integer, Long> counts = new HashMap<>();
        for (Map.Entry<String, Object> entry : ApiDataMapper.object(response).entrySet()) {
            counts.put(Integer.parseInt(entry.getKey()), SimpleJson.asLong(entry.getValue()));
        }
        return counts;
    }

    private Comparator<FacultyReportsPanel.AdvisorRow> advisorComparator(boolean mostFirst) {
        Comparator<FacultyReportsPanel.AdvisorRow> comparator =
                Comparator.comparingInt((FacultyReportsPanel.AdvisorRow row) -> row.appointments)
                        .thenComparing(row -> row.name);
        return mostFirst ? comparator.reversed() : comparator;
    }

    private Comparator<FacultyReportsPanel.StudentRow> studentComparator(boolean mostFirst) {
        Comparator<FacultyReportsPanel.StudentRow> comparator =
                Comparator.comparingInt((FacultyReportsPanel.StudentRow row) -> row.appointments)
                        .thenComparing(row -> row.studentId);
        return mostFirst ? comparator.reversed() : comparator;
    }
}
