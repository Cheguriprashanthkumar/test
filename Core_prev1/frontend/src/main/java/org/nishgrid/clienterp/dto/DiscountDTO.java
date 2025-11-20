package org.nishgrid.clienterp.dto;

public class DiscountDTO {
    private Long discountId;
    private String title;
    private String valueType;
    private String valueDescription;

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription;
    }
}