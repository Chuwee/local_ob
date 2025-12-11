package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.dal.dto.couch.enums.OrderType;
import es.onebox.dal.dto.couch.enums.SeasonProductReleaseStatus;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.dal.dto.couch.order.OrderSeasonSessionDTO;
import es.onebox.mgmt.datasources.ms.client.dto.CustomerSearch;
import es.onebox.mgmt.datasources.ms.client.dto.CustomerSearchFilter;
import es.onebox.mgmt.datasources.ms.client.dto.CustomersSearch;
import es.onebox.mgmt.datasources.ms.client.repositories.CustomersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketReleaseSeat;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.repository.OrderProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketReleaseSeatConverter;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatConfigDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesDTO;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleasesFilterDTO;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionDTO;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsResponse;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import es.onebox.mgmt.seasontickets.enums.ReleaseStatus;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketAssignationStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeasonTicketReleaseSeatService {

    private final SeasonTicketRepository seasonTicketRepository;
    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketSessionsService seasonTicketSessionsService;
    private final OrderProductsRepository productsRepository;
    private final CustomersRepository customersRepository;

    @Autowired
    public SeasonTicketReleaseSeatService(SeasonTicketRepository seasonTicketRepository,
                                          SeasonTicketService seasonTicketService,
                                          SeasonTicketSessionsService seasonTicketSessionsService,
                                          OrderProductsRepository productsRepository,
                                          CustomersRepository customersRepository) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketSessionsService = seasonTicketSessionsService;
        this.productsRepository = productsRepository;
        this.customersRepository = customersRepository;
    }

    public SeasonTicketReleaseSeatConfigDTO getSeasonTicketReleaseSeat(Long seasonTicketId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketReleaseSeat seasonTicketReleaseSeat = seasonTicketRepository.getSeasonTicketReleaseSeat(seasonTicketId);
        return SeasonTicketReleaseSeatConverter.toDto(seasonTicketReleaseSeat);
    }

    public void updateSeasonTicketReleaseSeat(Long seasonTicketId, SeasonTicketReleaseSeatConfigDTO dto) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        
        SeasonTicketReleaseSeat seasonTicketReleaseSeat = seasonTicketRepository.getSeasonTicketReleaseSeat(seasonTicketId);
        SeasonTicketReleaseSeatConfigDTO currentDTO = SeasonTicketReleaseSeatConverter.toDto(seasonTicketReleaseSeat);
        
        SeasonTicketReleaseSeatConfigDTO mergedDTO = mergeWithCurrent(dto, currentDTO);
        
        Set<Long> assignedSessionsIds = null;
        if (CollectionUtils.isNotEmpty(mergedDTO.getExcludedSessions())) {
            assignedSessionsIds =  getSeasonTicketAssignedSessions(seasonTicketId).getData().stream()
                    .map(SeasonTicketSessionDTO::getSessionId).collect(Collectors.toSet());
        }
        
        seasonTicketReleaseSeat = seasonTicketReleaseSeat == null ? new SeasonTicketReleaseSeat() : seasonTicketReleaseSeat;
        SeasonTicketReleaseSeatValidationService.validateReleaseSeatUpdate(mergedDTO, assignedSessionsIds, seasonTicketReleaseSeat);
        SeasonTicketReleaseSeatConverter.updateReleaseSeat(seasonTicketReleaseSeat, mergedDTO);
        seasonTicketRepository.updateSeasonTicketReleaseSeat(seasonTicketId, seasonTicketReleaseSeat);
    }

    private SeasonTicketReleaseSeatConfigDTO mergeWithCurrent(SeasonTicketReleaseSeatConfigDTO request, SeasonTicketReleaseSeatConfigDTO current) {
        SeasonTicketReleaseSeatConfigDTO merged = new SeasonTicketReleaseSeatConfigDTO();
        
        merged.setCustomerPercentage(request.getCustomerPercentage() != null ? request.getCustomerPercentage() : current.getCustomerPercentage());
        merged.setExcludedSessions(request.getExcludedSessions() != null ? request.getExcludedSessions() : current.getExcludedSessions());
        merged.setMaxReleases(request.getMaxReleases() != null ? request.getMaxReleases() : current.getMaxReleases());
        merged.setMaxReleasesEnabled(request.getMaxReleasesEnabled() != null ? request.getMaxReleasesEnabled() : current.getMaxReleasesEnabled());
        merged.setEarningsLimit(request.getEarningsLimit() != null ? request.getEarningsLimit() : current.getEarningsLimit());
        
        if (request.getEnableReleaseDelay() != null) {
            merged.setEnableReleaseDelay(request.getEnableReleaseDelay());
            if (Boolean.FALSE.equals(request.getEnableReleaseDelay())) {
                merged.setReleaseSeatMinDelayTime(null);
                merged.setReleaseSeatMaxDelayTime(null);
            } else {
                merged.setReleaseSeatMinDelayTime(request.getReleaseSeatMinDelayTime());
                merged.setReleaseSeatMaxDelayTime(request.getReleaseSeatMaxDelayTime());
            }
        } else {
            merged.setEnableReleaseDelay(current.getEnableReleaseDelay());
            merged.setReleaseSeatMinDelayTime(request.getReleaseSeatMinDelayTime() != null ? request.getReleaseSeatMinDelayTime() : current.getReleaseSeatMinDelayTime());
            merged.setReleaseSeatMaxDelayTime(request.getReleaseSeatMaxDelayTime() != null ? request.getReleaseSeatMaxDelayTime() : current.getReleaseSeatMaxDelayTime());
        }
        
        if (request.getEnableRecoverDelay() != null) {
            merged.setEnableRecoverDelay(request.getEnableRecoverDelay());
            if (Boolean.FALSE.equals(request.getEnableRecoverDelay())) {
                merged.setRecoverReleasedSeatMaxDelayTime(null);
            } else {
                merged.setRecoverReleasedSeatMaxDelayTime(request.getRecoverReleasedSeatMaxDelayTime());
            }
        } else {
            merged.setEnableRecoverDelay(current.getEnableRecoverDelay());
            merged.setRecoverReleasedSeatMaxDelayTime(request.getRecoverReleasedSeatMaxDelayTime() != null ? request.getRecoverReleasedSeatMaxDelayTime() : current.getRecoverReleasedSeatMaxDelayTime());
        }
        
        return merged;
    }

    public SeasonTicketReleasesDTO searchSeasonTicketReleases(Long seasonTicketId, SeasonTicketReleasesFilterDTO filter) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (filter.getSessionId() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Session id is mandatory", null);
        }
        List<SeasonTicketSessionDTO> sessions = getSeasonTicketAssignedSession(seasonTicketId, filter.getSessionId()).getData().stream().toList();
        if (CollectionUtils.isEmpty(sessions) || sessions.size() != 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.SESSIONS_NOT_FOUND_OR_INVALID_SESSION_STATE);
        }

        ProductSearchResponse response = productsRepository.searchProducts(SeasonTicketReleaseSeatConverter.toFilter(seasonTicketId, filter));
        if (CollectionUtils.isEmpty(response.getData())) {
            return new SeasonTicketReleasesDTO();
        }

        CustomerSearchFilter customerSearchFilter = new CustomerSearchFilter();
        customerSearchFilter.setCustomerIds(response.getData().stream()
                .map(p -> Optional.ofNullable(p.getUserId()).orElse(p.getAdditionalData().getCustomer().getUserId())).toList());

        CustomersSearch customers = customersRepository.findCustomers(seasonTicket.getEntityId(), customerSearchFilter);
        Map<String, CustomerSearch> customersById = customers.getData().stream().collect(Collectors.toMap(CustomerSearch::getId, Function.identity()));

        Map<Long, String> orderCodesByProductId = getOrderCodesBySoldSessionProductId(response);

        return SeasonTicketReleaseSeatConverter.toDTO(filter, response, customersById, sessions.get(0), orderCodesByProductId);
    }

    private SeasonTicketSessionsResponse getSeasonTicketAssignedSessions(Long seasonTicketId) {
        SeasonTicketSessionsSearchFilter seasonTicketSessionsSearchFilter = new SeasonTicketSessionsSearchFilter();
        seasonTicketSessionsSearchFilter.setAssignationStatus(SeasonTicketAssignationStatus.ASSIGNED);
        seasonTicketSessionsSearchFilter.setLimit(1000L);
        return seasonTicketSessionsService.getSessions(seasonTicketSessionsSearchFilter, seasonTicketId);
    }

    private SeasonTicketSessionsResponse getSeasonTicketAssignedSession(Long seasonTicketId, Long sessionId) {
        SeasonTicketSessionsSearchFilter seasonTicketSessionsSearchFilter = new SeasonTicketSessionsSearchFilter();
        seasonTicketSessionsSearchFilter.setAssignationStatus(SeasonTicketAssignationStatus.ASSIGNED);
        seasonTicketSessionsSearchFilter.setSessionId(sessionId);
        seasonTicketSessionsSearchFilter.setLimit(1000L);
        return seasonTicketSessionsService.getSessions(seasonTicketSessionsSearchFilter, seasonTicketId);
    }

    private Map<Long, String> getOrderCodesBySoldSessionProductId(ProductSearchResponse response) {
        List<Long> soldSessionProductIds = extractSoldSessionProductIds(response);
        if (CollectionUtils.isEmpty(soldSessionProductIds)) {
            return Collections.emptyMap();
        }

        List<OrderProductDTO> soldProducts = fetchSoldProducts(soldSessionProductIds);
        if (CollectionUtils.isEmpty(soldProducts)) {
            return Collections.emptyMap();
        }

        return mapOrderCodesByProductId(soldProducts);
    }

    private List<Long> extractSoldSessionProductIds(ProductSearchResponse response) {
        return response.getData().stream()
                .filter(p -> p.getSeasonData() != null && CollectionUtils.isNotEmpty(p.getSeasonData().getSessionProducts()))
                .flatMap(p -> p.getSeasonData().getSessionProducts().stream())
                .filter(sp -> sp.getRelease() != null
                        && sp.getRelease().getStatus() != null
                        && SeasonProductReleaseStatus.SOLD.equals(sp.getRelease().getStatus()))
                .map(OrderSeasonSessionDTO::getId)
                .toList();
    }

    private List<OrderProductDTO> fetchSoldProducts(List<Long> soldSessionProductIds) {
        ProductSearchRequest request = buildProductSearchRequest(soldSessionProductIds);
        ProductSearchResponse productSearchResponse = productsRepository.searchProducts(request);
        return productSearchResponse != null ? productSearchResponse.getData() : Collections.emptyList();
    }

    private ProductSearchRequest buildProductSearchRequest(List<Long> productIds) {
        ProductSearchRequest request = new ProductSearchRequest();
        request.setIds(productIds);
        request.setOrderTypes(List.of(OrderType.PURCHASE, OrderType.SEAT_REALLOCATION));
        request.setProductRefunded(false);
        request.setProductReallocated(false);
        return request;
    }

    private Map<Long, String> mapOrderCodesByProductId(List<OrderProductDTO> soldProducts) {
        return soldProducts.stream()
                .collect(Collectors.toMap(
                        OrderProductDTO::getId,
                        productDTO -> StringUtils.defaultIfEmpty(
                                productDTO.getAdditionalData() != null ? productDTO.getAdditionalData().getCode() : "",
                                ""
                        )
                ));
    }
}