ALTER TABLE system_task
    ADD COLUMN IF NOT EXISTS claimed_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS completed_by_user_id BIGINT;

COMMENT ON COLUMN system_task.claimed_at IS '任务认领时间';
COMMENT ON COLUMN system_task.completed_by_user_id IS '任务完成人用户ID';

GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE system_task TO yujia;
