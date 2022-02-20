package com.arun.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WorkerConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ConfigurableApplicationContext applicationContext;
    private final JobRepository jobRepository;

    @Bean
    @Profile("worker")
    public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer) {
        log.info("RUNNING DEPLOYER WORKERSTEP HANDLER");
        return new DeployerStepExecutionHandler(this.applicationContext, jobExplorer, this.jobRepository);
    }

    @Bean
    public Step workerStep(){
        log.info("RUNNING WORKERSTEP");
        return this.stepBuilderFactory.get("workerStep")
                .tasklet(workerTasklet(null)).build();
    }

    @Bean
    @StepScope
    public Tasklet workerTasklet(final @Value("#{stepExecutionContext['partitionNumber']}") Integer partitionNumber){
        log.info("RUNNING WORKER TASKLET");
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("Worker Step running partition {}", partitionNumber);
                return RepeatStatus.FINISHED;
            }
        };
    }

}
