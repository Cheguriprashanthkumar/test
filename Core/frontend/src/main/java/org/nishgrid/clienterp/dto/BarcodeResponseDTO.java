package org.nishgrid.clienterp.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true) // Safely ignore fields you don't need
public class BarcodeResponseDTO {
    private Long generationId;
    private String barcodeValue;
    private String productName;
    private LocalDateTime generatedAt;
    private String status;

    // Add other fields from the response if you need them

    // Getters and Setters
    public Long getGenerationId() { return generationId; }
    public void setGenerationId(Long generationId) { this.generationId = generationId; }
    public String getBarcodeValue() { return barcodeValue; }
    public void setBarcodeValue(String barcodeValue) { this.barcodeValue = barcodeValue; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Barcode '" + barcodeValue + "' for " + productName;
    }
}