import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AcademicDataStore {

    private final List<String> courseCatalog = new ArrayList<>();
    private final Map<String, List<String>> completedByStudent = new HashMap<>();
    private final Map<String, List<String>> suggestedByStudent = new HashMap<>();
    private final Map<String, List<String>> prereqsByCourse = new HashMap<>();
    private final Map<String, String> majorByStudent = new HashMap<>();

    public AcademicDataStore() {
        seedCatalog();
        seedStudentData();
        seedPrereqs();
    }

    public synchronized void ensureStudent(String studentId) {
        completedByStudent.putIfAbsent(studentId, new ArrayList<>());
        suggestedByStudent.putIfAbsent(studentId, new ArrayList<>());
        majorByStudent.putIfAbsent(studentId, "Undeclared");
    }

    public synchronized List<String> getCompletedCourses(String studentId) {
        ensureStudent(studentId);
        return new ArrayList<>(completedByStudent.get(studentId));
    }

    public synchronized List<String> getSuggestedCourses(String studentId) {
        ensureStudent(studentId);
        return new ArrayList<>(suggestedByStudent.get(studentId));
    }

    public synchronized List<String> searchCourses(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) {
            return new ArrayList<>(courseCatalog.subList(0, Math.min(8, courseCatalog.size())));
        }

        List<String> results = new ArrayList<>();
        for (String course : courseCatalog) {
            if (course.toLowerCase().contains(q)) {
                results.add(course);
            }
        }
        return results;
    }

    public synchronized List<String> getPrereqsForCourse(String courseCode) {
        return new ArrayList<>(prereqsByCourse.getOrDefault(courseCode, List.of()));
    }

    public synchronized Set<String> getCompletedCourseCodes(String studentId) {
        Set<String> codes = new HashSet<>();
        for (String course : getCompletedCourses(studentId)) {
            codes.add(extractCode(course));
        }
        return codes;
    }

    public synchronized String getMajorForStudent(String studentId) {
        ensureStudent(studentId);
        return majorByStudent.get(studentId);
    }

    public synchronized List<String> getAllMajors() {
        List<String> majors = new ArrayList<>();
        for (String major : majorByStudent.values()) {
            if (!majors.contains(major)) {
                majors.add(major);
            }
        }
        majors.sort(String::compareTo);
        return majors;
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

    private void seedCatalog() {
        courseCatalog.add("COMP-1000 - Key Concepts in Computer Science");
        courseCatalog.add("COMP-1400 - Introduction to Programming");
        courseCatalog.add("COMP-1410 - Introduction to Algorithms and Programming");
        courseCatalog.add("MATH-1720 - Differential Calculus");
        courseCatalog.add("COMP-2540 - Data Structures and Algorithms");
        courseCatalog.add("COMP-2650 - Computer Architecture I");
        courseCatalog.add("COMP-2800 - Software Development");
        courseCatalog.add("COMP-3150 - Database Management Systems");
        courseCatalog.add("COMP-3220 - Object-Oriented Software Analysis and Design");
        courseCatalog.add("COMP-3300 - Operating Systems");
        courseCatalog.add("COMP-3670 - Computer Networks");
        courseCatalog.add("COMP-4000 - Capstone / Senior Project");
    }

    private void seedStudentData() {
        completedByStudent.put("1001", new ArrayList<>(List.of(
                "COMP-1400 - Introduction to Programming",
                "COMP-1410 - Introduction to Algorithms and Programming",
                "MATH-1720 - Differential Calculus"
        )));
        completedByStudent.put("1002", new ArrayList<>(List.of(
                "COMP-1400 - Introduction to Programming"
        )));
        completedByStudent.put("1003", new ArrayList<>(List.of(
                "COMP-1400 - Introduction to Programming",
                "COMP-1410 - Introduction to Algorithms and Programming"
        )));

        suggestedByStudent.put("1001", new ArrayList<>(List.of(
                "COMP-2540 - Data Structures and Algorithms",
                "COMP-2800 - Software Development",
                "COMP-2650 - Computer Architecture I",
                "COMP-3150 - Database Management Systems"
        )));
        suggestedByStudent.put("1002", new ArrayList<>(List.of(
                "COMP-1410 - Introduction to Algorithms and Programming",
                "COMP-2650 - Computer Architecture I"
        )));
        suggestedByStudent.put("1003", new ArrayList<>(List.of(
                "COMP-2540 - Data Structures and Algorithms",
                "COMP-2800 - Software Development"
        )));

        majorByStudent.put("1001", "Computer Science");
        majorByStudent.put("1002", "Computer Science");
        majorByStudent.put("1003", "Nursing");
        majorByStudent.put("1004", "Engineering");
        majorByStudent.put("1005", "Engineering");
    }

    private void seedPrereqs() {
        prereqsByCourse.put("COMP-2540", List.of("COMP-1410"));
        prereqsByCourse.put("COMP-2800", List.of("COMP-2540"));
        prereqsByCourse.put("COMP-3150", List.of("COMP-1410"));
        prereqsByCourse.put("COMP-3220", List.of("COMP-2800"));
        prereqsByCourse.put("COMP-3300", List.of("COMP-2540"));
        prereqsByCourse.put("COMP-4000", List.of("COMP-3220", "COMP-3300"));
    }
}
