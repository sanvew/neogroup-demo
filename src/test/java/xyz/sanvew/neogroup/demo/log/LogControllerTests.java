package xyz.sanvew.neogroup.demo.log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import xyz.sanvew.neogroup.demo.IntegrationTestBase;
import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;
import xyz.sanvew.neogroup.demo.log.persistence.repository.LogRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class LogControllerTests extends IntegrationTestBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    LogRepository logRepository;

    @BeforeEach
    void clearDb() {
        logRepository.deleteAll();
    }

    @Test
    void getLogs_success() throws Exception {
        final var SAMPLE_START_LOCAL_DATE_TIME = LocalDateTime.parse(
                "2024-09-30T12:00:00.000",
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        final var EXPECTED_JSON = """
                [
                    {"logDateTime":"2024-09-30T12:00:00"},
                    {"logDateTime":"2024-09-30T12:00:01"},
                    {"logDateTime":"2024-09-30T12:00:02"},
                    {"logDateTime":"2024-09-30T12:00:03"},
                    {"logDateTime":"2024-09-30T12:00:04"}
                ]
                """.replaceAll("[\\s\\t\\n]+", "");

        logRepository.save(LogEntity.builder()
                                    .logDateTime(SAMPLE_START_LOCAL_DATE_TIME)
                                    .build()
        );
        logRepository.save(LogEntity.builder()
                                    .logDateTime(SAMPLE_START_LOCAL_DATE_TIME.plus(Duration.ofSeconds(1)))
                                    .build()
        );
        logRepository.save(LogEntity.builder()
                                    .logDateTime(SAMPLE_START_LOCAL_DATE_TIME.plus(Duration.ofSeconds(2)))
                                    .build()
        );
        logRepository.save(LogEntity.builder().
                                    logDateTime(SAMPLE_START_LOCAL_DATE_TIME.plus(Duration.ofSeconds(3)))
                                    .build()
        );
        logRepository.save(LogEntity.builder()
                                    .logDateTime(SAMPLE_START_LOCAL_DATE_TIME.plus(Duration.ofSeconds(4)))
                                    .build()
        );

        this.mockMvc.perform(get("/logs"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(EXPECTED_JSON));
    }
}