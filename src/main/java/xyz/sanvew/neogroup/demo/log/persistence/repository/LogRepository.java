package xyz.sanvew.neogroup.demo.log.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.sanvew.neogroup.demo.log.persistence.entity.LogEntity;

import java.util.UUID;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, UUID> {

}
