package org.nishgrid.clienterp.service;

import lombok.RequiredArgsConstructor;
import org.nishgrid.clienterp.dto.CreditNoteFileResponse;
import org.nishgrid.clienterp.model.CreditNote;
import org.nishgrid.clienterp.model.CreditNoteFile;
import org.nishgrid.clienterp.repository.CreditNoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CreditNoteFileService {

    private final CreditNoteRepository creditNoteRepository;
    private final FileStorageService fileStorageService;
    private final CreditNoteAuditLogService auditLogService;

    @Transactional(readOnly = true)
    public List<CreditNoteFileResponse> getFilesForCreditNote(Long creditNoteId) {
        CreditNote creditNote = creditNoteRepository.findById(creditNoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Note not found with id: " + creditNoteId));

        return creditNote.getFiles().stream()
                .map(CreditNoteFileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public CreditNoteFileResponse attachFileToCreditNote(Long creditNoteId, MultipartFile file) {
        CreditNote creditNote = creditNoteRepository.findById(creditNoteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Credit Note not found with id: " + creditNoteId));

        String filePath = fileStorageService.storeFile(file);

        CreditNoteFile creditNoteFile = new CreditNoteFile();
        creditNoteFile.setFilePath(filePath);
        creditNoteFile.setFileType(file.getContentType());

        creditNote.addFile(creditNoteFile);
        creditNoteRepository.save(creditNote);


        auditLogService.log(creditNoteId, "SYSTEM", "Attached file: " + file.getOriginalFilename());

        return CreditNoteFileResponse.fromEntity(creditNoteFile);
    }
}