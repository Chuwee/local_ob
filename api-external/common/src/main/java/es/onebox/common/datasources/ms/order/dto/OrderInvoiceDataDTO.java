package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class OrderInvoiceDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5497153406239416358L;

    private String invoiceCode;
    private ZonedDateTime invoiceDate;
    private ZonedDateTime invoiceGenerationDate;
    private Integer invoiceNumber;
    private String nif;
    private Boolean nifValidated;
    private String name;
    private String langCode;
    private String organizerCode;
    private String formatLocale;
    private String address;
    private String city;
    private String zipCode;
    private String notes;
    private String country;
    private String countrySubdivision;
    private String identification;

    public ZonedDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(ZonedDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Integer getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(Integer invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public Boolean getNifValidated() {
        return nifValidated;
    }

    public void setNifValidated(Boolean nifValidated) {
        this.nifValidated = nifValidated;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getFormatLocale() {
        return formatLocale;
    }

    public void setFormatLocale(String formatLocale) {
        this.formatLocale = formatLocale;
    }

    public String getOrganizerCode() {
        return organizerCode;
    }

    public void setOrganizerCode(String organizerCode) {
        this.organizerCode = organizerCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getInvoiceCode() {
        return invoiceCode;
    }

    public void setInvoiceCode(String invoiceCode) {
        this.invoiceCode = invoiceCode;
    }

    public ZonedDateTime getInvoiceGenerationDate() {
        return invoiceGenerationDate;
    }

    public void setInvoiceGenerationDate(ZonedDateTime invoiceGenerationDate) {
        this.invoiceGenerationDate = invoiceGenerationDate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
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
