package es.onebox.bepass.datasources.bepass.dto;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TicketExtended extends Ticket {

    private static final long serialVersionUID = 1L;

    private String updatedAt;
    private String createdAt;
    private Boolean syncBepass;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getSyncBepass() {
        return syncBepass;
    }

    public void setSyncBepass(Boolean syncBepass) {
        this.syncBepass = syncBepass;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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
