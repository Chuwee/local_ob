package es.onebox.mgmt.datasources.ms.payment.dto.benefits;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Benefit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1865024435830180686L;

    private BenefitType type;
    private List<BinGroup> binGroups;
    private List<BrandGroup> brandGroups;

    public BenefitType getType() {
        return type;
    }

    public void setType(BenefitType type) {
        this.type = type;
    }

    public List<BinGroup> getBinGroups() {
        return binGroups;
    }

    public void setBinGroups(List<BinGroup> binGroups) {
        this.binGroups = binGroups;
    }

    public List<BrandGroup> getBrandGroups() {
        return brandGroups;
    }

    public void setBrandGroups(List<BrandGroup> brandGroups) {
        this.brandGroups = brandGroups;
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
