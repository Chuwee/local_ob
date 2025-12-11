package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;


public class InsurerDTORequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 2464735925787848605L;

    private Integer id;
    @Length(max = 100, message = "name max size 100")
    private String name;
    @JsonProperty("tax_id")
    @Length(max = 50, message = "tax id max size 50")
    private String taxId;
    @JsonProperty("tax_name")
    @Length(max = 100, message = "tax name max size 100")
    private String taxName;
    @JsonProperty("contact_email")
    @Length(max = 225, message = "contact email max size 225")
    private String contactEmail;
    @Length(max = 100, message = "address max size 100")
    private String address;
    @Length(max = 100, message = "description max size 100")
    private String description;
    @JsonProperty("zip_code")
    @Length(max = 20, message = "zip code max size 20")
    private String zipCode;
    @Length(max = 45, message = "phone max size 45")
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
