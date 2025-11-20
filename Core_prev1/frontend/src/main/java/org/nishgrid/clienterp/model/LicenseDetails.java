package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "license_details")
@Data
public class LicenseDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License key is required")
    private String licenseKey;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;


    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private ClientDetails client;
}
