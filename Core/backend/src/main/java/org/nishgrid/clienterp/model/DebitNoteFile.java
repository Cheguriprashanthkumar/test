package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "debit_note_files")
@Getter
@Setter
public class DebitNoteFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debit_note_id", nullable = false)
    private DebitNote debitNote;

    @Column(nullable = false)
    private String filePath;

    private String fileType;
    private String uploadedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
}