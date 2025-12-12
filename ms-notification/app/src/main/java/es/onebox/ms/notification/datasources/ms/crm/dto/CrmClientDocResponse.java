package es.onebox.ms.notification.datasources.ms.crm.dto;

public class CrmClientDocResponse {

    private String id;
    private String name;
    private String surname;
    private String language;
    private Boolean newsletter_agreement;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getNewsletter_agreement() {
        return newsletter_agreement;
    }

    public void setNewsletter_agreement(Boolean newsletter_agreement) {
        this.newsletter_agreement = newsletter_agreement;
    }
}
