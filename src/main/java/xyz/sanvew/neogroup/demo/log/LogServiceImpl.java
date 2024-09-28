package xyz.sanvew.neogroup.demo.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;
import xyz.sanvew.neogroup.demo.log.persistence.repository.LogRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@RequiredArgsConstructor
@Log4j2
public class LogServiceImpl implements LogService {
    private final Deque<LogEntity> logsDeque = new ConcurrentLinkedDeque<>();

    private final LogRepository logRepository;

    @Override
    public void saveLog() {
        final LogEntity entity = LogEntity.builder()
                                          .logDateTime(LocalDateTime.now())
                                          .build();
        logsDeque.addLast(entity);
    }

    @Override
    public List<LogEntity> getLogs() {
        return logRepository.findAll();
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = SQLException.class,
            maxAttemptsExpression = "${log.flush.retry-count:5}",
            backoff = @Backoff(delayExpression = "${log.flush.retry-delay:5000}"),
            listeners = {"logFlushRetryListener"}
    )
    public void flushLogs() {
        final List<LogEntity> writtenLogs = logRepository.saveAllAndFlush(logsToWrite());
        final int writtenLogsSize = writtenLogs.size();
        log.info("Saved logs total: %d".formatted(writtenLogsSize));
        removeWrittenLogsFromDeque(writtenLogsSize);
    }

    private List<LogEntity> logsToWrite() {
        return List.copyOf(logsDeque);
    }

    private void removeWrittenLogsFromDeque(int logsSize) {
        for (int i = 0; i < logsSize; ++i) {
            logsDeque.removeFirst();
        }
    }
}
