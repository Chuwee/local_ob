package es.onebox.mgmt.salerequests.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.common.IdNameListWithLimited;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestPromotionsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.converter.SaleRequestPriceTypeConverter;
import es.onebox.mgmt.salerequests.converter.SaleRequestPromotionConverter;
import es.onebox.mgmt.salerequests.converter.SaleRequestsConverter;
import es.onebox.mgmt.salerequests.converter.SearchSaleRequestsFilterConverter;
import es.onebox.mgmt.salerequests.converter.SessionsSaleRequestConverter;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequest;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequestExtended;
import es.onebox.mgmt.salerequests.dto.PriceTypeFilter;
import es.onebox.mgmt.salerequests.dto.PriceTypesDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.dto.SaleRequestPromotionResponseDTO;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestSessionsFilter;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestsFilter;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestsResponse;
import es.onebox.mgmt.salerequests.dto.SessionSaleRequestResponseDTO;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.UpdateSaleRequestResponseDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.users.service.UsersService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static es.onebox.mgmt.salerequests.validation.SaleRequestsValidations.validateSaleRequestId;
import static java.util.Objects.isNull;

@Service
public class SaleRequestService {

    private final SaleRequestsRepository saleRequestsRepository;
    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final UsersService usersService;
    private final EventsRepository eventsRepository;
    @Autowired
    public SaleRequestService (SaleRequestsRepository saleRequestsRepository,
                               SecurityManager securityManager,
                               ChannelsRepository channelsRepository,
                               MasterdataService masterdataService,
                               UsersService usersService,
                               EventsRepository eventsRepository) {
        this.saleRequestsRepository = saleRequestsRepository;
        this.securityManager = securityManager;
        this.channelsRepository = channelsRepository;
        this.masterdataService = masterdataService;
        this.usersService = usersService;
        this.eventsRepository = eventsRepository;
    }

    public SearchSaleRequestsResponse search(final SearchSaleRequestsFilter request) {
        MsSaleRequestsFilter filter = SearchSaleRequestsFilterConverter.convertToMsSaleRequestsFilter(request, masterdataService.getCurrencies());

        MsSaleRequestsResponseDTO result = saleRequestsRepository.searchSaleRequests(filter);
        if (request.getCurrency() != null) {
            result.setData(result.getData().stream()
                    .filter(saleRequest -> {
                        if (saleRequest.getEvent() != null) {
                                Event event = eventsRepository.getEvent(saleRequest.getEvent().getId());
                                return event != null && CurrenciesUtils.getCurrencyId(masterdataService.getCurrencies(), request.getCurrency()).equals(event.getCurrencyId());
                            }
                        return false;
                    })
                    .toList());
        }
        return SaleRequestsConverter.fromMsChannelsResponse(result);
    }

    public SaleRequestDetailDTO getSaleRequestDetail(Long saleRequestId) {
        validateSaleRequestId(saleRequestId);

        MsSaleRequestDTO msResponse = saleRequestsRepository.getSaleRequestDetail(saleRequestId);
        if (isNull(msResponse)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(msResponse.getChannel().getEntity().getId());

        ChannelResponse channelResponse = channelsRepository.getChannel(msResponse.getChannel().getId());
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channelResponse.getLanguages(), languagesByIds);

        return SaleRequestsConverter.fromMsChannelToSaleRequestDetail(msResponse, languages, masterdataService.getCurrencies());
    }

    public SessionSaleRequestResponseDTO getSessions(Long saleRequestId, SearchSaleRequestSessionsFilter request) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        MsSessionSaleRequestResponseDTO msResponse = saleRequestsRepository.getSessions(SecurityUtils.getUserOperatorId(), saleRequestId, request);

        return SessionsSaleRequestConverter.fromMsChannelsResponse(msResponse);
    }

    public SaleRequestPromotionResponseDTO getSaleRequestPromotions(Long saleRequestId) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        MsSaleRequestPromotionsResponseDTO msSaleRequestPromotionsResponse = saleRequestsRepository.getSaleRequestPromotion(saleRequestId);

        return SaleRequestPromotionConverter.fromMsChannelResponse(msSaleRequestPromotionsResponse);
    }

    public UpdateSaleRequestResponseDTO updateSaleRequestStatus (Long saleRequestId, UpdateSaleRequestDTO updateSaleRequestDTO){

        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId, saleRequestsRepository::getSaleRequestDetail,
                securityManager::checkEntityAccessible);
        SaleRequestsValidations.validateUpdatableSaleRequestStatus(updateSaleRequestDTO);

        MsSaleRequestDTO saleRequestDTO = saleRequestsRepository.getSaleRequestDetail(saleRequestId);
        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setIncludeDynamicPriceConfig(true);
        Sessions sessions = eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), saleRequestDTO.getEvent().getId(), filter);
        validateDynamicPricesForSaleRequest(sessions.getData(), saleRequestDTO.getChannel().getId());

        MsUpdateSaleRequestDTO msUpdateSaleRequestDTO = new MsUpdateSaleRequestDTO();
        msUpdateSaleRequestDTO.setStatus(SaleRequestsStatus.toMsChannelEnum(updateSaleRequestDTO.getStatus()));
        msUpdateSaleRequestDTO.setUserId(usersService.getAuthUser().getId().intValue());
        MsUpdateSaleRequestResponseDTO response = saleRequestsRepository.updateSaleRequestStatus(saleRequestId, msUpdateSaleRequestDTO);

        UpdateSaleRequestResponseDTO result = new UpdateSaleRequestResponseDTO();
        result.setStatus(SaleRequestsStatus.fromMsChannelEnum(response.getStatus()));

        return result;
    }

    private void validateDynamicPricesForSaleRequest(List<Session> sessions, Long channelId) {
        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        sessions.forEach(session -> {
                if (BooleanUtils.isFalse(channelConfig.getV4Enabled()) && BooleanUtils.isTrue(session.getUseDynamicPrices())) {
                    throw new OneboxRestException(ApiMgmtErrorCode.DYNAMIC_PRICES_REQUIRE_V4_CHANNEL);
                }
            });
    }

    public IdNameListWithLimited filter(String filterType, FiltersSalesRequest filter) {
        SaleRequestsValidations.validateFilter(filterType);
        FiltersSalesRequestExtended filterExtended = SearchSaleRequestsFilterConverter.convertToFiltersSaleRequest(filter);
        return saleRequestsRepository.filtersSaleRequests(filterType, filterExtended);
    }

    public PriceTypesDTO getPriceTypes(Long saleRequestId, PriceTypeFilter filter) {
        MsSaleRequestDTO saleRequest =  SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessibleWithVisibility);
        Long eventId = saleRequest.getEvent().getId();
        PriceTypes priceTypes = eventsRepository.getPriceTypes(eventId, filter);
        PriceTypesDTO priceTypesDto = new PriceTypesDTO();
        priceTypesDto.setMetadata(priceTypes.getMetadata());
        if (CollectionUtils.isNotEmpty(priceTypes.getData())) {
            SaleRequestPriceTypeConverter.fillPriceTypeData(priceTypesDto, priceTypes.getData());
        }
        return priceTypesDto;
    }


}
