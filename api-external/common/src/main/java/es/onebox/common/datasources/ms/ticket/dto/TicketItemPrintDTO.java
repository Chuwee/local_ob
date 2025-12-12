package es.onebox.common.datasources.ms.ticket.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class TicketItemPrintDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sessionId;
    private Long productId;
    private String downloadLink;

    public TicketItemPrintDTO() {
    }

    public TicketItemPrintDTO(Long sessionId, Long productId, String downloadLink) {
        this.sessionId = sessionId;
        this.productId = productId;
        this.downloadLink = downloadLink;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
