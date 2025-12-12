package es.onebox.fifaqatar.tickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TicketHolder implements Serializable {

    private static final long serialVersionUID = 7604418773072391565L;

    private String name;
    private String lastname;
    private String nationality;
    private String country;
    @JsonProperty("doc_number")
    private String docNumber;
    private String phone;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
