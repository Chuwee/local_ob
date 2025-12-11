package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.surcharges.CommonSurchargeService;
import es.onebox.mgmt.common.surcharges.converter.SurchargeConverter;
import es.onebox.mgmt.common.surcharges.dto.SeasonTicketSurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SeasonTicketSurchargeListDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.event.SeasonTicketSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeasonTicketSurchargeService {

    private final SeasonTicketRepository seasonTicketRepository;
    private final SeasonTicketService seasonTicketService;
    private final CommonSurchargeService commonSurchargeService;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SeasonTicketSurchargeService(SeasonTicketRepository seasonTicketRepository,
                                        SeasonTicketService seasonTicketService,
                                        CommonSurchargeService commonSurchargeService,
                                        MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.seasonTicketService = seasonTicketService;
        this.commonSurchargeService = commonSurchargeService;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public void setSurcharge(Long seasonTicketId, SeasonTicketSurchargeListDTO seasonTicketSurchargeListDTO) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);

        if (Objects.isNull(seasonTicket.getStatus()) || (seasonTicket.getStatus() != SeasonTicketStatus.SET_UP && seasonTicket.getStatus() != SeasonTicketStatus.PENDING_PUBLICATION)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_SURCHARGE_INVALID_STATUS);
        }

        commonSurchargeService.validateSurcharges(seasonTicketSurchargeListDTO);

        Set<String> requestCurrencies = seasonTicketSurchargeListDTO.stream().map(SurchargeDTO::getRanges)
                .flatMap(Collection::stream).map(RangeDTO::getCurrency).collect(Collectors.toSet());
        if(requestCurrencies.size()>1) {
            throw new OneboxRestException(ApiMgmtErrorCode.MULTI_CURRENCY_NOT_ALLOWED);
        }

        List<Currency> currencies = masterdataService.getCurrencies();
        // TODO check if every provided currency is the same as the currency set for the season ticket instead of its operator defaultCurrency
        Currency seasonTicketCurrency = CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(seasonTicket.getEntityId()));
        if(requestCurrencies.stream().anyMatch(c -> c != null && !c.equals(seasonTicketCurrency.getCode()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_ALLOWED);
        }

        List<EventSurcharge> requests = seasonTicketSurchargeListDTO.stream()
                .map(eventSurcharge -> SurchargeConverter.fromDTO(eventSurcharge, currencies, seasonTicketCurrency))
                .collect(Collectors.toList());

        seasonTicketRepository.setSurcharge(seasonTicketId, requests);
    }

    public List<SeasonTicketSurchargeDTO> getSurcharges(Long seasonTicketId, List<SurchargeTypeDTO> types) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);

        List<SeasonTicketSurcharge> eventRanges = seasonTicketRepository.getSurcharges(seasonTicketId, types);

        List<Currency> currencies = masterdataService.getCurrencies();
        return SurchargeConverter.toSeasonTicketSurchargeDTO(eventRanges, currencies,
                CurrenciesUtils.getDefaultCurrency(entitiesRepository.getCachedOperator(seasonTicket.getEntityId())));
    }

}
