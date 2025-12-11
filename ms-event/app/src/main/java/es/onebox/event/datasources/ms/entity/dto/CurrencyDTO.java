package es.onebox.event.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CurrencyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String description;
    private String hexValue;
    private String locale;
    private Long iso;
    private Integer negativeFormat;
    private Integer decimalDigits;

    public CurrencyDTO() {
    }

    public CurrencyDTO(Long id, String code, String description, String hexValue, String locale, Long iso, Integer negativeFormat) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.hexValue = hexValue;
        this.locale = locale;
        this.iso = iso;
        this.negativeFormat = negativeFormat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hexValue) {
        this.hexValue = hexValue;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Long getIso() {
        return iso;
    }

    public void setIso(Long iso) {
        this.iso = iso;
    }

    public Integer getNegativeFormat() {
        return negativeFormat;
    }

    public void setNegativeFormat(Integer negativeFormat) {
        this.negativeFormat = negativeFormat;
    }

    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
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
