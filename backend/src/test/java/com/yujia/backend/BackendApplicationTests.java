package com.yujia.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 测试数据库是否能连上
    @Test
    void testDatabaseConnection() {
        jdbcTemplate.queryForList("SELECT 1");
        System.out.println("===== openGauss 数据库连接成功！=====");
    }

    // 查看当前数据库里所有表
    @Test
    void listAllTables() {
        String sql = "SELECT table_name " +
                "FROM information_schema.tables " +
                "WHERE table_schema = 'public' " +
                "AND table_type = 'BASE TABLE'";

        List<Map<String, Object>> tables = jdbcTemplate.queryForList(sql);

        System.out.println("===== 当前数据库中的表列表 =====");
        if (tables.isEmpty()) {
            System.out.println("暂无任何表");
        } else {
            for (Map<String, Object> map : tables) {
                System.out.println(map.get("table_name"));
            }
        }
    }

}