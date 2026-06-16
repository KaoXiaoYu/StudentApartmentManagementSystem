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

INSERT IGNORE INTO college(college_id, college_name) VALUES
('05', '人工智能学院'),
('06', '电子信息学院');

INSERT IGNORE INTO permission_def(permission_code, user_type, permission_name, scope_level, sort_no) VALUES
('STUDENT_VIEW_DORM', 'student', '查看自己寝室违规情况', 'dorm', 10),
('STUDENT_VIEW_COLLEGE', 'student', '查看自己教学院违规情况', 'college', 20),
('STUDENT_VIEW_SCHOOL', 'student', '查看全校寝室违规情况', 'school', 30),
('STUDENT_SUBMIT_COLLEGE', 'student', '提交自己教学院内的违规记录', 'college', 40),
('STUDENT_SUBMIT_SCHOOL', 'student', '提交全校范围内的违规记录', 'school', 50),
('STUDENT_AUDIT_COLLEGE', 'student', '审核自己教学院内的违规记录', 'college', 60),
('STUDENT_AUDIT_SCHOOL', 'student', '审核全校违规记录', 'school', 70),
('TEACHER_VIEW_COLLEGE', 'teacher', '查看自己教学院的违规情况', 'college', 110),
('TEACHER_VIEW_SCHOOL', 'teacher', '查看全校的违规情况', 'school', 120),
('TEACHER_AUDIT_COLLEGE', 'teacher', '审核自己教学院的违规情况', 'college', 130),
('TEACHER_AUDIT_SCHOOL', 'teacher', '审核全校违规情况', 'school', 140),
('TEACHER_GRANT_STUDENT_COLLEGE', 'teacher', '赋予学生院级权限', 'college', 150),
('TEACHER_GRANT_STUDENT_SCHOOL', 'teacher', '赋予学生校级权限', 'school', 160);

INSERT IGNORE INTO dorm_info(college_id, building_no, room_no) VALUES
('05', '01', '101'),
('05', '01', '102'),
('05', '02', '201'),
('06', '03', '101');

-- 初始密码为 123456；首次登录未绑定手机号和宿舍的学生需要补全资料。
INSERT IGNORE INTO student_info
(student_id, phone_number, name, password_hash, role, college_id, major_code, study_years, class_code, serial_no,
 building_no, room_no, first_login_required)
VALUES
('26050540101', '13800000001', '基础学生示例', '123456', 'student', '05', '05', '4', '01', '01', '01', '101', 0),
('26050540102', '13800000002', '学生干部示例', '123456', 'student', '05', '05', '4', '01', '02', '01', '102', 0),
('26050540201', NULL, '待绑定学生示例', '123456', 'student', '05', '05', '4', '02', '01', NULL, NULL, 1);

INSERT IGNORE INTO student_permission(student_id, permission_code) VALUES
('26050540102', 'STUDENT_VIEW_COLLEGE'),
('26050540102', 'STUDENT_SUBMIT_COLLEGE');

INSERT IGNORE INTO teacher_info
(teacher_id, phone_number, name, password_hash, role, college_id, class_code)
VALUES
('90000000001', '13900000001', '班级教师示例', '123456', 'teacher', '05', '01'),
('90000000002', '13900000002', '校级教师示例', '123456', 'teacher', NULL, NULL);

INSERT IGNORE INTO teacher_permission(teacher_id, permission_code) VALUES
('90000000002', 'TEACHER_VIEW_SCHOOL'),
('90000000002', 'TEACHER_AUDIT_SCHOOL'),
('90000000002', 'TEACHER_GRANT_STUDENT_SCHOOL');
