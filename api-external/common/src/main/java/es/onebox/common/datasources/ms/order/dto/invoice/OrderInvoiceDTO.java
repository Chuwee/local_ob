package es.onebox.common.datasources.ms.order.dto.invoice;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class OrderInvoiceDTO implements Serializable {

    private static final long serialVersionUID = 5154210258031869827L;

    private String clientFullName;
    private String clientDocument;
    private String clientAddress;
    private String clientZipCode;
    private String clientCity;
    private ZonedDateTime date;
    private ZonedDateTime generationDate;
    private String promoterCode;
    private Integer number;
    private String observations;

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getClientDocument() {
        return clientDocument;
    }

    public void setClientDocument(String clientDocument) {
        this.clientDocument = clientDocument;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientZipCode() {
        return clientZipCode;
    }

    public void setClientZipCode(String clientZipCode) {
        this.clientZipCode = clientZipCode;
    }

    public String getClientCity() {
        return clientCity;
    }

    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ZonedDateTime getGenerationDate() {
        return generationDate;
    }

    public void setGenerationDate(ZonedDateTime generationDate) {
        this.generationDate = generationDate;
    }

    public String getPromoterCode() {
        return promoterCode;
    }

    public void setPromoterCode(String promoterCode) {
        this.promoterCode = promoterCode;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
