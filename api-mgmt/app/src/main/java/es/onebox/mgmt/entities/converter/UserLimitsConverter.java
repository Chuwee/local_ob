package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.common.LimitDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.UserLimits;
import es.onebox.mgmt.entities.dto.BIUserLimitsDTO;
import es.onebox.mgmt.entities.dto.UserLimitsDTO;

public class UserLimitsConverter {

    private UserLimitsConverter() {
    }

    public static UserLimitsDTO fromMs(UserLimits in) {
        UserLimitsDTO out = new UserLimitsDTO();
        out.setBi(fromMsBi(in));
        return out;
    }

    private static BIUserLimitsDTO fromMsBi(UserLimits in) {
        BIUserLimitsDTO out = new BIUserLimitsDTO();

        out.setBasic(fromMsLimit(in.biBasicLimit(), in.biBasicUsed(), in.biBasicTotal()));
        out.setAdvanced(fromMsLimit(in.biAdvancedLimit(), in.biAdvancedUsed(), in.biAdvancedTotal()));
        out.setMobile(fromMsLimit(in.biMobileLimit(), in.biMobileUsed(), null));
        return out;
    }

    private static LimitDTO fromMsLimit(Long limit, Long used, Long total) {
        LimitDTO out = new LimitDTO();
        out.setLimit(limit);
        out.setUsed(used);
        out.setTotal(total);
        return out;
    }
}
