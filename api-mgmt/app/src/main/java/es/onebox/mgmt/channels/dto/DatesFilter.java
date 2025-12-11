package es.onebox.mgmt.channels.dto;

import java.io.Serial;
import java.io.Serializable;

public class DatesFilter implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String type;
    private MemberDatesFilter memberDatesFilter;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public MemberDatesFilter getMemberDatesFilter() { return memberDatesFilter; }

    public void setMemberDatesFilter(MemberDatesFilter memberDatesFilter) { this.memberDatesFilter = memberDatesFilter; }
}
