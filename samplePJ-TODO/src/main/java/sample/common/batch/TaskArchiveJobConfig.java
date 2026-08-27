package sample.common.batch;

import java.time.LocalDate;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import sample.common.dao.entity.Task;

@Configuration
public class TaskArchiveJobConfig {

    @Value("${batch.archive.threshold-days}")
    private int thresholdDays;

    @Bean
    public MyBatisCursorItemReader<Task> archiveReader(SqlSessionFactory sqlSessionFactory) {
        LocalDate thresholdDate = LocalDate.now().minusDays(thresholdDays);

        return new MyBatisCursorItemReaderBuilder<Task>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("sample.common.dao.mapper.TaskArchiveMapper.selectArchiveTargets")
                .parameterValues(Map.of("thresholdDate", thresholdDate))
                .build();
    }

    @Bean
    public Step archiveStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            MyBatisCursorItemReader<Task> archiveReader,
            TaskArchiveItemProcessor archiveProcessor,
            TaskArchiveItemWriter archiveWriter) {

        return new StepBuilder("archiveStep", jobRepository)
                .<Task, Task>chunk(10)
                .transactionManager(transactionManager)
                .reader(archiveReader)
                .processor(archiveProcessor)
                .writer(archiveWriter)
                .build();
    }

    @Bean
    public Job archiJob(JobRepository jobRepository, Step archiveStep) {
        return new JobBuilder("archivejob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(archiveStep)
                .build();
    }
}
