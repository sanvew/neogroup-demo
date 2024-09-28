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
public class LoggingJob implements Job {
    private static final String JOB_NAME = "LOGGING_JOB";
    public static final JobKey JOB_KEY = JobKey.jobKey(JOB_NAME);
    public static final TriggerKey TRIGGER_KEY = TriggerKey.triggerKey(JOB_NAME);

    private final LogService logService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logService.saveLog();
    }
}
