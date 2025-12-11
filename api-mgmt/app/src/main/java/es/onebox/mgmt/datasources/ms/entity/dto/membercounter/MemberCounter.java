package es.onebox.mgmt.datasources.ms.entity.dto.membercounter;

import java.io.Serial;
import java.io.Serializable;

public class MemberCounter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long memberCounter;

    public Long getMemberCounter() {
        return memberCounter;
    }

    public void setMemberCounter(Long memberCounter) {
        this.memberCounter = memberCounter;
    }

}
