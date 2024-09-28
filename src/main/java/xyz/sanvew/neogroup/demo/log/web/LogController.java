package xyz.sanvew.neogroup.demo.log.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.sanvew.neogroup.demo.log.LogService;
import xyz.sanvew.neogroup.demo.log.web.dto.LogDto;

import java.util.List;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    ResponseEntity<List<LogDto>> getLogs() {
        return ResponseEntity.ok(
                logService.getLogs().stream()
                          .map(LogDto::fromEntity)
                          .toList()
        );
    }
}
