package es.onebox.common.datasources.orders.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Promotion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1842843098180765650L;

    private int id;
    private List<ComElement> comElements;
    private Integer collectiveId;
    private Boolean mandatory;
    private Boolean selfManaged;
    private Boolean limitedUses;

    public Promotion() {
    }

    public Promotion(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<ComElement> getComElements() {
        return comElements;
    }

    public void setComElements(List<ComElement> comElements) {
        this.comElements = comElements;
    }

    public Integer getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(Integer collectiveId) {
        this.collectiveId = collectiveId;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
