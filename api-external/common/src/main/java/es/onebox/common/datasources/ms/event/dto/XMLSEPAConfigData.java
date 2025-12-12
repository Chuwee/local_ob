package es.onebox.common.datasources.ms.event.dto;

public class XMLSEPAConfigData {
    private String iban;
    private String bic;
    private String name;
    private String creditorId;
    private String substatus;

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditorId() { return creditorId; }

    public void setCreditorId(String creditorId) { this.creditorId = creditorId; }

    public String getSubstatus() { return substatus; }

    public void setSubstatus(String substatus) { this.substatus = substatus; }
}
