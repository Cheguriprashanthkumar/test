package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class CustomerKycFx {
    private final LongProperty kycId = new SimpleLongProperty();
    private final LongProperty customerId = new SimpleLongProperty();
    private final StringProperty customerName = new SimpleStringProperty();
    private final StringProperty panNumber = new SimpleStringProperty();
    private final StringProperty aadhaarNumber = new SimpleStringProperty();
    private final StringProperty photoPath = new SimpleStringProperty();
    private final StringProperty verifiedBy = new SimpleStringProperty();
    private final StringProperty verifiedOn = new SimpleStringProperty();
    private final StringProperty remarks = new SimpleStringProperty();

    public CustomerKycFx() {}

    public CustomerKycFx(Long kycId, Long customerId, String customerName, String panNumber,
                         String aadhaarNumber, String photoPath, String verifiedBy,
                         String verifiedOn, String remarks) {
        this.kycId.set(kycId);
        this.customerId.set(customerId);
        this.customerName.set(customerName);
        this.panNumber.set(panNumber);
        this.aadhaarNumber.set(aadhaarNumber);
        this.photoPath.set(photoPath);
        this.verifiedBy.set(verifiedBy);
        this.verifiedOn.set(verifiedOn);
        this.remarks.set(remarks);
    }

    public LongProperty kycIdProperty() { return kycId; }
    public Long getKycId() { return kycId.get(); }
    public void setKycId(Long value) { kycId.set(value); }

    public LongProperty customerIdProperty() { return customerId; }
    public Long getCustomerId() { return customerId.get(); }
    public void setCustomerId(Long value) { customerId.set(value); }

    public StringProperty customerNameProperty() { return customerName; }
    public String getCustomerName() { return customerName.get(); }
    public void setCustomerName(String value) { customerName.set(value); }

    public StringProperty panNumberProperty() { return panNumber; }
    public String getPanNumber() { return panNumber.get(); }
    public void setPanNumber(String value) { panNumber.set(value); }

    public StringProperty aadhaarNumberProperty() { return aadhaarNumber; }
    public String getAadhaarNumber() { return aadhaarNumber.get(); }
    public void setAadhaarNumber(String value) { aadhaarNumber.set(value); }

    public StringProperty photoPathProperty() { return photoPath; }
    public String getPhotoPath() { return photoPath.get(); }
    public void setPhotoPath(String value) { photoPath.set(value); }

    public StringProperty verifiedByProperty() { return verifiedBy; }
    public String getVerifiedBy() { return verifiedBy.get(); }
    public void setVerifiedBy(String value) { verifiedBy.set(value); }

    public StringProperty verifiedOnProperty() { return verifiedOn; }
    public String getVerifiedOn() { return verifiedOn.get(); }
    public void setVerifiedOn(String value) { verifiedOn.set(value); }

    public StringProperty remarksProperty() { return remarks; }
    public String getRemarks() { return remarks.get(); }
    public void setRemarks(String value) { remarks.set(value); }
}
