import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpStudentCurriculumService implements StudentCurriculumPanel.StudentCurriculumService {

    private final ApiClient apiClient;

    public HttpStudentCurriculumService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<String> getCompletedCourses(String studentId) {
        List<String> items = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(
                apiClient.get("/curriculum/students/" + ApiDataMapper.parseId(studentId) + "/completed"))) {
            BackendModels.CompletedCourse course = ApiDataMapper.toCompletedCourse(row);
            items.add(course.courseCode() + " - " + course.courseName() + " (" + course.grade() + ")");
        }
        return items;
    }

    @Override
    public List<String> getSuggestedCourses(String studentId) {
        List<String> items = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(
                apiClient.get("/curriculum/students/" + ApiDataMapper.parseId(studentId) + "/eligible"))) {
            BackendModels.Course course = ApiDataMapper.toCourse(row);
            items.add(course.courseCode() + " - " + course.courseName());
        }
        return items;
    }

    @Override
    public List<String> searchCourses(String query) {
        List<String> items = new ArrayList<>();
        String normalized = query == null ? "" : query.trim().toLowerCase();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/curriculum/courses"))) {
            BackendModels.Course course = ApiDataMapper.toCourse(row);
            String haystack = (course.courseCode() + " " + course.courseName() + " " + course.description()).toLowerCase();
            if (normalized.isBlank() || haystack.contains(normalized)) {
                items.add(course.courseCode() + " - " + course.courseName());
            }
        }
        return items;
    }

    @Override
    public StudentCurriculumPanel.PrereqResult checkPrereqs(String studentId, String courseCode) {
        Map<String, String> params = new HashMap<>();
        params.put("courseCode", ApiDataMapper.courseCodeFromDisplay(courseCode));

        Map<String, Object> result = ApiDataMapper.object(
                apiClient.get("/curriculum/students/" + ApiDataMapper.parseId(studentId) + "/check", params));

        boolean met = Boolean.TRUE.equals(SimpleJson.asBoolean(result.get("met")));
        List<String> prereqs = new ArrayList<>();
        for (Map<String, Object> prereq : ApiDataMapper.objectList(result.get("prerequisites"))) {
            prereqs.add(ApiDataMapper.string(prereq, "prereqCourseCode"));
        }

        String message = met
                ? "Prerequisites met for " + ApiDataMapper.courseCodeFromDisplay(courseCode) + "."
                : "Prerequisites not met for " + ApiDataMapper.courseCodeFromDisplay(courseCode)
                + ". Required: " + (prereqs.isEmpty() ? "none listed" : String.join(", ", prereqs));

        return new StudentCurriculumPanel.PrereqResult(met ? "Met" : "Not Met", message);
    }

    @Override
    public void sendBroadcastInquiry(String studentId, String studentName, String message) {
        apiClient.postNoContent("/curriculum/inquiries", Map.of(
                "studentId", studentId,
                "studentName", studentName,
                "message", message
        ));
    }

    @Override
    public List<StudentCurriculumPanel.FacultyResponse> getResponses(String studentId) {
        Map<String, String> inquiryTextById = new HashMap<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/curriculum/inquiries"))) {
            if (studentId.equals(ApiDataMapper.string(row, "studentId"))) {
                inquiryTextById.put(
                        ApiDataMapper.string(row, "inquiryId"),
                        ApiDataMapper.string(row, "body")
                );
            }
        }

        List<StudentCurriculumPanel.FacultyResponse> responses = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(
                apiClient.get("/curriculum/students/" + ApiDataMapper.parseId(studentId) + "/responses"))) {
            responses.add(new StudentCurriculumPanel.FacultyResponse(
                    ApiDataMapper.string(row, "responseId"),
                    ApiDataMapper.string(row, "facultyName"),
                    ApiDataMapper.string(row, "body"),
                    ApiDataMapper.string(row, "createdAt"),
                    inquiryTextById.get(ApiDataMapper.string(row, "inquiryId"))
            ));
        }
        return responses;
    }
}
