package es.onebox.common.datasources.distribution.dto.attendee;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.distribution.dto.MultiticketCodes;
import es.onebox.common.datasources.distribution.dto.state.BarcodeMode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Barcode implements Serializable {

    @Serial
    private static final long serialVersionUID = 4004378502474801157L;

    private BarcodeMode mode;
    private String code;
    @JsonProperty("multiticket_codes")
    private List<MultiticketCodes> multiticketCodes;

    public BarcodeMode getMode() {
        return mode;
    }

    public void setMode(BarcodeMode mode) {
        this.mode = mode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<MultiticketCodes> getMultiticketCodes() {
        return multiticketCodes;
    }

    public void setMultiticketCodes(List<MultiticketCodes> multiticketCodes) {
        this.multiticketCodes = multiticketCodes;
    }
}
