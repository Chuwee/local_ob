package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ReceiptTicketDisplayDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7279210077026570594L;

    @JsonProperty("pdf")
    private Boolean pdf;

    @JsonProperty("passbook")
    private Boolean passbook;

    @JsonProperty("qr")
    private Boolean qr;

    public Boolean getPdf() {
        return pdf;
    }

    public void setPdf(Boolean pdf) {
        this.pdf = pdf;
    }

    public Boolean getPassbook() {
        return passbook;
    }

    public void setPassbook(Boolean passbook) {
        this.passbook = passbook;
    }

    public Boolean getQr() {
        return qr;
    }

    public void setQr(Boolean qr) {
        this.qr = qr;
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
