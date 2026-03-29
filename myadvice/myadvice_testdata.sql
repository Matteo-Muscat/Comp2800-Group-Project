-- ============================================
-- myAdvice - Test Data
-- COMP 2800 - Group 18
-- ============================================

-- 1) users (mix of students, faculty, staff)
INSERT INTO users (name, email, password, role, status) VALUES
('Alice Johnson', 'alice@uwindsor.ca', 'pass123', 'STUDENT', 'APPROVED'),
('Bob Smith', 'bob@uwindsor.ca', 'pass123', 'STUDENT', 'APPROVED'),
('Carol Lee', 'carol@uwindsor.ca', 'pass123', 'STUDENT', 'APPROVED'),
('David Kim', 'david@uwindsor.ca', 'pass123', 'STUDENT', 'PENDING'),
('Emily Chen', 'emily@uwindsor.ca', 'pass123', 'STUDENT', 'APPROVED'),
('Dr. Frank Miller', 'frank@uwindsor.ca', 'pass123', 'FACULTY', 'APPROVED'),
('Dr. Grace Wang', 'grace@uwindsor.ca', 'pass123', 'FACULTY', 'APPROVED'),
('Dr. Henry Park', 'henry@uwindsor.ca', 'pass123', 'FACULTY', 'APPROVED'),
('Dr. Irene Davis', 'irene@uwindsor.ca', 'pass123', 'FACULTY', 'APPROVED'),
('Jane Wilson', 'jane@uwindsor.ca', 'pass123', 'STAFF', 'APPROVED'),
('Kevin Brown', 'kevin@uwindsor.ca', 'pass123', 'STAFF', 'APPROVED');

-- 2) students
INSERT INTO students (student_id, major, program, year_level, email) VALUES
(1, 'Computer Science', 'Honours Applied Computing', 3, 'alice@uwindsor.ca'),
(2, 'Computer Science', 'General Computer Science', 2, 'bob@uwindsor.ca'),
(3, 'Computer Science', 'Honours Computer Science', 4, 'carol@uwindsor.ca'),
(4, 'Computer Science', 'Honours Applied Computing', 1, 'david@uwindsor.ca'),
(5, 'Computer Science', 'Software Engineering', 3, 'emily@uwindsor.ca');

-- 3) faculty
INSERT INTO faculty (faculty_id, department, office_location, research_interests, email) VALUES
(6, 'Computer Science', 'LT 5102', 'Software Engineering, Agile Development', 'frank@uwindsor.ca'),
(7, 'Computer Science', 'LT 5104', 'Machine Learning, Data Mining', 'grace@uwindsor.ca'),
(8, 'Computer Science', 'LT 5106', 'Databases, Information Systems', 'henry@uwindsor.ca'),
(9, 'Computer Science', 'LT 5108', 'Cybersecurity, Networking', 'irene@uwindsor.ca');

-- 4) courses
INSERT INTO courses (course_code, course_name, credits, description) VALUES
('COMP-1000', 'Key Concepts in Computer Science', 3, 'Introduction to fundamental CS concepts'),
('COMP-1400', 'Intro to Programming and Algorithms I', 3, 'First programming course using C'),
('COMP-1410', 'Intro to Programming and Algorithms II', 3, 'Continuation of COMP-1400'),
('COMP-2120', 'Object Oriented Programming Using Java', 3, 'OOP fundamentals in Java'),
('COMP-2540', 'Data Structures and Algorithms', 3, 'Fundamental data structures'),
('COMP-2560', 'System Programming', 3, 'Unix system programming in C'),
('COMP-2650', 'Computer Architecture I', 3, 'Digital logic and computer organization'),
('COMP-2660', 'Computer Architecture II', 3, 'Advanced computer architecture'),
('COMP-2707', 'Advanced Website Design', 3, 'Web development technologies'),
('COMP-2800', 'Software Development', 3, 'Software development methodologies'),
('COMP-3110', 'Introduction to Software Engineering', 3, 'SE principles and practices'),
('COMP-3150', 'Database Management Systems', 3, 'Relational database design and SQL'),
('COMP-3220', 'Object Oriented Software Analysis and Design', 3, 'OOP analysis and design patterns'),
('COMP-3300', 'Operating System Fundamentals', 3, 'OS concepts and implementation'),
('COMP-3400', 'Advanced Object Oriented System Design Using C++', 3, 'Modern C++ development'),
('COMP-3670', 'Computer Networks', 3, 'Network protocols and architecture'),
('COMP-4150', 'Advanced and Practical Database Systems', 3, 'Advanced database topics'),
('COMP-4200', 'Mobile Application Development', 3, 'Android and mobile development'),
('COMP-4220', 'Agile Software Development', 3, 'Agile methodologies and practices'),
('COMP-4250', 'Big Data Analytics and Database Design', 3, 'Big data tools and techniques'),
('MATH-1020', 'Mathematical Foundations', 3, 'Discrete math for CS'),
('MATH-1250', 'Linear Algebra I', 3, 'Vectors, matrices, linear systems'),
('MATH-1720', 'Differential Calculus', 3, 'Limits, derivatives, applications'),
('MATH-1730', 'Integral Calculus', 3, 'Integration techniques and applications'),
('STAT-2910', 'Statistics for the Sciences', 3, 'Probability and statistics');

