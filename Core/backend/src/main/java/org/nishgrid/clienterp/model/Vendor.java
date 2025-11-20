package org.nishgrid.clienterp.model;



import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendors")
@Data
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String gstNumber;
    private String contactPerson;
    private String phone;
    @Column(unique = true)
    private String email;
    @Lob
    private String address;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String state;
}