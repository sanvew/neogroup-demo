package xyz.sanvew.neogroup.demo.log;

import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;

import java.util.List;

public interface LogService {
    void saveLog();
    void flushLogs();
    List<LogEntity> getLogs();
}
