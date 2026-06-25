CREATE DATABASE IF NOT EXISTS sams DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams;

CREATE TABLE IF NOT EXISTS college (
    college_id CHAR(2) PRIMARY KEY,
    college_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS dorm_info (
    dorm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    college_id CHAR(2) NOT NULL,
    building_no CHAR(2) NOT NULL,
    room_no CHAR(3) NOT NULL,
    UNIQUE KEY uk_dorm (college_id, building_no, room_no),
    CONSTRAINT fk_dorm_college FOREIGN KEY (college_id) REFERENCES college(college_id),
    CONSTRAINT ck_dorm_building CHECK (building_no REGEXP '^[0-9]{2}$'),
    CONSTRAINT ck_dorm_room CHECK (room_no REGEXP '^[0-9]{3}$')
);

CREATE TABLE IF NOT EXISTS class_info (
    class_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    college_id CHAR(2) NOT NULL,
    major_code CHAR(2) NOT NULL,
    major_name VARCHAR(100) NOT NULL,
    major_short_name VARCHAR(20) NOT NULL,
    class_code CHAR(2) NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_class (college_id, major_code, class_code),
    CONSTRAINT fk_class_college FOREIGN KEY (college_id) REFERENCES college(college_id),
    CONSTRAINT ck_class_major CHECK (major_code REGEXP '^[0-9]{2}$'),
    CONSTRAINT ck_class_code CHECK (class_code REGEXP '^[0-9]{2}$')
);

CREATE TABLE IF NOT EXISTS student_info (
    student_id CHAR(11) PRIMARY KEY,
    phone_number CHAR(11) UNIQUE,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'student',
    college_id CHAR(2) NOT NULL,
    major_code CHAR(2) NOT NULL,
    study_years CHAR(1) NOT NULL,
    class_code CHAR(2) NOT NULL,
    serial_no CHAR(2) NOT NULL,
    building_no CHAR(2),
    room_no CHAR(3),
    first_login_required TINYINT(1) NOT NULL DEFAULT 1,
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_student_class (college_id, class_code),
    INDEX idx_student_dorm (college_id, building_no, room_no),
    CONSTRAINT fk_student_college FOREIGN KEY (college_id) REFERENCES college(college_id),
    CONSTRAINT fk_student_dorm FOREIGN KEY (college_id, building_no, room_no)
        REFERENCES dorm_info(college_id, building_no, room_no),
    CONSTRAINT ck_student_id_format CHECK (
        student_id REGEXP '^[0-9]{11}$'
        AND substring(student_id, 3, 2) = college_id
        AND substring(student_id, 5, 2) = major_code
        AND substring(student_id, 7, 1) = study_years
        AND substring(student_id, 8, 2) = class_code
        AND substring(student_id, 10, 2) = serial_no
    )
);

CREATE TABLE IF NOT EXISTS teacher_info (
    teacher_id CHAR(11) PRIMARY KEY,
    phone_number CHAR(11) UNIQUE,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'teacher',
    college_id CHAR(2),
    class_code CHAR(2),
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_teacher_class (college_id, class_code),
    CONSTRAINT fk_teacher_college FOREIGN KEY (college_id) REFERENCES college(college_id)
);

CREATE TABLE IF NOT EXISTS permission_def (
    permission_code VARCHAR(50) PRIMARY KEY,
    user_type ENUM('student', 'teacher') NOT NULL,
    permission_name VARCHAR(100) NOT NULL,
    scope_level ENUM('dorm', 'college', 'school') NOT NULL,
    sort_no INT NOT NULL
);

CREATE TABLE IF NOT EXISTS student_permission (
    student_id CHAR(11) NOT NULL,
    permission_code VARCHAR(50) NOT NULL,
    PRIMARY KEY (student_id, permission_code),
    CONSTRAINT fk_student_permission_student FOREIGN KEY (student_id) REFERENCES student_info(student_id),
    CONSTRAINT fk_student_permission_def FOREIGN KEY (permission_code) REFERENCES permission_def(permission_code)
);

CREATE TABLE IF NOT EXISTS teacher_permission (
    teacher_id CHAR(11) NOT NULL,
    permission_code VARCHAR(50) NOT NULL,
    PRIMARY KEY (teacher_id, permission_code),
    CONSTRAINT fk_teacher_permission_teacher FOREIGN KEY (teacher_id) REFERENCES teacher_info(teacher_id),
    CONSTRAINT fk_teacher_permission_def FOREIGN KEY (permission_code) REFERENCES permission_def(permission_code)
);

CREATE TABLE IF NOT EXISTS violation_info (
    violation_id CHAR(32) PRIMARY KEY,
    college_id CHAR(2) NOT NULL,
    building_no CHAR(2) NOT NULL,
    room_no CHAR(3) NOT NULL,
    violation_type VARCHAR(50) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    occurred_at DATETIME NOT NULL,
    created_by_type ENUM('student', 'teacher') NOT NULL,
    created_by CHAR(11) NOT NULL,
    created_by_name VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_violation_dorm_time (college_id, building_no, room_no, occurred_at),
    CONSTRAINT fk_violation_dorm FOREIGN KEY (college_id, building_no, room_no)
        REFERENCES dorm_info(college_id, building_no, room_no)
);

CREATE TABLE IF NOT EXISTS appeal_info (
    appeal_id CHAR(32) PRIMARY KEY,
    violation_id CHAR(32) NOT NULL,
    student_id CHAR(11) NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
    audit_reply VARCHAR(1000),
    audited_by CHAR(11),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    audited_at DATETIME,
    INDEX idx_appeal_status (status, created_at),
    CONSTRAINT fk_appeal_violation FOREIGN KEY (violation_id) REFERENCES violation_info(violation_id),
    CONSTRAINT fk_appeal_student FOREIGN KEY (student_id) REFERENCES student_info(student_id)
);

CREATE TABLE IF NOT EXISTS remember_token (
    token_id CHAR(32) PRIMARY KEY,
    token_hash CHAR(64) NOT NULL UNIQUE,
    user_type ENUM('student', 'teacher') NOT NULL,
    user_id CHAR(11) NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_token_user (user_type, user_id),
    INDEX idx_token_expiry (expires_at)
);
