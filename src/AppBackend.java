public class AppBackend {

    private final AuthService authService = new AuthService();
    private final AcademicDataStore academicDataStore = new AcademicDataStore();
    private final AdvisorDirectory advisorDirectory = new AdvisorDirectory();
    private final SchedulingStore schedulingStore = new SchedulingStore();

    private final StudentCurriculumPanel.StudentCurriculumService studentCurriculumService =
            new BackendStudentCurriculumService(academicDataStore, MockInquiryStore.getInstance());
    private final FacultyCurriculumPanel.FacultyCurriculumService facultyCurriculumService =
            new BackendFacultyCurriculumService(authService, academicDataStore, MockInquiryStore.getInstance());
    private final StudentBookingsPanel.StudentBookingsService studentBookingsService =
            new BackendStudentBookingsService(advisorDirectory);
    private final StudentReportsPanel.StudentReportsService studentReportsService =
            new StoreBasedStudentReportsService(MockBookingStore.getInstance(), advisorDirectory);
    private final FacultyReportsPanel.FacultyReportsService facultyReportsService =
            new StoreBasedFacultyReportsService(MockBookingStore.getInstance(), advisorDirectory, academicDataStore);

    public AuthService getAuthService() {
        return authService;
    }

    public AcademicDataStore getAcademicDataStore() {
        return academicDataStore;
    }

    public AdvisorDirectory getAdvisorDirectory() {
        return advisorDirectory;
    }

    public SchedulingStore getSchedulingStore() {
        return schedulingStore;
    }

    public StudentCurriculumPanel.StudentCurriculumService getStudentCurriculumService() {
        return studentCurriculumService;
    }

    public FacultyCurriculumPanel.FacultyCurriculumService getFacultyCurriculumService() {
        return facultyCurriculumService;
    }

    public StudentBookingsPanel.StudentBookingsService getStudentBookingsService() {
        return studentBookingsService;
    }

    public StudentReportsPanel.StudentReportsService getStudentReportsService() {
        return studentReportsService;
    }

    public FacultyReportsPanel.FacultyReportsService getFacultyReportsService() {
        return facultyReportsService;
    }

    public void ensureUserData(UserRecord user) {
        if (user != null && user.role == UserRole.STUDENT) {
            academicDataStore.ensureStudent(user.id);
        }
    }
}
