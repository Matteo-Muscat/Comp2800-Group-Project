import java.util.ArrayList;
import java.util.List;

/*
 * MockStudentBookingsService
 * -------------------------
 * Cleaned-up mock advisor provider for StudentBookingsPanel.
 *
 * This now ONLY provides:
 * - Advisor options (display name + description)
 * - Find advisor by display name
 *
 * IMPORTANT:
 * - Booking requests are no longer created here.
 * - Requests are created/stored in MockBookingStore (shared with faculty page).
 */
public class MockStudentBookingsService implements StudentBookingsPanel.StudentBookingsService {

    // List of advisors shown to students
    private final List<StudentBookingsPanel.AdvisorOption> advisors = new ArrayList<>();

    public MockStudentBookingsService() {

        // Advisors + brief descriptions (matches your design)
        advisors.add(new StudentBookingsPanel.AdvisorOption(
                "Curriculum / Schedule Help",
                "Curriculum/Schedule help — advice on course planning and timetable building."
        ));

        advisors.add(new StudentBookingsPanel.AdvisorOption(
                "Computer Science Department",
                "Computer Science Department — questions about CS program requirements and advising."
        ));

        advisors.add(new StudentBookingsPanel.AdvisorOption(
                "Nursing Department",
                "Nursing Department — general advising and department support."
        ));

        advisors.add(new StudentBookingsPanel.AdvisorOption(
                "Engineering Department",
                "Engineering Department — program guidance and academic support."
        ));
    }

    @Override
    public List<StudentBookingsPanel.AdvisorOption> getAdvisorOptions() {
        // Return a copy so callers can’t modify our internal list
        return new ArrayList<>(advisors);
    }

    @Override
    public StudentBookingsPanel.AdvisorOption findAdvisorByDisplayName(String displayName) {
        for (StudentBookingsPanel.AdvisorOption a : advisors) {
            if (a.displayName.equals(displayName)) {
                return a;
            }
        }
        return null;
    }
}