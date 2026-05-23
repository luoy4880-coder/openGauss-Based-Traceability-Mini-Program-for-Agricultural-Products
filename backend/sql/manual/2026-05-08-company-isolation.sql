-- 公司隔离补丁
-- 目标：
-- 1. 建立 company 主表
-- 2. 为核心业务表补 company_id
-- 3. 回填历史数据
-- 4. 补外键、索引与授权
-- 说明：
-- - 该脚本按“可重复执行”设计
-- - 兼容不支持部分 IF NOT EXISTS 语法的 openGauss / PostgreSQL 兼容环境

CREATE TABLE IF NOT EXISTS company (
    id BIGSERIAL PRIMARY KEY,
    company_code VARCHAR(64) NOT NULL UNIQUE,
    company_name VARCHAR(128) NOT NULL UNIQUE,
    status SMALLINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO company (company_code, company_name, status)
SELECT 'CP-20260508-0001', '默认公司', 1
WHERE NOT EXISTS (
    SELECT 1 FROM company WHERE company_name = '默认公司'
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'sys_user' AND column_name = 'company_id'
    ) THEN
        EXECUTE 'ALTER TABLE sys_user ADD COLUMN company_id BIGINT';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'base_info' AND column_name = 'company_id'
    ) THEN
        EXECUTE 'ALTER TABLE base_info ADD COLUMN company_id BIGINT';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'product_batch' AND column_name = 'company_id'
    ) THEN
        EXECUTE 'ALTER TABLE product_batch ADD COLUMN company_id BIGINT';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'user_feedback' AND column_name = 'company_id'
    ) THEN
        EXECUTE 'ALTER TABLE user_feedback ADD COLUMN company_id BIGINT';
    END IF;
END
$$;

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
  AND bi.company_id IS NOT NULL
  AND (pb.company_id IS NULL OR pb.company_id <> bi.company_id);

UPDATE user_feedback uf
SET company_id = pb.company_id
FROM product_batch pb
WHERE uf.company_id IS NULL
  AND uf.batch_id = pb.id
  AND pb.company_id IS NOT NULL;

UPDATE user_feedback uf
SET company_id = pb.company_id
FROM product_item pi
JOIN product_batch pb ON pb.id = pi.batch_id
WHERE uf.company_id IS NULL
  AND uf.trace_id = pi.trace_id
  AND pb.company_id IS NOT NULL;

UPDATE user_feedback uf
SET company_id = pb.company_id
FROM trace_code tc
JOIN product_batch pb ON pb.id = tc.batch_id
WHERE uf.company_id IS NULL
  AND uf.trace_id = tc.trace_id
  AND pb.company_id IS NOT NULL;

UPDATE user_feedback uf
SET company_id = su.company_id
FROM sys_user su
WHERE uf.company_id IS NULL
  AND uf.user_id = su.id
  AND su.company_id IS NOT NULL;

UPDATE user_feedback
SET company_id = (SELECT id FROM company WHERE company_name = '默认公司')
WHERE company_id IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_sys_user_company'
    ) THEN
        EXECUTE 'ALTER TABLE sys_user ADD CONSTRAINT fk_sys_user_company FOREIGN KEY (company_id) REFERENCES company(id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_base_info_company'
    ) THEN
        EXECUTE 'ALTER TABLE base_info ADD CONSTRAINT fk_base_info_company FOREIGN KEY (company_id) REFERENCES company(id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_product_batch_company'
    ) THEN
        EXECUTE 'ALTER TABLE product_batch ADD CONSTRAINT fk_product_batch_company FOREIGN KEY (company_id) REFERENCES company(id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_user_feedback_company'
    ) THEN
        EXECUTE 'ALTER TABLE user_feedback ADD CONSTRAINT fk_user_feedback_company FOREIGN KEY (company_id) REFERENCES company(id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'idx_sys_user_company_id'
    ) THEN
        EXECUTE 'CREATE INDEX idx_sys_user_company_id ON sys_user(company_id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'idx_base_info_company_id'
    ) THEN
        EXECUTE 'CREATE INDEX idx_base_info_company_id ON base_info(company_id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'idx_product_batch_company_id'
    ) THEN
        EXECUTE 'CREATE INDEX idx_product_batch_company_id ON product_batch(company_id)';
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = 'public' AND indexname = 'idx_user_feedback_company_id'
    ) THEN
        EXECUTE 'CREATE INDEX idx_user_feedback_company_id ON user_feedback(company_id)';
    END IF;
END
$$;

GRANT USAGE ON SCHEMA public TO yujia;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE company TO yujia;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.sequences
        WHERE sequence_name = 'company_id_seq'
    ) THEN
        EXECUTE 'GRANT USAGE, SELECT, UPDATE ON SEQUENCE company_id_seq TO yujia';
    END IF;
END
$$;
