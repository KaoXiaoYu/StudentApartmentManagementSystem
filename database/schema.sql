CREATE DATABASE IF NOT EXISTS sams DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sams;

CREATE TABLE IF NOT EXISTS college (
    college_id VARCHAR(20) PRIMARY KEY,
    college_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS dorm_info (
    dorm_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    college_id VARCHAR(20) NOT NULL,
    building_no CHAR(2) NOT NULL,
    room_no CHAR(3) NOT NULL,
    UNIQUE KEY uk_dorm (college_id, building_no, room_no),
    CONSTRAINT fk_dorm_college FOREIGN KEY (college_id) REFERENCES college(college_id),
    CONSTRAINT ck_dorm_building CHECK (building_no REGEXP '^[0-9]{2}$'),
    CONSTRAINT ck_dorm_room CHECK (room_no REGEXP '^[0-9]{3}$')
);

CREATE TABLE IF NOT EXISTS student_info (
    student_id CHAR(11) PRIMARY KEY,
    phone_number CHAR(11) UNIQUE,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student', 'cadre') NOT NULL DEFAULT 'student',
    college_id VARCHAR(20) NOT NULL,
    building_no CHAR(2),
    room_no CHAR(3),
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_college FOREIGN KEY (college_id) REFERENCES college(college_id),
    CONSTRAINT fk_student_dorm FOREIGN KEY (college_id, building_no, room_no)
        REFERENCES dorm_info(college_id, building_no, room_no)
);

CREATE TABLE IF NOT EXISTS teacher_info (
    teacher_id CHAR(11) PRIMARY KEY,
    phone_number CHAR(11) UNIQUE,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('college_teacher', 'school_teacher') NOT NULL DEFAULT 'college_teacher',
    college_id VARCHAR(20),
    enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_teacher_college FOREIGN KEY (college_id) REFERENCES college(college_id),
    CONSTRAINT ck_school_teacher_college CHECK (
        (role = 'school_teacher' AND college_id IS NULL)
        OR (role = 'college_teacher' AND college_id IS NOT NULL)
    )
);

CREATE TABLE IF NOT EXISTS violation_info (
    violation_id CHAR(32) PRIMARY KEY,
    college_id VARCHAR(20) NOT NULL,
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

INSERT IGNORE INTO college(college_id, college_name) VALUES
('CS', '计算机学院'),
('EE', '电子信息学院');

INSERT IGNORE INTO dorm_info(college_id, building_no, room_no) VALUES
('CS', '01', '101'),
('CS', '01', '102'),
('CS', '02', '201'),
('EE', '03', '101');

-- 首次登录后，兼容的明文演示密码会自动升级为 PBKDF2 摘要。
INSERT IGNORE INTO student_info
(student_id, phone_number, name, password_hash, role, college_id, building_no, room_no)
VALUES
('20260000001', '13800000001', '基础学生示例', '123456', 'student', 'CS', '01', '101'),
('20260000002', '13800000002', '学生干部示例', '123456', 'cadre', 'CS', '01', '102');

INSERT IGNORE INTO teacher_info
(teacher_id, phone_number, name, password_hash, role, college_id)
VALUES
('90000000001', '13900000001', '学院教师示例', '123456', 'college_teacher', 'CS'),
('90000000002', '13900000002', '校级教师示例', '123456', 'school_teacher', NULL);
