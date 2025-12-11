package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.dal.dto.couch.enums.OrderType;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.dal.dto.couch.order.OrderSeasonSessionDTO;
import es.onebox.dal.dto.couch.order.OrderTicketDataDTO;
import es.onebox.dal.dto.couch.order.SeasonSessionReleaseDTO;
import es.onebox.mgmt.customers.dto.CustomerDTO;
import es.onebox.mgmt.datasources.ms.client.dto.CustomerSearch;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.EarningsLimit;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketReleaseSeat;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.seasontickets.dto.releaseseat.EarningsLimitDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatConfigDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseSessionDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseTicketDataDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesFilterDTO;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionDTO;
import es.onebox.mgmt.seasontickets.enums.ReleaseStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class SeasonTicketReleaseSeatConverter {

    public static SeasonTicketReleaseSeatConfigDTO toDto(SeasonTicketReleaseSeat seasonTicketReleaseSeat) {
        if (seasonTicketReleaseSeat == null) {
            return null;
        }
        SeasonTicketReleaseSeatConfigDTO dto = new SeasonTicketReleaseSeatConfigDTO();
        
        boolean hasReleaseDelay = seasonTicketReleaseSeat.getReleaseMinDelayTime() != null
                || seasonTicketReleaseSeat.getReleaseMaxDelayTime() != null;
        dto.setEnableReleaseDelay(hasReleaseDelay);
        dto.setReleaseSeatMinDelayTime(seasonTicketReleaseSeat.getReleaseMinDelayTime());
        dto.setReleaseSeatMaxDelayTime(seasonTicketReleaseSeat.getReleaseMaxDelayTime());
        
        boolean hasRecoverDelay = seasonTicketReleaseSeat.getRecoverMaxDelayTime() != null;
        dto.setEnableRecoverDelay(hasRecoverDelay);
        dto.setRecoverReleasedSeatMaxDelayTime(seasonTicketReleaseSeat.getRecoverMaxDelayTime());
        
        dto.setCustomerPercentage(seasonTicketReleaseSeat.getCustomerPercentage());
        dto.setExcludedSessions(seasonTicketReleaseSeat.getExcludedSessions());
        dto.setMaxReleases(seasonTicketReleaseSeat.getMaxReleases());
        dto.setMaxReleasesEnabled(seasonTicketReleaseSeat.getMaxReleasesEnabled());
        dto.setEarningsLimit(toEarningsLimitDto(seasonTicketReleaseSeat.getEarningsLimit()));
        return dto;
    }

    public static void updateReleaseSeat(SeasonTicketReleaseSeat seasonTicketReleaseSeat, SeasonTicketReleaseSeatConfigDTO dto) {
            seasonTicketReleaseSeat.setReleaseMinDelayTime(dto.getReleaseSeatMinDelayTime());
        seasonTicketReleaseSeat.setReleaseMaxDelayTime(dto.getReleaseSeatMaxDelayTime());
        seasonTicketReleaseSeat.setRecoverMaxDelayTime(dto.getRecoverReleasedSeatMaxDelayTime());
        seasonTicketReleaseSeat.setCustomerPercentage(dto.getCustomerPercentage());
        seasonTicketReleaseSeat.setExcludedSessions(dto.getExcludedSessions());
        seasonTicketReleaseSeat.setMaxReleases(dto.getMaxReleases());
        seasonTicketReleaseSeat.setMaxReleasesEnabled(dto.getMaxReleasesEnabled());
        seasonTicketReleaseSeat.setEarningsLimit(fromEarningsLimitDto(dto.getEarningsLimit()));
    }

    private static EarningsLimit fromEarningsLimitDto(EarningsLimitDTO earningsLimitDTO) {
        if (earningsLimitDTO == null) {
            return null;
        }
        EarningsLimit earningsLimit = new EarningsLimit();
        earningsLimit.setEnabled(earningsLimitDTO.getEnabled());
        earningsLimit.setPercentage(earningsLimitDTO.getPercentage());
        return earningsLimit;
    }

    private static EarningsLimitDTO toEarningsLimitDto(EarningsLimit earningsLimit) {
        if (earningsLimit == null) {
            return null;
        }
        EarningsLimitDTO earningsLimitDTO = new EarningsLimitDTO();
        earningsLimitDTO.setEnabled(earningsLimit.getEnabled());
        earningsLimitDTO.setPercentage(earningsLimit.getPercentage());
        return earningsLimitDTO;
    }

    public static ProductSearchRequest toFilter(Long seasonTicketId, SeasonTicketReleasesFilterDTO filter) {
        ProductSearchRequest target = new ProductSearchRequest();
        target.setEventIds(List.of(seasonTicketId));
        target.setLimit(filter.getLimit());
        target.setOffset(filter.getOffset());
        target.setOrderTypes(List.of(OrderType.PURCHASE, OrderType.SEAT_REALLOCATION));
        target.setProductRefunded(false);
        target.setProductReallocated(false);
        if (CollectionUtils.isNotEmpty(filter.getReleaseStatus())) {
            target.setReleaseStatus(filter.getReleaseStatus().stream().map(Enum::name).toList());
        }
        if (filter.getSessionId() != null) {
            target.setSeasonTicketSessionIds(List.of(filter.getSessionId()));
        }
        return target;
    }

    public static SeasonTicketReleasesDTO toDTO(SeasonTicketReleasesFilterDTO filter, ProductSearchResponse products,
                                                Map<String, CustomerSearch> customersById, SeasonTicketSessionDTO session,
                                                Map<Long, String> orderCodesByProductId) {
        List<SeasonTicketReleaseDTO> data = products.getData().stream()
                .flatMap(product -> toDTO(product, filter, customersById, session, orderCodesByProductId).stream())
                .toList();
        SeasonTicketReleasesDTO releases = new SeasonTicketReleasesDTO();
        releases.setData(data);
        releases.setMetadata(buildMetadata(products.getMetadata(), filter));
        return releases;
    }

    private static Metadata buildMetadata(Metadata origin, SeasonTicketReleasesFilterDTO filter) {
        Metadata metadata = new Metadata();
        metadata.setLimit(filter.getLimit());
        metadata.setOffset(filter.getOffset());
        metadata.setTotal(origin.getTotal());
        return metadata;
    }

    private static List<SeasonTicketReleaseDTO> toDTO(OrderProductDTO source, SeasonTicketReleasesFilterDTO filter,
                                                      Map<String, CustomerSearch> customersById, SeasonTicketSessionDTO session,
                                                      Map<Long, String> orderCodesByProductId) {
        return source.getSeasonData().getSessionProducts().stream()
                .filter(s -> filter.getSessionId().equals(s.getSessionId()))
                .map(s -> toDTO(s, source, session, customersById, orderCodesByProductId))
                .toList();
    }

    private static SeasonTicketReleaseDTO toDTO(OrderSeasonSessionDTO seasonSession, OrderProductDTO product, SeasonTicketSessionDTO session,
                                                Map<String, CustomerSearch> customersById, Map<Long, String> orderCodesByProductId) {
        SeasonTicketReleaseDTO target = new SeasonTicketReleaseDTO();
        target.setProductId(product.getId());
        target.setTicketData(toDTO(product.getTicketData()));
        target.setSession(toDTO(session));
        String customerId = product.getAdditionalData().getCustomer().getUserId();
        target.setUserId(customerId);
        CustomerSearch customerSearch = customersById.getOrDefault(customerId, null);
        if (customerSearch != null) {
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setName(customerSearch.getName());
            customerDTO.setSurname(customerSearch.getSurname());
            target.setCustomer(customerDTO);
        }
        SeasonSessionReleaseDTO release = seasonSession.getRelease();
        if (release != null) {
            String status = release.getStatus().name();
            target.setStatus(ReleaseStatus.valueOf(status));
            if (ReleaseStatus.RELEASED.name().equals(status) || ReleaseStatus.SOLD.name().equals(status)) {
                target.setReleaseDate(ZonedDateTime.parse(release.getData().get("date").toString()));
                target.setPercentage(Double.parseDouble(release.getData().getOrDefault("percentage", 0.00).toString()));
                if (ReleaseStatus.SOLD.name().equals(status)) {
                    target.setOrderCode(MapUtils.getString(orderCodesByProductId, seasonSession.getId()));
                    target.setPrice(Double.parseDouble(release.getData().getOrDefault("price", 0.00).toString()));
                }
            }
        } else {
            target.setStatus(ReleaseStatus.NOT_RELEASED);
        }
        return target;
    }

    private static SeasonTicketReleaseTicketDataDTO toDTO(OrderTicketDataDTO ticketData) {
        SeasonTicketReleaseTicketDataDTO target = new SeasonTicketReleaseTicketDataDTO();
        target.setSeat(ticketData.getNumSeat());
        target.setRow(ticketData.getRowName());
        target.setSector(ticketData.getSectorName());
        target.setPriceZone(ticketData.getPriceZoneName());
        return target;
    }

    private static SeasonTicketReleaseSessionDTO toDTO(SeasonTicketSessionDTO session) {
        SeasonTicketReleaseSessionDTO target = new SeasonTicketReleaseSessionDTO();
        target.setId(session.getSessionId());
        target.setName(session.getSessionName());
        target.setStartDate(session.getSessionStartingDate());
        return target;
    }

}
