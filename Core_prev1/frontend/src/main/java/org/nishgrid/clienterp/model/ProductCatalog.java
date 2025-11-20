package org.nishgrid.clienterp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class ProductCatalog {
    private final long id;
    private final String name;
    private final String purity;
    private final String defaultRate;

    @JsonCreator
    public ProductCatalog(@JsonProperty("id") long id,
                          @JsonProperty("name") String name,
                          @JsonProperty("purity") String purity,
                          @JsonProperty("defaultRate") String defaultRate) {
        this.id = id;
        this.name = name;
        this.purity = purity;
        this.defaultRate = defaultRate;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getPurity() { return purity; }
    public String getDefaultRate() { return defaultRate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCatalog that = (ProductCatalog) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}