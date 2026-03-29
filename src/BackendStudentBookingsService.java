import java.util.ArrayList;
import java.util.List;

public class BackendStudentBookingsService implements StudentBookingsPanel.StudentBookingsService {

    private final AdvisorDirectory advisorDirectory;

    public BackendStudentBookingsService(AdvisorDirectory advisorDirectory) {
        this.advisorDirectory = advisorDirectory;
    }

    @Override
    public List<StudentBookingsPanel.AdvisorOption> getAdvisorOptions() {
        List<StudentBookingsPanel.AdvisorOption> options = new ArrayList<>();
        for (AdvisorDirectory.AdvisorInfo info : advisorDirectory.getAdvisors()) {
            options.add(new StudentBookingsPanel.AdvisorOption(info.name, info.description));
        }
        return options;
    }

    @Override
    public StudentBookingsPanel.AdvisorOption findAdvisorByDisplayName(String displayName) {
        AdvisorDirectory.AdvisorInfo info = advisorDirectory.findByName(displayName);
        if (info == null) {
            return null;
        }
        return new StudentBookingsPanel.AdvisorOption(info.name, info.description);
    }
}
