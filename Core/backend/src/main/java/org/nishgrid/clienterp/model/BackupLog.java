package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "backup_logs")
@Getter
@Setter
public class BackupLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    private String fileName;
    private Long fileSize; // in bytes

    private String performedBy; // For simplicity, using String. Can be linked to a User entity later.

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime performedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Lob
    private String remarks;

    public enum ActionType {
        Export, Import, Auto_Backup
    }

    public enum Status {
        Success, Failed
    }
}