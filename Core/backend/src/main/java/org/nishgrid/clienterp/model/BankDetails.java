package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bank_details")
@Getter
@Setter
public class BankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bank_id")
    private Long bankId;

    @Column(nullable = false, name = "account_name")
    private String accountName;

    @Column(nullable = false, name = "bank_name")
    private String bankName;

    @Column(name = "branch_name")
    private String branchName;

    @Column(nullable = false, name = "account_number")
    private String accountNumber;

    @Column(nullable = false, name = "ifsc_code")
    private String ifscCode;

    // ✅ Changed to match LONGBLOB column in DB
    @Lob
    @Column(name = "qr_code_data", columnDefinition = "LONGBLOB")
    private byte[] qrCodeData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.Active;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        Active, Inactive
    }
}
