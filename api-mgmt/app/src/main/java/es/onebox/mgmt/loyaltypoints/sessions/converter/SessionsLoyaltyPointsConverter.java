package es.onebox.mgmt.loyaltypoints.sessions.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.UpdateSessionsLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.PointGain;
import es.onebox.mgmt.loyaltypoints.sessions.dto.UpdateLoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.sessions.dto.LoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.sessions.dto.PointGainDTO;
import es.onebox.mgmt.loyaltypoints.sessions.dto.enums.SessionPointsTypeDTO;
import es.onebox.mgmt.sessions.enums.SessionPointsType;

public class SessionsLoyaltyPointsConverter {

    private SessionsLoyaltyPointsConverter() {
    }

    public static UpdateSessionsLoyaltyPointsConfig toMs(UpdateLoyaltyPointsConfigDTO in) {
        UpdateSessionsLoyaltyPointsConfig out = new UpdateSessionsLoyaltyPointsConfig();
        if (in.getPointGain() != null) {
            out.setPointGain(toMs(in.getPointGain()));
        }
        return out;
    }

    public static LoyaltyPointsConfigDTO toDTO(LoyaltyPointsConfig in) {
        if (in == null) {
            return null;
        }
        LoyaltyPointsConfigDTO out = new LoyaltyPointsConfigDTO();
        if (in.getPointGain() != null) {
            out.setPointGain(toDTO(in.getPointGain()));
        }
        return out;
    }

    private static PointGainDTO toDTO(PointGain in) {
        PointGainDTO out = new PointGainDTO();
        out.setAmount(in.getAmount());
        out.setType(toDTO(in.getType()));
        return out;
    }

    private static SessionPointsTypeDTO toDTO(SessionPointsType in) {
        if (in == null) {
            return null;
        }
        return SessionPointsTypeDTO.valueOf(in.name());
    }

    private static PointGain toMs(PointGainDTO in) {
        PointGain out = new PointGain();
        out.setAmount(in.getAmount());
        out.setType(toDTO(in.getType()));
        return out;
    }

    private static SessionPointsType toDTO(SessionPointsTypeDTO in) {
        if (in == null) {
            return null;
        }
        return SessionPointsType.valueOf(in.name());
    }
}
