package es.onebox.event.catalog.dao.record;


import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class SessionForCatalogRecord extends CpanelSesionRecord {

    private Long ticketTaxId;
    private String ticketTaxName;
    private Double ticketTaxValue;
    private Long surchargesTaxId;
    private String surchargesTaxName;
    private Double surchargesTaxValue;

    public String getSurchargesTaxName() {
        return surchargesTaxName;
    }

    public void setSurchargesTaxName(String surchargesTaxName) {
        this.surchargesTaxName = surchargesTaxName;
    }

    public Long getSurchargesTaxId() {
        return surchargesTaxId;
    }

    public void setSurchargesTaxId(Long surchargesTaxId) {
        this.surchargesTaxId = surchargesTaxId;
    }

    public Double getSurchargesTaxValue() {
        return surchargesTaxValue;
    }

    public void setSurchargesTaxValue(Double surchargesTaxValue) {
        this.surchargesTaxValue = surchargesTaxValue;
    }

    public String getTicketTaxName() {
        return ticketTaxName;
    }

    public void setTicketTaxName(String ticketTaxName) {
        this.ticketTaxName = ticketTaxName;
    }

    public Long getTicketTaxId() {
        return ticketTaxId;
    }

    public void setTicketTaxId(Long ticketTaxId) {
        this.ticketTaxId = ticketTaxId;
    }

    public Double getTicketTaxValue() {
        return ticketTaxValue;
    }

    public void setTicketTaxValue(Double ticketTaxValue) {
        this.ticketTaxValue = ticketTaxValue;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
