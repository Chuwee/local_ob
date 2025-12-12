package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PromotionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4639424352217318112L;

    private int id;
    private List<ComElementDTO> comElements;
    private Integer collectiveId;
    private String collectiveKey;
    private Boolean mandatory;
    private RestrictionsDTO restrictionsDTO;
    private Boolean selfManaged;
    private Boolean limitedUses;
    private Boolean nonCumulative;
    private Boolean useGenericCharges;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ComElementDTO> getComElements() {
        return comElements;
    }

    public void setComElements(List<ComElementDTO> comElements) {
        this.comElements = comElements;
    }

    public Integer getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(Integer collectiveId) {
        this.collectiveId = collectiveId;
    }

    public String getCollectiveKey() {
        return collectiveKey;
    }

    public void setCollectiveKey(String collectiveKey) {
        this.collectiveKey = collectiveKey;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public RestrictionsDTO getRestrictionsDTO() {
        return restrictionsDTO;
    }

    public void setRestrictionsDTO(RestrictionsDTO restrictionsDTO) {
        this.restrictionsDTO = restrictionsDTO;
    }

    public Boolean getSelfManaged() {
        return selfManaged;
    }

    public void setSelfManaged(Boolean selfManaged) {
        this.selfManaged = selfManaged;
    }

    public Boolean getLimitedUses() {
        return limitedUses;
    }

    public void setLimitedUses(Boolean limitedUses) {
        this.limitedUses = limitedUses;
    }

    public Boolean getNonCumulative() {
        return nonCumulative;
    }

    public void setNonCumulative(Boolean nonCumulative) {
        this.nonCumulative = nonCumulative;
    }

    public Boolean getUseGenericCharges() {
        return useGenericCharges;
    }

    public void setUseGenericCharges(Boolean useGenericCharges) {
        this.useGenericCharges = useGenericCharges;
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
