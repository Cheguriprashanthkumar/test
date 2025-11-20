package org.nishgrid.clienterp.dto;
import java.math.BigDecimal;

public class BarcodeGenerationDTO {
    private Long itemId;
    private String itemType;
    private String productName;
    private BigDecimal weight;
    private String purity;
    private String hsnCode;
    private BigDecimal purchaseRate;
    private BigDecimal makingCharge;
    private BigDecimal stoneCharge;
    private String createdFor;
    private Long linkedTransactionId;
    private EmployeeDTO generatedBy;
    private Long locationId;
    private String remarks;

    // --- Inner class for the nested employee object ---
    public static class EmployeeDTO {
        private Integer employeeId;
        public EmployeeDTO(Integer employeeId) { this.employeeId = employeeId; }
        // Getters & Setters
        public Integer getEmployeeId() { return employeeId; }
        public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    }

    // Getters and Setters for all fields...
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getPurity() { return purity; }
    public void setPurity(String purity) { this.purity = purity; }
    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }
    public BigDecimal getPurchaseRate() { return purchaseRate; }
    public void setPurchaseRate(BigDecimal purchaseRate) { this.purchaseRate = purchaseRate; }
    public BigDecimal getMakingCharge() { return makingCharge; }
    public void setMakingCharge(BigDecimal makingCharge) { this.makingCharge = makingCharge; }
    public BigDecimal getStoneCharge() { return stoneCharge; }
    public void setStoneCharge(BigDecimal stoneCharge) { this.stoneCharge = stoneCharge; }
    public String getCreatedFor() { return createdFor; }
    public void setCreatedFor(String createdFor) { this.createdFor = createdFor; }
    public Long getLinkedTransactionId() { return linkedTransactionId; }
    public void setLinkedTransactionId(Long linkedTransactionId) { this.linkedTransactionId = linkedTransactionId; }
    public EmployeeDTO getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(EmployeeDTO generatedBy) { this.generatedBy = generatedBy; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}