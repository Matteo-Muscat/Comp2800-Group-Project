import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BackendFacultyCurriculumService implements FacultyCurriculumPanel.FacultyCurriculumService {

    private final AuthService authService;
    private final AcademicDataStore academicDataStore;
    private final MockInquiryStore inquiryStore;

    public BackendFacultyCurriculumService(AuthService authService,
                                           AcademicDataStore academicDataStore,
                                           MockInquiryStore inquiryStore) {
        this.authService = authService;
        this.academicDataStore = academicDataStore;
        this.inquiryStore = inquiryStore;
    }

    @Override
    public List<String> getStudentList() {
        List<String> students = new ArrayList<>();
        for (UserRecord user : authService.getUsersByRole(UserRole.STUDENT, true)) {
            students.add(user.id + " - " + user.name);
        }
        students.sort(Comparator.naturalOrder());
        return students;
    }

    @Override
    public List<String> getCompletedCourses(String studentIdOrName) {
        return academicDataStore.getCompletedCourses(extractStudentId(studentIdOrName));
    }

    @Override
    public List<String> getSuggestedCourses(String studentIdOrName) {
        return academicDataStore.getSuggestedCourses(extractStudentId(studentIdOrName));
    }

    @Override
    public List<String> searchCourses(String query) {
        return academicDataStore.searchCourses(query);
    }

    @Override
    public List<FacultyCurriculumPanel.InboxMessage> getInboxMessages() {
        List<FacultyCurriculumPanel.InboxMessage> out = new ArrayList<>();
        for (MockInquiryStore.Inquiry inquiry : inquiryStore.getAllInquiries()) {
            out.add(new FacultyCurriculumPanel.InboxMessage(
                    inquiry.inquiryId,
                    inquiry.studentId,
                    inquiry.studentName,
                    inquiry.body,
                    inquiry.createdAt
            ));
        }
        return out;
    }

    @Override
    public void sendResponse(FacultyCurriculumPanel.InboxMessage msg, String facultyName, String reply) {
        inquiryStore.addResponse(msg.inquiryId, facultyName, reply);
    }

    private String extractStudentId(String studentIdOrName) {
        if (studentIdOrName == null) {
            return "";
        }

        int idx = studentIdOrName.indexOf(" - ");
        if (idx == -1) {
            return studentIdOrName.trim();
        }
        return studentIdOrName.substring(0, idx).trim();
    }
}
