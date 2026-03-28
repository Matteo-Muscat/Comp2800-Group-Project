import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * MockInquiryStore
 * ----------------
 * Shared in-memory "database" for curriculum inquiries + faculty responses.
 *
 * StudentCurriculumPanel calls addInquiry(...)
 * FacultyCurriculumPanel reads inquiries and calls addResponse(...)
 * StudentCurriculumPanel reads responses for the logged-in student.
 */
public class MockInquiryStore {

    // ===== Models =====
    public static class Inquiry {
        public final String inquiryId;
        public final String studentId;
        public final String studentName;
        public final String body;
        public final String createdAt;

        public Inquiry(String inquiryId, String studentId, String studentName, String body) {
            this.inquiryId = inquiryId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.body = body;
            this.createdAt = now();
        }
    }

    public static class Response {
        public final String inquiryId;      // ties response to an inquiry
        public final String facultyName;    // who replied
        public final String body;
        public final String createdAt;

        public Response(String inquiryId, String facultyName, String body) {
            this.inquiryId = inquiryId;
            this.facultyName = facultyName;
            this.body = body;
            this.createdAt = now();
        }
    }

    // ===== Storage =====
    private final List<Inquiry> inquiries = new ArrayList<>();
    private final List<Response> responses = new ArrayList<>();
    private int counter = 0;

    // Singleton shared instance (like your bookings store)
    private static final MockInquiryStore INSTANCE = new MockInquiryStore();
    public static MockInquiryStore getInstance() { return INSTANCE; }

    private MockInquiryStore() { }

    // Create a new inquiry ID
    private synchronized String nextInquiryId() {
        counter++;
        return String.format("Q-%04d", counter);
    }

    // Student sends inquiry
    public synchronized Inquiry addInquiry(String studentId, String studentName, String body) {
        Inquiry q = new Inquiry(nextInquiryId(), studentId, studentName, body);
        inquiries.add(q);
        return q;
    }

    // Faculty views inbox
    public synchronized List<Inquiry> getAllInquiries() {
        return new ArrayList<>(inquiries);
    }

    // Faculty sends response
    public synchronized void addResponse(String inquiryId, String facultyName, String body) {
        responses.add(new Response(inquiryId, facultyName, body));
    }

    // Student views responses only for their inquiries
    public synchronized List<Response> getResponsesForStudent(String studentId) {
        // Find inquiry IDs that belong to this student
        List<String> ids = new ArrayList<>();
        for (Inquiry q : inquiries) {
            if (q.studentId.equals(studentId)) ids.add(q.inquiryId);
        }

        // Return responses that match those inquiry IDs
        List<Response> out = new ArrayList<>();
        for (Response r : responses) {
            if (ids.contains(r.inquiryId)) out.add(r);
        }
        return out;
    }

    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}