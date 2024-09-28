package xyz.sanvew.neogroup.demo.log.web.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LogDto {
    @Getter
    private final LocalDateTime logDateTime;

    public static LogDto fromEntity(LogEntity entity) {
        return new LogDto(entity.getLogDateTime());

    }
}
