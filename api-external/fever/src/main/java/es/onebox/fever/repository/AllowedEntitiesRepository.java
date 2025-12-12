package es.onebox.fever.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.webhook.dto.fever.AllowedEntitiesFileData;
import es.onebox.fever.dao.AllowedEntitiesCouchDao;
import org.springframework.stereotype.Repository;

@Repository
public class AllowedEntitiesRepository {

  private final AllowedEntitiesCouchDao allowedEntitiesCouchDao;

  public AllowedEntitiesRepository(AllowedEntitiesCouchDao allowedEntitiesCouchDao) {
    this.allowedEntitiesCouchDao = allowedEntitiesCouchDao;
  }

  @Cached(key = "getAllowedEntities", expires = 10 * 60)
  public AllowedEntitiesFileData getAllowedEntitiesFileData(@CachedArg String fileId) {
    return allowedEntitiesCouchDao.get(fileId);
  }
}
