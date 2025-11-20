package org.nishgrid.clienterp.repository;

import org.nishgrid.clienterp.model.DebitNoteFile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DebitNoteFileRepository extends JpaRepository<DebitNoteFile, Long> {
    List<DebitNoteFile> findByDebitNoteDebitNoteId(Long debitNoteId);
}