package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "license_details")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ Fix for ByteBuddy proxy issue
public class LicenseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License key is required")
    @Column(unique = true, nullable = false)
    private String licenseKey;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    // ✅ Relationship unchanged — client_id saving will still work
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_license_client"))
    @JsonBackReference
    private ClientDetails client;

    // ✅ Helper method (optional)
    public void linkClient(ClientDetails client) {
        this.client = client;
        if (client != null && client.getLicenseDetails() != this) {
            client.setLicenseDetails(this);
        }
    }
}
