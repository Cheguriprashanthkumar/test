package org.nishgrid.clienterp.model;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.math.BigDecimal;

public class GiftVoucher {
    private final LongProperty id;
    private final StringProperty voucherCode;
    private final StringProperty customerName;
    private final ObjectProperty<BigDecimal> value;
    private final StringProperty status;

    public GiftVoucher(Long id, String voucherCode, String customerName, BigDecimal value, String status) {
        this.id = new SimpleLongProperty(id);
        this.voucherCode = new SimpleStringProperty(voucherCode);
        this.customerName = new SimpleStringProperty(customerName);
        this.value = new SimpleObjectProperty<>(value);
        this.status = new SimpleStringProperty(status);
    }

    public LongProperty idProperty() { return id; }
    public StringProperty voucherCodeProperty() { return voucherCode; }
    public StringProperty customerNameProperty() { return customerName; }

    public ObservableValue<Number> valueProperty() {
        return new ReadOnlyObjectWrapper<>(value.get());
    }

    public StringProperty statusProperty() { return status; }

    public Long getId() { return id.get(); }
    public String getVoucherCode() { return voucherCode.get(); }
    public String getCustomerName() { return customerName.get(); }
    public BigDecimal getValue() { return value.get(); }
    public String getStatus() { return status.get(); }
}
