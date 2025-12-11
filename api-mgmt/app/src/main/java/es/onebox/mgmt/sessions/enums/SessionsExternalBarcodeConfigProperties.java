package es.onebox.mgmt.sessions.enums;

public enum SessionsExternalBarcodeConfigProperties {
    PERSON_TYPES("personTypes"),
    VARIABLE_CODES("variableCodes"),
    PASS_TYPES("passTypes");

    String property;

    SessionsExternalBarcodeConfigProperties(String property){
        this.property = property;
    }

    public String getProperty(){
        return this.property;
    }
}
