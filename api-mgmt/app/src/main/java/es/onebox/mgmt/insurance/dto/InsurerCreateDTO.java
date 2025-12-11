package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;


public class InsurerCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7546180646664183005L;

    @NotNull(message = "Operator ID cannot be null.")
    @JsonProperty("operator_id")
    private Integer operatorId;
    @NotNull(message = "Name cannot be null.")
    @Length(max = 100, message = "name max size 100")
    private String name;
    @JsonProperty("tax_id")
    @NotNull(message = "Tax ID cannot be null.")
    @Length(max = 50, message = "tax id max size 50")
    private String taxId;
    @NotNull(message = "Tax name cannot be null.")
    @JsonProperty("tax_name")
    @Length(max = 100, message = "tax name max size 100")
    private String taxName;
    @NotNull(message = "Contact email cannot be null.")
    @JsonProperty("contact_email")
    @Length(max = 225, message = "contact email max size 225")
    private String contactEmail;
    @NotNull(message = "Address cannot be null.")
    @Length(max = 100, message = "address max size 100")
    private String address;
    @JsonProperty("zip_code")
    @NotNull(message = "Zip code cannot be null.")
    @Length(max = 20, message = "zip code max size 20")
    private String zipCode;
    @NotNull(message = "Phone cannot be null.")
    @Length(max = 45, message = "phone max size 45")
    private String phone;

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
