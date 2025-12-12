package es.onebox.atm.webhook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class PayloadRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34356364782L;

    @JsonProperty("code")
    private String orderCode;

    @JsonProperty("url")
    private String link;

    @JsonProperty("previous_code")
    private String prevOrderCode;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPrevOrderCode() {
        return prevOrderCode;
    }

    public void setPrevOrderCode(String prevOrderCode) {
        this.prevOrderCode = prevOrderCode;
    }
}
