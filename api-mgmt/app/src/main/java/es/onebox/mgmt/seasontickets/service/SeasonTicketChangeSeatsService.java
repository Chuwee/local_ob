package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceRelation;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceRelations;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketChangeSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRates;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketChangeSeatConverter;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketChangeSeatDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketOperativeDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangeSeatSeasonTicketPriceCompleteRelationDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.ChangeSeatSeasonTicketPriceFilterDTO;
import es.onebox.mgmt.seasontickets.dto.changeseats.UpdateChangeSeatSeasonTicketPriceRelationsDTO;
import es.onebox.mgmt.validation.ChangeSeatValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonTicketChangeSeatsService {

    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final VenuesRepository venuesRepository;
    @Autowired
    public SeasonTicketChangeSeatsService(@Lazy SeasonTicketService seasonTicketService,
                                          SeasonTicketRepository seasonTicketRepository,
                                          VenuesRepository venuesRepository) {
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.venuesRepository = venuesRepository;
    }

    public void setChangeSeatData(SeasonTicket seasonTicketDTO, UpdateSeasonTicketOperativeDTO operative) {
        seasonTicketDTO.setAllowChangeSeat(operative.getAllowChangeSeat());
        if (BooleanUtils.isTrue(seasonTicketDTO.getAllowChangeSeat())) {
            createChangeSeatPricesTable(seasonTicketDTO.getId(), null);
        }
        if (operative.getChangeSeat() != null) {
            UpdateSeasonTicketChangeSeatDTO changeSeatDTO = operative.getChangeSeat();
            SeasonTicketChangeSeat changeSeat = new SeasonTicketChangeSeat();

            changeSeat.setChangeSeatEnabled(changeSeatDTO.getEnable());
            changeSeat.setChangeSeatStartingDate(changeSeatDTO.getStartDate());
            changeSeat.setChangeSeatEndDate(changeSeatDTO.getEndDate());
            changeSeat.setMaxChangeSeatValue(changeSeatDTO.getMaxValue());
            seasonTicketDTO.setChangeSeat(changeSeat);
        }
    }

    public void createChangeSeatPricesTable(Long seasonTicketId, Long rateId) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Venue venue = seasonTicket.getVenues().stream().findFirst().orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND));
        List<PriceType> priceTypes = venuesRepository.getPriceTypes(venue.getConfigId());
        SeasonTicketRates rates =
                rateId != null ? getRate(rateId) : seasonTicketRepository.getSeasonTicketRates(seasonTicketId.intValue());
        if (CollectionUtils.isEmpty(priceTypes) || rates == null || CollectionUtils.isEmpty(rates.getData())) {
            return;
        }
        ChangeSeatSeasonTicketPriceRelations changeSeatSeasonTicketPriceRelations = new ChangeSeatSeasonTicketPriceRelations();
        for (SeasonTicketRate rate : rates.getData()) {
            for (PriceType sourcePriceType : priceTypes) {
                for (PriceType targetPriceType : priceTypes) {
                    ChangeSeatSeasonTicketPriceRelation relation = new ChangeSeatSeasonTicketPriceRelation();
                    relation.setSeasonTicketId(seasonTicketId);
                    relation.setSourcePriceTypeId(sourcePriceType.getId());
                    relation.setTargetPriceTypeId(targetPriceType.getId());
                    relation.setRateId(rate.getId());
                    relation.setValue(0.0);
                    changeSeatSeasonTicketPriceRelations.add(relation);
                }
            }
        }
        seasonTicketRepository.createChangeSeatPricesTable(seasonTicketId, changeSeatSeasonTicketPriceRelations);
    }

    public void handleNewPriceZone(Long seasonTicketId) {
        try {
            createChangeSeatPricesTable(seasonTicketId, null);
        } catch (Exception ignore) {}
    }

    public SeasonTicketChangeSeatDTO getSeasonTicketChangeSeat(Long seasonTicketId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketChangeSeat seasonTicketChangeSeat = seasonTicketRepository.getSeasonTicketChangeSeat(seasonTicketId);
        return SeasonTicketChangeSeatConverter.fromMsEvent(seasonTicketChangeSeat);
    }

    public void updateSeasonTicketChangeSeat(Long seasonTicketId, UpdateSeasonTicketChangeSeatDTO updateSeasonTicketChangeSeat) {
        SeasonTicketChangeSeat seasonTicketChangeSeat = seasonTicketRepository.getSeasonTicketChangeSeat(seasonTicketId);
        if (updateSeasonTicketChangeSeat != null && ChangeSeatValidator.isChangeSeatModified(updateSeasonTicketChangeSeat, seasonTicketChangeSeat)) {
            SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
            List<Long> quotaIds = venuesRepository.getQuotas(seasonTicket.getVenues().get(0).getConfigId()).stream().map(Quota::getId).toList();
            ChangeSeatValidator.validateChangeSeatOnUpdate(updateSeasonTicketChangeSeat, seasonTicketChangeSeat, seasonTicket, quotaIds);
            seasonTicketRepository.updateSeasonTicketChangeSeat(seasonTicketId, SeasonTicketChangeSeatConverter.toMsEvent(updateSeasonTicketChangeSeat));
        }
    }

    public List<ChangeSeatSeasonTicketPriceCompleteRelationDTO> searchChangeSeatPriceRelations(Long seasonTicketId,
                                                                                               ChangeSeatSeasonTicketPriceFilterDTO seasonTicketPriceFilter) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        return seasonTicketRepository.searchChangeSeatPriceRelations(
                    seasonTicketId,
                    SeasonTicketChangeSeatConverter.toFilter(seasonTicketPriceFilter)
                ).stream().map(SeasonTicketChangeSeatConverter::toDTO).collect(Collectors.toList());
    }

    public void updateChangeSeatPriceRelations(Long seasonTicketId, UpdateChangeSeatSeasonTicketPriceRelationsDTO updatePriceRelations) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        seasonTicketRepository.updateChangeSeatPriceRelations(seasonTicketId, updatePriceRelations.stream()
                .map(SeasonTicketChangeSeatConverter::toRelation).collect(Collectors.toList()));
    }

    private SeasonTicketRates getRate(Long rateId) {
        SeasonTicketRates rates = new SeasonTicketRates();
        SeasonTicketRate rate = new SeasonTicketRate();
        rate.setId(rateId);
        rates.setData(List.of(rate));
        return rates;
    }
}
