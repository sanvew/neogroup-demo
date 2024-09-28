package xyz.sanvew.neogroup.demo.log;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;
import xyz.sanvew.neogroup.demo.log.persistence.repository.LogRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @Mock
    LogRepository logRepository;

    @InjectMocks
    LogServiceImpl logService;

    @Test
    void getLogs() {
        final var STUB_LOG_ENTITY_LIST = List.of(
                LogEntity.builder().logDateTime(LocalDateTime.now()).build(),
                LogEntity.builder().logDateTime(LocalDateTime.now().plus(Duration.ofSeconds(1))).build(),
                LogEntity.builder().logDateTime(LocalDateTime.now().plus(Duration.ofSeconds(2))).build()
        );
        when(logRepository.findAll())
                .thenReturn(STUB_LOG_ENTITY_LIST);

        assertIterableEquals(STUB_LOG_ENTITY_LIST, logService.getLogs());
    }

}