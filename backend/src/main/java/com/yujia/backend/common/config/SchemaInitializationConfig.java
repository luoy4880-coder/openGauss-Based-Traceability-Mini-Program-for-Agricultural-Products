package com.yujia.backend.common.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SchemaInitializationConfig {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void initializeSchema() {
        createSequenceIfMissing("seq_company_code");
        createSequenceIfMissing("seq_logistics_code");
        createTableIfMissing("product_item", """
                CREATE TABLE product_item (
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
                )
                """);
        createTableIfMissing("scan_log", """
                CREATE TABLE scan_log (
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
                )
                """);
        createTableIfMissing("logistics_record", """
                CREATE TABLE logistics_record (
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
                )
                """);

        addColumnIfMissing("user_feedback", "ai_category", "ALTER TABLE user_feedback ADD COLUMN ai_category VARCHAR(32)");
        addColumnIfMissing("user_feedback", "ai_priority", "ALTER TABLE user_feedback ADD COLUMN ai_priority SMALLINT");
        addColumnIfMissing("user_feedback", "batch_id", "ALTER TABLE user_feedback ADD COLUMN batch_id BIGINT");
        addColumnIfMissing("user_feedback", "risk_level", "ALTER TABLE user_feedback ADD COLUMN risk_level VARCHAR(16)");
        addColumnIfMissing("user_feedback", "urgent_flag", "ALTER TABLE user_feedback ADD COLUMN urgent_flag SMALLINT DEFAULT 0");
        addColumnIfMissing("user_feedback", "ai_summary", "ALTER TABLE user_feedback ADD COLUMN ai_summary VARCHAR(255)");
        addColumnIfMissing("user_feedback", "assignee_user_id", "ALTER TABLE user_feedback ADD COLUMN assignee_user_id BIGINT");
        addColumnIfMissing("user_feedback", "linked_task_id", "ALTER TABLE user_feedback ADD COLUMN linked_task_id BIGINT");
        addColumnIfMissing("user_feedback", "linked_recall_id", "ALTER TABLE user_feedback ADD COLUMN linked_recall_id BIGINT");
        addColumnIfMissing("user_feedback", "handle_note", "ALTER TABLE user_feedback ADD COLUMN handle_note VARCHAR(255)");
        addColumnIfMissing("user_feedback", "handled_at", "ALTER TABLE user_feedback ADD COLUMN handled_at TIMESTAMP");
        addColumnIfMissing("user_feedback", "updated_at", "ALTER TABLE user_feedback ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

        createIndexIfMissing("idx_product_item_batch_id", "CREATE INDEX idx_product_item_batch_id ON product_item (batch_id)");
        createIndexIfMissing("idx_product_item_trace_id", "CREATE INDEX idx_product_item_trace_id ON product_item (trace_id)");
        createIndexIfMissing("idx_scan_log_trace_id", "CREATE INDEX idx_scan_log_trace_id ON scan_log (trace_id)");
        createIndexIfMissing("idx_scan_log_scanned_at", "CREATE INDEX idx_scan_log_scanned_at ON scan_log (scanned_at)");
        createIndexIfMissing("idx_logistics_record_batch_id", "CREATE INDEX idx_logistics_record_batch_id ON logistics_record (batch_id)");
        createIndexIfMissing("idx_logistics_record_item_id", "CREATE INDEX idx_logistics_record_item_id ON logistics_record (item_id)");
        createIndexIfMissing("idx_user_feedback_status_priority", "CREATE INDEX idx_user_feedback_status_priority ON user_feedback (status, ai_priority)");
        createIndexIfMissing("idx_user_feedback_assignee", "CREATE INDEX idx_user_feedback_assignee ON user_feedback (assignee_user_id)");
        createIndexIfMissing("idx_user_feedback_risk_level", "CREATE INDEX idx_user_feedback_risk_level ON user_feedback (risk_level, status)");
        createIndexIfMissing("idx_user_feedback_batch_id", "CREATE INDEX idx_user_feedback_batch_id ON user_feedback (batch_id)");

        log.info("Database incremental schema initialization completed.");
    }

    private void createSequenceIfMissing(String sequenceName) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM pg_class WHERE relkind = 'S' AND relname = ?",
                Integer.class,
                sequenceName
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.execute("CREATE SEQUENCE " + sequenceName + " START 1");
    }

    private void createTableIfMissing(String tableName, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM information_schema.tables WHERE table_schema = current_schema() AND table_name = ?",
                Integer.class,
                tableName
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.execute(ddl);
    }

    private void addColumnIfMissing(String tableName, String columnName, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(1)
                FROM information_schema.columns
                WHERE table_schema = current_schema() AND table_name = ? AND column_name = ?
                """,
                Integer.class,
                tableName,
                columnName
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.execute(ddl);
    }

    private void createIndexIfMissing(String indexName, String ddl) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM pg_indexes WHERE schemaname = current_schema() AND indexname = ?",
                Integer.class,
                indexName
        );
        if (count != null && count > 0) {
            return;
        }
        jdbcTemplate.execute(ddl);
    }
}
