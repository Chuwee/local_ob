package es.onebox.event.seasontickets.service.releaseseat;

import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeatCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketReleaseSeatService {

    private final SeasonTicketReleaseSeatCouchDao releaseSeatCouchDao;

    @Autowired
    public SeasonTicketReleaseSeatService(SeasonTicketReleaseSeatCouchDao releaseSeatCouchDao) {
        this.releaseSeatCouchDao = releaseSeatCouchDao;
    }

    public SeasonTicketReleaseSeatDTO getSeasonTicketReleaseSeat(Long seasonTicketId) {
        SeasonTicketReleaseSeat document = releaseSeatCouchDao.get(seasonTicketId.toString());
        return SeasonTicketReleaseSeatConverter.toDto(document);
    }

    public void updateSeasonTicketReleaseSeat(Long seasonTicketId, SeasonTicketReleaseSeatDTO dto) {
        SeasonTicketReleaseSeat document = releaseSeatCouchDao.get(seasonTicketId.toString());
        document = document == null ? new SeasonTicketReleaseSeat() : document;
        SeasonTicketReleaseSeatConverter.updateReleaseSeatDocument(document, dto);
        releaseSeatCouchDao.upsert(seasonTicketId.toString(), document);
    }

}
