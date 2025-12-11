package es.onebox.event.sessions.domain.sessionconfig;

import java.io.Serializable;

public class StreamingVendorConfig implements Serializable {

    private Boolean enabled;
    private StreamingVendor vendor;
    private String value;
    private Integer emailMinutesBeforeStart;
    private Boolean sendEmail;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public StreamingVendor getVendor() {
        return vendor;
    }

    public void setVendor(StreamingVendor vendor) {
        this.vendor = vendor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getEmailMinutesBeforeStart() {
        return emailMinutesBeforeStart;
    }

    public void setEmailMinutesBeforeStart(Integer emailMinutesBeforeStart) {
        this.emailMinutesBeforeStart = emailMinutesBeforeStart;
    }

    public Boolean getSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(Boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}
