package com.arun.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.PassThroughCommandLineArgsProvider;
import org.springframework.cloud.task.batch.partition.SimpleEnvironmentVariablesProvider;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Configuration
@EnableBatchProcessing
@EnableTask
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ParitionedBatchJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DelegatingResourceLoader delegatingResourceLoader;
    private final Environment environment;
    private final JobConfig jobConfig;
    private final WorkerConfig workerConfig;

    @Bean
    public Step masterStep(PartitionHandler partitionHandler){
        return this.stepBuilderFactory.get("masterStep")
                .partitioner("workerStep",partitioner())
                .partitionHandler(partitionHandler)
                .gridSize(10)
                .step(workerConfig.workerStep())
                .build();
    }

    @Bean
    public PartitionHandler partitionHandler(TaskLauncher taskLauncher,
                                             JobExplorer jobExplorer,
                                             TaskRepository taskRepository) {
        log.info("RUNNING PARTITION HANDLER");
        Resource resource = this.delegatingResourceLoader
                .getResource(jobConfig.getMavenUrl());
        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher,
                jobExplorer,
                resource,
                "workerStep",
                taskRepository);
        List<String> commandLineArgs = new ArrayList<>();
        commandLineArgs.add("--spring.profiles.active=worker");
        commandLineArgs.add("--spring.cloud.task.initialize-enabled=false");
        commandLineArgs.add("--spring.batch.initializer.enabled=false");

        partitionHandler.setCommandLineArgsProvider(new PassThroughCommandLineArgsProvider(commandLineArgs));
        partitionHandler.setEnvironmentVariablesProvider(new SimpleEnvironmentVariablesProvider(this.environment));
        partitionHandler.setMaxWorkers(5);
        partitionHandler.setApplicationName("PartitionedBatchtask");
        return partitionHandler;
    }

    public Partitioner partitioner() {
        log.info("RUNNING PARTITIONER");
        return new Partitioner() {
            @Override
            public Map<String, ExecutionContext> partition(int gridSize) {
                Map<String,ExecutionContext> partitions = new HashMap<>(gridSize);
                IntStream.range(1,10)
                        .forEach(x-> {
                            ExecutionContext context = new ExecutionContext();
                            context.put("from", x);
                            context.put("to",x+1);
                            context.put("partitionNumber",x);
                            partitions.put("partition" + x, context);
                        });
                log.info(partitions.toString());
                return partitions;
            }
        };
    }

    @Bean
    @Profile("master")
    public Job partitionedJob(PartitionHandler partitionHandler){
        return this.jobBuilderFactory.get("partitionedJob")
                .incrementer(new RunIdIncrementer())
                .start(masterStep(partitionHandler))
                .build();
    }
}
