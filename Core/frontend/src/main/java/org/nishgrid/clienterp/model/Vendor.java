package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vendor {
    private long id;
    private String name;
    private String email;
    private String phone;
    private String gstNumber;
    private String contactPerson;
    private String address;
    private LocalDateTime createdAt;
    private String state; // The new field

    // No-argument constructor for Jackson and JavaFX
    public Vendor() {}

    // Getters and Setters for all fields
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vendor vendor = (Vendor) o;
        return id == vendor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}