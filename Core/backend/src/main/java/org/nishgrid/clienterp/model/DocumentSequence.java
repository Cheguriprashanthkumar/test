package org.nishgrid.clienterp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "document_sequences")
public class DocumentSequence {
    @Id
    private String name;
    private Long nextValue;
}