package es.onebox.mgmt.sessions.importbarcodes;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.mgmt.accesscontrol.dto.BarcodesFileDTO;

import java.util.List;

public class ExternalBarcodesMessage extends AbstractNotificationMessage {
    private static final long serialVersionUID = 1L;

    private Integer importProcessId;
    private Long eventId;
    private Long sessionId;
    private List<BarcodesFileDTO> barcodes;
    private String email;
    private String language;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<BarcodesFileDTO> getBarcodes() {
        return barcodes;
    }

    public void setBarcodes(List<BarcodesFileDTO> barcodes) {
        this.barcodes = barcodes;
    }

    public Integer getImportProcessId() {
        return importProcessId;
    }

    public void setImportProcessId(Integer importProcessId) {
        this.importProcessId = importProcessId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
