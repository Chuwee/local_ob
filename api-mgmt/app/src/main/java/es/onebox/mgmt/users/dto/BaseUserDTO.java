package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class BaseUserDTO implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("name")       private String name;
    @JsonProperty("last_name")  private String lastname;
    @JsonProperty("job_title")  private String jobTitle;
    @JsonProperty("language")   private String language;
    @JsonProperty("notes")      private String notes;
    @JsonProperty("contact")    private UserContactDTO contact;
    @JsonProperty("location")   private UserLocationDTO location;


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

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public UserContactDTO getContact() {
        return contact;
    }
    public void setContact(UserContactDTO contact) {
        this.contact = contact;
    }

    public UserLocationDTO getLocation() {
        return location;
    }
    public void setLocation(UserLocationDTO location) {
        this.location = location;
    }
}

