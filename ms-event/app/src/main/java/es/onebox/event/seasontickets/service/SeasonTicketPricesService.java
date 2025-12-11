package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.PriceType;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.converter.SeasonTicketPriceConverter;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import es.onebox.event.seasontickets.dao.VenueConfigDao;
import es.onebox.event.seasontickets.dto.SeasonTicketPriceDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.event.seasontickets.dto.UpdateSeasonTicketPriceDTO;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SeasonTicketPricesService {

    public static final String VENUE_NOT_FOUND_ERROR_MESSAGE = "Template not found for this season ticket";

    private final EventPricesDao eventPricesDao;
    private final VenueConfigDao venueConfigDao;
    private final SeasonTicketEventDao seasonTicketEventDao;
    private final SeasonTicketService seasonTicketService;

    @Autowired
    public SeasonTicketPricesService(EventPricesDao eventPricesDao, VenueConfigDao venueConfigDao, SeasonTicketEventDao seasonTicketEventDao, SeasonTicketService seasonTicketService) {
        this.eventPricesDao = eventPricesDao;
        this.venueConfigDao = venueConfigDao;
        this.seasonTicketEventDao = seasonTicketEventDao;
        this.seasonTicketService = seasonTicketService;
    }

    @MySQLRead
    public List<SeasonTicketPriceDTO> getPrices(Long seasonTicketId) {
        checkInputParameters(seasonTicketId);
        checkSeasonTicket(seasonTicketId.intValue());

        return getSeasonTicketPriceDTOS(seasonTicketId);
    }

    private List<SeasonTicketPriceDTO> getSeasonTicketPriceDTOS(Long seasonTicketId) {
        CpanelConfigRecintoRecord venueConfigRecord = venueConfigDao.getVenueConfigBySeasonTicketId(seasonTicketId.intValue());
        if (venueConfigRecord == null) {
            throw OneboxRestException.builder(MsEventErrorCode.INVALID_VENUE_TEMPLATE).
                    setMessage(VENUE_NOT_FOUND_ERROR_MESSAGE).build();
        }

        List<EventPriceRecord> prices = eventPricesDao.getVenueTemplatePrices(venueConfigRecord.getIdconfiguracion());
        if (prices.stream().anyMatch(p -> p.getEventId() == null || !p.getEventId().equals(seasonTicketId.intValue()))) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_NOT_FROM_EVENT);
        }
        return SeasonTicketPriceConverter.fromRecords(prices);
    }

    @MySQLWrite
    public void updatePrices(Long seasonTicketId, List<UpdateSeasonTicketPriceDTO> prices) {
        checkInputParameters(seasonTicketId);
        checkSeasonTicket(seasonTicketId.intValue());
        if (prices.isEmpty()) {
            return;
        }

        SeasonTicketStatusResponseDTO status = seasonTicketService.getStatus(seasonTicketId);
        if (Objects.isNull(status.getStatus()) || (
                status.getStatus() != SeasonTicketStatusDTO.SET_UP &&
                status.getStatus() != SeasonTicketStatusDTO.PENDING_PUBLICATION &&
                status.getStatus() != SeasonTicketStatusDTO.READY
        )) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_EVENT_STATUS);
        }

        List<SeasonTicketPriceDTO> currentPrices = getSeasonTicketPriceDTOS(seasonTicketId);
        Map<Long, PriceType> priceTypes = currentPrices.stream().
                collect(Collectors.toMap(SeasonTicketPriceDTO::getPriceTypeId, SeasonTicketPriceDTO::getPriceType, (p1, p2) -> p1));

        for (UpdateSeasonTicketPriceDTO price : prices) {
            PriceType priceType = EventUtils.checkPrices(priceTypes, price.getPrice(), price.getPriceTypeId());

            int updateRows = 0;
            if (PriceType.INDIVIDUAL.equals(priceType)) {
                updateRows = eventPricesDao.updateIndividual(price.getPriceTypeId().intValue(), price.getRateId(), roundPrice(price.getPrice()), null);
            } else if (PriceType.GROUP.equals(priceType)) {
                updateRows = eventPricesDao.updateGroup(price.getPriceTypeId().intValue(), price.getRateId(), roundPrice(price.getPrice()));
            }
            if (updateRows == 0) {
                throw new OneboxRestException(MsEventErrorCode.NO_PRICES_FOUND,
                        "Price does not exists for price-type: " + price.getPriceTypeId() + " - rate: " + price.getRateId(), null);
            }
        }
    }

    private Double roundPrice(Double price) {
        return NumberUtils.scale(price).doubleValue();
    }

    private void checkInputParameters(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
    }

    private void checkSeasonTicket(Integer seasonTicketId) {
        try {
            CpanelEventoRecord record = seasonTicketEventDao.getById(seasonTicketId);
            if (!EventType.SEASON_TICKET.getId().equals(record.getTipoevento())) {
                throw OneboxRestException.builder(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND).build();
            }
        } catch (EntityNotFoundException ex) {
            throw OneboxRestException.builder(MsEventSeasonTicketErrorCode.SEASON_TICKET_NOT_FOUND).build();
        }
    }
}
