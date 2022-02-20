package com.arun.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Profile("master")
public class SchedulerConfig {
    private final JobLauncher jobLauncher;
    private final Job partitionedJob;

    @Scheduled(fixedDelay = 1,timeUnit = TimeUnit.MINUTES)
//    @Scheduled(cron="-")
    public void runMaster() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        this.jobLauncher.run(partitionedJob, new JobParametersBuilder()
                .addString("runtime", LocalDateTime.now().toString())
                .toJobParameters());
    }
}
