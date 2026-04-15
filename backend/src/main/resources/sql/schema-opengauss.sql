CREATE TABLE IF NOT EXISTS sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(64) NOT NULL,
    phone VARCHAR(20),
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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

CREATE INDEX idx_product_batch_base_id ON product_batch (base_id);
CREATE INDEX idx_production_record_batch_id ON production_record (batch_id);
CREATE INDEX idx_production_record_operation_time ON production_record (operation_time);
CREATE INDEX idx_inspection_report_batch_id ON inspection_report (batch_id);
CREATE INDEX idx_trace_code_batch_id ON trace_code (batch_id);
CREATE INDEX idx_recall_record_batch_id ON recall_record (batch_id);
