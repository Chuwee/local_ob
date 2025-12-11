package es.onebox.event.catalog.elasticsearch.service;

import es.onebox.event.catalog.dao.couch.smartbooking.SBPriceZone;
import es.onebox.event.catalog.dao.couch.smartbooking.SBPriceZoneSaleMode;
import es.onebox.event.catalog.dao.couch.smartbooking.SBSession;
import es.onebox.event.catalog.elasticsearch.context.BaseIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForOccupationIndexation;
import es.onebox.event.catalog.elasticsearch.context.ChannelSessionPriceZones;
import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.Ticket;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OccupationAppender {

    private OccupationAppender() {
    }

    public static void fillSecondaryMarketConfig(BaseIndexationContext<?, ?> ctx,
                                                 ChannelSessionPriceZones channelSession,
                                                 SessionWithQuotasDTO sessionWithQuotas,
                                                 List<SessionPriceZoneOccupationDTO> sessionPriceZoneOccupations,
                                                 List<Long> updatedPriceZones) {
        SecondaryMarketConfigDTO secondaryConfig = null;
        if (channelSession instanceof ChannelSessionForEventIndexation cs) {
            secondaryConfig = cs.getSecondaryMarketConfig();
        } else if (channelSession instanceof ChannelSessionForOccupationIndexation csfo) {
            secondaryConfig = csfo.getChannelSessionIndexed().getSecondaryMarketConfig();
        }

        if (secondaryConfig != null && secondaryConfig.getDates() != null &&
                Boolean.TRUE.equals(secondaryConfig.getDates().getEnabled()) &&
                CollectionUtils.isNotEmpty(ctx.getSecondaryMarketForSale())) {

            var secondaryMarketSearch = ctx.getSecondaryMarketForSale().stream().filter(sm ->
                            sm.getTicket().getSessionId().equals(sessionWithQuotas.getSessionId())
                                    && (CollectionUtils.isEmpty(sessionWithQuotas.getQuotas()) ||
                                    sessionWithQuotas.getQuotas().contains(sm.getTicket().getQuotaId())))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(secondaryMarketSearch)) {
                secondaryMarketSearch.forEach(secondarySearch -> {
                    Ticket ticket = secondarySearch.getTicket();
                    if (ticket != null && ticket.getPriceZoneId() != null) {
                        Long priceZoneId = ticket.getPriceZoneId();
                        sessionPriceZoneOccupations.stream()
                                .filter(sessionPriceZoneOccupation -> !updatedPriceZones.contains(priceZoneId)
                                        && priceZoneId.equals(sessionPriceZoneOccupation.getPriceZoneId()))
                                .findFirst()
                                .ifPresent(occupation -> {
                                    updatedPriceZones.add(priceZoneId);
                                    Map<TicketStatus, Long> statusMap = occupation.getStatus();
                                    if (statusMap == null) {
                                        statusMap = new HashMap<>();
                                        occupation.setStatus(statusMap);
                                    }
                                    statusMap.merge(TicketStatus.AVAILABLE, 1L, Long::sum);
                                });
                    }
                });
            }
        }
    }


    public static void fillPairSeatInPriceTypes(BaseIndexationContext<?, ?> ctx, Long sessionId,
                                                Integer venueTemplateType,
                                                List<SessionPriceZoneOccupationDTO> priceZoneOccupations) {
        EventType eventType = ctx.getEventType();
        if (EventType.AVET.equals(eventType) && EventUtils.isActivityTemplate(venueTemplateType)) {
            SBSession sbSession = ctx.getSbBySession().get(sessionId);
            if (sbSession != null && MapUtils.isNotEmpty(sbSession.getPriceZonesMapping()) && priceZoneOccupations != null) {
                priceZoneOccupations.forEach(pZonOccupation -> {
                    SBPriceZone sbPriceZone = sbSession.getPriceZonesMapping().get(pZonOccupation.getPriceZoneId());
                    if (sbPriceZone != null && sbPriceZone.getSaleMode() != null) {
                        Map<String, Object> additionalProperties = MapUtils.isNotEmpty(pZonOccupation.getAdditionalProperties())
                                ? pZonOccupation.getAdditionalProperties() : new HashMap<>();
                        additionalProperties.put("pairSeat", SBPriceZoneSaleMode.PAIR_SEAT.equals(sbPriceZone.getSaleMode()));
                        pZonOccupation.setAdditionalProperties(additionalProperties);
                    }
                });
            }
        }
    }
}