-- 5) student_completed_courses
INSERT INTO student_completed_courses (student_id, course_code, grade) VALUES
(1, 'COMP-1000', 'A'),
(1, 'COMP-1400', 'A-'),
(1, 'COMP-1410', 'B+'),
(1, 'COMP-2120', 'A'),
(1, 'COMP-2540', 'B+'),
(1, 'COMP-2560', 'B'),
(1, 'COMP-2650', 'A-'),
(1, 'COMP-2800', 'A'),
(1, 'MATH-1020', 'B+'),
(1, 'MATH-1720', 'B'),
(2, 'COMP-1000', 'B'),
(2, 'COMP-1400', 'B+'),
(2, 'COMP-1410', 'B'),
(2, 'COMP-2120', 'C+'),
(2, 'MATH-1020', 'A-'),
(3, 'COMP-1000', 'A'),
(3, 'COMP-1400', 'A'),
(3, 'COMP-1410', 'A-'),
(3, 'COMP-2120', 'A'),
(3, 'COMP-2540', 'A'),
(3, 'COMP-2560', 'A-'),
(3, 'COMP-2650', 'A'),
(3, 'COMP-2660', 'A-'),
(3, 'COMP-2800', 'A'),
(3, 'COMP-3150', 'A'),
(3, 'COMP-3220', 'A-'),
(3, 'COMP-3300', 'B+'),
(3, 'COMP-3670', 'A'),
(3, 'COMP-4150', 'A'),
(5, 'COMP-1000', 'A-'),
(5, 'COMP-1400', 'A'),
(5, 'COMP-1410', 'A'),
(5, 'COMP-2120', 'B+'),
(5, 'COMP-2540', 'A-'),
(5, 'COMP-2800', 'B+');

-- 6) course_prerequisites
INSERT INTO course_prerequisites (course_code, prereq_course_code) VALUES
('COMP-1410', 'COMP-1400'),
('COMP-2120', 'COMP-1410'),
('COMP-2540', 'COMP-2120'),
('COMP-2560', 'COMP-1410'),
('COMP-2660', 'COMP-2650'),
('COMP-2800', 'COMP-2120'),
('COMP-3150', 'COMP-2540'),
('COMP-3220', 'COMP-2120'),
('COMP-3300', 'COMP-2560'),
('COMP-3400', 'COMP-2120'),
('COMP-3670', 'COMP-2560'),
('COMP-4150', 'COMP-3150'),
('COMP-4200', 'COMP-2120'),
('COMP-4220', 'COMP-3110'),
('COMP-4250', 'COMP-3150');

-- 7) terms
INSERT INTO terms (term_name, start_date, end_date) VALUES
('Fall 2025', '2025-09-08', '2025-12-19'),
('Winter 2026', '2026-01-05', '2026-04-17'),
('Summer 2026', '2026-05-04', '2026-08-14'),
('Fall 2026', '2026-09-07', '2026-12-18');

-- 8) sections
INSERT INTO sections (course_code, term_id, section_number, instructor_user_id, building, room) VALUES
('COMP-2800', 2, '01', 6, 'Erie Hall', '1118'),
('COMP-2540', 2, '01', 7, 'Erie Hall', '2123'),
('COMP-3150', 2, '01', 8, 'Lambton Tower', '3102'),
('COMP-3670', 2, '01', 9, 'Lambton Tower', '3104'),
('COMP-1400', 2, '01', 6, 'Erie Hall', '1120'),
('COMP-1400', 2, '02', 7, 'Erie Hall', '1118'),
('COMP-2120', 2, '01', 8, 'Lambton Tower', '3102'),
('COMP-4200', 2, '01', 9, 'Erie Hall', '2123'),
('COMP-2800', 3, '01', 6, 'Erie Hall', '1118'),
('COMP-3300', 3, '01', 9, 'Lambton Tower', '3104');

