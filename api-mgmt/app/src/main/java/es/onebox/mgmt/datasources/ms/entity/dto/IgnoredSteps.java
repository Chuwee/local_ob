package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.MemberIgnoredSteps;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class IgnoredSteps implements Serializable {

    @Serial
    private static final long serialVersionUID = 2622973950650097086L;

    private List<MemberIgnoredSteps> renewalIgnoredSteps;

    private List<MemberIgnoredSteps> buySeatIgnoredSteps;

    private List<MemberIgnoredSteps> changeSeatIgnoredSteps;

    public IgnoredSteps() {
    }

    public List<MemberIgnoredSteps> getRenewalIgnoredSteps() {
        return renewalIgnoredSteps;
    }

    public void setRenewalIgnoredSteps(List<MemberIgnoredSteps> renewalIgnoredSteps) {
        this.renewalIgnoredSteps = renewalIgnoredSteps;
    }

    public List<MemberIgnoredSteps> getBuySeatIgnoredSteps() {
        return buySeatIgnoredSteps;
    }

    public void setBuySeatIgnoredSteps(List<MemberIgnoredSteps> buySeatIgnoredSteps) {
        this.buySeatIgnoredSteps = buySeatIgnoredSteps;
    }

    public List<MemberIgnoredSteps> getChangeSeatIgnoredSteps() {
        return changeSeatIgnoredSteps;
    }

    public void setChangeSeatIgnoredSteps(List<MemberIgnoredSteps> changeSeatIgnoredSteps) {
        this.changeSeatIgnoredSteps = changeSeatIgnoredSteps;
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
