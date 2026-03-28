import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * MockBookingStore
 * ----------------
 * A shared in-memory store (mock database) for booking requests.
 *
 * Why this exists:
 * - Student page creates booking requests.
 * - Faculty/Staff page reads those requests and approves/denies.
 * - This store allows both screens to see the SAME data without a real database.
 *
 * Later:
 * - Replace this with backend + real database tables (appointments, suggestions, messages).
 */
public class MockBookingStore {

    /*
     * BookingRequest
     * --------------
     * A simple model representing one booking request.
     */
    public static class BookingRequest {
        public final String requestId;
        public final String studentId;
        public final String studentName;

        public final String advisor;   // who student requested
        public final String day;
        public final String time;
        public final String reason;

        // REQUESTED / APPROVED / DENIED
        public String status;

        // Staff message shown to student (approve note or deny + suggestion)
        public String staffMessage;

        public final String createdAt;

        public BookingRequest(String requestId,
                              String studentId,
                              String studentName,
                              String advisor,
                              String day,
                              String time,
                              String reason) {
            this.requestId = requestId;
            this.studentId = studentId;
            this.studentName = studentName;
            this.advisor = advisor;
            this.day = day;
            this.time = time;
            this.reason = reason;

            this.status = "REQUESTED";
            this.staffMessage = "(Waiting for approval)";
            this.createdAt = now();
        }

        private static String now() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
    }

    // Shared list of all requests
    private final List<BookingRequest> requests = new ArrayList<>();

    // Simple request counter
    private int counter = 100;

    // Singleton-style shared instance (easy for GUI demo)
    private static final MockBookingStore INSTANCE = new MockBookingStore();

    public static MockBookingStore getInstance() {
        return INSTANCE;
    }

    private MockBookingStore() {
        // Seed a couple of requests so faculty has something to see immediately
        requests.add(new BookingRequest(nextId(), "1001", "Talha Hanif",
                "Curriculum / Schedule Help", "Tue", "11:00",
                "I want help picking courses for next term."));
        requests.add(new BookingRequest(nextId(), "1002", "Student B",
                "Computer Science Department", "Thu", "13:00",
                "I need clarification on prerequisites for COMP-3300."));
    }

    // Generate new request id
    public synchronized String nextId() {
        counter++;
        return String.format("R-%04d", counter);
    }

    // Student adds a request
    public synchronized void addRequest(BookingRequest r) {
        requests.add(r);
    }

    // Faculty reads all requests
    public synchronized List<BookingRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    // Find one request by id
    public synchronized BookingRequest findById(String requestId) {
        for (BookingRequest r : requests) {
            if (r.requestId.equals(requestId)) return r;
        }
        return null;
    }
}