package es.onebox.bepass.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

public class CreateUserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "id is required")
    public String id;
    @NotEmpty(message = "name is required")
    public String name;
    @NotEmpty(message = "surname is required")
    public String surname;
    @NotEmpty(message = "surname is required")
    public String phone;
    @NotEmpty(message = "gender is required")
    public String gender;
    @NotEmpty(message = "email is required")
    public String email;
    @NotEmpty(message = "birthday is required")
    public String birthday;
    @NotEmpty(message = "id_card is required")
    @JsonProperty("id_card")
    public String idCard;
    @NotEmpty(message = "id_card_type is required")
    @JsonProperty("id_card_type")
    public String idCardType;
    @NotEmpty(message = "callback_url is required")
    @JsonProperty("callback_url")
    private String callbackUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardType() {
        return idCardType;
    }

    public void setIdCardType(String idCardType) {
        this.idCardType = idCardType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
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
