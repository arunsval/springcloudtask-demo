package com.arun.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.arun.demo.constants.SqlConstants.CREATE_TABLE;

@Configuration
@EnableTask
@RequiredArgsConstructor
public class TaskConfiguration {
    private final DataSource dataSource;

    @Bean
    public CommandLineRunner commandLineRunner(){
        return args -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute(CREATE_TABLE);
        };
    }
}
