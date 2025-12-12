package es.onebox.fifaqatar.config.translation;

import es.onebox.cache.annotation.Cached;
import es.onebox.fifaqatar.config.config.FifaQatarConfigCouchDao;
import es.onebox.fifaqatar.config.config.FifaQatarConfigDocument;
import org.springframework.stereotype.Repository;

@Repository
public class FifaQatarTranslationRepository {

    private final FifaQatarTranslationsCouchDao translationsCouchDao;

    public FifaQatarTranslationRepository(FifaQatarTranslationsCouchDao translationsCouchDao) {
        this.translationsCouchDao = translationsCouchDao;
    }

    @Cached(key = "qatar_translations")
    public FifaQatarTranslation getTranslations() {
        return translationsCouchDao.get();
    }
}
