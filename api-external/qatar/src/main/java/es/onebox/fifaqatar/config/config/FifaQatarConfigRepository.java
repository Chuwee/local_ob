package es.onebox.fifaqatar.config.config;

import es.onebox.cache.annotation.Cached;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslation;
import es.onebox.fifaqatar.config.translation.FifaQatarTranslationsCouchDao;
import org.springframework.stereotype.Repository;

@Repository
public class FifaQatarConfigRepository {

    private final FifaQatarConfigCouchDao configCouchDao;

    public FifaQatarConfigRepository(FifaQatarConfigCouchDao configCouchDao) {
        this.configCouchDao = configCouchDao;
    }

    @Cached(key = "qatar_config")
    public FifaQatarConfigDocument getMainConfig() {
        return configCouchDao.get("");
    }

}
