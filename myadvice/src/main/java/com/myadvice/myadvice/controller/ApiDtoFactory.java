package com.myadvice.myadvice.controller;

import com.myadvice.myadvice.entity.*;

public final class ApiDtoFactory {

    private ApiDtoFactory() {
    }

    public record UserDto(
            Integer userId,
            String name,
            String email,
            User.Role role,
            User.Status status
    ) {}

    public record SigninRequestDto(
            Integer requestId,
            Integer userId,
            String userName,
            String userEmail,
            User.Role userRole,
            SigninRequest.Decision decision,
            String decisionNote,
            Integer handledByUserId,
            String requestedAt
    ) {}

    public record CourseDto(
            String courseCode,
            String courseName,
            Integer credits,
            String description
    ) {}

    public record CoursePrerequisiteDto(
            String courseCode,
            String prereqCourseCode
    ) {}

    public record CompletedCourseDto(
            Integer studentId,
            String courseCode,
            String courseName,
            Integer termId,
            String grade
    ) {}

    public record AdvisingCategoryDto(
            Integer categoryId,
            String categoryName,
            String description
    ) {}

    public record AdvisorDto(
            Integer advisorId,
            Integer userId,
            String displayName,
            Integer categoryId,
            String categoryName,
            Boolean active
    ) {}

    public record AppointmentDto(
            Integer appointmentId,
            Integer studentId,
            String studentName,
            Integer advisorId,
            String advisorName,
            String requestedDate,
            String requestedStartTime,
            String requestedEndTime,
            String reason,
            Appointment.AppointmentStatus status,
            String createdAt
    ) {}

    public record AppointmentSuggestionDto(
            Integer suggestionId,
            Integer appointmentId,
            String suggestedDate,
            String suggestedStartTime,
            String suggestedEndTime,
            String message,
            String createdAt
    ) {}

    public record StudentDto(
            Integer studentId,
            String name,
            String email,
            String major,
            String program,
            Integer yearLevel
    ) {}

    public record FacultyDto(
            Integer facultyId,
            String name,
            String email,
            String department,
            String officeLocation,
            String researchInterests
    ) {}

    public record TermDto(
            Integer termId,
            String termName,
            String startDate,
            String endDate
    ) {}

    public record SectionDto(
            Integer sectionId,
            String courseCode,
            String courseName,
            Integer termId,
            String termName,
            String sectionNumber,
            Integer instructorUserId,
            String instructorName,
            String building,
            String room
    ) {}

    public record SectionMeetingDto(
            Integer meetingId,
            Integer sectionId,
            SectionMeeting.DayOfWeek dayOfWeek,
            String startTime,
            String endTime
    ) {}

    public record StudentScheduleDto(
            Integer studentId,
            Integer termId,
            Integer sectionId,
            String courseCode,
            String courseName,
            String sectionNumber
    ) {}

    public record TimetableChangeDto(
            Integer changeId,
            Integer adminUserId,
            String adminName,
            Integer sectionId,
            String changeType,
            String oldValue,
            String newValue,
            String createdAt
    ) {}

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    public static SigninRequestDto toSigninRequestDto(SigninRequest request) {
        User user = request.getUser();
        return new SigninRequestDto(
                request.getRequestId(),
                request.getUserId(),
                user == null ? null : user.getName(),
                user == null ? null : user.getEmail(),
                user == null ? null : user.getRole(),
                request.getDecision(),
                request.getDecisionNote(),
                request.getHandledByUserId(),
                request.getRequestedAt() == null ? null : request.getRequestedAt().toString()
        );
    }

    public static CourseDto toCourseDto(Course course) {
        return new CourseDto(
                course.getCourseCode(),
                course.getCourseName(),
                course.getCredits(),
                course.getDescription()
        );
    }

    public static CoursePrerequisiteDto toCoursePrerequisiteDto(CoursePrerequisite prerequisite) {
        return new CoursePrerequisiteDto(
                prerequisite.getCourseCode(),
                prerequisite.getPrereqCourseCode()
        );
    }

    public static CompletedCourseDto toCompletedCourseDto(StudentCompletedCourse completedCourse) {
        Course course = completedCourse.getCourse();
        return new CompletedCourseDto(
                completedCourse.getStudentId(),
                completedCourse.getCourseCode(),
                course == null ? null : course.getCourseName(),
                completedCourse.getTermId(),
                completedCourse.getGrade()
        );
    }

    public static AdvisingCategoryDto toAdvisingCategoryDto(AdvisingCategory category) {
        return new AdvisingCategoryDto(
                category.getCategoryId(),
                category.getCategoryName(),
                category.getDescription()
        );
    }

