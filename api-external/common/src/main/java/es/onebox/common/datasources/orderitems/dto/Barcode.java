package es.onebox.common.datasources.orderitems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.orderitems.enums.BarcodeMode;
import es.onebox.common.datasources.orderitems.enums.BarcodeType;
import es.onebox.common.datasources.orders.dto.MultiticketCode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Barcode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private BarcodeMode mode;
    private BarcodeType type;
    private String code;
    @JsonProperty("multiticket_codes")
    private List<MultiticketCode> multiticketCodes;

    public BarcodeMode getMode() {
        return mode;
    }

    public void setMode(BarcodeMode mode) {
        this.mode = mode;
    }

    public BarcodeType getType() {
        return type;
    }

    public void setType(BarcodeType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MultiticketCode> getMultiticketCodes() {
        return multiticketCodes;
    }

    public void setMultiticketCodes(List<MultiticketCode> multiticketCodes) {
        this.multiticketCodes = multiticketCodes;
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
