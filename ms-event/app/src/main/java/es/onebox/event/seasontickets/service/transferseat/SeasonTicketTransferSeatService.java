package es.onebox.event.seasontickets.service.transferseat;

import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.seasontickets.converter.SeasonTicketTransferConverter;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketTransferConfigCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketTransferConfig;
import es.onebox.event.seasontickets.dto.transferseat.SeasonTicketTransferConfigDTO;
import es.onebox.event.seasontickets.dto.transferseat.SeasonTicketTransferConfigUpdateDTO;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketTransferSeatService {

    private final SeasonTicketTransferConfigCouchDao transferSeatDao;
    private final EntitiesRepository entitiesRepository;
    private final SeasonTicketService seasonTicketService;

    public SeasonTicketTransferSeatService(SeasonTicketTransferConfigCouchDao transferSeatDao, EntitiesRepository entitiesRepository,
                                           SeasonTicketService seasonTicketService) {
        this.transferSeatDao = transferSeatDao;
        this.entitiesRepository = entitiesRepository;
        this.seasonTicketService = seasonTicketService;
    }

    public SeasonTicketTransferConfigDTO getSeasonTicketTransferSeat(Long seasonTicketId) {
        SeasonTicketTransferConfig config = transferSeatDao.getOrDefault(seasonTicketId.toString());
        EventRecord seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);

        return SeasonTicketTransferConverter.toDTO(config, seasonTicket.getIdentidad(), entitiesRepository);
    }

    public void updateSeasonTicketTransferSeat(Long seasonTicketId, SeasonTicketTransferConfigUpdateDTO seasonTicketTransferConfig) {
        SeasonTicketTransferConfig existingConfig = transferSeatDao.getOrDefault(seasonTicketId.toString());

        SeasonTicketTransferConverter.fromDTO(existingConfig, seasonTicketTransferConfig);

        transferSeatDao.upsert(seasonTicketId.toString(), existingConfig);
    }
}
