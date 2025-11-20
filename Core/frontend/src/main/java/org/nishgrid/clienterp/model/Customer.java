package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDate;
import java.util.Objects;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private Long customerId;
    private String name;
    private String mobile;
    private String email;
    private String address;
    private String gstin;
    private LocalDate createdAt;

    // --- ADD THESE METHODS FOR RELIABLE COMPARISON ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(customerId, customer.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}