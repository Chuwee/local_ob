package es.onebox.common.datasources.orderitems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.orderitems.dto.validation.ItemTicketValidation;
import es.onebox.common.datasources.orderitems.enums.TicketType;
import es.onebox.common.datasources.orders.dto.GroupData;
import es.onebox.common.datasources.orders.dto.Rate;
import es.onebox.common.datasources.orders.dto.Sales;
import es.onebox.common.datasources.orders.dto.TicketAllocation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ItemTicket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private TicketType type;
    private TicketAllocation allocation;
    private List<ItemTicketValidation> validations;
    private Rate rate;
    private Sales sales;
    @JsonProperty("group_data")
    private GroupData groupData;
    private Barcode barcode;

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public TicketAllocation getAllocation() {
        return allocation;
    }

    public void setAllocation(TicketAllocation allocation) {
        this.allocation = allocation;
    }

    public List<ItemTicketValidation> getValidations() {
        return validations;
    }

    public void setValidations(List<ItemTicketValidation> validations) {
        this.validations = validations;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public GroupData getGroupData() {
        return groupData;
    }

    public void setGroupData(GroupData groupData) {
        this.groupData = groupData;
    }

    public Barcode getBarcode() {
        return barcode;
    }

    public void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
