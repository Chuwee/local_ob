package es.onebox.event.forms.domain;

public enum ValidationType {
    INTEGER("[0-9]*"),
    ALPHANUMERIC("[a-zA-Z_0-9]*"),
    CHARACTER("[a-zA-Z]*"),
    ID_CARD("^[A-z0-9]{1,9}$"),
    EMAIL("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

    private final String validationRegexp;

    ValidationType(String validationRegexp) {
        this.validationRegexp = validationRegexp;
    }

    public String getValidationRegexp() {
        return validationRegexp;
    }

} 