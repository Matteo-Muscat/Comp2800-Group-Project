-- ============================================
-- myAdvice Student Advising System
-- Database Schema - All Tables
-- COMP 2800 - Group 18
-- ============================================

-- 1) users
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    password VARCHAR(255),
    role ENUM('STUDENT', 'FACULTY', 'STAFF') NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'DENIED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2) students
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    major VARCHAR(100),
    program VARCHAR(100),
    year_level INT,
    email VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 3) faculty
CREATE TABLE faculty (
    faculty_id INT PRIMARY KEY,
    department VARCHAR(100),
    office_location VARCHAR(100),
    research_interests TEXT,
    email VARCHAR(100),
    FOREIGN KEY (faculty_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 4) courses
CREATE TABLE courses (
    course_code VARCHAR(20) PRIMARY KEY,
    course_name VARCHAR(150) NOT NULL,
    credits INT,
    description TEXT
) ENGINE=InnoDB;

-- 5) student_completed_courses
CREATE TABLE student_completed_courses (
    student_id INT NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    term_id INT,
    grade VARCHAR(5),
    PRIMARY KEY (student_id, course_code),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_code) REFERENCES courses(course_code)
) ENGINE=InnoDB;

-- 6) course_prerequisites
CREATE TABLE course_prerequisites (
    course_code VARCHAR(20) NOT NULL,
    prereq_course_code VARCHAR(20) NOT NULL,
    PRIMARY KEY (course_code, prereq_course_code),
    FOREIGN KEY (course_code) REFERENCES courses(course_code),
    FOREIGN KEY (prereq_course_code) REFERENCES courses(course_code)
) ENGINE=InnoDB;

-- 7) terms
CREATE TABLE terms (
    term_id INT AUTO_INCREMENT PRIMARY KEY,
    term_name VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE
) ENGINE=InnoDB;

-- 8) sections
CREATE TABLE sections (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL,
    term_id INT NOT NULL,
    section_number VARCHAR(10),
    instructor_user_id INT,
    building VARCHAR(50),
    room VARCHAR(20),
    FOREIGN KEY (course_code) REFERENCES courses(course_code),
    FOREIGN KEY (term_id) REFERENCES terms(term_id),
    FOREIGN KEY (instructor_user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 9) section_meetings
CREATE TABLE section_meetings (
    meeting_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    day_of_week ENUM('MON', 'TUE', 'WED', 'THU', 'FRI') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
) ENGINE=InnoDB;

-- 10) student_schedule
CREATE TABLE student_schedule (
    student_id INT NOT NULL,
    term_id INT NOT NULL,
    section_id INT NOT NULL,
    PRIMARY KEY (student_id, term_id, section_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (term_id) REFERENCES terms(term_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
) ENGINE=InnoDB;

-- 11) advising_categories
CREATE TABLE advising_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT
) ENGINE=InnoDB;

-- 12) advisors
CREATE TABLE advisors (
    advisor_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    display_name VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (category_id) REFERENCES advising_categories(category_id)
) ENGINE=InnoDB;

-- 13) appointments
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    advisor_id INT NOT NULL,
    requested_date DATE NOT NULL,
    requested_start_time TIME NOT NULL,
    requested_end_time TIME,
    reason TEXT,
    status ENUM('REQUESTED', 'APPROVED', 'DENIED', 'CANCELLED') DEFAULT 'REQUESTED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (advisor_id) REFERENCES advisors(advisor_id)
) ENGINE=InnoDB;

-- 14) appointment_suggestions
CREATE TABLE appointment_suggestions (
    suggestion_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id INT NOT NULL,
    suggested_date DATE NOT NULL,
    suggested_start_time TIME NOT NULL,
    suggested_end_time TIME,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id)
) ENGINE=InnoDB;

-- 15) signin_requests
CREATE TABLE signin_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    handled_by_user_id INT,
    decision ENUM('PENDING', 'APPROVED', 'DENIED') DEFAULT 'PENDING',
    decision_note TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (handled_by_user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

-- 16) timetable_changes
CREATE TABLE timetable_changes (
    change_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id INT NOT NULL,
    section_id INT NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id),
    FOREIGN KEY (section_id) REFERENCES sections(section_id)
) ENGINE=InnoDB;
