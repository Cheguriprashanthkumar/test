package org.nishgrid.clienterp.model;



import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_catalog")
@Data
public class ProductCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String purity;
    private String defaultRate;
}