-- 9) section_meetings
INSERT INTO section_meetings (section_id, day_of_week, start_time, end_time) VALUES
(1, 'MON', '14:30:00', '15:50:00'),
(1, 'WED', '14:30:00', '15:50:00'),
(2, 'TUE', '10:00:00', '11:20:00'),
(2, 'THU', '10:00:00', '11:20:00'),
(3, 'MON', '11:30:00', '12:50:00'),
(3, 'WED', '11:30:00', '12:50:00'),
(4, 'TUE', '13:00:00', '14:20:00'),
(4, 'THU', '13:00:00', '14:20:00'),
(5, 'MON', '08:30:00', '09:50:00'),
(5, 'WED', '08:30:00', '09:50:00'),
(6, 'TUE', '08:30:00', '09:50:00'),
(6, 'THU', '08:30:00', '09:50:00'),
(7, 'MON', '10:00:00', '11:20:00'),
(7, 'WED', '10:00:00', '11:20:00'),
(8, 'TUE', '14:30:00', '15:50:00'),
(8, 'THU', '14:30:00', '15:50:00');

-- 10) student_schedule (Winter 2026 enrollments)
INSERT INTO student_schedule (student_id, term_id, section_id) VALUES
(1, 2, 1),
(1, 2, 3),
(2, 2, 2),
(2, 2, 5),
(3, 2, 4),
(3, 2, 8),
(5, 2, 1),
(5, 2, 7);

-- 11) advising_categories
INSERT INTO advising_categories (category_name, description) VALUES
('Curriculum Advising', 'Help with course selection, degree requirements, and academic planning'),
('Schedule Planning', 'Assistance with timetable creation and course scheduling'),
('Research Supervision', 'COMP 400/405 project supervision and graduate research advising'),
('Career Guidance', 'Career planning, co-op opportunities, and industry connections'),
('Technical Support', 'Help with lab access, software issues, and technical problems');

-- 12) advisors
INSERT INTO advisors (user_id, display_name, category_id, active) VALUES
(6, 'Dr. Frank Miller', 1, TRUE),
(7, 'Dr. Grace Wang', 2, TRUE),
(8, 'Dr. Henry Park', 1, TRUE),
(8, 'Dr. Henry Park', 3, TRUE),
(9, 'Dr. Irene Davis', 3, TRUE),
(9, 'Dr. Irene Davis', 4, TRUE),
(10, 'Jane Wilson', 5, TRUE),
(11, 'Kevin Brown', 2, TRUE);

-- 13) appointments
INSERT INTO appointments (student_id, advisor_id, requested_date, requested_start_time, requested_end_time, reason, status) VALUES
(1, 1, '2026-03-10', '10:00:00', '10:30:00', 'Need help choosing courses for Fall 2026', 'APPROVED'),
(1, 4, '2026-03-15', '14:00:00', '14:30:00', 'Interested in COMP 400 project supervision', 'REQUESTED'),
(2, 1, '2026-03-11', '11:00:00', '11:30:00', 'Want to switch from General to Honours program', 'APPROVED'),
(3, 5, '2026-03-12', '13:00:00', '13:30:00', 'Looking for graduate research supervisor', 'APPROVED'),
(3, 6, '2026-03-18', '15:00:00', '15:30:00', 'Career advice for cybersecurity path', 'REQUESTED'),
(5, 2, '2026-03-13', '09:00:00', '09:30:00', 'Need help with timetable conflicts', 'DENIED'),
(5, 3, '2026-03-20', '10:00:00', '10:30:00', 'Curriculum planning for remaining semesters', 'REQUESTED'),
(1, 7, '2026-03-22', '14:00:00', '14:30:00', 'Cannot access lab computers', 'APPROVED'),
(2, 8, '2026-03-25', '11:00:00', '11:30:00', 'Schedule planning for Summer 2026', 'REQUESTED');

-- 14) appointment_suggestions (for denied appointment)
INSERT INTO appointment_suggestions (appointment_id, suggested_date, suggested_start_time, suggested_end_time, message) VALUES
(6, '2026-03-16', '10:00:00', '10:30:00', 'I am unavailable on March 13. Could we meet on March 16 instead?');

-- 15) signin_requests
INSERT INTO signin_requests (user_id, requested_at, handled_by_user_id, decision, decision_note) VALUES
(1, '2026-01-10 09:00:00', 10, 'APPROVED', 'Verified student status'),
(2, '2026-01-10 09:15:00', 10, 'APPROVED', 'Verified student status'),
(3, '2026-01-10 09:30:00', 10, 'APPROVED', 'Verified student status'),
(4, '2026-01-12 10:00:00', NULL, 'PENDING', NULL),
(5, '2026-01-11 08:00:00', 11, 'APPROVED', 'Verified student status');

-- 16) timetable_changes
INSERT INTO timetable_changes (admin_user_id, section_id, change_type, old_value, new_value) VALUES
(10, 1, 'ROOM_CHANGE', 'Erie Hall 1120', 'Erie Hall 1118'),
(10, 3, 'TIME_CHANGE', 'MON 10:00-11:20', 'MON 11:30-12:50'),
(11, 6, 'INSTRUCTOR_CHANGE', 'Dr. Frank Miller', 'Dr. Grace Wang');
