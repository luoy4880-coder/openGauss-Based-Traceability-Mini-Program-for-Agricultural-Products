CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(64) NOT NULL,
    phone VARCHAR(20),
    openid VARCHAR(128),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'uk_sys_user_openid') THEN
        CREATE UNIQUE INDEX uk_sys_user_openid ON sys_user (openid);
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_code VARCHAR(64) NOT NULL UNIQUE,
    role_name VARCHAR(64) NOT NULL,
    remark VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO sys_role (role_code, role_name, remark)
SELECT 'ADMIN', '系统管理员', '默认管理员角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'ADMIN');

INSERT INTO sys_role (role_code, role_name, remark)
SELECT 'OPERATOR', '业务员', '默认业务操作角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'OPERATOR');

INSERT INTO sys_role (role_code, role_name, remark)
SELECT 'USER', '小程序用户', '默认小程序用户角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_code = 'USER');

CREATE TABLE IF NOT EXISTS base_info (
    id BIGSERIAL PRIMARY KEY,
    base_code VARCHAR(64) NOT NULL UNIQUE,
    base_name VARCHAR(128) NOT NULL,
    manager_name VARCHAR(64),
    contact_phone VARCHAR(20),
    province VARCHAR(32),
    city VARCHAR(32),
    district VARCHAR(32),
    address VARCHAR(255),
    acreage DECIMAL(10, 2),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS product_batch (
    id BIGSERIAL PRIMARY KEY,
    batch_code VARCHAR(64) NOT NULL UNIQUE,
    base_id BIGINT NOT NULL,
    product_name VARCHAR(128) NOT NULL,
    product_category VARCHAR(64),
    planting_date DATE,
    expected_harvest_date DATE,
    actual_harvest_date DATE,
    quantity DECIMAL(12, 2),
    unit VARCHAR(16),
    batch_status SMALLINT NOT NULL DEFAULT 1,
    recall_status SMALLINT NOT NULL DEFAULT 0,
    remark VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS production_record (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    record_type VARCHAR(32) NOT NULL,
    operation_time TIMESTAMP NOT NULL,
    operator_name VARCHAR(64),
    material_name VARCHAR(128),
    dosage VARCHAR(64),
    content TEXT NOT NULL,
    attachment_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inspection_report (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    report_no VARCHAR(64) NOT NULL UNIQUE,
    agency_name VARCHAR(128) NOT NULL,
    inspector_name VARCHAR(64),
    inspection_time TIMESTAMP NOT NULL,
    result_status SMALLINT NOT NULL DEFAULT 1,
    conclusion TEXT,
    report_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS trace_code (
    id BIGSERIAL PRIMARY KEY,
    trace_id VARCHAR(64) NOT NULL UNIQUE,
    batch_id BIGINT NOT NULL,
    qr_content TEXT NOT NULL,
    sign_value VARCHAR(255),
    code_status SMALLINT NOT NULL DEFAULT 1,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recall_record (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    recall_level SMALLINT NOT NULL DEFAULT 1,
    reason TEXT NOT NULL,
    recall_status SMALLINT NOT NULL DEFAULT 1,
    notice_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    type VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,
    contact VARCHAR(128),
    trace_id VARCHAR(64),
    batch_id BIGINT,
    ai_category VARCHAR(32),
    ai_priority SMALLINT,
    risk_level VARCHAR(16),
    urgent_flag SMALLINT DEFAULT 0,
    ai_summary VARCHAR(255),
    assignee_user_id BIGINT,
    linked_task_id BIGINT,
    linked_recall_id BIGINT,
    handle_note VARCHAR(255),
    status SMALLINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_base_code') THEN
        CREATE SEQUENCE seq_base_code START 1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_batch_code') THEN
        CREATE SEQUENCE seq_batch_code START 1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_report_no') THEN
        CREATE SEQUENCE seq_report_no START 1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_recall_code') THEN
        CREATE SEQUENCE seq_recall_code START 1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_logistics_code') THEN
        CREATE SEQUENCE seq_logistics_code START 1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_company_code') THEN
        CREATE SEQUENCE seq_company_code START 1;
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS product_item (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    item_code VARCHAR(96) NOT NULL UNIQUE,
    trace_id VARCHAR(64) NOT NULL UNIQUE,
    qr_content TEXT NOT NULL,
    sign_value VARCHAR(255),
    item_status SMALLINT NOT NULL DEFAULT 1,
    scan_count INTEGER NOT NULL DEFAULT 0,
    first_scanned_at TIMESTAMP,
    last_scanned_at TIMESTAMP,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS scan_log (
    id BIGSERIAL PRIMARY KEY,
    trace_id VARCHAR(64) NOT NULL,
    item_id BIGINT,
    batch_id BIGINT,
    scan_source VARCHAR(32),
    ip_address VARCHAR(64),
    user_agent VARCHAR(255),
    verify_result SMALLINT NOT NULL DEFAULT 1,
    risk_message VARCHAR(255),
    scanned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS logistics_record (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    item_id BIGINT,
    logistics_code VARCHAR(64) NOT NULL,
    node_type VARCHAR(32) NOT NULL,
    node_name VARCHAR(64) NOT NULL,
    operation_time TIMESTAMP NOT NULL,
    operator_name VARCHAR(64),
    contact_phone VARCHAR(20),
    location VARCHAR(255),
    temperature VARCHAR(32),
    humidity VARCHAR(32),
    attachment_url VARCHAR(255),
    remark VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'batch_id'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN batch_id BIGINT;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'ai_category'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN ai_category VARCHAR(32);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'ai_priority'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN ai_priority SMALLINT;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'ai_summary'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN ai_summary VARCHAR(255);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'risk_level'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN risk_level VARCHAR(16);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'urgent_flag'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN urgent_flag SMALLINT DEFAULT 0;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'assignee_user_id'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN assignee_user_id BIGINT;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'linked_task_id'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN linked_task_id BIGINT;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'linked_recall_id'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN linked_recall_id BIGINT;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'handle_note'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN handle_note VARCHAR(255);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'handled_at'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN handled_at TIMESTAMP;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'updated_at'
    ) THEN
        ALTER TABLE user_feedback ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_product_batch_base_id') THEN
        CREATE INDEX idx_product_batch_base_id ON product_batch (base_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_production_record_batch_id') THEN
        CREATE INDEX idx_production_record_batch_id ON production_record (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_production_record_operation_time') THEN
        CREATE INDEX idx_production_record_operation_time ON production_record (operation_time);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_inspection_report_batch_id') THEN
        CREATE INDEX idx_inspection_report_batch_id ON inspection_report (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_trace_code_batch_id') THEN
        CREATE INDEX idx_trace_code_batch_id ON trace_code (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_recall_record_batch_id') THEN
        CREATE INDEX idx_recall_record_batch_id ON recall_record (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_feedback_created_at') THEN
        CREATE INDEX idx_user_feedback_created_at ON user_feedback (created_at);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_feedback_status_priority') THEN
        CREATE INDEX idx_user_feedback_status_priority ON user_feedback (status, ai_priority);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_feedback_assignee') THEN
        CREATE INDEX idx_user_feedback_assignee ON user_feedback (assignee_user_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_feedback_risk_level') THEN
        CREATE INDEX idx_user_feedback_risk_level ON user_feedback (risk_level, status);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_user_feedback_batch_id') THEN
        CREATE INDEX idx_user_feedback_batch_id ON user_feedback (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_product_item_batch_id') THEN
        CREATE INDEX idx_product_item_batch_id ON product_item (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_product_item_trace_id') THEN
        CREATE INDEX idx_product_item_trace_id ON product_item (trace_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_scan_log_trace_id') THEN
        CREATE INDEX idx_scan_log_trace_id ON scan_log (trace_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_scan_log_scanned_at') THEN
        CREATE INDEX idx_scan_log_scanned_at ON scan_log (scanned_at);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_logistics_record_batch_id') THEN
        CREATE INDEX idx_logistics_record_batch_id ON logistics_record (batch_id);
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_logistics_record_item_id') THEN
        CREATE INDEX idx_logistics_record_item_id ON logistics_record (item_id);
    END IF;
END
$$;
