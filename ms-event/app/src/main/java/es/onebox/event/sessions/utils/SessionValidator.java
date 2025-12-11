package es.onebox.event.sessions.utils;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.RateRecord;
import es.onebox.event.events.enums.BookingExpirationType;
import es.onebox.event.events.enums.BookingSessionExpiration;
import es.onebox.event.events.enums.BookingSessionTimespan;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigExtended;
import es.onebox.event.sessions.dto.CreateSessionDTO;
import es.onebox.event.sessions.dto.RateDTO;
import es.onebox.event.sessions.dto.SessionDateDTO;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionTarifaRecord;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class SessionValidator {

    private static final int SESSION_NAME_LENGTH = 50;
    private static final String SESSION_INVALID_CHAR = "|";

    private SessionValidator() {
    }

    public static void validateSessionName(String sessionName) {
        if (sessionName != null) {
            if (sessionName.length() > SESSION_NAME_LENGTH) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_NAME_FORMAT,
                        "session name length cannot be above " + SESSION_NAME_LENGTH + " characters", null);
            }
            if (sessionName.contains(SESSION_INVALID_CHAR)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_NAME_FORMAT,
                        "session name has invalid characters. | not allowed", null);
            }
        }
    }

    public static void validateSessionRates(List<CpanelSesionTarifaRecord> eventRates, List<RateDTO> sessionRates) {
        if (CommonUtils.isEmpty(sessionRates) || sessionRates.stream().filter(RateDTO::isDefault).count() != 1) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("invalid sessionRates configuration").build();
        }
        for (RateDTO rate : sessionRates) {
            if (eventRates.stream().noneMatch(r -> r.getIdtarifa().equals(rate.getId().intValue()))) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).setHttpStatus(HttpStatus.BAD_REQUEST)
                        .setMessage("invalid sessionRates - not available for event").build();
            }
        }
    }

    public static void validateRates(List<RateRecord> eventRates, List<RateDTO> sessionRates) {
        if (CommonUtils.isEmpty(sessionRates) || sessionRates.stream().filter(RateDTO::isDefault).count() != 1) {
            throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).setHttpStatus(HttpStatus.BAD_REQUEST)
                    .setMessage("invalid sessionRates configuration").build();
        }
        for (RateDTO rate : sessionRates) {
            if (eventRates.stream().noneMatch(r -> r.getIdTarifa().equals(rate.getId().intValue()))) {
                throw OneboxRestException.builder(MsEventRateErrorCode.INVALID_RATE).setHttpStatus(HttpStatus.BAD_REQUEST)
                        .setMessage("invalid sessionRates - not available for event").build();
            }
        }
    }

    public static void validateActivity(CreateSessionDTO session, CpanelEventoRecord event) {
        if (EventType.ACTIVITY.getId().equals(event.getTipoevento())) {
            if (session.getSaleType() == null || SessionSalesType.byId(session.getSaleType()) == null) {
                throw new OneboxRestException(MsEventSessionErrorCode.SALE_TYPE_MANDATORY);
            }
            if ((SessionSalesType.byId(session.getSaleType()).equals(SessionSalesType.GROUP) ||
                    SessionSalesType.byId(session.getSaleType()).equals(SessionSalesType.MIXED)) &&
                    !CommonUtils.isTrue(event.getPermitegrupos())) {
                throw new OneboxRestException(MsEventSessionErrorCode.SALE_TYPE_GROUP_FORBIDDEN);
            }
        }
    }

    public static void validateCreateDates(CreateSessionDTO session, CpanelEventoRecord event) {
        if (session.getSessionStartDate() == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_START_REQUIRED);
        }
        if (session.getPublishDate() == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_REQUIRED);
        }
        if (session.getSalesStartDate() == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_REQUIRED);
        }
        if (session.getSalesEndDate() == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_END_REQUIRED);
        }
        if (session.getSalesStartDate().isAfter(session.getSalesEndDate())) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END);
        }
        if (CommonUtils.isTrue(event.getPermitereservas())) {
            if (session.getBookingStartDate() == null) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_REQUIRED);
            }
            if (session.getBookingEndDate() == null) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_REQUIRED);
            }
        }
    }

    public static void validateUpdateDates(UpdateSessionRequestDTO session, CpanelSesionRecord sesionRecord,
                                           CpanelEventoRecord event, SessionSecondaryMarketConfigExtended secondaryMarketConfig) {

        Timestamp sessionStart = sesionRecord.getFechainiciosesion();
        Optional<Timestamp> targetSessionStart = Optional.ofNullable(CommonUtils.zonedDateTimeToTimestamp(session.getDate().getStart()));

        validateStart(getChangedOrCurrent(session.getDate().getStart(), sessionStart));
        validateEnd(getChangedOrCurrent(session.getDate().getStart(), sessionStart),
                getChangedOrCurrent(session.getDate().getEnd(), sesionRecord.getFecharealfinsesion()));

        Boolean eventHasBookingsEnabled = CommonUtils.isTrue(event.getPermitereservas());
        if (CommonUtils.isTrue(session.getEnableChannels()) ||
                (session.getEnableChannels() == null && CommonUtils.isTrue(sesionRecord.getPublicado()))) {
            validateRelease(
                    getChangedOrCurrent(session.getDate().getStart(), sessionStart),
                    getChangedOrCurrent(session.getDate().getChannelPublication(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechapublicacion()),
                    getChangedOrCurrent(session.getDate().getSalesStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechaventa()),
                    getChangedOrCurrent(session.getDate().getBookingsStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechainicioreserva()),
                    eventHasBookingsEnabled);
        }
        if (CommonUtils.isTrue(session.getEnableSales()) || (
                session.getEnableSales() == null && CommonUtils.isTrue(sesionRecord.getEnventa()))) {
            validateSalesStart(
                    getChangedOrCurrent(session.getDate().getStart(), sessionStart),
                    getChangedOrCurrent(session.getDate().getChannelPublication(), sessionStart, sesionRecord.getFechapublicacion()),
                    getChangedOrCurrent(session.getDate().getSalesStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechaventa()));
            validateSalesEnd(
                    getChangedOrCurrent(session.getDate().getSalesStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechaventa()),
                    getChangedOrCurrent(session.getDate().getSalesEnd(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechafinsesion()),
                    getChangedOrCurrent(session.getDate().getBookingsEnd(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechafinreserva()),
                    eventHasBookingsEnabled);
        }
        if (eventHasBookingsEnabled && (CommonUtils.isTrue(session.getEnableBookings()) ||
                (session.getEnableBookings() == null && CommonUtils.isTrue(sesionRecord.getReservasactivas())))) {
            validateBookingsStart(
                    getChangedOrCurrent(session.getDate().getChannelPublication(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechapublicacion()),
                    getChangedOrCurrent(session.getDate().getBookingsStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechainicioreserva()));
            validateBookingsEnd(event,
                    getChangedOrCurrent(session.getDate().getStart(), sessionStart),
                    getChangedOrCurrent(session.getDate().getSalesEnd(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechafinsesion()),
                    getChangedOrCurrent(session.getDate().getBookingsStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechainicioreserva()),
                    getChangedOrCurrent(session.getDate().getBookingsEnd(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechafinreserva()));
        }
        if (hasUpdateSecondaryMarketConfigs(session)) {
            Timestamp sessionEnd = sesionRecord.getFechafinsesion();
            Optional<Timestamp> targetSessionEnd = Optional.ofNullable(CommonUtils.zonedDateTimeToTimestamp(session.getDate().getEnd()));
            validateSecondaryMarketConfig(session,
                    secondaryMarketConfig,
                    getChangedOrCurrent(session.getDate().getSecondaryMarketStart(), targetSessionStart.orElse(sessionStart), sesionRecord.getFechafinsesion()),
                    getChangedOrCurrent(session.getDate().getSecondaryMarketEnd(), targetSessionEnd.orElse(sessionEnd), sesionRecord.getFechafinsesion())
            );
        }
    }

    private static ZonedDateTime getChangedOrCurrent(ZonedDateTime dto, Timestamp record) {
        return dto != null ? dto : CommonUtils.timestampToZonedDateTime(record);
    }

    private static ZonedDateTime getChangedOrCurrent(ZonedDateTimeWithRelative dto, Timestamp sessionStart, Timestamp record) {
        ZonedDateTime target = ConverterUtils.resolveZonedRelativeDateTimeValue(dto, sessionStart);
        return target != null ? target : CommonUtils.timestampToZonedDateTime(record);
    }

    private static void validateStart(ZonedDateTime start) {
        if (start == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_START_REQUIRED);
        }
    }

    private static void validateEnd(ZonedDateTime start, ZonedDateTime end) {
        if (end != null && end.isBefore(start)) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_END_BEFORE_START);
        }
    }

    private static void validateRelease(ZonedDateTime start, ZonedDateTime release, ZonedDateTime salesStart,
                                        ZonedDateTime bookingStart, Boolean eventAllowBookings) {
        if (release == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_REQUIRED);
        } else {
            if (release.isAfter(start)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_START);
            }
            if (salesStart != null && release.isAfter(salesStart)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_SALES_START);
            }
            if (eventAllowBookings && bookingStart != null && release.isAfter(bookingStart)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_RELEASE_AFTER_BOOKING_START);
            }
        }
    }

    private static void validateSalesStart(ZonedDateTime start, ZonedDateTime release, ZonedDateTime salesStart) {
        if (salesStart == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_REQUIRED);
        } else {
            if (salesStart.isBefore(release)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_BEFORE_RELEASE);
            }
            if (salesStart.isAfter(start)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_AFTER_START);
            }
        }
    }

    private static void validateSalesEnd(ZonedDateTime salesStart, ZonedDateTime salesEnd, ZonedDateTime bookingEnd,
                                         Boolean eventAllowBookings) {
        if (salesEnd == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_END_REQUIRED);
        } else {
            if (!salesStart.isBefore(salesEnd)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END);
            }
            if (eventAllowBookings && bookingEnd != null && salesEnd.isBefore(bookingEnd)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_SALES_END_BEFORE_BOOKING_END);
            }
        }
    }

    private static void validateBookingsStart(ZonedDateTime release, ZonedDateTime bookingStart) {
        if (bookingStart == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_REQUIRED);
        } else {
            if (bookingStart.isBefore(release)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_BEFORE_RELEASE);
            }
        }
    }

    public static void validateBookingsEnd(CpanelEventoRecord event, ZonedDateTime start, ZonedDateTime salesEnd,
                                           ZonedDateTime bookingStart, ZonedDateTime bookingEnd) {
        if (bookingEnd == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_REQUIRED);
        } else {
            if (!bookingStart.isBefore(bookingEnd)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_START_NOT_BEFORE_BOOKING_END);
            }
            if (bookingEnd.isAfter(salesEnd)) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_AFTER_SALES_END);
            }
            if (event.getTipofechalimitereserva() != null) {
                if (event.getTipofechalimitereserva().equals(BookingExpirationType.DATE.getTipo())) {
                    validateBookingDateLimitDate(event, bookingEnd);
                } else if (event.getTipofechalimitereserva().equals(BookingExpirationType.SESSION.getTipo())) {
                    validateBookingSessionLimitDate(event, start, bookingEnd);
                }
            }
        }
    }

    private static void validateBookingDateLimitDate(CpanelEventoRecord event, ZonedDateTime bookingEnd) {
        if (CommonUtils.timestampToZonedDateTime(event.getFechalimite()).isBefore(bookingEnd)) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_CONFLICT_EVENT_LIMIT);
        }
    }

    private static void validateBookingSessionLimitDate(CpanelEventoRecord event, ZonedDateTime
            start, ZonedDateTime bookingEnd) {
        ZonedDateTime limitDate = ZonedDateTime.from(start);
        BookingSessionTimespan bookingSessionTimespan = BookingSessionTimespan.byId(event.getTipounidadeslimite());
        if (event.getTipolimite().equals(BookingSessionExpiration.BEFORE.getTipo())) {
            switch (bookingSessionTimespan) {
                case DAY:
                    limitDate = limitDate.minusDays(event.getNumunidadeslimite());
                    break;
                case WEEK:
                    limitDate = limitDate.minusWeeks(event.getNumunidadeslimite());
                    break;
                case MONTH:
                    limitDate = limitDate.minusMonths(event.getNumunidadeslimite());
                    break;
                case HOUR:
                    limitDate = limitDate.minusHours(event.getNumunidadeslimite());
                    break;
                default:
            }
        } else {
            switch (BookingSessionTimespan.byId(event.getTipolimite())) {
                case DAY:
                    limitDate = limitDate.plusDays(event.getNumunidadeslimite());
                    break;
                case WEEK:
                    limitDate = limitDate.plusWeeks(event.getNumunidadeslimite());
                    break;
                case MONTH:
                    limitDate = limitDate.plusMonths(event.getNumunidadeslimite());
                    break;
                case HOUR:
                    limitDate = limitDate.plusHours(event.getNumunidadeslimite());
                    break;
                default:
            }
        }
        if (!BookingSessionTimespan.HOUR.equals(bookingSessionTimespan)) {
            limitDate = limitDate.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        if (limitDate.isBefore(bookingEnd)) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_DATES_BOOKING_END_CONFLICT_EVENT_LIMIT);
        }
    }

    public static void validateSessionSalesType(CreateSessionDTO session) {
        if(session.getSaleType() != null && SessionSalesType.byId(session.getSaleType()) == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SALES_TYPE);
        }
    }

    public static void validateNonRelativeDates(SessionDateDTO date) {
        if (Boolean.FALSE.equals(hasNotRelativeDates(date))) {
            throw ExceptionBuilder.build(MsEventSessionErrorCode.RELATIVE_DATES_NOT_ALLOWED);
        }
    }

    public static boolean hasNotRelativeDates(SessionDateDTO date) {
        if(date == null) {
            return Boolean.TRUE;
        }
        boolean[] matrix = { validateNonRelativeDate(date.getChannelPublication()),
                validateNonRelativeDate(date.getBookingsStart()), validateNonRelativeDate(date.getBookingsEnd()),
                validateNonRelativeDate(date.getSalesStart()), validateNonRelativeDate(date.getSalesEnd()),
                validateNonRelativeDate(date.getAdmissionStart()), validateNonRelativeDate(date.getAdmissionEnd()) };
        return BooleanUtils.and(matrix);
    }

    private static boolean validateNonRelativeDate(ZonedDateTimeWithRelative date) {
        return date == null || date.isAbsolute();
    }

    public static boolean hasAnySessionConfigFields(UpdateSessionRequestDTO dto) {
        return ObjectUtils.anyNotNull(dto.getPresalePromotionId(), dto.getEnableMembersLoginsLimit(),
                dto.getPresaleEnabled(), dto.getCountries(), dto.getEnableCountryFilter(), dto.getQueueAlias(),
                dto.getEnableQueue(), dto.getStreaming());
    }

    private static void validateSecondaryMarketConfig(UpdateSessionRequestDTO session, SessionSecondaryMarketConfigExtended secondaryMarketConfig,
                                                      ZonedDateTime secondaryMarketStart, ZonedDateTime secondaryMarketEnd) {

        if (hasSecondaryMarketEnableRequired(session, secondaryMarketConfig)) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_SECONDARY_MARKET_ENABLE_REQUIRED);
        }

        if (secondaryMarketStart == null || secondaryMarketEnd == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SECONDARY_MARKET_CONFIG);
        }

        if (hasSecondaryMarketStartDateRequired(session, secondaryMarketConfig)) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_SECONDARY_MARKET_START_DATE_REQUIRED);
        }

        if (hasSecondaryMarketEndDateRequired(session, secondaryMarketConfig)) {
            throw new OneboxRestException(MsEventSessionErrorCode.SESSION_SECONDARY_MARKET_END_DATE_REQUIRED);
        }

        if (secondaryMarketStart.isAfter(secondaryMarketEnd)) {
            throw new OneboxRestException(MsEventSessionErrorCode.SECONDARY_MARKET_START_DATE_AFTER_END_DATE);
        }
    }

    private static boolean hasSecondaryMarketEnableRequired(UpdateSessionRequestDTO session, SessionSecondaryMarketConfigExtended secondaryMarketConfig) {
        return session.getEnableSecondaryMarket() == null && (secondaryMarketConfig == null
                || secondaryMarketConfig.getDates() == null || secondaryMarketConfig.getDates().getEnabled() == null);
    }

    private static boolean hasSecondaryMarketStartDateRequired(UpdateSessionRequestDTO session, SessionSecondaryMarketConfigExtended secondaryMarketConfig) {
        return session.getDate().getSecondaryMarketStart() == null && (secondaryMarketConfig == null
                || secondaryMarketConfig.getDates() == null || secondaryMarketConfig.getDates().getStartDate() == null);
    }

    private static boolean hasSecondaryMarketEndDateRequired(UpdateSessionRequestDTO session, SessionSecondaryMarketConfigExtended secondaryMarketConfig) {
        return session.getDate().getSecondaryMarketEnd() == null && (secondaryMarketConfig == null
                || secondaryMarketConfig.getDates() == null || secondaryMarketConfig.getDates().getEndDate() == null);
    }

    public static boolean hasUpdateSecondaryMarketConfigs(UpdateSessionRequestDTO session) {
        if (session == null) {
            return false;
        }

        boolean enableSecondaryMarketNotNull = session.getEnableSecondaryMarket() != null;
        boolean dateNotNull = session.getDate() != null;
        boolean secondaryMarketStartNotNull = dateNotNull && session.getDate().getSecondaryMarketStart() != null;
        boolean secondaryMarketEndNotNull = dateNotNull && session.getDate().getSecondaryMarketEnd() != null;

        return (enableSecondaryMarketNotNull || secondaryMarketStartNotNull || secondaryMarketEndNotNull);
    }

}