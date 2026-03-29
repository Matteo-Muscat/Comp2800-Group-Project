import java.util.*;

/*
 * AuthService
 * -----------
 * This is a MOCK authentication service for your GUI.
 *
 * What it does:
 * - Pretends to be the database for login/sign-up approval.
 * - Lets LoginPanel check: does this name+id exist and is it approved?
 *
 * Why we use this now:
 * - You are focusing on GUI first.
 * - Backend/database is not ready yet.
 * - This lets your GUI work today without waiting for teammates.
 *
 * Later:
 * - Backend dev can replace this with real DB + API calls.
 */
public class AuthService {

    /*
     * "Database" for users (mock):
     * Key = user ID
     * Value = UserRecord containing name/role/approval
     */
    private final Map<String, UserRecord> usersById = new HashMap<>();

    /*
     * List of pending sign-up requests waiting for approval.
     * Admin screen can show these and approve/deny them later.
     */
    private final List<UserRecord> pendingRequests = new ArrayList<>();

    /*
     * Constructor: seeds some fake users so login can succeed immediately.
     */
    public AuthService() {

        // Approved student (can log in)
        usersById.put("1001", new UserRecord("1001", "Talha", UserRole.STUDENT, true));

        // Approved faculty/staff (can log in)
        usersById.put("2001", new UserRecord("2001", "Adrian", UserRole.FACULTY_STAFF, true));

        // Existing but not approved (should show "waiting approval")
        UserRecord pendingStudent = new UserRecord("1002", "Student B", UserRole.STUDENT, false);
        usersById.put("1002", pendingStudent);
        pendingRequests.add(pendingStudent);

        // If you want more test accounts, add them here.
    }

    /*
     * login(...)
     * ----------
     * Checks if a user exists and is allowed into the system.
     *
     * Returns:
     * - "OK" if login is successful
     * - otherwise a message explaining why login failed
     *
     * We keep this simple so your GUI can show the message in a popup.
     */
    public synchronized String login(String name, String id, UserRole role) {

        // Look up the user by ID
        UserRecord u = usersById.get(id);

        // If not found, user doesn't exist (prompt to sign up)
        if (u == null) {
            return "User does not exist in the system.";
        }

        // If role does not match, user chose wrong role button
        if (u.role != role) {
            return "Role mismatch. Please select the correct role.";
        }

        // If the name doesn't match the ID, reject login
        if (!u.name.equalsIgnoreCase(name.trim())) {
            return "Name does not match this ID.";
        }

        // If user exists but not approved yet
        if (!u.approved) {
            return "Account exists, but is waiting for approval.";
        }

        // If everything checks out
        return "OK";
    }

    /*
     * requestSignup(...)
     * -----------------
     * Creates a sign-up request that must be approved by Admin.
     *
     * Returns a message that your GUI can show in a popup.
     */
    public synchronized String requestSignup(String name, String id, UserRole role) {

        // If the ID already exists, do not allow another signup request
        if (usersById.containsKey(id)) {
            return "That ID already exists. Try logging in instead.";
        }

        // Create a new user record, but not approved yet
        UserRecord req = new UserRecord(id, name.trim(), role, false);

        // Add to pending request list
        pendingRequests.add(req);

        // Also add to the "database" as unapproved
        usersById.put(id, req);

        return "Sign-up request submitted. Please wait for approval.";
    }

    /*
     * getPendingRequests()
     * --------------------
     * Returns a copy of pending requests for the Admin screen.
     */
    public synchronized List<UserRecord> getPendingRequests() {
        return new ArrayList<>(pendingRequests);
    }

    /*
     * approve(...)
     * -----------
     * Approves a pending user account.
     * Used by Admin screen.
     */
    public synchronized void approve(String id) {
        UserRecord u = usersById.get(id);
        if (u != null) {
            u.approved = true;
        }

        // Remove from pending list
        pendingRequests.removeIf(r -> r.id.equals(id));
    }

    /*
     * deny(...)
     * --------
     * Denies a pending request.
     * In this mock version, we remove them completely from the system.
     */
    public synchronized void deny(String id) {
        pendingRequests.removeIf(r -> r.id.equals(id));
        usersById.remove(id);
    }

    /*
     * getUser(...)
     * -----------
     * Returns the user record by ID (useful if you want to store current user).
     */
    public synchronized UserRecord getUser(String id) {
        return usersById.get(id);
    }

    public synchronized List<UserRecord> getUsersByRole(UserRole role, boolean approvedOnly) {
        List<UserRecord> users = new ArrayList<>();
        for (UserRecord user : usersById.values()) {
            if (user.role != role) {
                continue;
            }
            if (approvedOnly && !user.approved) {
                continue;
            }
            users.add(user);
        }
        users.sort(Comparator.comparing(u -> u.id));
        return users;
    }
}
