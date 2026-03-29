/*
 * UserRole
 * --------
 * This enum represents the two role options in your system.
 *
 * We use an enum (instead of plain strings) because:
 * - It prevents typos (e.g., "STUDNET" would break things)
 * - It makes code easier to read and safer
 */
public enum UserRole {
    STUDENT,
    FACULTY_STAFF
}