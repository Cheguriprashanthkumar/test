package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditNoteFileResponse {

    @JsonProperty("fileId")
    private Long fileId;


    @JsonProperty("filePath")
    private String filePath;

    @JsonProperty("fileType")
    private String fileType;

    @JsonProperty("uploadedAt")
    private LocalDateTime uploadedAt;


}