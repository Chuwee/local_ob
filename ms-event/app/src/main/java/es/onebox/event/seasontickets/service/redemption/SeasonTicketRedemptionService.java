package es.onebox.event.seasontickets.service.redemption;

import es.onebox.event.seasontickets.dao.couch.SeasonTicketRedemptionConfigCouchDao;
import es.onebox.event.seasontickets.dto.redemption.SeasonTicketRedemption;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketRedemptionService {

    private final SeasonTicketRedemptionConfigCouchDao redemptionDao;

    public SeasonTicketRedemptionService(SeasonTicketRedemptionConfigCouchDao redemptionDao) {
        this.redemptionDao = redemptionDao;
    }

    public SeasonTicketRedemption getSeasonTicketRedemption(Long seasonTicketId) {
        return redemptionDao.getOrDefault(seasonTicketId.toString());
    }

    public void updateSeasonTicketRedemption(Long seasonTicketId, SeasonTicketRedemption seasonTicketRedemption) {
        SeasonTicketRedemption existingConfig = redemptionDao.getOrDefault(seasonTicketId.toString());
        
        if (seasonTicketRedemption.getEnabled() != null) {
            existingConfig.setEnabled(seasonTicketRedemption.getEnabled());
        }
        
        if (seasonTicketRedemption.getExcludedSessions() != null) {
            existingConfig.setExcludedSessions(seasonTicketRedemption.getExcludedSessions());
        }
        
        if (seasonTicketRedemption.getExpiration() != null) {
            existingConfig.setExpiration(seasonTicketRedemption.getExpiration());
        }
        
        redemptionDao.upsert(seasonTicketId.toString(), existingConfig);
    }
}