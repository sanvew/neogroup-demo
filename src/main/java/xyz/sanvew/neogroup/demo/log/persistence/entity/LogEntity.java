package xyz.sanvew.neogroup.demo.log.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = LogEntity.TABLE_NAME)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntity {

    public static final String TABLE_NAME = "LOG";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime logDateTime;
}
