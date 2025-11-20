package org.nishgrid.clienterp.model;

import javafx.beans.property.*;

public class Discount {
    private final IntegerProperty discountId;
    private final StringProperty title;
    private final StringProperty valueType;
    private final StringProperty valueDescription;

    public Discount(int discountId, String title, String valueType, String valueDescription) {
        this.discountId = new SimpleIntegerProperty(discountId);
        this.title = new SimpleStringProperty(title);
        this.valueType = new SimpleStringProperty(valueType);
        this.valueDescription = new SimpleStringProperty(valueDescription);
    }

    public int getDiscountId() { return discountId.get(); }
    public void setDiscountId(int id) { discountId.set(id); }
    public IntegerProperty discountIdProperty() { return discountId; }

    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public StringProperty titleProperty() { return title; }

    public String getValueType() { return valueType.get(); }
    public void setValueType(String valueType) { this.valueType.set(valueType); }
    public StringProperty valueTypeProperty() { return valueType; }

    public String getValueDescription() { return valueDescription.get(); }
    public void setValueDescription(String desc) { this.valueDescription.set(desc); }
    public StringProperty valueDescriptionProperty() { return valueDescription; }
}
