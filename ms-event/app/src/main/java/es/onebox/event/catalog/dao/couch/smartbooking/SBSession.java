package es.onebox.event.catalog.dao.couch.smartbooking;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

@CouchDocument
public class SBSession {

    @Id
    private Integer sessionId;
    private String partnerPriceListName;
    private Map<Long, SBPriceZone> priceZonesMapping;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getPartnerPriceListName() {
        return partnerPriceListName;
    }

    public void setPartnerPriceListName(String partnerPriceListName) {
        this.partnerPriceListName = partnerPriceListName;
    }

    public Map<Long, SBPriceZone> getPriceZonesMapping() {
        return priceZonesMapping;
    }

    public void setPriceZonesMapping(Map<Long, SBPriceZone> priceZonesMapping) {
        this.priceZonesMapping = priceZonesMapping;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

