package org.nishgrid.clienterp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.nishgrid.clienterp.model.Customer;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerDTO {
    private Long customerId;
    private String name;
    private String mobile;
    private String email;
    private String address;
    private String gstin;
    private LocalDate createdAt;

    // Helper method for frontend compatibility
    public Long getId() {
        return this.customerId;
    }

    public static CustomerDTO fromEntity(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setName(customer.getName());
        dto.setMobile(customer.getMobile());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setGstin(customer.getGstin());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
}