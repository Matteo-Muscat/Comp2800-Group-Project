//import java.util.List;
//
///*
// * AdvisingService is an INTERFACE.
// * An interface is a "contract" that says:
// * "Any class that implements me MUST provide these methods."
// *
// * Why this is good for GUI work:
// * - Your GUI only talks to AdvisingService (the contract)
// * - Right now you use MockAdvisingService (fake data)
// * - Later your backend teammate can create RealAdvisingService (database/API)
// * - You do NOT have to rewrite your GUI when that happens
// */
//public interface AdvisingService {
//
//    /*
//     * Returns a list of students (names or IDs) that will populate the dropdown.
//     */
//    List<String> getStudents();
//
//    /*
//     * Given a student, return the courses they have already completed.
//     * The GUI will show these in the "Completed Courses" list.
//     */
//    List<String> getCompletedCourses(String student);
//
//    /*
//     * Given a student, return courses that are available to plan.
//     * The GUI will show these in the "Available Courses" list.
//     */
//    List<String> getAvailableCourses(String student);
//
//    /*
//     * The GUI sends the student's planned courses to this method.
//     *
//     * For now (mock), this returns a confirmation message.
//     * Later (real backend), this could:
//     * - save to database
//     * - run prerequisite checks
//     * - return warnings/errors
//     */
//    String submitPlannedCourses(String student, List<String> planned);
//}