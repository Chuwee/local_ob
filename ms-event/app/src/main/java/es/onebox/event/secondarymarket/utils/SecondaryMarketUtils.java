package es.onebox.event.secondarymarket.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketDates;
import es.onebox.event.secondarymarket.dto.ResalePriceTypeDTO;
import es.onebox.event.secondarymarket.dto.RestrictionsDTO;
import es.onebox.event.secondarymarket.dto.SaleType;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigExtended;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SecondaryMarketUtils {

    private SecondaryMarketUtils() {
    }


    public static void validateSecondaryMarketPricesAndSaleTypesConfig(SecondaryMarketConfigDTO currentConfig,
                                                                       SecondaryMarketConfigDTO newConfig,
                                                                       EventType eventType) {
        if (EventType.PRODUCT.equals(eventType)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_TYPE_DOES_NOT_SUPPORT_SECONDARY_MARKET);
        }
        else {
            boolean willEnable = CommonUtils.isTrue(newConfig.getEnabled())
                    || (newConfig.getEnabled() == null && (currentConfig != null &&
                                                            CommonUtils.isTrue(currentConfig.getEnabled())));
            boolean willSetSeasonTicket = CommonUtils.isTrue(newConfig.getIsSeasonTicket())
                    || (newConfig.getIsSeasonTicket() == null && (currentConfig != null &&
                                                                CommonUtils.isTrue(currentConfig.getIsSeasonTicket())));
            boolean willSetSaleTypeFull = SaleType.FULL.equals(newConfig.getSaleType())
                    || (newConfig.getSaleType() == null
                        && (currentConfig != null && SaleType.FULL.equals(currentConfig.getSaleType()))
            );
            boolean willSetResaleTypeProrated = ResalePriceTypeDTO.PRORATED.equals(newConfig.getPrice().getType());

            if (EventType.SEASON_TICKET.equals(eventType)) {
                if (!willSetSeasonTicket) {
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                            "Config is not correctly setting the seasonTicket flag",
                            null);
                }
                if (willSetSaleTypeFull && willSetResaleTypeProrated){
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                            "Full sale types cannot set a Prorated resale price type",
                            null);
                }
                if (!willSetSaleTypeFull && !willSetResaleTypeProrated) {
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                            "Partial sale types must set a Prorated resale price type",
                            null);
                }
                if (willEnable && willSetResaleTypeProrated && newConfig.getNumSessions() == null
                    && (currentConfig == null || ( currentConfig != null && (currentConfig.getNumSessions() == null
                                                                            || currentConfig.getNumSessions() < 1)))) {
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                                                 "Prorated resale price type must provide numSessions",
                                                 null);
                }

            } else {
                if (willSetSeasonTicket) {
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                            "Config is not correctly setting the seasonTicket flag",
                            null);
                }
                if (newConfig.getSaleType() != null) {
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                            "SaleType applies only to Season Ticket Configs",
                            null);
                }
                if (newConfig.getPrice() != null && willSetResaleTypeProrated) {
                    throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                            "Prorated resale price type applies only to Season Ticket Partial sale types",
                            null);
                }
            }
        }
        checkPriceRestrictions(newConfig);
    }

    public static void checkPriceRestrictions(SecondaryMarketConfigDTO newConfig) {
        // no need to validate other ResalePriceTypes because restrictions will not be copied in the converter to CouchEntity
        if (ResalePriceTypeDTO.PRICE_WITH_RESTRICTIONS.equals(newConfig.getPrice().getType())) {
            if (newConfig.getPrice().getRestrictions() == null) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_CONFIG,
                                             "Price with restrictions needs to providerestrictions",
                                              null);
            } else {
                 RestrictionsDTO restrictions = newConfig.getPrice().getRestrictions();

                 if (restrictions.getMax() == null || restrictions.getMin() == null) {
                     throw new OneboxRestException(MsEventErrorCode.NULL_SECONDARY_MARKET_VALUES);
                 }

                 if (restrictions.getMax() < 0 || restrictions.getMin() < 0) {
                     throw new OneboxRestException(MsEventErrorCode.NEGATIVE_SECONDARY_MARKET_VALUES);
                 }

                 if (restrictions.getMax() < restrictions.getMin()) {
                     throw new OneboxRestException(MsEventErrorCode.INVALID_SECONDARY_MARKET_RANGE);
                 }
             }
        }
    }

    public static ZonedDateTime getSecondaryMarketStartDate(SessionSecondaryMarketConfigExtended secondaryMarket) {
        return secondaryMarket.getDates() != null ? secondaryMarket.getDates().getStartDate() : null;
    }

    public static ZonedDateTime getSecondaryMarketEndDate(SessionSecondaryMarketConfigExtended secondaryMarket) {
        return secondaryMarket.getDates() != null ? secondaryMarket.getDates().getEndDate() : null;
    }

    public static ZonedDateTime calculateNewDate(ZonedDateTimeWithRelative requestDate, Timestamp sessionTimestamp,
                                                 ZonedDateTime fallbackDate, String venueTz) {

        if (requestDate != null) {
            if (requestDate.isAbsolute()) {
                return requestDate.absolute();
            } else if (requestDate.isRelative() && sessionTimestamp != null) {
                ZoneId zone = (fallbackDate != null) ? fallbackDate.getZone() : ZoneId.of(venueTz);
                return requestDate.relative().calculate(sessionTimestamp.toInstant().atZone(zone));
            }
        }
        return fallbackDate;
    }

    public static ZonedDateTime findFirstSessionSecMktStartDate(List<SessionConfig> eventSessions) {

        return Optional.ofNullable(eventSessions)
                .stream()
                .flatMap(Collection::stream)
                .map(SessionConfig::getSecondaryMarketDates)
                .filter(Objects::nonNull)
                .map(SessionSecondaryMarketDates::getStartDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public static ZonedDateTime findLastSessionSecMktEndDate(List<SessionConfig> eventSessions) {
        return Optional.ofNullable(eventSessions)
                .stream()
                .flatMap(Collection::stream)
                .map(SessionConfig::getSecondaryMarketDates)
                .filter(Objects::nonNull)
                .map(SessionSecondaryMarketDates::getEndDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    public static void validateSecondaryMarketAccess(Boolean allowSecondaryMarket) {
        if (!allowSecondaryMarket) {
            throw new OneboxRestException(MsEventErrorCode.SECONDARY_MARKET_NOT_ALLOWED_BY_ENTITY);
        }
    }

}
