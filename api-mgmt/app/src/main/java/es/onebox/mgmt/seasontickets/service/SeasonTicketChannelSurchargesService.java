package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.CommonSurchargeService;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.SaleRequestSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestSurchargesExtendedDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.surcharges.respository.SaleRequestSurchargesRepository;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSurchargeDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelSurchargeListDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class SeasonTicketChannelSurchargesService {
    private final EventChannelsRepository eventChannelsRepository;
    private final CommonSurchargeService commonSurchargeService;
    private final SaleRequestsRepository saleRequestsRepository;
    private final SaleRequestSurchargesRepository saleRequestSurchargesRepository;
    private final SeasonTicketValidationService stValidationService;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SeasonTicketChannelSurchargesService(EventChannelsRepository eventChannelsRepository,
                                                CommonSurchargeService commonSurchargeService,
                                                SaleRequestsRepository saleRequestsRepository,
                                                SaleRequestSurchargesRepository saleRequestSurchargesRepository,
                                                SeasonTicketValidationService stValidationService,
                                                MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.eventChannelsRepository = eventChannelsRepository;
        this.commonSurchargeService = commonSurchargeService;
        this.saleRequestsRepository = saleRequestsRepository;
        this.saleRequestSurchargesRepository = saleRequestSurchargesRepository;
        this.stValidationService = stValidationService;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public List<SeasonTicketChannelSurchargeDTO> getSeasonTicketChannelSurcharges(Long seasonTicketId, Long channelId, List<SurchargeTypeDTO> types) {
        stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);

        List<EventSurcharge> surcharges = eventChannelsRepository.getEventChannelSurcharges(seasonTicketId, channelId, SurchargeConverter.toSurchargeTypes(types));
        if (nonNull(surcharges)) {
            List<Currency> currencies = masterdataService.getCurrencies();
            MsSaleRequestDTO saleRequestDTO = getSaleRequest(seasonTicketId, channelId);
            Currency eventCurrency = saleRequestDTO.getEvent().getCurrencyId() != null
                    ? CurrenciesUtils.getCurrencyByCurrencyId(saleRequestDTO.getEvent().getCurrencyId(), currencies)
                    : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(saleRequestDTO.getEvent().getEntity().getId()));
        return SurchargeConverter.toSeasonTicketChannelSurchargeDTO(surcharges, currencies, eventCurrency);
        }
        return new ArrayList<>();
    }

    public void createSeasonTicketChannelSurcharges(Long seasonTicketId, Long channelId, SeasonTicketChannelSurchargeListDTO surcharges) {
        stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);

        commonSurchargeService.validateSurcharges(surcharges);

        Set<String> requestCurrencies = surcharges.stream().map(SurchargeDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());
        if(requestCurrencies.size()>1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }
        List<Currency> currencies = masterdataService.getCurrencies();
        MsSaleRequestDTO saleRequestDTO = getSaleRequest(seasonTicketId, channelId);
        Currency eventCurrency = saleRequestDTO.getEvent().getCurrencyId() != null
                ? CurrenciesUtils.getCurrencyByCurrencyId(saleRequestDTO.getEvent().getCurrencyId(), currencies)
                : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(saleRequestDTO.getEvent().getEntity().getId()));
        if(requestCurrencies.stream().anyMatch(c -> c != null && !c.equals(eventCurrency.getCode()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<EventSurcharge> requests = surcharges.stream()
                .map(surcharge -> SurchargeConverter.fromDTO(surcharge, currencies, eventCurrency))
                .collect(Collectors.toList());

        eventChannelsRepository.setEventChannelSurcharges(seasonTicketId, channelId, requests);
    }

    public List<SaleRequestSurchargeDTO> getChannelSurcharges(Long seasonTicketId, Long channelId, List<SurchargeTypeDTO> types) {
        stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);

        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(seasonTicketId));
        filter.setStatus(List.of(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(1L);
        MsSaleRequestsResponseDTO searchSaleRequest = saleRequestsRepository.searchSaleRequests(filter);

        if (nonNull(searchSaleRequest)) {
            Optional<MsSaleRequestDTO> optSaleRequestId = searchSaleRequest.getData().stream().findFirst();
            if(optSaleRequestId.isPresent()) {
                Long saleRequestId = optSaleRequestId.get().getId();
                MsSaleRequestSurchargesExtendedDTO surchargesDto = saleRequestSurchargesRepository.saleRequestSurcharges(saleRequestId,
                        SurchargeConverter.toSurchargeTypes(types));
                if (nonNull(surchargesDto) && CollectionUtils.isNotEmpty(surchargesDto.getSurcharges())) {
                    List<Currency> currencies = masterdataService.getCurrencies();
                    Long entityId = optSaleRequestId.get().getEvent().getEntity().getId();
                    Currency eventCurrency = optSaleRequestId.get().getEvent().getCurrencyId() != null
                            ? CurrenciesUtils.getCurrencyByCurrencyId(optSaleRequestId.get().getEvent().getCurrencyId(), currencies)
                            : CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(entityId));
                    return SurchargeConverter.toSaleRequestSurchargeDTO(surchargesDto.getSurcharges(), currencies, eventCurrency);
                }
            }
        }
        return new ArrayList<>();
    }

    private MsSaleRequestDTO getSaleRequest(Long seasonTicketId, Long channelId) {
        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(seasonTicketId));
        filter.setStatus(List.of(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(1L);
        MsSaleRequestsResponseDTO searchSaleRequest = saleRequestsRepository.searchSaleRequests(filter);

        if (nonNull(searchSaleRequest)) {
            Optional<MsSaleRequestDTO> optSaleRequestId = searchSaleRequest.getData().stream().findFirst();
            if(optSaleRequestId.isPresent()) {
                return optSaleRequestId.get();
            }
        }
        throw  new OneboxRestException(ApiMgmtErrorCode.SALE_REQUEST_NOT_FOUND);
    }

}
