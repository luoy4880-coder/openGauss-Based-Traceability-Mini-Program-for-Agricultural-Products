CREATE TABLE system_task (
    id BIGSERIAL PRIMARY KEY,
    task_type VARCHAR(64) NOT NULL,
    biz_type VARCHAR(64) NOT NULL,
    biz_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    description VARCHAR(500),
    priority SMALLINT NOT NULL DEFAULT 3,
    status SMALLINT NOT NULL DEFAULT 0,
    assignee_user_id BIGINT,
    source_type VARCHAR(32) NOT NULL DEFAULT 'SYSTEM',
    due_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uk_system_task_unique ON system_task (task_type, biz_type, biz_id);
CREATE INDEX idx_system_task_status_priority ON system_task (status, priority);
CREATE INDEX idx_system_task_assignee ON system_task (assignee_user_id);

ALTER TABLE system_task OWNER TO yujia;
ALTER SEQUENCE system_task_id_seq OWNER TO yujia;

GRANT USAGE ON SCHEMA public TO yujia;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE system_task TO yujia;
GRANT USAGE, SELECT, UPDATE ON SEQUENCE system_task_id_seq TO yujia;
