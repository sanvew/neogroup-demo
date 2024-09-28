package xyz.sanvew.neogroup.demo.log.job;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "log.enabled", havingValue = "true")
@RequiredArgsConstructor
public class LogJobConfig {

    @Bean("loggingJobDetail")
    public JobDetail loggingJobDetail() {
        return JobBuilder.newJob()
                         .ofType(LoggingJob.class)
                         .withIdentity(LoggingJob.JOB_KEY)
                         .storeDurably()
                         .build();
    }

    @Bean("loggingJobTrigger")
    public Trigger loggingJobTrigger(
            @Qualifier("loggingJobDetail") JobDetail jobDetail,
            @Value("${log.log.cron}") String logCronExpression
    ) {
        final ScheduleBuilder<CronTrigger> scheduleBuilder = CronScheduleBuilder
                .cronSchedule(logCronExpression);

        return TriggerBuilder.newTrigger()
                             .forJob(jobDetail)
                             .withIdentity(LoggingJob.TRIGGER_KEY)
                             .withSchedule(scheduleBuilder)
                             .build();
    }

    @Bean("logFlushingJobDetail")
    public JobDetail logFlushingJobDetail() {
        return JobBuilder.newJob()
                         .ofType(LogFlushingJob.class)
                         .withIdentity(LogFlushingJob.JOB_KEY)
                         .storeDurably()
                         .build();
    }

    @Bean("logFlushingJobTrigger")
    public Trigger logFlushingJobTrigger(
            @Qualifier("logFlushingJobDetail") JobDetail jobDetail,
            @Value("${log.flush.cron}") String flushCronExpression
    ) {
        final ScheduleBuilder<CronTrigger> scheduleBuilder = CronScheduleBuilder
                .cronSchedule(flushCronExpression)
                .withMisfireHandlingInstructionDoNothing();

        return TriggerBuilder.newTrigger()
                             .forJob(jobDetail)
                             .withIdentity(LogFlushingJob.TRIGGER_KEY)
                             .withSchedule(scheduleBuilder)
                             .build();
    }
}
