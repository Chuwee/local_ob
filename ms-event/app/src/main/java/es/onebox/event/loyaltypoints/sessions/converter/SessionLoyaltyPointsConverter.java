package es.onebox.event.loyaltypoints.sessions.converter;

import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import es.onebox.event.loyaltypoints.sessions.domain.PointGain;
import es.onebox.event.loyaltypoints.sessions.domain.enums.SessionPointsType;
import es.onebox.event.loyaltypoints.sessions.dto.SessionLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.sessions.dto.PointGainDTO;
import es.onebox.event.loyaltypoints.sessions.dto.UpdateSessionLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.sessions.dto.enums.SessionPointsTypeDTO;
import es.onebox.event.sessions.dto.CreatePointGainDTO;
import es.onebox.event.sessions.dto.CreateSessionLoyaltyPointsConfigDTO;
import es.onebox.event.sessions.dto.CreateSessionPointsTypeDTO;

public class SessionLoyaltyPointsConverter {

    private SessionLoyaltyPointsConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static SessionLoyaltyPointsConfigDTO toDTO(SessionLoyaltyPointsConfig in) {
        SessionLoyaltyPointsConfigDTO out = new SessionLoyaltyPointsConfigDTO();
        if (in == null) {
            return out;
        }
        out.setPointGain(toDTO(in.getPointGain()));
        return out;
    }

    public static void updateLoyaltyPointsConfig(SessionLoyaltyPointsConfig target, UpdateSessionLoyaltyPointsConfigDTO source) {
        if (source != null) {
            target.setPointGain(toDao(source.getPointGain()));
        }
    }

    public static UpdateSessionLoyaltyPointsConfigDTO toUpdateLoyaltyPointsConfigDTO(CreateSessionLoyaltyPointsConfigDTO in) {
        if (in == null) {
            return null;
        }
        UpdateSessionLoyaltyPointsConfigDTO out = new UpdateSessionLoyaltyPointsConfigDTO();
        out.setPointGain(toDTO(in.getPointGain()));
        return out;
    }

    private static PointGainDTO toDTO(PointGain in) {
        if (in == null) {
            return null;
        }
        PointGainDTO out = new PointGainDTO();
        out.setAmount(in.getAmount());
        out.setType(toDTO(in.getType()));
        return out;
    }

    private static PointGainDTO toDTO(CreatePointGainDTO in) {
        if (in == null) {
            return null;
        }
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

    private static SessionPointsTypeDTO toDTO(CreateSessionPointsTypeDTO in) {
        if (in == null) {
            return null;
        }
        return SessionPointsTypeDTO.valueOf(in.name());
    }

    private static PointGain toDao(PointGainDTO in) {
        if (in == null) {
            return null;
        }
        PointGain out = new PointGain();
        out.setAmount(in.getAmount());
        out.setType(toDao(in.getType()));
        return out;
    }

    private static SessionPointsType toDao(SessionPointsTypeDTO in) {
        if (in == null) {
            return null;
        }
        return SessionPointsType.valueOf(in.name());
    }
}
