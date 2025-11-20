package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString(exclude = "creditNote")
@Entity
@Table(name = "credit_note_files")
public class CreditNoteFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_note_id", nullable = false)
    @JsonIgnore
    private CreditNote creditNote;

    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @CreationTimestamp
    @Column(name = "uploaded_at", updatable = false)
    private LocalDateTime uploadedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditNoteFile that = (CreditNoteFile) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }
}