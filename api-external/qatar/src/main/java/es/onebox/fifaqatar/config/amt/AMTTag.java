package es.onebox.fifaqatar.config.amt;

public enum AMTTag {

    FEVER_USER_ID("fever.user.id"),
    FEVER_USER_EMAIL("fever.user.email");

    private String value;

    AMTTag(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
