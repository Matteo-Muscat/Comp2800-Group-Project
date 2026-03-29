import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class HttpStudentReportsService implements StudentReportsPanel.StudentReportsService {

    private final ApiClient apiClient;

    public HttpStudentReportsService(ApiClient apiClient) {
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
    public List<StudentReportsPanel.AdvisorRow> getAdvisorReport(String departmentFilter, boolean sortMostFirst) {
        Map<Integer, BackendModels.FacultyProfile> facultyByUserId = new HashMap<>();
        for (Map<String, Object> faculty : ApiDataMapper.objectList(apiClient.get("/reports/faculty"))) {
            BackendModels.FacultyProfile profile = ApiDataMapper.toFacultyProfile(faculty);
            facultyByUserId.put(profile.facultyId(), profile);
        }

        Map<Integer, Long> countByAdvisorId = countMap(apiClient.get("/reports/advisors/most-appointments"));
        Map<String, StudentReportsPanel.AdvisorRow> rowsByDisplayName = new LinkedHashMap<>();

        for (Map<String, Object> advisor : ApiDataMapper.objectList(apiClient.get("/bookings/advisors"))) {
            BackendModels.Advisor info = ApiDataMapper.toAdvisor(advisor);
            BackendModels.FacultyProfile faculty = facultyByUserId.get(info.userId());
            String department = faculty == null ? "Staff / Advising" : faculty.department();

            if (departmentFilter != null
                    && !"All".equals(departmentFilter)
                    && !departmentFilter.equals(department)) {
                continue;
            }

            int appointments = countByAdvisorId.getOrDefault(info.advisorId(), 0L).intValue();
            StudentReportsPanel.AdvisorRow existing = rowsByDisplayName.get(info.displayName());
            if (existing == null) {
                rowsByDisplayName.put(info.displayName(),
                        new StudentReportsPanel.AdvisorRow(info.displayName(), department, appointments));
            } else {
                rowsByDisplayName.put(info.displayName(),
                        new StudentReportsPanel.AdvisorRow(existing.name, existing.department,
                                existing.appointments + appointments));
            }
        }

        List<StudentReportsPanel.AdvisorRow> rows = new ArrayList<>(rowsByDisplayName.values());
        Comparator<StudentReportsPanel.AdvisorRow> comparator =
                Comparator.comparingInt((StudentReportsPanel.AdvisorRow row) -> row.appointments)
                        .thenComparing(row -> row.name);
        rows.sort(sortMostFirst ? comparator.reversed() : comparator);
        return rows;
    }

    private Map<Integer, Long> countMap(Object response) {
        Map<Integer, Long> counts = new HashMap<>();
        for (Map.Entry<String, Object> entry : ApiDataMapper.object(response).entrySet()) {
            counts.put(Integer.parseInt(entry.getKey()), SimpleJson.asLong(entry.getValue()));
        }
        return counts;
    }
}
