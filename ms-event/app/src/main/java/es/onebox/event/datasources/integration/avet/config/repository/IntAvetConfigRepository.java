package es.onebox.event.datasources.integration.avet.config.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.event.datasources.integration.avet.config.IntAvetConfigDatasource;
import es.onebox.event.datasources.integration.avet.config.dto.AvetPrice;
import es.onebox.event.datasources.integration.avet.config.dto.ClubConfig;
import es.onebox.event.datasources.integration.avet.config.dto.Competition;
import es.onebox.event.datasources.integration.avet.config.dto.SessionMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class IntAvetConfigRepository {

    private final IntAvetConfigDatasource intAvetConfigDatasource;

    @Autowired
    public IntAvetConfigRepository(IntAvetConfigDatasource intAvetConfigDatasource) {
        this.intAvetConfigDatasource = intAvetConfigDatasource;
    }

    @Cached(key = "intAvetConfigRepository.sessionMatch", expires = 3, timeUnit = TimeUnit.MINUTES)
    public SessionMatch getSessionMatch(@CachedArg Long sessionId) {
        return intAvetConfigDatasource.getSessionMatch(sessionId);
    }

    @Cached(key = "intAvetConfigRepository.getCompetition", timeUnit = TimeUnit.MINUTES)
    public Competition getCompetition(@CachedArg Long competitionId) {
        return intAvetConfigDatasource.getCompetition(competitionId);
    }

    @Cached(key = "intAvetConfigRepository.avetPrices", expires = 5, timeUnit = TimeUnit.MINUTES)
    public List<AvetPrice> getAvetPrices(@CachedArg Integer eventId) {
        return intAvetConfigDatasource.getAvetPrices(eventId);
    }

    @Cached(key = "intAvetConfigRepository.clubConfig", expires = 3, timeUnit = TimeUnit.MINUTES)
    public ClubConfig getClubConfig(@CachedArg Integer entityId) {
        return intAvetConfigDatasource.getClubConfig(entityId);
    }
}
