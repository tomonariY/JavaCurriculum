package sample.common.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskArchiveScheduler {
    
    private final JobOperator jobOperator;
    private final Job archiveJob;

    public TaskArchiveScheduler(JobOperator jobOperator, Job archiveJob) {
        this.jobOperator = jobOperator;
        this.archiveJob = archiveJob;

    }

    @Scheduled(cron = "0 0 2 7 * *") // 毎日深夜2時
    public void runArchiveJob() throws Exception {
        JobParameters params  = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobOperator.start(archiveJob, params);
    }
}