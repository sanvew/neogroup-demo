package xyz.sanvew.neogroup.demo.log.misc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogFlushRetryListener implements RetryListener {
    @Override
    public <T, E extends Throwable> void onError(
            RetryContext context, RetryCallback<T, E> callback, Throwable throwable
    ) {
        log.error("Unable to flush logs, total attempts: {}, exception: {} {}", context.getRetryCount(), throwable, throwable.getMessage());
    }
}
