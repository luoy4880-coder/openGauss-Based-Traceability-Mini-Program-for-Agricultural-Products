CREATE TABLE IF NOT EXISTS company (
    id BIGSERIAL PRIMARY KEY,
    company_code VARCHAR(64) NOT NULL UNIQUE,
    company_name VARCHAR(128) NOT NULL UNIQUE,
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'seq_company_code') THEN
        CREATE SEQUENCE seq_company_code START 1;
    END IF;
END
$$;

INSERT INTO company (company_code, company_name, status)
SELECT 'CP-20260508-0001', '默认公司', 1
WHERE NOT EXISTS (SELECT 1 FROM company WHERE company_name = '默认公司');

ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS company_id BIGINT;
ALTER TABLE base_info ADD COLUMN IF NOT EXISTS company_id BIGINT;
ALTER TABLE product_batch ADD COLUMN IF NOT EXISTS company_id BIGINT;
ALTER TABLE user_feedback ADD COLUMN IF NOT EXISTS company_id BIGINT;

UPDATE sys_user
SET company_id = (SELECT id FROM company WHERE company_name = '默认公司')
WHERE company_id IS NULL;

UPDATE base_info
SET company_id = (SELECT id FROM company WHERE company_name = '默认公司')
WHERE company_id IS NULL;

UPDATE product_batch pb
SET company_id = bi.company_id
FROM base_info bi
WHERE pb.base_id = bi.id
  AND (pb.company_id IS NULL OR pb.company_id <> bi.company_id);

UPDATE user_feedback uf
SET company_id = COALESCE(
    uf.company_id,
    uf.batch_id,
    pi.batch_id,
    tc.batch_id
)
WHERE uf.company_id IS NULL;

UPDATE user_feedback uf
SET company_id = pb.company_id
FROM product_batch pb
WHERE uf.company_id = pb.id;

UPDATE user_feedback uf
SET company_id = su.company_id
FROM sys_user su
WHERE uf.company_id IS NULL
  AND uf.user_id = su.id;

ALTER TABLE sys_user
    ADD CONSTRAINT IF NOT EXISTS fk_sys_user_company
    FOREIGN KEY (company_id) REFERENCES company(id);

ALTER TABLE base_info
    ADD CONSTRAINT IF NOT EXISTS fk_base_info_company
    FOREIGN KEY (company_id) REFERENCES company(id);

ALTER TABLE product_batch
    ADD CONSTRAINT IF NOT EXISTS fk_product_batch_company
    FOREIGN KEY (company_id) REFERENCES company(id);

ALTER TABLE user_feedback
    ADD CONSTRAINT IF NOT EXISTS fk_user_feedback_company
    FOREIGN KEY (company_id) REFERENCES company(id);

CREATE INDEX IF NOT EXISTS idx_sys_user_company_id ON sys_user(company_id);
CREATE INDEX IF NOT EXISTS idx_base_info_company_id ON base_info(company_id);
CREATE INDEX IF NOT EXISTS idx_product_batch_company_id ON product_batch(company_id);
CREATE INDEX IF NOT EXISTS idx_user_feedback_company_id ON user_feedback(company_id);
