package xyz.sanvew.neogroup.demo.log.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;
import xyz.sanvew.neogroup.demo.log.LogService;

@Component
@DisallowConcurrentExecution
@Log4j2
@RequiredArgsConstructor
public class LogFlushingJob implements Job {
    private static final String JOB_NAME = "LOG_FLUSHING_JOB";
    public static final JobKey JOB_KEY = JobKey.jobKey(JOB_NAME);
    public static final TriggerKey TRIGGER_KEY = TriggerKey.triggerKey(JOB_NAME);

    private final LogService logFlushingService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("{} - executing", JOB_NAME);
        try {
            logFlushingService.flushLogs();
        } catch (Exception e) {
            log.error("{} - failed: {}", JOB_NAME, e.getMessage());
        }
    }
}
