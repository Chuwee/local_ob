package es.onebox.fifaqatar.adapter.dto.response.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class OrderPriceBreakdown implements Serializable {

    @Serial
    private static final long serialVersionUID = -6229003392609853595L;

    @JsonProperty("label")
    private String label;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;
    @JsonProperty("schema_type")
    private String schemaType;
    @JsonProperty("price")
    private OrderPrice price;
    @JsonProperty("strikethrough_price")
    private OrderPrice strikethroughPrice;
    @JsonProperty("value")
    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchemaType() {
        return schemaType;
    }

    public void setSchemaType(String schemaType) {
        this.schemaType = schemaType;
    }

    public OrderPrice getPrice() {
        return price;
    }

    public void setPrice(OrderPrice price) {
        this.price = price;
    }

    public OrderPrice getStrikethroughPrice() {
        return strikethroughPrice;
    }

    public void setStrikethroughPrice(OrderPrice strikethroughPrice) {
        this.strikethroughPrice = strikethroughPrice;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
