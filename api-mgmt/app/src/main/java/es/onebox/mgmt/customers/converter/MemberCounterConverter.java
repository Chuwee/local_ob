package es.onebox.mgmt.customers.converter;

import es.onebox.mgmt.customers.dto.MemberCounterDTO;
import es.onebox.mgmt.customers.dto.UpdateMemberCounterDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.MemberCounter;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.UpdateMemberCounter;

public class MemberCounterConverter {

    private MemberCounterConverter() {
    }

    public static MemberCounterDTO toDTO(MemberCounter memberCounter) {
        if (memberCounter == null) {
            return null;
        }
        MemberCounterDTO result = new MemberCounterDTO();
        result.setMemberCounter(memberCounter.getMemberCounter());
        return result;
    }

    public static UpdateMemberCounter toMS(UpdateMemberCounterDTO source) {
        if (source == null) {
            return null;
        }
        UpdateMemberCounter result = new UpdateMemberCounter();
        result.setMemberCounter(source.getMemberCounter());
        return result;
    }

}
