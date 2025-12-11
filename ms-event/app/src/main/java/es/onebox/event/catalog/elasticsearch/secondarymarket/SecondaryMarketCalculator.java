package es.onebox.event.catalog.elasticsearch.secondarymarket;

import es.onebox.event.catalog.elasticsearch.context.ChannelSessionForEventIndexation;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SectorOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearch;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.Ticket;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// REVIEW!!
public class SecondaryMarketCalculator {


    private SecondaryMarketCalculator() {

    }

    public static List<SecondaryMarketSearch> decoratePriceZoneAvailability(ChannelSessionForEventIndexation channelSessionContext, EventIndexationContext ctx,
                                                      List<Long> priceZonesWithAvailability) {
        SecondaryMarketConfigDTO secondaryConfig = channelSessionContext.getSecondaryMarketConfig();
        if (secondaryConfig != null && secondaryConfig.getDates() != null && Boolean.TRUE.equals(secondaryConfig.getDates().getEnabled()) &&
                CollectionUtils.isNotEmpty(ctx.getSecondaryMarketForSale())) {

            List<SecondaryMarketSearch> skmResponse = ctx.getSecondaryMarketForSale().stream().filter(sm ->
                            sm.getTicket().getSessionId().equals(channelSessionContext.getSessionId())
                                    && (CollectionUtils.isEmpty(channelSessionContext.getQuotas())||
                                    channelSessionContext.getQuotas().contains(sm.getTicket().getQuotaId())))
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(skmResponse)) {
                skmResponse.forEach(secondarySearch -> {
                    Ticket ticket = secondarySearch.getTicket();
                    if (ticket != null && ticket.getPriceZoneId() != null) {
                        Long priceZoneId = ticket.getPriceZoneId();
                        if (!priceZonesWithAvailability.contains(priceZoneId)) {
                            priceZonesWithAvailability.add(priceZoneId);
                        }
                    }
                });
            }
            return skmResponse;
        }
        return Collections.emptyList();
    }

    public static void decorateSecondaryMarketAvailability(List<SecondaryMarketSearch> secondaryMarketItems, List<SessionOccupationVenueContainer> occupation) {
        if (occupation != null && CollectionUtils.isNotEmpty(secondaryMarketItems)) {
            for (SecondaryMarketSearch secondarySearch : secondaryMarketItems) {
                Ticket ticket = secondarySearch.getTicket();
                if (ticket != null) {
                    Long priceZoneId = ticket.getPriceZoneId();
                    Long sectorId = ticket.getSectorId();
                    for (SessionOccupationVenueContainer container : occupation) {
                        updateOccupationStatus(container.getOccupation());
                        updatePriceZoneOccupation(container.getPriceZones(), priceZoneId);
                        updateSectorOccupation(container.getSectors(), sectorId);
                    }
                }
            }
        }
    }

    public static List<SecondaryMarketSearch> getSecondaryMarketItems(List<SecondaryMarketSearch> secondaryMarketSearchList, SecondaryMarketConfigDTO secondaryMarketConfig, List<Long> quotas, Long sessionId) {
        List<SecondaryMarketSearch> secondaryMarketItems = null;
        if (secondaryMarketConfig != null && secondaryMarketConfig.getDates() != null &&
                BooleanUtils.isTrue(secondaryMarketConfig.getDates().getEnabled()) &&
                CollectionUtils.isNotEmpty(secondaryMarketSearchList)) {
            secondaryMarketItems = secondaryMarketSearchList.stream().filter(sm ->
                            sm.getTicket().getSessionId().equals(sessionId)
                                    && (CollectionUtils.isEmpty(quotas) || quotas.contains(sm.getTicket().getQuotaId())))
                    .collect(Collectors.toList());
        }
        return secondaryMarketItems;
    }

    public static void buildSecondaryOccupation(ChannelSessionForEventIndexation channelSessionContext, List<SecondaryMarketSearch> secondaryMarketData) {
        List<SessionOccupationVenueContainer> containerOccupations = channelSessionContext.getContainerOccupations();

        if (containerOccupations != null) {
            containerOccupations.forEach(container -> {
                List<SessionPriceZoneOccupationDTO> priceZones = container.getPriceZones();
                if (priceZones != null) {
                    priceZones.forEach(priceZone -> {
                        Long priceZoneId = priceZone.getPriceZoneId();
                        long availableTicketsInMS = secondaryMarketData.stream()
                                .filter(secondarySearch -> secondarySearch.getTicket() != null)
                                .filter(secondarySearch -> priceZoneId.equals(secondarySearch.getTicket().getPriceZoneId()))
                                .count();

                        if (availableTicketsInMS > 0) {
                            Map<TicketStatus, Long> statusMap = priceZone.getStatus();
                            if (statusMap == null) {
                                statusMap = new HashMap<>();
                                priceZone.setStatus(statusMap);
                            }
                            statusMap.merge(TicketStatus.AVAILABLE, availableTicketsInMS, Long::sum);
                        }
                    });
                }

                List<SectorOccupationDTO> sectors = container.getSectors();
                if (sectors != null) {
                    sectors.forEach(sector -> {
                        Long sectorId = sector.getId();
                        long availableTicketsInMSForSector = secondaryMarketData.stream()
                                .filter(secondarySearch -> secondarySearch.getTicket() != null)
                                .filter(secondarySearch -> sectorId.equals(secondarySearch.getTicket().getSectorId()))
                                .count();

                        if (availableTicketsInMSForSector > 0) {
                            Map<TicketStatus, Long> statusMap = sector.getStatus();
                            if (statusMap == null) {
                                statusMap = new HashMap<>();
                                sector.setStatus(statusMap);
                            }
                            statusMap.merge(TicketStatus.AVAILABLE, availableTicketsInMSForSector, Long::sum);
                        }
                    });
                }

                long totalAvailableTicketsForSession = secondaryMarketData.stream()
                        .filter(secondarySearch -> secondarySearch.getTicket() != null)
                        .filter(secondarySearch -> channelSessionContext.getSessionId().equals(secondarySearch.getTicket().getSessionId()))
                        .count();

                if (totalAvailableTicketsForSession > 0) {
                    SessionOccupationDTO sessionOccupation = container.getOccupation();
                    if (sessionOccupation == null) {
                        sessionOccupation = new SessionOccupationDTO();
                        container.setOccupation(sessionOccupation);
                    }

                    Map<TicketStatus, Long> statusMap = sessionOccupation.getStatus();
                    if (statusMap == null) {
                        statusMap = new HashMap<>();
                        sessionOccupation.setStatus(statusMap);
                    }
                    statusMap.merge(TicketStatus.AVAILABLE, totalAvailableTicketsForSession, Long::sum);
                }
            });
        }
    }

    private static void updateOccupationStatus(SessionOccupationDTO occupation) {
        if (occupation != null) {
            increase(occupation);
        }
    }

    private static void updatePriceZoneOccupation(List<SessionPriceZoneOccupationDTO> priceZones, Long priceZoneId) {
        if (priceZones != null) {
            for (SessionPriceZoneOccupationDTO priceZoneOccupation : priceZones) {
                if (priceZoneOccupation.getPriceZoneId().equals(priceZoneId)) {
                    increase(priceZoneOccupation);
                    break;
                }
            }
        }
    }

    private static void updateSectorOccupation(List<SectorOccupationDTO> sectors, Long sectorId) {
        if (sectors != null) {
            for (SectorOccupationDTO sectorOccupation : sectors) {
                if (sectorOccupation.getId().equals(sectorId)) {
                    increase(sectorOccupation);
                    break;
                }
            }
        }
    }

    private static <T extends SessionOccupationDTO> void increase(T occupation) {
        Map<TicketStatus, Long> statusMap = occupation.getStatus();
        if (statusMap == null) {
            statusMap = new HashMap<>();
            occupation.setStatus(statusMap);
        }
        statusMap.merge(TicketStatus.AVAILABLE, 1L, Long::sum);
    }

    public static boolean calculateSaleChannelEvent(EventIndexationContext ctx, Long channelId) {
        boolean secMktEventChannelForSale = false;
        if (ctx.getEventSecondaryMarketConfig() != null && ctx.getEventSecondaryMarketConfig().getEnabledChannels() != null) {
            secMktEventChannelForSale = ctx.getEventSecondaryMarketConfig().getEnabledChannels()
                    .stream()
                    .anyMatch(enabledChannel ->
                    {
                        if (enabledChannel != null && enabledChannel.getId() != null) {
                            return enabledChannel.getId().equals(channelId);
                        } else {
                            return false;
                        }
                    });
        }
        return secMktEventChannelForSale;
    }
}
