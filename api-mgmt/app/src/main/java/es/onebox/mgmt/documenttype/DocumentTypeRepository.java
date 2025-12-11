package es.onebox.mgmt.documenttype;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class DocumentTypeRepository {

    private static final int CACHE_DOCUMENT_TYPE_TTL = 2;
    private static final String DOCUMENT_TYPES_KEY = "document.types.cache.key";

    private final MsEntityDatasource entityDatasource;

    @Autowired
    public DocumentTypeRepository(MsEntityDatasource entityDatasource){
        this.entityDatasource = entityDatasource;
    }

    @Cached(key = DOCUMENT_TYPES_KEY, expires = CACHE_DOCUMENT_TYPE_TTL, timeUnit = TimeUnit.HOURS)
    public List<IdNameDTO> getDocumentTypesByOperatorId(@CachedArg Long operatorId) {
        return entityDatasource.getDocumentTypesByOperatorId(operatorId);
    }

}
