package es.onebox.event.events.dao.bean;

import es.onebox.event.priceengine.taxes.domain.ChannelTaxInfo;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.ChannelSurchargesTaxesOrigin;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -3414410067856686952L;

    protected Long id;
    protected String name;
    protected Long entityId;
    protected Integer subtypeId;
    protected Integer surchargesTaxesOrigin;
    protected List<ChannelTaxInfo> surchargesTaxes;

    public ChannelInfo(Long id, String name, Long entityId, Integer subtypeId, Integer surchargesTaxesOrigin) {
        this.entityId = entityId;
        this.id = id;
        this.name = name;
        this.subtypeId = subtypeId;
        this.surchargesTaxesOrigin = surchargesTaxesOrigin;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeId(Integer subtypeId) {
        this.subtypeId = subtypeId;
    }

    public ChannelSubtype getSubtype() {
        return ChannelSubtype.getById(subtypeId);
    }

    public ChannelSurchargesTaxesOrigin getSurchargesTaxesOrigin() {
        return ChannelSurchargesTaxesOrigin.getById(surchargesTaxesOrigin);
    }

    public void setSurchargesTaxesOrigin(Integer surchargesTaxesOrigin) {
        this.surchargesTaxesOrigin = surchargesTaxesOrigin;
    }

    public List<ChannelTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<ChannelTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public boolean isB2BChannel() {
        return ChannelSubtype.PORTAL_B2B.equals(getSubtype());
    }

    public boolean notB2BChannel() {
        return !ChannelSubtype.PORTAL_B2B.equals(getSubtype());
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
