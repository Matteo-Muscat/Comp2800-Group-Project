import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpFacultyCurriculumService implements FacultyCurriculumPanel.FacultyCurriculumService {

    private final ApiClient apiClient;

    public HttpFacultyCurriculumService(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public List<String> getStudentList() {
        List<String> students = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/reports/students"))) {
            BackendModels.StudentProfile student = ApiDataMapper.toStudentProfile(row);
            students.add(student.name() + " (" + student.studentId() + ")");
        }
        return students;
    }

    @Override
    public List<String> getCompletedCourses(String studentIdOrName) {
        int studentId = ApiDataMapper.parseId(ApiDataMapper.studentIdFromDisplay(studentIdOrName));
        List<String> items = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(
                apiClient.get("/curriculum/students/" + studentId + "/completed"))) {
            BackendModels.CompletedCourse course = ApiDataMapper.toCompletedCourse(row);
            items.add(course.courseCode() + " - " + course.courseName() + " (" + course.grade() + ")");
        }
        return items;
    }

    @Override
    public List<String> getSuggestedCourses(String studentIdOrName) {
        int studentId = ApiDataMapper.parseId(ApiDataMapper.studentIdFromDisplay(studentIdOrName));
        List<String> items = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(
                apiClient.get("/curriculum/students/" + studentId + "/eligible"))) {
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
    public List<FacultyCurriculumPanel.InboxMessage> getInboxMessages() {
        List<FacultyCurriculumPanel.InboxMessage> messages = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/curriculum/inquiries"))) {
            messages.add(new FacultyCurriculumPanel.InboxMessage(
                    ApiDataMapper.string(row, "inquiryId"),
                    ApiDataMapper.string(row, "studentId"),
                    ApiDataMapper.string(row, "studentName"),
                    ApiDataMapper.string(row, "body"),
                    ApiDataMapper.string(row, "createdAt")
            ));
        }
        return messages;
    }

    @Override
    public void sendResponse(FacultyCurriculumPanel.InboxMessage msg, String facultyName, String reply) {
        apiClient.postNoContent("/curriculum/inquiries/" + msg.inquiryId + "/responses", Map.of(
                "facultyName", facultyName,
                "response", reply
        ));
    }
}
