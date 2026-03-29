import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AdvisorDirectory {

    public static class AdvisorInfo {
        public final String name;
        public final String department;
        public final String description;

        public AdvisorInfo(String name, String department, String description) {
            this.name = name;
            this.department = department;
            this.description = description;
        }
    }

    private final Map<String, AdvisorInfo> advisorsByName = new LinkedHashMap<>();

    public AdvisorDirectory() {
        add("Curriculum / Schedule Help",
                "Curriculum / Schedule Help",
                "Curriculum and timetable guidance for planning an upcoming term.");
        add("Computer Science Department",
                "Computer Science Department",
                "Questions about CS program requirements, electives, and advising.");
        add("Nursing Department",
                "Nursing Department",
                "General advising, progression support, and departmental guidance.");
        add("Engineering Department",
                "Engineering Department",
                "Program guidance, degree planning, and academic support.");
    }

    private void add(String name, String department, String description) {
        advisorsByName.put(name, new AdvisorInfo(name, department, description));
    }

    public synchronized List<AdvisorInfo> getAdvisors() {
        return new ArrayList<>(advisorsByName.values());
    }

    public synchronized AdvisorInfo findByName(String name) {
        return advisorsByName.get(name);
    }

    public synchronized List<String> getDepartments() {
        List<String> departments = new ArrayList<>();
        for (AdvisorInfo info : advisorsByName.values()) {
            if (!departments.contains(info.department)) {
                departments.add(info.department);
            }
        }
        return departments;
    }
}
