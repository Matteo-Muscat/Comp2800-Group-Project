import java.util.List;

public final class BackendModels {

    private BackendModels() {
    }

    public record Course(String courseCode, String courseName, Integer credits, String description) {}

    public record Prerequisite(String courseCode, String prereqCourseCode) {}

    public record CompletedCourse(Integer studentId, String courseCode, String courseName, Integer termId, String grade) {}

    public record FacultyProfile(Integer facultyId, String name, String email, String department,
                                 String officeLocation, String researchInterests) {}

    public record StudentProfile(Integer studentId, String name, String email, String major,
                                 String program, Integer yearLevel) {}

    public record Advisor(Integer advisorId, Integer userId, String displayName,
                          Integer categoryId, String categoryName, boolean active) {}

    public record Appointment(Integer appointmentId, Integer studentId, String studentName,
                              Integer advisorId, String advisorName, String requestedDate,
                              String requestedStartTime, String requestedEndTime, String reason,
                              String status, String createdAt, String suggestionMessage,
                              String suggestionDate, String suggestionStartTime, String suggestionEndTime) {}

    public record Term(Integer termId, String termName, String startDate, String endDate) {}

    public record SectionMeeting(Integer meetingId, Integer sectionId, String dayOfWeek,
                                 String startTime, String endTime) {}

    public record Section(Integer sectionId, String courseCode, String courseName,
                          Integer termId, String termName, String sectionNumber,
                          Integer instructorUserId, String instructorName,
                          String building, String room, List<SectionMeeting> meetings) {}

    public record PendingSigninRequest(Integer requestId, Integer userId, String userName,
                                       String userEmail, String userRole, String decision,
                                       String decisionNote, Integer handledByUserId, String requestedAt) {}

    public record CurriculumInquiry(String inquiryId, String studentId, String studentName,
                                    String body, String createdAt) {}

    public record CurriculumResponse(String responseId, String inquiryId, String studentId,
                                     String facultyName, String body, String createdAt) {}
}
