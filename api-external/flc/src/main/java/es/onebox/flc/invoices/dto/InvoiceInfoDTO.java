package es.onebox.flc.invoices.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.utils.TimeZoneResolver;
import es.onebox.core.utils.dto.DateConvertible;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

public class InvoiceInfoDTO implements DateConvertible, Serializable {
    @JsonProperty("entity_logo")
    private String entityLogo;
    @JsonProperty("entity_name")
    private String entityName;
    @JsonProperty("entity_address")
    private String entityAddress;
    @JsonProperty("entity_address_zipcode")
    private String entityAddressZipCode;
    @JsonProperty("entity_address_city")
    private String entityAddressCity;
    @JsonProperty("entity_document")
    private String entityDocument;
    @JsonProperty("invoice_id")
    private Integer invoiceId;
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("invoice_date")
    private ZonedDateTime invoiceDate;
    @JsonProperty("invoice_generation_date")
    private ZonedDateTime invoiceGenerationDate;
    @JsonProperty("invoice_order_code")
    private String invoiceOrderCode;
    @JsonProperty("client_full_name")
    private String clientFullName;
    @JsonProperty("client_address")
    private String clientAddress;
    @JsonProperty("client_address_zipcode")
    private String clientAddressZipCode;
    @JsonProperty("client_address_city")
    private String clientAddressCity;
    @JsonProperty("client_document")
    private String clientDocument;
    private String observations;
    @JsonProperty("invoice_events_data")
    private List<InvoiceEventDTO> invoiceEventsData = new LinkedList<>();
    @JsonProperty("invoice_taxes")
    private List<InvoiceTaxDTO> invoiceTaxes = new LinkedList<>();

    @JsonIgnore
    private String timeZone;

    public String getEntityLogo() {
        return entityLogo;
    }

    public void setEntityLogo(String entityLogo) {
        this.entityLogo = entityLogo;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityAddress() {
        return entityAddress;
    }

    public void setEntityAddress(String entityAddress) {
        this.entityAddress = entityAddress;
    }

    public String getEntityAddressZipCode() {
        return entityAddressZipCode;
    }

    public void setEntityAddressZipCode(String entityAddressZipCode) {
        this.entityAddressZipCode = entityAddressZipCode;
    }

    public String getEntityAddressCity() {
        return entityAddressCity;
    }

    public void setEntityAddressCity(String entityAddressCity) {
        this.entityAddressCity = entityAddressCity;
    }

    public String getEntityDocument() {
        return entityDocument;
    }

    public void setEntityDocument(String entityDocument) {
        this.entityDocument = entityDocument;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public ZonedDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(ZonedDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public ZonedDateTime getInvoiceGenerationDate() {
        return invoiceGenerationDate;
    }

    public void setInvoiceGenerationDate(ZonedDateTime invoiceGenerationDate) {
        this.invoiceGenerationDate = invoiceGenerationDate;
    }

    public String getInvoiceOrderCode() {
        return invoiceOrderCode;
    }

    public void setInvoiceOrderCode(String invoiceOrderCode) {
        this.invoiceOrderCode = invoiceOrderCode;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientAddressZipCode() {
        return clientAddressZipCode;
    }

    public void setClientAddressZipCode(String clientAddressZipCode) {
        this.clientAddressZipCode = clientAddressZipCode;
    }

    public String getClientAddressCity() {
        return clientAddressCity;
    }

    public void setClientAddressCity(String clientAddressCity) {
        this.clientAddressCity = clientAddressCity;
    }

    public String getClientDocument() {
        return clientDocument;
    }

    public void setClientDocument(String clientDocument) {
        this.clientDocument = clientDocument;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<InvoiceEventDTO> getInvoiceEventsData() {
        return invoiceEventsData;
    }

    public void setInvoiceEventsData(List<InvoiceEventDTO> invoiceEventsData) {
        this.invoiceEventsData = invoiceEventsData;
    }

    public List<InvoiceTaxDTO> getInvoiceTaxes() {
        return invoiceTaxes;
    }

    public void setInvoiceTaxes(List<InvoiceTaxDTO> invoiceTaxes) {
        this.invoiceTaxes = invoiceTaxes;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void convertDates() {
        if (invoiceDate != null) {
            invoiceDate = TimeZoneResolver.applyTimeZone(invoiceDate, timeZone);
        }

        if (invoiceGenerationDate != null) {
            invoiceGenerationDate = TimeZoneResolver.applyTimeZone(invoiceGenerationDate, timeZone);
        }
    }
}
