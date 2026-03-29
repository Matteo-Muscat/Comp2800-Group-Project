import java.util.*;

/*
 * MockPrereqAdminService
 * ----------------------
 * In-memory prereq store for AdminPanel (GUI-only).
 *
 * Later replace with database tables:
 * - courses
 * - course_prerequisites
 */
public class MockPrereqAdminService {

    // Key: course code, Value: set of prereq course codes
    private final Map<String, Set<String>> prereqs = new HashMap<>();

    // Small mock list of known courses for dropdowns/search
    private final List<String> courses = new ArrayList<>();

    public MockPrereqAdminService() {
        // Mock course list
        courses.addAll(List.of(
                "COMP-1400", "COMP-1410", "COMP-2540", "COMP-2650", "COMP-2800",
                "COMP-3150", "COMP-3220", "COMP-3300", "COMP-3670", "COMP-4000"
        ));

        // Mock prereqs
        addPrereq("COMP-2540", "COMP-1410");
        addPrereq("COMP-2800", "COMP-2540");
        addPrereq("COMP-3300", "COMP-2540");
        addPrereq("COMP-3220", "COMP-2800");
        addPrereq("COMP-4000", "COMP-3220");
        addPrereq("COMP-4000", "COMP-3300");
    }

    public List<String> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public List<String[]> getAllPrereqPairs() {
        List<String[]> rows = new ArrayList<>();
        for (String course : prereqs.keySet()) {
            for (String pre : prereqs.get(course)) {
                rows.add(new String[]{course, pre});
            }
        }
        // Sort for nicer display
        rows.sort(Comparator.comparing((String[] r) -> r[0]).thenComparing(r -> r[1]));
        return rows;
    }

    public void addPrereq(String course, String prereq) {
        course = course.trim().toUpperCase();
        prereq = prereq.trim().toUpperCase();

        prereqs.putIfAbsent(course, new HashSet<>());
        prereqs.get(course).add(prereq);

        // If admin types a new course code not in list, optionally add it
        if (!courses.contains(course)) courses.add(course);
        if (!courses.contains(prereq)) courses.add(prereq);
    }

    public void removePrereq(String course, String prereq) {
        course = course.trim().toUpperCase();
        prereq = prereq.trim().toUpperCase();

        if (!prereqs.containsKey(course)) return;
        prereqs.get(course).remove(prereq);

        if (prereqs.get(course).isEmpty()) {
            prereqs.remove(course);
        }
    }

    public void replacePair(String oldCourse, String oldPrereq, String newCourse, String newPrereq) {
        removePrereq(oldCourse, oldPrereq);
        addPrereq(newCourse, newPrereq);
    }
}