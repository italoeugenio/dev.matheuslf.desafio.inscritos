CREATE TABLE TB_USERS(
    user_id UUID PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role TEXT,
    is_verified BOOLEAN,
    create_at TIMESTAMP,
    update_at TIMESTAMP
);

CREATE TABLE TB_PROJECTS(
    project_id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP
);

CREATE TABLE TB_TASKS(
    task_id UUID PRIMARY KEY,
    title VARCHAR(150),
    description TEXT,
    status TEXT,
    priority TEXT,
    due_time TIMESTAMP,
    project_id UUID REFERENCES TB_PROJECTS(project_id) ON DELETE CASCADE
);

CREATE TABLE TB_VALIDATION_CODES(
    validation_code_id UUID PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    user_id UUID REFERENCES TB_USERS(user_id) ON DELETE CASCADE,
    code_type TEXT NOT NULL,
    confirmed_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    create_at TIMESTAMP
)