package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class TicketAddonItems implements Serializable {

    @Serial
    private static final long serialVersionUID = -7403702913502287171L;

    @JsonProperty("add_on_id")
    private Integer id;
    private String label;
    @JsonProperty("label_price")
    private String labelPrice;
    private String description;
    private String category; // TODO make me enum [premium_refund_policy, donation, session_as_add_on]
    @JsonProperty("base_price")
    private Pricing basePrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelPrice() {
        return labelPrice;
    }

    public void setLabelPrice(String labelPrice) {
        this.labelPrice = labelPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Pricing getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Pricing basePrice) {
        this.basePrice = basePrice;
    }
}
