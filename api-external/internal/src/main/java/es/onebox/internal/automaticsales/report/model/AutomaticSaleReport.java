package es.onebox.internal.automaticsales.report.model;

import es.onebox.internal.automaticsales.report.annotation.AutomaticSalesFieldBinder;
import es.onebox.internal.automaticsales.report.enums.AutomaticSalesFields;
import es.onebox.core.file.exporter.generator.model.ExportableBean;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;

public class AutomaticSaleReport implements ExportableBean {

    @Serial
    private static final long serialVersionUID = 5839743841035690475L;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.GROUP)
    private Long group;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.NUM)
    private Long num;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.NAME)
    private String name;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.FIRST_SURNAME)
    private String firstSurname;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.SECOND_SURNAME)
    private String secondSurname;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.DNI)
    private String dni;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.PHONE)
    private String phone;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.EMAIL)
    private String email;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.SECTOR)
    private String sector;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.PRICE_ZONE)
    private String priceZone;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.OWNER)
    private boolean owner;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.SEAT_ID)
    private Long seatId;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.ORIGINAL_LOCATOR)
    private String originalLocator;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.LANGUAGE)
    private String language;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.PROCESSED)
    private boolean processed;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.ERROR_CODE)
    private String errorCode;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.ERROR_DESCRIPTION)
    private String errorDescription;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.ORDER_ID)
    private String orderId;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.TRACE_ID)
    private String traceId;
    @AutomaticSalesFieldBinder(AutomaticSalesFields.EXTRA_FIELD)
    private String extraField;

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

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

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
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
