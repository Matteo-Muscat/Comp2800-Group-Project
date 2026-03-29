import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ApiDataMapper {

    private ApiDataMapper() {
    }

    public static Map<String, Object> object(Object value) {
        return SimpleJson.asObject(value);
    }

    public static List<Map<String, Object>> objectList(Object value) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (value == null) {
            return items;
        }
        for (Object item : SimpleJson.asArray(value)) {
            items.add(SimpleJson.asObject(item));
        }
        return items;
    }

    public static String string(Map<String, Object> data, String key) {
        return SimpleJson.asString(data.get(key));
    }

    public static Integer integer(Map<String, Object> data, String key) {
        return SimpleJson.asInt(data.get(key));
    }

    public static Long longValue(Map<String, Object> data, String key) {
        return SimpleJson.asLong(data.get(key));
    }

    public static BackendModels.Course toCourse(Map<String, Object> data) {
        return new BackendModels.Course(
                string(data, "courseCode"),
                string(data, "courseName"),
                integer(data, "credits"),
                string(data, "description")
        );
    }

    public static BackendModels.Prerequisite toPrerequisite(Map<String, Object> data) {
        return new BackendModels.Prerequisite(
                string(data, "courseCode"),
                string(data, "prereqCourseCode")
        );
    }

    public static BackendModels.CompletedCourse toCompletedCourse(Map<String, Object> data) {
        return new BackendModels.CompletedCourse(
                integer(data, "studentId"),
                string(data, "courseCode"),
                string(data, "courseName"),
                integer(data, "termId"),
                string(data, "grade")
        );
    }

    public static BackendModels.FacultyProfile toFacultyProfile(Map<String, Object> data) {
        return new BackendModels.FacultyProfile(
                integer(data, "facultyId"),
                string(data, "name"),
                string(data, "email"),
                string(data, "department"),
                string(data, "officeLocation"),
                string(data, "researchInterests")
        );
    }

    public static BackendModels.StudentProfile toStudentProfile(Map<String, Object> data) {
        return new BackendModels.StudentProfile(
                integer(data, "studentId"),
                string(data, "name"),
                string(data, "email"),
                string(data, "major"),
                string(data, "program"),
                integer(data, "yearLevel")
        );
    }

    public static BackendModels.Advisor toAdvisor(Map<String, Object> data) {
        return new BackendModels.Advisor(
                integer(data, "advisorId"),
                integer(data, "userId"),
                string(data, "displayName"),
                integer(data, "categoryId"),
                string(data, "categoryName"),
                Boolean.TRUE.equals(SimpleJson.asBoolean(data.get("active")))
        );
    }

    public static BackendModels.Appointment toAppointment(Map<String, Object> data) {
        return new BackendModels.Appointment(
                integer(data, "appointmentId"),
                integer(data, "studentId"),
                string(data, "studentName"),
                integer(data, "advisorId"),
                string(data, "advisorName"),
                string(data, "requestedDate"),
                string(data, "requestedStartTime"),
                string(data, "requestedEndTime"),
                string(data, "reason"),
                string(data, "status"),
                string(data, "createdAt"),
                null,
                null,
                null,
                null
        );
    }

    public static BackendModels.Appointment withSuggestion(BackendModels.Appointment appointment,
                                                           String message,
                                                           String date,
                                                           String startTime,
                                                           String endTime) {
        return new BackendModels.Appointment(
                appointment.appointmentId(),
                appointment.studentId(),
                appointment.studentName(),
                appointment.advisorId(),
                appointment.advisorName(),
                appointment.requestedDate(),
                appointment.requestedStartTime(),
                appointment.requestedEndTime(),
                appointment.reason(),
                appointment.status(),
                appointment.createdAt(),
                message,
                date,
                startTime,
                endTime
        );
    }

    public static BackendModels.Term toTerm(Map<String, Object> data) {
        return new BackendModels.Term(
                integer(data, "termId"),
                string(data, "termName"),
                string(data, "startDate"),
                string(data, "endDate")
        );
    }

    public static BackendModels.SectionMeeting toMeeting(Map<String, Object> data) {
        return new BackendModels.SectionMeeting(
                integer(data, "meetingId"),
                integer(data, "sectionId"),
                string(data, "dayOfWeek"),
                string(data, "startTime"),
                string(data, "endTime")
        );
    }

    public static BackendModels.Section toSection(Map<String, Object> data, List<BackendModels.SectionMeeting> meetings) {
        return new BackendModels.Section(
                integer(data, "sectionId"),
                string(data, "courseCode"),
                string(data, "courseName"),
                integer(data, "termId"),
                string(data, "termName"),
                string(data, "sectionNumber"),
                integer(data, "instructorUserId"),
                string(data, "instructorName"),
                string(data, "building"),
                string(data, "room"),
                meetings
        );
    }

    public static BackendModels.PendingSigninRequest toPendingRequest(Map<String, Object> data) {
        return new BackendModels.PendingSigninRequest(
                integer(data, "requestId"),
                integer(data, "userId"),
                string(data, "userName"),
                string(data, "userEmail"),
                string(data, "userRole"),
                string(data, "decision"),
                string(data, "decisionNote"),
                integer(data, "handledByUserId"),
                string(data, "requestedAt")
        );
    }

    public static UserRecord toUserRecord(Map<String, Object> data) {
        String backendRole = string(data, "role");
        return new UserRecord(
                String.valueOf(integer(data, "userId")),
                string(data, "name"),
                string(data, "email"),
                toUserRole(backendRole),
                "APPROVED".equals(string(data, "status"))
        );
    }

    public static UserRole toUserRole(String backendRole) {
        return "STUDENT".equalsIgnoreCase(backendRole) ? UserRole.STUDENT : UserRole.FACULTY_STAFF;
    }

    public static String toBackendRole(String selectedRole) {
        if ("STUDENT".equals(selectedRole)) {
            return "STUDENT";
        }
        return "STAFF";
    }

    public static boolean roleMatchesSelection(String backendRole, String selectedRole) {
        if ("STUDENT".equals(selectedRole)) {
            return "STUDENT".equalsIgnoreCase(backendRole);
        }
        return "FACULTY".equalsIgnoreCase(backendRole) || "STAFF".equalsIgnoreCase(backendRole);
    }

    public static int parseId(String rawId) {
        return Integer.parseInt(rawId.trim());
    }

    public static String courseCodeFromDisplay(String display) {
        if (display == null) {
            return "";
        }
        int firstSpace = display.indexOf(' ');
        return firstSpace <= 0 ? display.trim() : display.substring(0, firstSpace).trim();
    }

    public static String studentIdFromDisplay(String display) {
        if (display == null) {
            return "";
        }
        int open = display.lastIndexOf('(');
        int close = display.lastIndexOf(')');
        if (open >= 0 && close > open) {
            return display.substring(open + 1, close).trim();
        }
        return display.trim();
    }

    public static LocalDate nextDateForShortDay(String shortDay) {
        DayOfWeek target = switch (shortDay.toUpperCase(Locale.ROOT)) {
            case "MON" -> DayOfWeek.MONDAY;
            case "TUE" -> DayOfWeek.TUESDAY;
            case "WED" -> DayOfWeek.WEDNESDAY;
            case "THU" -> DayOfWeek.THURSDAY;
            case "FRI" -> DayOfWeek.FRIDAY;
            default -> throw new IllegalArgumentException("Unsupported day: " + shortDay);
        };

        LocalDate today = LocalDate.now();
        int delta = target.getValue() - today.getDayOfWeek().getValue();
        if (delta <= 0) {
            delta += 7;
        }
        return today.plusDays(delta);
    }

    public static LocalTime parseHourMinute(String value) {
        return LocalTime.parse(value.length() == 5 ? value + ":00" : value);
    }

    public static String compactTimeRange(String start, String end) {
        return trimSeconds(start) + "-" + trimSeconds(end);
    }

    public static String trimSeconds(String value) {
        if (value == null) {
            return "";
        }
        return value.length() >= 5 ? value.substring(0, 5) : value;
    }

    public static String shortDay(String apiDay) {
        if (apiDay == null) {
            return "";
        }
        return apiDay.substring(0, Math.min(3, apiDay.length())).toUpperCase(Locale.ROOT);
    }

    public static List<BackendModels.Term> sortTerms(List<BackendModels.Term> terms) {
        terms.sort(Comparator.comparing(BackendModels.Term::termId));
        return terms;
    }
}
