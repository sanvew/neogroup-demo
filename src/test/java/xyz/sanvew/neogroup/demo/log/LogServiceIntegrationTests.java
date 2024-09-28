package xyz.sanvew.neogroup.demo.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.CannotCreateTransactionException;
import xyz.sanvew.neogroup.demo.IntegrationTestBase;
import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;
import xyz.sanvew.neogroup.demo.log.persistence.repository.LogRepository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Timeout(value = 5, unit = TimeUnit.SECONDS)
class LogServiceIntegrationTests extends IntegrationTestBase {

    @Autowired
    LogRepository logRepository;

    @Autowired
    LogService logService;

    @BeforeEach
    void clearDb() {
        logRepository.deleteAll();
    }

    @Test
    void persistLogs_sequential() throws IOException {
        final var LOGS_COUNT = 100;

        for (int i = 0; i < LOGS_COUNT; ++i) {
            logService.saveLog();
        }
        logService.flushLogs();
        checkLogsIntegrity(LOGS_COUNT);
    }

    @Nested
    @TestPropertySource(properties = {
            "log.flush.retry-delay=" + DbOutageReconnect.RETRY_DELAY,
            "log.flush.retry-count=" + DbOutageReconnect.RETRY_COUNT,
            "spring.datasource.hikari.connection-timeout=" + DbOutageReconnect.TRANSACTION_TIMEOUT,
    })
    class DbOutageReconnect {
        static final String RETRY_DELAY = "300";
        static final String RETRY_COUNT = "5";
        static final String TRANSACTION_TIMEOUT = "300";

        @Autowired
        LogService logServiceDbOutageReconnect;

        @Test
        void persistLogs_success() throws IOException, InterruptedException {
            final var LOGS_COUNT = 100;

            for (int i = 0; i < LOGS_COUNT; ++i) {
                logServiceDbOutageReconnect.saveLog();
            }

            var disconnectTimeout = (Integer.parseInt(RETRY_DELAY) + Integer.parseInt(TRANSACTION_TIMEOUT))
                    * (Integer.parseInt(RETRY_COUNT) / 2);
            var disconnectThread = disconnectDbTimeout(disconnectTimeout);

            logServiceDbOutageReconnect.flushLogs();
            checkLogsIntegrity(LOGS_COUNT);

            disconnectThread.join();
        }

        @Test
        void persistLogs_failedTimeoutExceeded_caughtTransactionException() throws InterruptedException {
            final var LOGS_COUNT = 100;

            for (int i = 0; i < LOGS_COUNT; ++i) {
                logServiceDbOutageReconnect.saveLog();
            }

            var disconnectTimeout = (Integer.parseInt(RETRY_DELAY) + Integer.parseInt(TRANSACTION_TIMEOUT))
                    * (Integer.parseInt(RETRY_COUNT));
            var disconnectThread = disconnectDbTimeout(disconnectTimeout);

            assertThrows(CannotCreateTransactionException.class, () -> logServiceDbOutageReconnect.flushLogs());

            disconnectThread.join();
        }

        private Thread disconnectDbTimeout(long timeout) {
            var thread = new Thread(() -> {
                try {
                    postgresDisconnect();
                    TimeUnit.MILLISECONDS.sleep(timeout);
                    postgresConnect();
                } catch (IOException | InterruptedException ignored) {}
            });
            thread.start();
            return thread;
        }
    }

    private void checkLogsIntegrity(int expectedSize) throws IOException {
        postgresConnect();
        final var ascLogs = logRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        assertEquals(expectedSize, ascLogs.size());

        LogEntity prevLog = null;
        for (final var currLog : ascLogs) {
            if (prevLog != null) {
                assertTrue(currLog.getLogDateTime().isAfter(prevLog.getLogDateTime()));
            }
            prevLog = currLog;
        }
    }
}