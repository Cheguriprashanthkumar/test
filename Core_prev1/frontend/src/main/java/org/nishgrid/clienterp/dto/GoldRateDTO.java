package org.nishgrid.clienterp.dto;

import java.time.LocalDateTime;

/**
 * This Data Transfer Object (DTO) is used on the client-side (JavaFX)
 * to deserialize the JSON response from the /api/gold-rates/latest endpoint.
 * Its structure mirrors the backend GoldRate JPA entity.
 */
public class GoldRateDTO {

    private Long id;
    private LocalDateTime date;
    private Double rate24k;
    private Double rate22k;
    private Double rate18k;
    private Double rate14k;
    private Double rate12k;
    private Double rate10k;
    private Double rate09k;
    private Double fineSilver;
    private Double sterlingSilver;
    private Double coinSilver;

    // A no-argument constructor is required for deserialization libraries like Jackson.
    public GoldRateDTO() {
    }

    // Getters and Setters for all fields below...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getRate24k() {
        return rate24k;
    }

    public void setRate24k(Double rate24k) {
        this.rate24k = rate24k;
    }

    public Double getRate22k() {
        return rate22k;
    }

    public void setRate22k(Double rate22k) {
        this.rate22k = rate22k;
    }

    public Double getRate18k() {
        return rate18k;
    }

    public void setRate18k(Double rate18k) {
        this.rate18k = rate18k;
    }

    public Double getRate14k() {
        return rate14k;
    }

    public void setRate14k(Double rate14k) {
        this.rate14k = rate14k;
    }

    public Double getRate12k() {
        return rate12k;
    }

    public void setRate12k(Double rate12k) {
        this.rate12k = rate12k;
    }

    public Double getRate10k() {
        return rate10k;
    }

    public void setRate10k(Double rate10k) {
        this.rate10k = rate10k;
    }

    public Double getRate09k() {
        return rate09k;
    }

    public void setRate09k(Double rate09k) {
        this.rate09k = rate09k;
    }

    public Double getFineSilver() {
        return fineSilver;
    }

    public void setFineSilver(Double fineSilver) {
        this.fineSilver = fineSilver;
    }

    public Double getSterlingSilver() {
        return sterlingSilver;
    }

    public void setSterlingSilver(Double sterlingSilver) {
        this.sterlingSilver = sterlingSilver;
    }

    public Double getCoinSilver() {
        return coinSilver;
    }

    public void setCoinSilver(Double coinSilver) {
        this.coinSilver = coinSilver;
    }
}