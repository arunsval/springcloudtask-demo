package com.arun.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableBatchProcessing
public class SimpleBatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    @Bean
    public Job job2(){
        return this.jobBuilderFactory.get("Job2")
                .start(this.stepBuilderFactory.get("step1")
                        .tasklet(new Tasklet() {
                            @Override
                            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                                log.info("Running Job 2");
                                return RepeatStatus.FINISHED;
                            }
                        }).build()).build();
    }

    @Bean
    public Job job1(){
        return this.jobBuilderFactory.get("Job1")
                .start(this.stepBuilderFactory.get("step1")
                        .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                       log.info("Running Job 1");
                        return RepeatStatus.FINISHED;
                    }
                }).build()).build();
    }


}
