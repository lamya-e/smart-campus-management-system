-- Smart Campus Management System - PostgreSQL schema for Supabase
-- Run this script in the Supabase SQL Editor to create all tables.

-- Admin users (login)
CREATE TABLE IF NOT EXISTS admin (
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Teachers
CREATE TABLE IF NOT EXISTS teacher (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    department VARCHAR(255)
);

-- Students
CREATE TABLE IF NOT EXISTS student (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    major      VARCHAR(255)
);

-- Courses (teacher optional)
CREATE TABLE IF NOT EXISTS course (
    id         SERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    teacher_id INTEGER REFERENCES teacher(id)
);

-- Enrollment (student-course many-to-many)
CREATE TABLE IF NOT EXISTS enrollment (
    student_id INTEGER NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    course_id  INTEGER NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, course_id)
);

-- Grades (one grade per student per course)
CREATE TABLE IF NOT EXISTS grade (
    id         SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES student(id) ON DELETE CASCADE,
    course_id  INTEGER NOT NULL REFERENCES course(id) ON DELETE CASCADE,
    value      DOUBLE PRECISION NOT NULL,
    UNIQUE (student_id, course_id)
);

-- Optional: indexes for common lookups
CREATE INDEX IF NOT EXISTS idx_grade_student ON grade(student_id);
CREATE INDEX IF NOT EXISTS idx_grade_course ON grade(course_id);
CREATE INDEX IF NOT EXISTS idx_enrollment_course ON enrollment(course_id);

-- Seed data (idempotent)
INSERT INTO admin (username, password_hash)
VALUES ('admin', 'admin')
ON CONFLICT (username) DO NOTHING;

INSERT INTO teacher (id, name, email, department)
VALUES (1, 'Dr. Jane Smith', 'jane.smith@campus.edu', 'Computer Science')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence for teacher if we inserted id=1 manually (optional)
SELECT setval(pg_get_serial_sequence('teacher', 'id'), (SELECT COALESCE(MAX(id), 1) FROM teacher));
