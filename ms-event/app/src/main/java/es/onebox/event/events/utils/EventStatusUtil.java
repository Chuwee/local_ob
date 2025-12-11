package es.onebox.event.events.utils;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.dto.BaseEventChannelDTO;
import es.onebox.event.events.dto.EventInfoDTO;
import es.onebox.event.events.dto.statusflag.EventChannelReleaseFlagStatus;
import es.onebox.event.events.dto.statusflag.EventChannelSaleFlagStatus;
import es.onebox.event.events.dto.statusflag.EventFlags;
import es.onebox.event.events.dto.statusflag.EventReleaseFlag;
import es.onebox.event.events.dto.statusflag.EventSaleFlag;
import es.onebox.event.events.dto.statusflag.SessionFlags;
import es.onebox.event.events.dto.statusflag.SessionReleaseFlagStatus;
import es.onebox.event.events.dto.statusflag.SessionSaleFlagStatus;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventChannelStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionDTO;
import es.onebox.event.sessions.dto.SessionStatus;
import org.apache.commons.lang3.BooleanUtils;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EventStatusUtil {

    private EventStatusUtil() {
    }

    public static SessionFlags calcularEstadoSesion(SessionRecord sesion, EventStatus eventStatus) {
        SessionFlags sessionFlags = new SessionFlags();

        Date fechaActual = new Date();

        // Calculo los estados de programacion
        if (eventStatus != null && eventStatus.equals(EventStatus.READY)) {
            // Se el estado del evento es listo calculamos el estado
            fillSessionStateReady(sessionFlags, sesion, fechaActual);
        } else {
            // En otro caso cogemos el estado del evento
            fillSessionStateOthers(sessionFlags, eventStatus);
        }

        // Calculo el indicador de venta
        if (eventStatus != null && eventStatus.equals(EventStatus.READY) && sessionFlags.getRelease() != null) {
            // Se el estado del evento es listo calculamos el estado
            fillSessionStateReadyIndicators(sessionFlags, sesion, fechaActual);
        } else {
            // En otro caso cogemos el estado del evento
            fillSessionStateOthersIndicators(sessionFlags, eventStatus);
        }

        return sessionFlags;
    }

    private static void fillSessionStateOthersIndicators(SessionFlags sessionFlags, EventStatus eventStatus) {
        if (EventStatus.PLANNED.equals(eventStatus)) {
            sessionFlags.setSale(SessionSaleFlagStatus.PLANNED);
        } else if (EventStatus.IN_PROGRAMMING.equals(eventStatus)) {
            sessionFlags.setSale(SessionSaleFlagStatus.IN_PROGRAMMING);
        } else if (EventStatus.CANCELLED.equals(eventStatus)) {
            sessionFlags.setSale(SessionSaleFlagStatus.CANCELLED);
        } else if (EventStatus.NOT_ACCOMPLISHED.equals(eventStatus)) {
            sessionFlags.setSale(SessionSaleFlagStatus.NOT_ACCOMPLISHED);
        } else if (EventStatus.FINISHED.equals(eventStatus)) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_FINISHED);
        }
    }

    private static void fillSessionStateReadyIndicators(SessionFlags sessionFlags, SessionRecord session, Date fechaActual) {
        if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.PLANNED)) {
            sessionFlags.setSale(SessionSaleFlagStatus.PLANNED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.IN_PROGRAMMING)) {
            sessionFlags.setSale(SessionSaleFlagStatus.IN_PROGRAMMING);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASE_PENDING) && session.getEnventa() == 1) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_PENDING);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASE_PENDING) && session.getFechaventa().compareTo(fechaActual) > 0 && session.getEnventa() == 0) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_CANCELLED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASE_PENDING) && session.getFechaventa().compareTo(fechaActual) <= 0 && session.getEnventa() == 0) {
            sessionFlags.setSale(SessionSaleFlagStatus.PENDING_SALE_CANCELLED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASED) && session.getFechaventa().compareTo(fechaActual) > 0 && session.getEnventa() == 1) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_PENDING);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASED) && session.getFechaventa().compareTo(fechaActual) <= 0 && session.getEnventa() == 1) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASED) && session.getEnventa() == 0) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_CANCELLED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASE_CANCELLED)) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_CANCELLED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.RELEASE_FINISHED)) {
            sessionFlags.setSale(SessionSaleFlagStatus.SALE_FINISHED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.CANCELLED)) {
            sessionFlags.setSale(SessionSaleFlagStatus.CANCELLED);
        } else if (sessionFlags.getRelease().equals(SessionReleaseFlagStatus.NOT_ACCOMPLISHED)) {
            sessionFlags.setSale(SessionSaleFlagStatus.NOT_ACCOMPLISHED);
        }
    }

    private static void fillSessionStateOthers(SessionFlags sessionFlags, EventStatus eventStatus) {
        if (EventStatus.PLANNED.equals(eventStatus)) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.PLANNED);
        } else if (EventStatus.IN_PROGRAMMING.equals(eventStatus)) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.IN_PROGRAMMING);
        } else if (EventStatus.CANCELLED.equals(eventStatus)) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.CANCELLED);
        } else if (EventStatus.NOT_ACCOMPLISHED.equals(eventStatus)) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.NOT_ACCOMPLISHED);
        } else if (EventStatus.FINISHED.equals(eventStatus)) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.RELEASE_FINISHED);
        }
    }

    private static void fillSessionStateReady(SessionFlags sessionFlags, SessionRecord session, Date now) {
        if (session.getEstado().equals(SessionStatus.FINALIZED.getId())) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.RELEASE_FINISHED);
        } else if (session.getEstado().equals(SessionStatus.PLANNED.getId())) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.PLANNED);
        } else if (session.getEstado().equals(SessionStatus.SCHEDULED.getId())) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.IN_PROGRAMMING);
        } else if (session.getEstado().equals(SessionStatus.READY.getId())) {
            checkSessionStatetReady(sessionFlags, session, now);
        } else if (session.getEstado().equals(SessionStatus.CANCELLED.getId())
                || session.getEstado().equals(SessionStatus.CANCELLED_EXTERNAL.getId())) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.CANCELLED);
        } else if (session.getEstado().equals(SessionStatus.NOT_ACCOMPLISHED.getId())) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.NOT_ACCOMPLISHED);
        }
    }

    private static void checkSessionStatetReady(SessionFlags sessionFlags, SessionRecord session, Date fechaActual) {
        if (session.getFechafinsesion().compareTo(fechaActual) >= 0 && session.getFechapublicacion().compareTo(fechaActual) > 0) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.RELEASE_PENDING);
        } else if (session.getFechafinsesion().compareTo(fechaActual) >= 0 && session.getPublicado() == 1) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.RELEASED);
        } else if (session.getFechafinsesion().compareTo(fechaActual) >= 0 && session.getPublicado() == 0) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.RELEASE_CANCELLED);
        } else if (session.getFechafinsesion().compareTo(fechaActual) < 0) {
            sessionFlags.setRelease(SessionReleaseFlagStatus.RELEASE_FINISHED);
        }
    }

    private static EventFlags getEventStatus(EventInfoDTO event, List<SessionRecord> sessions) {
        EventFlags eventFlags = new EventFlags();

        Map<SessionReleaseFlagStatus, Integer> releaseCounter = new EnumMap<>(SessionReleaseFlagStatus.class);
        Map<SessionSaleFlagStatus, Integer> saleCounter = new EnumMap<>(SessionSaleFlagStatus.class);

        fillEventStateSession(event, sessions, releaseCounter, saleCounter);

        if (releaseCounter.isEmpty()) {
            releaseCounter.put(SessionReleaseFlagStatus.IN_PROGRAMMING, 1);
        }
        if (saleCounter.isEmpty()) {
            saleCounter.put(SessionSaleFlagStatus.IN_PROGRAMMING, 1);
        }

        // Indicador de publicado
        fillEventPublishingIndicators(eventFlags, releaseCounter);

        // Indicador de venta
        fillEventSalesIndicators(eventFlags, saleCounter);

        return eventFlags;
    }

    private static void fillEventStateSession(EventInfoDTO event,
                                              List<SessionRecord> sessions,
                                              Map<SessionReleaseFlagStatus, Integer> releaseCounter,
                                              Map<SessionSaleFlagStatus, Integer> saleCounter) {
        Integer number;
        for (SessionRecord sesion : sessions) {
            SessionFlags sessionFlags = calcularEstadoSesion(sesion, event.getStatus());

            // Indicador de publicado
            if (sessionFlags.getRelease() != null) {
                number = releaseCounter.get(sessionFlags.getRelease());
                if (number != null) {
                    releaseCounter.put(sessionFlags.getRelease(), number + 1);
                } else {
                    releaseCounter.put(sessionFlags.getRelease(), 1);
                }
            }

            // Indicador de venta
            if (sessionFlags.getSale() != null) {
                number = saleCounter.get(sessionFlags.getSale());
                if (number != null) {
                    saleCounter.put(sessionFlags.getSale(), number + 1);
                } else {
                    saleCounter.put(sessionFlags.getSale(), 1);
                }
            }
        }
    }

    private static void fillEventSalesIndicators(EventFlags eventFlags, Map<SessionSaleFlagStatus, Integer> saleCounter) {
        if (saleCounter.get(SessionSaleFlagStatus.SALE) != null) {
            eventFlags.setSale(EventSaleFlag.SALE);
        } else if (saleCounter.get(SessionSaleFlagStatus.SALE_PENDING) != null) {
            eventFlags.setSale(EventSaleFlag.SALE_PENDING);
        } else if (saleCounter.get(SessionSaleFlagStatus.SALE_CANCELLED) != null) {
            eventFlags.setSale(EventSaleFlag.SALE_CANCELLED);
        } else if (saleCounter.get(SessionSaleFlagStatus.PLANNED) != null) {
            eventFlags.setSale(EventSaleFlag.PLANNED);
        } else if (saleCounter.get(SessionSaleFlagStatus.IN_PROGRAMMING) != null) {
            eventFlags.setSale(EventSaleFlag.IN_PROGRAMMING);
        } else if (saleCounter.get(SessionSaleFlagStatus.CANCELLED) != null) {
            eventFlags.setSale(EventSaleFlag.CANCELLED);
        } else if (saleCounter.get(SessionSaleFlagStatus.NOT_ACCOMPLISHED) != null) {
            eventFlags.setSale(EventSaleFlag.NOT_ACCOMPLISHED);
        } else if (saleCounter.get(SessionSaleFlagStatus.SALE_FINISHED) != null) {
            eventFlags.setSale(EventSaleFlag.SALE_FINISHED);
        }
    }

    private static void fillEventPublishingIndicators(EventFlags eventFlags, Map<SessionReleaseFlagStatus, Integer> releaseCounter) {
        if (releaseCounter.get(SessionReleaseFlagStatus.RELEASED) != null) {
            eventFlags.setRelease(EventReleaseFlag.RELEASED);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.RELEASE_PENDING) != null) {
            eventFlags.setRelease(EventReleaseFlag.RELEASE_PENDING);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.RELEASE_CANCELLED) != null) {
            eventFlags.setRelease(EventReleaseFlag.RELEASE_CANCELLED);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.PLANNED) != null) {
            eventFlags.setRelease(EventReleaseFlag.PLANNED);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.IN_PROGRAMMING) != null) {
            eventFlags.setRelease(EventReleaseFlag.IN_PROGRAMMING);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.CANCELLED) != null) {
            eventFlags.setRelease(EventReleaseFlag.CANCELLED);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.NOT_ACCOMPLISHED) != null) {
            eventFlags.setRelease(EventReleaseFlag.NOT_ACCOMPLISHED);
        } else if (releaseCounter.get(SessionReleaseFlagStatus.RELEASE_FINISHED) != null) {
            eventFlags.setRelease(EventReleaseFlag.RELEASE_FINISHED);
        }
    }

    public static void applyEventChannelFlagStatus(BaseEventChannelDTO baseEventChannelDTO, List<SessionRecord> sessions) {
        applyEventChannelDates(baseEventChannelDTO, sessions);
        ZonedDateTime now = CommonUtils.getZonedDateTime(new Date());

        // Calculamos todos los indicadores de sesion y evento para poder calcular los de canal
        EventFlags eventStatus = getEventStatus(baseEventChannelDTO.getEvent(), sessions);

        // Calculamos los indicadores de publicacion de canalEvento
        if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.PENDING_REQUEST)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.PENDING_RELATIONSHIP);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.PENDING)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.PENDING_RELATIONSHIP);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.REJECTED)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.REJECTED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.PLANNED)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.PLANNED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.IN_PROGRAMMING)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.IN_PROGRAMMING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASE_PENDING)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASE_PENDING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASED) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) >= 0 && baseEventChannelDTO.getSettings().getReleaseDate() != null && baseEventChannelDTO.getSettings().getReleaseDate().compareTo(now) > 0 && baseEventChannelDTO.getSettings().getReleaseEnabled()) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASE_PENDING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASED) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) >= 0 && baseEventChannelDTO.getSettings().getReleaseDate() != null && baseEventChannelDTO.getSettings().getReleaseDate().compareTo(now) <= 0 && baseEventChannelDTO.getSettings().getReleaseEnabled()) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASED) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) >= 0 && !baseEventChannelDTO.getSettings().getReleaseEnabled()) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASE_CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASED) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) < 0) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASE_FINISHED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASE_CANCELLED)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASE_CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.RELEASE_FINISHED)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.RELEASE_FINISHED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.CANCELLED)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && eventStatus.getRelease().equals(EventReleaseFlag.NOT_ACCOMPLISHED)) {
            baseEventChannelDTO.getStatus().setRelease(EventChannelReleaseFlagStatus.NOT_ACCOMPLISHED);
        }

        // Calculamos los indicadores de publicacion de canalEvento
        if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.PENDING_REQUEST) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.PENDING_RELATIONSHIP)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.PENDING_RELATIONSHIP);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.PENDING) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.PENDING_RELATIONSHIP)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.PENDING_RELATIONSHIP);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.REJECTED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.REJECTED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.IN_PROGRAMMING)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.IN_PROGRAMMING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.PLANNED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.PLANNED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASE_PENDING)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_PENDING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED) && eventStatus.getSale().equals(EventSaleFlag.SALE_PENDING)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_PENDING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED) && eventStatus.getSale().equals(EventSaleFlag.SALE) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) >= 0 && baseEventChannelDTO.getSettings().getSaleStartDate() != null && baseEventChannelDTO.getSettings().getSaleStartDate().compareTo(now) > 0 && baseEventChannelDTO.getSettings().getSaleEnabled()) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_PENDING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED) && eventStatus.getSale().equals(EventSaleFlag.SALE) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) >= 0 && baseEventChannelDTO.getSettings().getSaleStartDate() != null && baseEventChannelDTO.getSettings().getSaleStartDate().compareTo(now) <= 0 && baseEventChannelDTO.getSettings().getSaleEnabled()) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED)
                && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED)
                && eventStatus.getSale().equals(EventSaleFlag.SALE)
                && !baseEventChannelDTO.getSettings().getSaleEnabled()
                && BooleanUtils.isTrue(baseEventChannelDTO.getSettings().getSecondaryMarketEnabled())
                && baseEventChannelDTO.getSettings().getSecondaryMarketStartDate() != null
                && baseEventChannelDTO.getSettings().getSecondaryMarketStartDate().compareTo(now) > 0
        ) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_PENDING);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED)
                && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED)
                && eventStatus.getSale().equals(EventSaleFlag.SALE)
                && !baseEventChannelDTO.getSettings().getSaleEnabled()
                && BooleanUtils.isTrue(baseEventChannelDTO.getSettings().getSecondaryMarketEnabled())
                && baseEventChannelDTO.getSettings().getSecondaryMarketStartDate() != null
                && baseEventChannelDTO.getSettings().getSecondaryMarketStartDate().compareTo(now) < 0
                && baseEventChannelDTO.getSettings().getSecondaryMarketEndDate() != null
                && baseEventChannelDTO.getSettings().getSecondaryMarketEndDate().compareTo(now) >= 0
        ) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_ONLY_SECONDARY_MARKET);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED) && eventStatus.getSale().equals(EventSaleFlag.SALE) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) >= 0 && !baseEventChannelDTO.getSettings().getSaleEnabled()) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED) && eventStatus.getSale().equals(EventSaleFlag.SALE) && baseEventChannelDTO.getSettings().getSaleEndDate() != null && baseEventChannelDTO.getSettings().getSaleEndDate().compareTo(now) < 0) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.RELEASE_FINISHED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASED) && eventStatus.getSale().equals(EventSaleFlag.SALE_CANCELLED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.SALE_CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASE_CANCELLED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.RELEASE_CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.RELEASE_FINISHED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.RELEASE_FINISHED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.CANCELLED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.CANCELLED);
        } else if (baseEventChannelDTO.getStatus().getRequest().equals(EventChannelStatus.ACCEPTED) && baseEventChannelDTO.getStatus().getRelease().equals(EventChannelReleaseFlagStatus.NOT_ACCOMPLISHED)) {
            baseEventChannelDTO.getStatus().setSale(EventChannelSaleFlagStatus.NOT_ACCOMPLISHED);
        }
    }

    public static void applySessionFlagStatus(SessionDTO sessionDTO, SessionRecord sessionRecord) {
        if (sessionRecord.getEventStatus() != null
                && sessionRecord.getEstado() != null
                && sessionRecord.getEnventa() != null
                && sessionRecord.getFechaventa() != null
                && sessionRecord.getFechafinsesion() != null
                && sessionRecord.getPublicado() != null
                && sessionRecord.getFechapublicacion() != null) {
            EventStatus eventStatus = EventStatus.byId(sessionRecord.getEventStatus());

            SessionFlags sessionFlags = calcularEstadoSesion(sessionRecord, eventStatus);

            sessionDTO.setRelease(sessionFlags.getRelease());
            sessionDTO.setSale(sessionFlags.getSale());
        }
    }

    private static void applyEventChannelDates(BaseEventChannelDTO
                                                       baseEventChannelDTO, List<SessionRecord> sessions) {
        if (EventStatus.DELETED.equals(baseEventChannelDTO.getEvent().getStatus())) {
            return;
        }
        if (baseEventChannelDTO.getSettings().getUseEventDates()) {
            sessions.stream()
                    .filter(sessionRecord -> !sessionRecord.getEstado().equals(SessionStatus.DELETED.getId()))
                    .forEach(sessionRecord -> {
                        baseEventChannelDTO.getSettings().setReleaseDate(applyEventChannelReleaseDate(baseEventChannelDTO, sessionRecord));
                        baseEventChannelDTO.getSettings().setSaleStartDate(applyEventChannelSaleStartDate(baseEventChannelDTO, sessionRecord));
                        baseEventChannelDTO.getSettings().setSaleEndDate(applyEventChannelSaleEndDate(baseEventChannelDTO, sessionRecord));
                        baseEventChannelDTO.getSettings().setBookingStartDate(applyEventChannelBookingStartDate(baseEventChannelDTO.getSettings().getBookingStartDate(), sessionRecord));
                        baseEventChannelDTO.getSettings().setBookingEndDate(applyEventChannelBookingEndDate(baseEventChannelDTO.getSettings().getBookingEndDate(), sessionRecord));
                    });
        }
        //Dirty hack to allow OB Portal bookings by default
        if (!baseEventChannelDTO.getSettings().getUseEventDates() && baseEventChannelDTO.getChannel().getType().equals(ChannelSubtype.PORTAL_WEB)) {
            sessions.stream()
                    .filter(sessionRecord -> !sessionRecord.getEstado().equals(SessionStatus.DELETED.getId()))
                    .forEach(sessionRecord -> {
                        baseEventChannelDTO.getSettings().setBookingStartDate(applyEventChannelBookingStartDate(baseEventChannelDTO.getSettings().getBookingStartDate(), sessionRecord));
                        baseEventChannelDTO.getSettings().setBookingEndDate(applyEventChannelBookingEndDate(baseEventChannelDTO.getSettings().getBookingEndDate(), sessionRecord));
                    });
        }
    }

    private static ZonedDateTime applyEventChannelReleaseDate(BaseEventChannelDTO
                                                                      baseEventChannelDTO, SessionRecord sessionRecord) {
        if (baseEventChannelDTO.getSettings().getReleaseDate() == null || baseEventChannelDTO.getSettings().getReleaseDate().isAfter(CommonUtils.getZonedDateTime(sessionRecord.getFechapublicacion()))) {
            return CommonUtils.getZonedDateTime(sessionRecord.getFechapublicacion());
        }
        return baseEventChannelDTO.getSettings().getReleaseDate();
    }

    private static ZonedDateTime applyEventChannelSaleStartDate(BaseEventChannelDTO
                                                                        baseEventChannelDTO, SessionRecord sessionRecord) {
        if (baseEventChannelDTO.getSettings().getSaleStartDate() == null || baseEventChannelDTO.getSettings().getSaleStartDate().isAfter(CommonUtils.getZonedDateTime(sessionRecord.getFechaventa()))) {
            return CommonUtils.getZonedDateTime(sessionRecord.getFechaventa());
        }
        return baseEventChannelDTO.getSettings().getSaleStartDate();
    }

    private static ZonedDateTime applyEventChannelSaleEndDate(BaseEventChannelDTO
                                                                      baseEventChannelDTO, SessionRecord sessionRecord) {
        if (baseEventChannelDTO.getSettings().getSaleEndDate() == null || baseEventChannelDTO.getSettings().getSaleEndDate().isBefore(CommonUtils.getZonedDateTime(sessionRecord.getFechafinsesion()))) {
            return CommonUtils.getZonedDateTime(sessionRecord.getFechafinsesion());
        }
        return baseEventChannelDTO.getSettings().getSaleEndDate();
    }

    public static ZonedDateTime applyEventChannelBookingStartDate(ZonedDateTime bookingStartDate, SessionRecord
            sessionRecord) {
        if (sessionRecord.getFechainicioreserva() != null &&
                (bookingStartDate == null || bookingStartDate.isAfter(CommonUtils.getZonedDateTime(sessionRecord.getFechainicioreserva())))) {
            return CommonUtils.getZonedDateTime(sessionRecord.getFechainicioreserva());
        }
        return bookingStartDate;
    }

    public static ZonedDateTime applyEventChannelBookingEndDate(ZonedDateTime bookingEndDate, SessionRecord
            sessionRecord) {
        if (sessionRecord.getFechafinreserva() != null &&
                (bookingEndDate == null || bookingEndDate.isBefore(CommonUtils.getZonedDateTime(sessionRecord.getFechafinreserva())))) {
            return CommonUtils.getZonedDateTime(sessionRecord.getFechafinreserva());
        }
        return bookingEndDate;
    }

}
