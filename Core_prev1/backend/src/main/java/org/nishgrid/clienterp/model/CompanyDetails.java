package org.nishgrid.clienterp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CompanyDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(length = 512)
    private String companyAddress;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "company_contacts", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "contact_number")
    private List<String> companyContacts = new ArrayList<>();

    private String companyTagline;

    @Column(nullable = false)
    private boolean active = false;

    public CompanyDetails() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public List<String> getCompanyContacts() {
        return companyContacts;
    }

    public void setCompanyContacts(List<String> companyContacts) {
        this.companyContacts = companyContacts;
    }

    public String getCompanyTagline() {
        return companyTagline;
    }

    public void setCompanyTagline(String companyTagline) {
        this.companyTagline = companyTagline;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}