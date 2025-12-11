package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelCanalCupoB2bRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class ChannelEventB2BAssignationRecord extends CpanelCanalCupoB2bRecord {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer quotaId;

    private String quotaDescription;

    private Integer clientId;

    public Integer getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Integer quotaId) {
        this.quotaId = quotaId;
    }

    public String getQuotaDescription() {
        return quotaDescription;
    }

    public void setQuotaDescription(String quotaDescription) {
        this.quotaDescription = quotaDescription;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
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
