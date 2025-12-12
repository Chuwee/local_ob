package es.onebox.internal.automaticsales.filemanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SaleRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3840342836003334103L;
    @CsvBindByName(column = "group")
    private Long group;
    @CsvBindByName(column = "num")
    private Long num;
    @CsvBindByName(column = "name")
    private String name;
    @CsvBindByName(column = "first_surname")
    @JsonProperty("first_surname")
    private String firstSurname;
    @CsvBindByName(column = "second_surname")
    @JsonProperty("second_surname")
    private String secondSurname;
    @CsvBindByName(column = "dni")
    private String dni;
    @CsvBindByName(column = "phone")
    private String phone;
    @CsvBindByName(column = "email")
    private String email;
    @CsvBindByName(column = "sector")
    private String sector;
    @CsvBindByName(column = "price_zone")
    @JsonProperty("price_zone")
    private String priceZone;
    @CsvBindByName(column = "owner")
    private boolean owner;
    @CsvBindByName(column = "seat_id")
    @JsonProperty("seat_id")
    private Long seatId;
    @CsvBindByName(column = "original_locator")
    @JsonProperty("original_locator")
    private String originalLocator;
    @CsvBindByName(column = "language")
    private String language;
    @CsvBindByName(column = "processed")
    private boolean processed;
    @CsvBindByName(column = "error_code")
    @JsonProperty("error_code")
    private String errorCode;
    @CsvBindByName(column = "error_description")
    @JsonProperty("error_description")
    private String errorDescription;
    @CsvBindByName(column = "extra_field")
    @JsonProperty("extra_field")
    private String extraField;

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

    @CsvBindByName(column = "order_id")
    @JsonProperty("order_id")
    private String orderId;
    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getSecondSurname() {
        return secondSurname;
    }

    public void setSecondSurname(String secondSurname) {
        this.secondSurname = secondSurname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getPriceZone() {
        return priceZone;
    }

    public void setPriceZone(String priceZone) {
        this.priceZone = priceZone;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public String getOriginalLocator() {
        return originalLocator;
    }

    public void setOriginalLocator(String originalLocator) {
        this.originalLocator = originalLocator;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
        return ToStringBuilder.reflectionToString(this);
    }

}
