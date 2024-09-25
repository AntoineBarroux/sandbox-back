CREATE TABLE IF NOT EXISTS employee (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    position VARCHAR(80) NOT NULL,
    supervisor_id UUID,
    CONSTRAINT fk_employee_supervisor FOREIGN KEY(supervisor_id) REFERENCES employee(id) ON DELETE SET NULL
);