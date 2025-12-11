package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;

public class InsurerBasicDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6225991829775553399L;

    private Integer id;
    private IdNameDTO operator;
    private String name;
    @JsonProperty("tax_name")
    private String taxName;
    @JsonProperty("contact_email")
    private String contactEmail;
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public IdNameDTO getOperator() {
        return operator;
    }

    public void setOperator(IdNameDTO operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
