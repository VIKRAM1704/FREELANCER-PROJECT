-- Flyway V1: Create schema for project service
CREATE TABLE IF NOT EXISTS projects (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  client_id BIGINT,
  title VARCHAR(255),
  description TEXT,
  budget_min DECIMAL(10,2),
  budget_max DECIMAL(10,2),
  duration_days INT,
  required_skills TEXT,
  category VARCHAR(100),
  status VARCHAR(20),
  deadline DATE,
  assigned_freelancer BIGINT,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS proposals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  project_id BIGINT,
  freelancer_id BIGINT,
  cover_letter TEXT,
  proposed_budget DECIMAL(10,2),
  delivery_days INT,
  ai_score DOUBLE,
  status VARCHAR(20),
  submitted_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS project_milestones (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  project_id BIGINT,
  title VARCHAR(255),
  description TEXT,
  amount DECIMAL(10,2),
  due_date DATE,
  status VARCHAR(20),
  completed_at TIMESTAMP NULL
);

-- sample data
INSERT INTO projects (client_id, title, description, budget_min, budget_max, duration_days, required_skills, category, status, deadline, created_at, updated_at)
VALUES (1, 'Sample project', 'This is a sample project.', 100.00, 500.00, 30, '["java","spring"]', 'Software', 'OPEN', DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY), NOW(), NOW());
