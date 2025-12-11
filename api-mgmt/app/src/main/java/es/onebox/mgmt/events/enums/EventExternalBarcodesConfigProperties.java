package es.onebox.mgmt.events.enums;

public enum EventExternalBarcodesConfigProperties {
    FAIR_CODE("fairCodes"),
    FAIR_EDITION("fairEditions");

    String property;

    EventExternalBarcodesConfigProperties(String property){
        this.property = property;
    }

    public String getProperty(){
        return this.property;
    }
}
