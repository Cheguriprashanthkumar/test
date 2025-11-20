package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "client_photo")
@Data
public class ClientPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This links the photo to the LicenseResponse uniqueId
    @Column(name = "unique_id", unique = true, nullable = false)
    private String uniqueId;

    // This stores the actual image data
    @Lob
    @Column(name = "photo", columnDefinition = "LONGBLOB")
    private byte[] photo;
}
