package es.onebox.mgmt.loyaltypoints.entities.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.PointExchange;
import es.onebox.mgmt.datasources.ms.entity.dto.MaxPoints;
import es.onebox.mgmt.datasources.ms.entity.dto.Expiration;
import es.onebox.mgmt.loyaltypoints.entities.dto.ExpirationDTO;
import es.onebox.mgmt.loyaltypoints.entities.dto.LoyaltyPointsConfigDTO;
import es.onebox.mgmt.loyaltypoints.entities.dto.MaxPointsDTO;
import es.onebox.mgmt.loyaltypoints.entities.dto.PointExchangeDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateLoyaltyPointsConfig;
import es.onebox.mgmt.loyaltypoints.entities.dto.UpdateLoyaltyPointsConfigDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class LoyaltyPointsConverter {
    public static LoyaltyPointsConfigDTO toDTO(LoyaltyPointsConfig in) {
        if (in == null) {
            return null;
        }
        LoyaltyPointsConfigDTO out = new LoyaltyPointsConfigDTO();
        out.setExpiration(toDTO(in.getExpiration()));
        out.setMaxPoints(toDTO(in.getMaxPoints()));
        out.setPointExchange(toDTO(in.getPointExchange()));
        out.setLastReset(in.getLastReset());
        return out;
    }

    public static UpdateLoyaltyPointsConfig toMs(UpdateLoyaltyPointsConfigDTO in) {
        if (in == null) {
            return null;
        }
        UpdateLoyaltyPointsConfig out = new UpdateLoyaltyPointsConfig();
        out.setExpiration(toMs(in.getExpiration()));
        out.setMaxPoints(toMs(in.getMaxPoints()));
        out.setPointExchange(toMs(in.getPointExchange()));
        return out;
    }

    private static ExpirationDTO toDTO(Expiration in) {
        if (in == null) {
            return null;
        }
        ExpirationDTO out = new ExpirationDTO();
        out.setEnabled(in.getEnabled());
        out.setMonths(in.getMonths());
        return out;
    }

    private static Expiration toMs(ExpirationDTO in) {
        if (in == null) {
            return null;
        }
        Expiration out = new Expiration();
        out.setEnabled(in.getEnabled());
        out.setMonths(in.getMonths());
        return out;
    }

    private static MaxPointsDTO toDTO(MaxPoints in) {
        if (in == null) {
            return null;
        }
        MaxPointsDTO out = new MaxPointsDTO();
        out.setEnabled(in.getEnabled());
        out.setAmount(in.getAmount());
        return out;
    }

    private static MaxPoints toMs(MaxPointsDTO in) {
        if (in == null) {
            return null;
        }
        MaxPoints out = new MaxPoints();
        out.setEnabled(in.getEnabled());
        out.setAmount(in.getAmount());
        return out;
    }

    private static List<PointExchangeDTO> toDTO(List<PointExchange> in) {
        if (CollectionUtils.isEmpty(in)) {
            return null;
        }
        return in.stream().map(LoyaltyPointsConverter::toDTO).toList();
    }

    private static PointExchangeDTO toDTO(PointExchange in) {
        if (in == null) {
            return null;
        }
        PointExchangeDTO out = new PointExchangeDTO();
        out.setCode(in.getCode());
        out.setValue(in.getValue());
        return out;
    }

    private static List<PointExchange> toMs(List<PointExchangeDTO> in) {
        if (CollectionUtils.isEmpty(in)) {
            return null;
        }
        return in.stream().map(LoyaltyPointsConverter::toMs).toList();
    }

    private static PointExchange toMs(PointExchangeDTO in) {
        if (in == null) {
            return null;
        }
        PointExchange out = new PointExchange();
        out.setCode(in.getCode());
        out.setValue(in.getValue());
        return out;
    }
}