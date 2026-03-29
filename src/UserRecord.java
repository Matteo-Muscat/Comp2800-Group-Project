/*
 * UserRecord
 * ----------
 * A simple "data container" (model) representing a user account in the system.
 *
 * In a real system, this data would come from the database.
 * For now, AuthService stores a few UserRecords in memory (mock data).
 */
public class UserRecord {

    // Unique identifier (like a student ID or staff/faculty ID)
    public final String id;

    // Person's name
    public final String name;
    public final String email;

    // Role (Student or Faculty/Staff)
    public final UserRole role;

    // Whether this user is approved to access the system
    // (Your design includes approval for new sign-ins)
    public boolean approved;

    /*
     * Constructor: creates a new UserRecord object.
     */
    public UserRecord(String id, String name, UserRole role, boolean approved) {
        this(id, name, null, role, approved);
    }

    public UserRecord(String id, String name, String email, UserRole role, boolean approved) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.approved = approved;
    }
}
