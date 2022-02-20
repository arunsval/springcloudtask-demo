package com.arun.demo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskConfigurationTests {
    @Autowired
    private DataSource dataSource;

    @Test
    void testTableCreated(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        int result = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ARUN_DEMOTASK", Integer.class);
        assertThat(result).isEqualTo(0);
    }


}

