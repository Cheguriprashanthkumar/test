package org.nishgrid.clienterp.dto;

public class DebitNoteFileResponse {
    private Long id;
    private Long debitNoteId;
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private String uploadedBy;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDebitNoteId() { return debitNoteId; }
    public void setDebitNoteId(Long debitNoteId) { this.debitNoteId = debitNoteId; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileDownloadUri() { return fileDownloadUri; }
    public void setFileDownloadUri(String fileDownloadUri) { this.fileDownloadUri = fileDownloadUri; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }
}