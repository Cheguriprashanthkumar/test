package org.nishgrid.clienterp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class BackupLog {

    private Long logId;
    private ActionType actionType;
    private String fileName;
    private Long fileSize;
    private String performedBy;
    private LocalDateTime performedAt;
    private Status status;
    private String remarks;

    public enum ActionType {
        Export, Import, Auto_Backup
    }

    public enum Status {
        Success, Failed
    }

}