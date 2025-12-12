package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

@JsonNaming(SnakeCaseStrategy.class)
public class EntityDetailDTO  implements Serializable {

    @JsonProperty("fv_id")
    private Integer fvId;
    private String name;
    private String email;
    private String phone;
    private FeverAddressDTO address;
    @JsonProperty("invoice_address")
    private FeverAddressDTO invoiceAddress;
    @JsonProperty("business_name")
    private String businessName;
    private String nif;

    public Integer getFvId() {
        return fvId;
    }

    public void setFvId(Integer fvId) {
        this.fvId = fvId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public FeverAddressDTO getAddress() {
        return address;
    }

    public void setAddress(FeverAddressDTO address) {
        this.address = address;
    }

    public FeverAddressDTO getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(FeverAddressDTO invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
