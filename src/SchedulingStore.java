import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SchedulingStore {

    public static class MeetingRecord {
        public String day;
        public String startTime;
        public String endTime;

        public MeetingRecord(String day, String startTime, String endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public static class SectionRecord {
        public String term;
        public String courseId;
        public String courseName;
        public String section;
        public String professor;
        public String building;
        public String room;
        public List<MeetingRecord> meetings;

        public SectionRecord(String term,
                             String courseId,
                             String courseName,
                             String section,
                             String professor,
                             String building,
                             String room,
                             List<MeetingRecord> meetings) {
            this.term = term;
            this.courseId = courseId;
            this.courseName = courseName;
            this.section = section;
            this.professor = professor;
            this.building = building;
            this.room = room;
            this.meetings = new ArrayList<>(meetings);
        }

        public String key() {
            return courseId + "::" + section;
        }
    }

    private final Map<String, List<SectionRecord>> sectionsByTerm = new LinkedHashMap<>();
    private final Map<String, Map<String, List<String>>> studentSchedules = new LinkedHashMap<>();

    public SchedulingStore() {
        seed();
    }

    public synchronized List<String> getTerms() {
        return new ArrayList<>(sectionsByTerm.keySet());
    }

    public synchronized List<SectionRecord> getSectionsForTerm(String term) {
        return copySections(sectionsByTerm.getOrDefault(term, List.of()));
    }

    public synchronized void replaceSectionsForTerm(String term, List<SectionRecord> sections) {
        sectionsByTerm.put(term, copySections(sections));
    }

    public synchronized boolean addSectionToStudentSchedule(String studentId, String term, String sectionKey) {
        Map<String, List<String>> schedulesByTerm =
                studentSchedules.computeIfAbsent(studentId, ignored -> new LinkedHashMap<>());
        List<String> scheduled = schedulesByTerm.computeIfAbsent(term, ignored -> new ArrayList<>());
        if (scheduled.contains(sectionKey)) {
            return false;
        }
        scheduled.add(sectionKey);
        return true;
    }

    public synchronized void removeSectionFromStudentSchedule(String studentId, String term, String sectionKey) {
        Map<String, List<String>> schedulesByTerm = studentSchedules.get(studentId);
        if (schedulesByTerm == null) {
            return;
        }
        List<String> scheduled = schedulesByTerm.get(term);
        if (scheduled == null) {
            return;
        }
        scheduled.remove(sectionKey);
    }

    public synchronized void clearStudentSchedule(String studentId, String term) {
        Map<String, List<String>> schedulesByTerm = studentSchedules.get(studentId);
        if (schedulesByTerm != null) {
            schedulesByTerm.remove(term);
        }
    }

    public synchronized List<SectionRecord> getStudentSchedule(String studentId, String term) {
        Map<String, List<String>> schedulesByTerm = studentSchedules.get(studentId);
        if (schedulesByTerm == null) {
            return List.of();
        }

        List<String> scheduledKeys = schedulesByTerm.getOrDefault(term, List.of());
        List<SectionRecord> sections = sectionsByTerm.getOrDefault(term, List.of());
        List<SectionRecord> out = new ArrayList<>();

        for (String key : scheduledKeys) {
            for (SectionRecord section : sections) {
                if (section.key().equals(key)) {
                    out.add(copySection(section));
                    break;
                }
            }
        }

        return out;
    }

    private List<SectionRecord> copySections(List<SectionRecord> sections) {
        List<SectionRecord> copy = new ArrayList<>();
        for (SectionRecord section : sections) {
            copy.add(copySection(section));
        }
        return copy;
    }

    private SectionRecord copySection(SectionRecord section) {
        List<MeetingRecord> meetings = new ArrayList<>();
        for (MeetingRecord meeting : section.meetings) {
            meetings.add(new MeetingRecord(meeting.day, meeting.startTime, meeting.endTime));
        }
        return new SectionRecord(
                section.term,
                section.courseId,
                section.courseName,
                section.section,
                section.professor,
                section.building,
                section.room,
                meetings
        );
    }

    private void seed() {
        sectionsByTerm.put("2026W", List.of(
                new SectionRecord("2026W", "COMP-2540", "Data Structures and Algorithms", "001",
                        "Dr. X", "Erie", "101", List.of(new MeetingRecord("MWF", "10:00", "10:50"))),
                new SectionRecord("2026W", "COMP-2800", "Software Development", "002",
                        "Dr. Y", "Erie", "202", List.of(new MeetingRecord("TR", "11:30", "12:50"))),
                new SectionRecord("2026W", "COMP-3150", "Database Management Systems", "001",
                        "Dr. Z", "Leddy", "110", List.of(new MeetingRecord("TR", "14:30", "15:50"))),
                new SectionRecord("2026W", "COMP-3300", "Operating Systems", "003",
                        "Dr. A", "Erie", "210", List.of(new MeetingRecord("MW", "16:00", "17:20")))
        ));
        sectionsByTerm.put("2026S", List.of(
                new SectionRecord("2026S", "COMP-2650", "Computer Architecture I", "001",
                        "Dr. B", "Erie", "105", List.of(new MeetingRecord("TR", "09:00", "10:20"))),
                new SectionRecord("2026S", "COMP-3220", "OO Analysis and Design", "001",
                        "Dr. C", "Erie", "120", List.of(new MeetingRecord("MW", "13:00", "14:20"))),
                new SectionRecord("2026S", "COMP-3670", "Computer Networks", "002",
                        "Dr. D", "Leddy", "210", List.of(new MeetingRecord("TR", "15:00", "16:20")))
        ));
        sectionsByTerm.put("2026F", List.of(
                new SectionRecord("2026F", "COMP-2540", "Data Structures and Algorithms", "002",
                        "Dr. X", "Erie", "101", List.of(new MeetingRecord("MWF", "11:00", "11:50"))),
                new SectionRecord("2026F", "COMP-2800", "Software Development", "001",
                        "Dr. Y", "Erie", "202", List.of(new MeetingRecord("TR", "10:00", "11:20"))),
                new SectionRecord("2026F", "COMP-3300", "Operating Systems", "001",
                        "Dr. A", "Erie", "210", List.of(new MeetingRecord("TR", "12:30", "13:50")))
        ));

        addSectionToStudentSchedule("1001", "2026W", "COMP-2540::001");
        addSectionToStudentSchedule("1001", "2026W", "COMP-2800::002");
    }
}
