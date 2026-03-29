import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BackendStudentCurriculumService implements StudentCurriculumPanel.StudentCurriculumService {

    private final AcademicDataStore academicDataStore;
    private final MockInquiryStore inquiryStore;

    public BackendStudentCurriculumService(AcademicDataStore academicDataStore, MockInquiryStore inquiryStore) {
        this.academicDataStore = academicDataStore;
        this.inquiryStore = inquiryStore;
    }

    @Override
    public List<String> getCompletedCourses(String studentId) {
        return academicDataStore.getCompletedCourses(studentId);
    }

    @Override
    public List<String> getSuggestedCourses(String studentId) {
        return academicDataStore.getSuggestedCourses(studentId);
    }

    @Override
    public List<String> searchCourses(String query) {
        return academicDataStore.searchCourses(query);
    }

    @Override
    public StudentCurriculumPanel.PrereqResult checkPrereqs(String studentId, String courseDisplay) {
        String courseCode = extractCode(courseDisplay);
        List<String> prereqs = academicDataStore.getPrereqsForCourse(courseCode);
        if (prereqs.isEmpty()) {
            return new StudentCurriculumPanel.PrereqResult("MET", courseCode + ": No prerequisites required.");
        }

        Set<String> completed = academicDataStore.getCompletedCourseCodes(studentId);
        List<String> missing = new ArrayList<>();
        for (String prereq : prereqs) {
            if (!completed.contains(prereq)) {
                missing.add(prereq);
            }
        }

        if (missing.isEmpty()) {
            return new StudentCurriculumPanel.PrereqResult("MET", courseCode + ": All prerequisites are met.");
        }

        return new StudentCurriculumPanel.PrereqResult(
                "NOT MET",
                courseCode + ": Missing prerequisite(s): " + String.join(", ", missing)
        );
    }

    @Override
    public void sendBroadcastInquiry(String studentId, String studentName, String message) {
        inquiryStore.addInquiry(studentId, studentName, message);
    }

    @Override
    public List<StudentCurriculumPanel.FacultyResponse> getResponses(String studentId) {
        List<StudentCurriculumPanel.FacultyResponse> responses = new ArrayList<>();
        for (MockInquiryStore.Response response : inquiryStore.getResponsesForStudent(studentId)) {
            responses.add(new StudentCurriculumPanel.FacultyResponse(
                    response.inquiryId,
                    response.facultyName,
                    response.body,
                    response.createdAt
            ));
        }
        return responses;
    }

    private String extractCode(String courseDisplay) {
        if (courseDisplay == null) {
            return "";
        }

        int idx = courseDisplay.indexOf(" - ");
        if (idx == -1) {
            return courseDisplay.trim();
        }
        return courseDisplay.substring(0, idx).trim();
    }
}
