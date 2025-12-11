package es.onebox.event.secondarymarket.converter;

import es.onebox.event.secondarymarket.domain.Commission;
import es.onebox.event.secondarymarket.domain.ResalePrice;
import es.onebox.event.secondarymarket.domain.ResalePriceType;
import es.onebox.event.secondarymarket.domain.Restrictions;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketConfig;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;
import es.onebox.event.secondarymarket.dto.CommissionDTO;
import es.onebox.event.secondarymarket.dto.CreateSessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.DatesDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.ResalePriceDTO;
import es.onebox.event.secondarymarket.dto.ResalePriceTypeDTO;
import es.onebox.event.secondarymarket.dto.RestrictionsDTO;
import es.onebox.event.secondarymarket.dto.SecondaryMarketType;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigExtended;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.CreateSessionDTO;

public class SessionSecondaryMarketConverter {

    private SessionSecondaryMarketConverter() {
    }

    public static SessionSecondaryMarketConfigDTO toDTO(SessionSecondaryMarketConfig sessionSecondaryMarketConfig,
                                                        EventSecondaryMarketConfigDTO eventSecondaryMarketConfig,
                                                        SessionConfig sessionConfig) {
        if (sessionSecondaryMarketConfig != null) {
            return toDTO(sessionSecondaryMarketConfig, sessionConfig);
        }
        if (eventSecondaryMarketConfig != null) {
            return toDTO(eventSecondaryMarketConfig, sessionConfig);
        }
        if (sessionConfig != null && sessionConfig.getSecondaryMarketDates() != null) {
            return buildFromSessionConfigDates(sessionConfig);
        }
        return null;
    }

    public static SessionSecondaryMarketConfigDTO toDTO(SessionSecondaryMarketConfig sessionSecondaryMarketConfig,
                                                        SessionConfig sessionConfig) {
        SessionSecondaryMarketConfigDTO dto = new SessionSecondaryMarketConfigDTO();

        dto.setEnabled(sessionSecondaryMarketConfig.getEnabled());
        setPriceAndCommission(dto, sessionSecondaryMarketConfig.getPrice(), sessionSecondaryMarketConfig.getCommission());
        setDates(dto, sessionConfig);

        dto.setType(SecondaryMarketType.SESSION);

        return dto;
    }

    private static SessionSecondaryMarketConfigDTO toDTO(EventSecondaryMarketConfigDTO eventSecondaryMarketConfig,
                                                        SessionConfig sessionConfig) {
        SessionSecondaryMarketConfigDTO dto = new SessionSecondaryMarketConfigDTO();

        dto.setEnabled(eventSecondaryMarketConfig.getEnabled());
        setPriceAndCommission(dto, eventSecondaryMarketConfig.getPrice(), eventSecondaryMarketConfig.getCommission());
        setDates(dto, sessionConfig);

        dto.setType(SecondaryMarketType.EVENT);

        return dto;
    }


    private static void setDates(SessionSecondaryMarketConfigDTO dto, SessionConfig sessionConfig) {
        if (sessionConfig != null && sessionConfig.getSecondaryMarketDates() != null) {
            SessionSecondaryMarketDates sessionSecondaryMarketDates = sessionConfig.getSecondaryMarketDates();
            DatesDTO datesDTO = new DatesDTO();
            datesDTO.setEnabled(sessionSecondaryMarketDates.getEnabled());
            datesDTO.setStartDate(sessionSecondaryMarketDates.getStartDate());
            datesDTO.setEndDate(sessionSecondaryMarketDates.getEndDate());
            dto.setDates(datesDTO);
        }
    }

    private static void setPriceAndCommission(SessionSecondaryMarketConfigDTO dto, Object price, Object commission) {
        if (price != null) {
            ResalePriceDTO resalePriceDTO = new ResalePriceDTO();
            if (price instanceof ResalePrice) {
                ResalePrice resalePrice = (ResalePrice) price;
                resalePriceDTO.setType(ResalePriceTypeDTO.valueOf(resalePrice.getType().name()));

                if (resalePrice.getRestrictions() != null) {
                    RestrictionsDTO restrictionsDTO = new RestrictionsDTO();
                    restrictionsDTO.setMin(resalePrice.getRestrictions().getMin());
                    restrictionsDTO.setMax(resalePrice.getRestrictions().getMax());
                    resalePriceDTO.setRestrictions(restrictionsDTO);
                }
            } else if (price instanceof ResalePriceDTO) {
                ResalePriceDTO resalePrice = (ResalePriceDTO) price;
                resalePriceDTO.setType(resalePrice.getType());

                if (resalePrice.getRestrictions() != null) {
                    RestrictionsDTO restrictionsDTO = new RestrictionsDTO();
                    restrictionsDTO.setMin(resalePrice.getRestrictions().getMin());
                    restrictionsDTO.setMax(resalePrice.getRestrictions().getMax());
                    resalePriceDTO.setRestrictions(restrictionsDTO);
                }
            }
            dto.setPrice(resalePriceDTO);
        }

        if (commission != null) {
            CommissionDTO commissionDTO = new CommissionDTO();
            if (commission instanceof Commission commissionEntity) {
                commissionDTO.setPercentage(commissionEntity.getPercentage());
            } else if (commission instanceof CommissionDTO) {
                commissionDTO = (CommissionDTO) commission;
            }
            dto.setCommission(commissionDTO);
        }
    }

    public static SessionSecondaryMarketConfig toEntity(CreateSessionSecondaryMarketConfigDTO dto) {
        SessionSecondaryMarketConfig entity = new SessionSecondaryMarketConfig();
        entity.setEnabled(dto.getEnabled());

        if (dto.getPrice() != null) {
            ResalePrice resalePrice = new ResalePrice();
            resalePrice.setType(ResalePriceType.valueOf(dto.getPrice().getType().name()));

            if (ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS.equals(dto.getPrice().getType())
                && dto.getPrice().getRestrictions() != null) {
                Restrictions restrictions = new Restrictions();
                restrictions.setMin(dto.getPrice().getRestrictions().getMin());
                restrictions.setMax(dto.getPrice().getRestrictions().getMax());
                resalePrice.setRestrictions(restrictions);
            }

            entity.setPrice(resalePrice);
        }

        if (dto.getCommission() != null) {
            Commission commission = new Commission();
            commission.setPercentage(dto.getCommission().getPercentage());
            entity.setCommission(commission);
        }

        return entity;
    }

    public static SessionSecondaryMarketDates buildSessionSecondaryMarketDateToSession(CreateSessionDTO request) {
        SessionSecondaryMarketDates date = new SessionSecondaryMarketDates();
        date.setEnabled(Boolean.TRUE);
        date.setStartDate(request.getSecondaryMarketStartDate());
        date.setEndDate(request.getSecondaryMarketEndDate());

        return date;
    }

    public static SessionSecondaryMarketConfig toEntity(SessionSecondaryMarketConfigExtended extended) {
        if (extended == null) {
            return null;
        }
        SessionSecondaryMarketConfig sessionSecondaryMarketConfig = new SessionSecondaryMarketConfig();
        sessionSecondaryMarketConfig.setPrice(extended.getPrice());
        sessionSecondaryMarketConfig.setEnabled(extended.getEnabled());
        sessionSecondaryMarketConfig.setCommission(extended.getCommission());

        return sessionSecondaryMarketConfig;
    }

    private static SessionSecondaryMarketConfigDTO buildFromSessionConfigDates(SessionConfig sessionConfig) {
        SessionSecondaryMarketConfigDTO dto = new SessionSecondaryMarketConfigDTO();
        setDates(dto, sessionConfig);
        return dto;
    }
}
