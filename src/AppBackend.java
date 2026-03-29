import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppBackend {

    private final ApiClient apiClient;

    private final StudentCurriculumPanel.StudentCurriculumService studentCurriculumService;
    private final FacultyCurriculumPanel.FacultyCurriculumService facultyCurriculumService;
    private final StudentReportsPanel.StudentReportsService studentReportsService;
    private final FacultyReportsPanel.FacultyReportsService facultyReportsService;

    public AppBackend() {
        this(new ApiClient("http://localhost:8080/api"));
    }

    public AppBackend(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.studentCurriculumService = new HttpStudentCurriculumService(apiClient);
        this.facultyCurriculumService = new HttpFacultyCurriculumService(apiClient);
        this.studentReportsService = new HttpStudentReportsService(apiClient);
        this.facultyReportsService = new HttpFacultyReportsService(apiClient);
    }

    public UserRecord login(String email, String password, String selectedRole) {
        Map<String, Object> response = ApiDataMapper.object(apiClient.post("/auth/login", Map.of(
                "email", email,
                "password", password
        )));

        String backendRole = ApiDataMapper.string(response, "role");
        if (!ApiDataMapper.roleMatchesSelection(backendRole, selectedRole)) {
            throw new IllegalArgumentException("That account does not match the selected role.");
        }

        return ApiDataMapper.toUserRecord(response);
    }

    public void register(String name, String email, String password, String selectedRole) {
        apiClient.post("/auth/register", Map.of(
                "name", name,
                "email", email,
                "password", password,
                "role", ApiDataMapper.toBackendRole(selectedRole)
        ));
    }

    public StudentCurriculumPanel.StudentCurriculumService getStudentCurriculumService() {
        return studentCurriculumService;
    }

    public FacultyCurriculumPanel.FacultyCurriculumService getFacultyCurriculumService() {
        return facultyCurriculumService;
    }

    public StudentReportsPanel.StudentReportsService getStudentReportsService() {
        return studentReportsService;
    }

    public FacultyReportsPanel.FacultyReportsService getFacultyReportsService() {
        return facultyReportsService;
    }

    public List<BackendModels.Advisor> getAdvisors(Integer categoryId) {
        Object response = categoryId == null
                ? apiClient.get("/bookings/advisors")
                : apiClient.get("/bookings/advisors", Map.of("categoryId", String.valueOf(categoryId)));

        List<BackendModels.Advisor> advisors = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(response)) {
            advisors.add(ApiDataMapper.toAdvisor(row));
        }
        return advisors;
    }

    public List<BackendModels.Appointment> getAppointmentsForStudent(int studentId) {
        return enrichAppointments(ApiDataMapper.objectList(
                apiClient.get("/bookings/students/" + studentId + "/appointments")));
    }

    public List<BackendModels.Appointment> getAppointmentsForAdvisorUser(int userId, boolean pendingOnly) {
        List<BackendModels.Appointment> appointments = new ArrayList<>();
        for (BackendModels.Advisor advisor : getAdvisors(null)) {
            if (advisor.userId() != null && advisor.userId() == userId) {
                Object response = apiClient.get(
                        "/bookings/advisors/" + advisor.advisorId() + "/appointments",
                        Map.of("pendingOnly", String.valueOf(pendingOnly))
                );
                appointments.addAll(enrichAppointments(ApiDataMapper.objectList(response)));
            }
        }
        return appointments;
    }

    public void submitAppointmentRequest(int studentId, int advisorId, LocalDate date, LocalTime startTime, String reason) {
        apiClient.post("/bookings/appointments", Map.of(
                "studentId", studentId,
                "advisorId", advisorId,
                "requestedDate", date.toString(),
                "requestedStartTime", startTime.toString(),
                "requestedEndTime", startTime.plusMinutes(30).toString(),
                "reason", reason
        ));
    }

    public void approveAppointment(int appointmentId) {
        apiClient.postNoContent("/bookings/appointments/" + appointmentId + "/approve", Map.of());
    }

    public void denyAppointment(int appointmentId, String note) {
        BackendModels.Appointment appointment = findAppointment(appointmentId);
        apiClient.postNoContent("/bookings/appointments/" + appointmentId + "/deny", Map.of());

        if (appointment != null && note != null && !note.isBlank()) {
            apiClient.postNoContent("/bookings/appointments/" + appointmentId + "/suggestions", Map.of(
                    "suggestedDate", appointment.requestedDate(),
                    "suggestedStartTime", appointment.requestedStartTime(),
                    "suggestedEndTime", appointment.requestedEndTime(),
                    "message", note
            ));
        }
    }

    public void cancelAppointment(int appointmentId) {
        apiClient.postNoContent("/bookings/appointments/" + appointmentId + "/cancel", Map.of());
    }

    public List<BackendModels.Term> getTerms() {
        List<BackendModels.Term> terms = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/scheduling/terms"))) {
            terms.add(ApiDataMapper.toTerm(row));
        }
        return ApiDataMapper.sortTerms(terms);
    }

    public List<BackendModels.Section> getSectionsForTerm(int termId) {
        List<BackendModels.Section> sections = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/scheduling/terms/" + termId + "/sections"))) {
            int sectionId = ApiDataMapper.integer(row, "sectionId");
            sections.add(ApiDataMapper.toSection(row, getMeetingsForSection(sectionId)));
        }
        return sections;
    }

    public List<BackendModels.Section> getStudentSchedule(int studentId, int termId) {
        Map<Integer, BackendModels.Section> sectionById = new LinkedHashMap<>();
        for (BackendModels.Section section : getSectionsForTerm(termId)) {
            sectionById.put(section.sectionId(), section);
        }

        List<BackendModels.Section> sections = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(
                apiClient.get("/scheduling/students/" + studentId + "/schedule", Map.of("termId", String.valueOf(termId))))) {
            Integer sectionId = ApiDataMapper.integer(row, "sectionId");
            BackendModels.Section section = sectionById.get(sectionId);
            if (section != null) {
                sections.add(section);
            }
        }
        return sections;
    }

    public void addSectionToSchedule(int studentId, int termId, int sectionId) {
        apiClient.post("/scheduling/schedule", Map.of(
                "studentId", studentId,
                "termId", termId,
                "sectionId", sectionId
        ));
    }

    public void removeSectionFromSchedule(int studentId, int termId, int sectionId) {
        apiClient.delete("/scheduling/schedule", Map.of(
                "studentId", String.valueOf(studentId),
                "termId", String.valueOf(termId),
                "sectionId", String.valueOf(sectionId)
        ));
    }

    public void clearSchedule(int studentId, int termId) {
        for (BackendModels.Section section : getStudentSchedule(studentId, termId)) {
            removeSectionFromSchedule(studentId, termId, section.sectionId());
        }
    }

    public BackendModels.Section createSection(String courseCode, int termId, String sectionNumber,
                                               int instructorUserId, String building, String room) {
        Map<String, Object> row = ApiDataMapper.object(apiClient.post("/scheduling/sections", Map.of(
                "courseCode", courseCode,
                "termId", termId,
                "sectionNumber", sectionNumber,
                "instructorUserId", instructorUserId,
                "building", building,
                "room", room
        )));
        return ApiDataMapper.toSection(row, getMeetingsForSection(ApiDataMapper.integer(row, "sectionId")));
    }

    public BackendModels.Section updateSection(int sectionId, String courseCode, int termId, String sectionNumber,
                                               int instructorUserId, String building, String room) {
        Map<String, Object> row = ApiDataMapper.object(apiClient.put("/scheduling/sections/" + sectionId, Map.of(
                "courseCode", courseCode,
                "termId", termId,
                "sectionNumber", sectionNumber,
                "instructorUserId", instructorUserId,
                "building", building,
                "room", room
        )));
        return ApiDataMapper.toSection(row, getMeetingsForSection(sectionId));
    }

    public void replaceMeetings(int sectionId, List<BackendModels.SectionMeeting> meetings) {
        List<Map<String, Object>> payload = new ArrayList<>();
        for (BackendModels.SectionMeeting meeting : meetings) {
            payload.add(Map.of(
                    "dayOfWeek", meeting.dayOfWeek(),
                    "startTime", meeting.startTime(),
                    "endTime", meeting.endTime()
            ));
        }
        apiClient.put("/scheduling/sections/" + sectionId + "/meetings", payload);
    }

    public void deleteSection(int sectionId) {
        apiClient.delete("/scheduling/sections/" + sectionId, Map.of());
    }

    public List<BackendModels.Course> getCourses() {
        List<BackendModels.Course> courses = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/admin/courses"))) {
            courses.add(ApiDataMapper.toCourse(row));
        }
        return courses;
    }

    public List<BackendModels.Prerequisite> getPrerequisites(String courseCode) {
        List<BackendModels.Prerequisite> prerequisites = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/admin/courses/" + courseCode + "/prerequisites"))) {
            prerequisites.add(ApiDataMapper.toPrerequisite(row));
        }
        return prerequisites;
    }

    public void addPrerequisite(String courseCode, String prereqCourseCode) {
        apiClient.post("/admin/courses/" + courseCode + "/prerequisites", Map.of(
                "prereqCourseCode", prereqCourseCode
        ));
    }

    public void removePrerequisite(String courseCode, String prereqCourseCode) {
        apiClient.delete("/admin/courses/" + courseCode + "/prerequisites/" + prereqCourseCode, Map.of());
    }

    public List<BackendModels.PendingSigninRequest> getPendingSigninRequests() {
        List<BackendModels.PendingSigninRequest> requests = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/admin/signin-requests/pending"))) {
            requests.add(ApiDataMapper.toPendingRequest(row));
        }
        return requests;
    }

    public void approveSigninRequest(int requestId, int handledByUserId) {
        apiClient.postNoContent("/admin/signin-requests/" + requestId + "/approve", Map.of(
                "handledByUserId", handledByUserId
        ));
    }

    public void denySigninRequest(int requestId, int handledByUserId, String note) {
        apiClient.postNoContent("/admin/signin-requests/" + requestId + "/deny", Map.of(
                "handledByUserId", handledByUserId,
                "note", note
        ));
    }

    public int resolveInstructorUserId(String professorName, int fallbackUserId) {
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/admin/faculty"))) {
            BackendModels.FacultyProfile faculty = ApiDataMapper.toFacultyProfile(row);
            if (faculty.name() != null && faculty.name().equalsIgnoreCase(professorName.trim())) {
                return faculty.facultyId();
            }
        }
        return fallbackUserId;
    }

    public void ensureUserData(UserRecord user) {
        // The backend now owns all user data, so there is nothing to seed locally.
    }

    private List<BackendModels.Appointment> enrichAppointments(List<Map<String, Object>> rows) {
        List<BackendModels.Appointment> appointments = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            BackendModels.Appointment appointment = ApiDataMapper.toAppointment(row);
            List<Map<String, Object>> suggestions =
                    ApiDataMapper.objectList(apiClient.get("/bookings/appointments/" + appointment.appointmentId() + "/suggestions"));
            if (!suggestions.isEmpty()) {
                Map<String, Object> suggestion = suggestions.get(suggestions.size() - 1);
                appointment = ApiDataMapper.withSuggestion(
                        appointment,
                        ApiDataMapper.string(suggestion, "message"),
                        ApiDataMapper.string(suggestion, "suggestedDate"),
                        ApiDataMapper.string(suggestion, "suggestedStartTime"),
                        ApiDataMapper.string(suggestion, "suggestedEndTime")
                );
            }
            appointments.add(appointment);
        }
        return appointments;
    }

    private List<BackendModels.SectionMeeting> getMeetingsForSection(int sectionId) {
        List<BackendModels.SectionMeeting> meetings = new ArrayList<>();
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/scheduling/sections/" + sectionId + "/meetings"))) {
            meetings.add(ApiDataMapper.toMeeting(row));
        }
        return meetings;
    }

    private BackendModels.Appointment findAppointment(int appointmentId) {
        for (Map<String, Object> row : ApiDataMapper.objectList(apiClient.get("/bookings/appointments"))) {
            BackendModels.Appointment appointment = ApiDataMapper.toAppointment(row);
            if (appointment.appointmentId() == appointmentId) {
                return appointment;
            }
        }
        return null;
    }
}
