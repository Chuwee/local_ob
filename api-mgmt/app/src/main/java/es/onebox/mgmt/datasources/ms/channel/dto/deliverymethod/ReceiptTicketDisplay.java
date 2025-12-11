package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ReceiptTicketDisplay implements Serializable {

    @Serial private static final long serialVersionUID = 5958759627426581888L;

    private Boolean passbook;
    private Boolean pdf;
    private Boolean qr;

    public Boolean getPassbook() {
        return passbook;
    }

    public void setPassbook(Boolean passbook) {
        this.passbook = passbook;
    }

    public Boolean getPdf() {
        return pdf;
    }

    public void setPdf(Boolean pdf) {
        this.pdf = pdf;
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