    public static AdvisorDto toAdvisorDto(Advisor advisor) {
        AdvisingCategory category = advisor.getCategory();
        return new AdvisorDto(
                advisor.getAdvisorId(),
                advisor.getUserId(),
                advisor.getDisplayName(),
                advisor.getCategoryId(),
                category == null ? null : category.getCategoryName(),
                advisor.getActive()
        );
    }

    public static AppointmentDto toAppointmentDto(Appointment appointment) {
        Student student = appointment.getStudent();
        Advisor advisor = appointment.getAdvisor();
        return new AppointmentDto(
                appointment.getAppointmentId(),
                appointment.getStudentId(),
                student != null && student.getUser() != null ? student.getUser().getName() : null,
                appointment.getAdvisorId(),
                advisor == null ? null : advisor.getDisplayName(),
                appointment.getRequestedDate() == null ? null : appointment.getRequestedDate().toString(),
                appointment.getRequestedStartTime() == null ? null : appointment.getRequestedStartTime().toString(),
                appointment.getRequestedEndTime() == null ? null : appointment.getRequestedEndTime().toString(),
                appointment.getReason(),
                appointment.getStatus(),
                appointment.getCreatedAt() == null ? null : appointment.getCreatedAt().toString()
        );
    }

    public static AppointmentSuggestionDto toAppointmentSuggestionDto(AppointmentSuggestion suggestion) {
        return new AppointmentSuggestionDto(
                suggestion.getSuggestionId(),
                suggestion.getAppointmentId(),
                suggestion.getSuggestedDate() == null ? null : suggestion.getSuggestedDate().toString(),
                suggestion.getSuggestedStartTime() == null ? null : suggestion.getSuggestedStartTime().toString(),
                suggestion.getSuggestedEndTime() == null ? null : suggestion.getSuggestedEndTime().toString(),
                suggestion.getMessage(),
                suggestion.getCreatedAt() == null ? null : suggestion.getCreatedAt().toString()
        );
    }

    public static StudentDto toStudentDto(Student student) {
        User user = student.getUser();
        return new StudentDto(
                student.getStudentId(),
                user == null ? null : user.getName(),
                student.getEmail(),
                student.getMajor(),
                student.getProgram(),
                student.getYearLevel()
        );
    }

    public static FacultyDto toFacultyDto(Faculty faculty) {
        User user = faculty.getUser();
        return new FacultyDto(
                faculty.getFacultyId(),
                user == null ? null : user.getName(),
                faculty.getEmail(),
                faculty.getDepartment(),
                faculty.getOfficeLocation(),
                faculty.getResearchInterests()
        );
    }

    public static TermDto toTermDto(Term term) {
        return new TermDto(
                term.getTermId(),
                term.getTermName(),
                term.getStartDate() == null ? null : term.getStartDate().toString(),
                term.getEndDate() == null ? null : term.getEndDate().toString()
        );
    }

    public static SectionDto toSectionDto(Section section) {
        Course course = section.getCourse();
        Term term = section.getTerm();
        User instructor = section.getInstructor();
        return new SectionDto(
                section.getSectionId(),
                section.getCourseCode(),
                course == null ? null : course.getCourseName(),
                section.getTermId(),
                term == null ? null : term.getTermName(),
                section.getSectionNumber(),
                section.getInstructorUserId(),
                instructor == null ? null : instructor.getName(),
                section.getBuilding(),
                section.getRoom()
        );
    }

    public static SectionMeetingDto toSectionMeetingDto(SectionMeeting meeting) {
        return new SectionMeetingDto(
                meeting.getMeetingId(),
                meeting.getSectionId(),
                meeting.getDayOfWeek(),
                meeting.getStartTime() == null ? null : meeting.getStartTime().toString(),
                meeting.getEndTime() == null ? null : meeting.getEndTime().toString()
        );
    }

    public static StudentScheduleDto toStudentScheduleDto(StudentSchedule schedule) {
        Section section = schedule.getSection();
        Course course = section == null ? null : section.getCourse();
        return new StudentScheduleDto(
                schedule.getStudentId(),
                schedule.getTermId(),
                schedule.getSectionId(),
                section == null ? null : section.getCourseCode(),
                course == null ? null : course.getCourseName(),
                section == null ? null : section.getSectionNumber()
        );
    }

    public static TimetableChangeDto toTimetableChangeDto(TimetableChange change) {
        User adminUser = change.getAdminUser();
        return new TimetableChangeDto(
                change.getChangeId(),
                change.getAdminUserId(),
                adminUser == null ? null : adminUser.getName(),
                change.getSectionId(),
                change.getChangeType(),
                change.getOldValue(),
                change.getNewValue(),
                change.getCreatedAt() == null ? null : change.getCreatedAt().toString()
        );
    }
}
