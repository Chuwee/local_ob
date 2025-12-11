package es.onebox.mgmt.accesscontrol.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class BarcodesDTO implements Serializable {

    private static final long serialVersionUID = 1980576297346138707L;

    private String email;
    private List<BarcodesFileDTO> barcodes;

    public List<BarcodesFileDTO> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodesFileDTO> barcodes) {
        this.barcodes = barcodes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
