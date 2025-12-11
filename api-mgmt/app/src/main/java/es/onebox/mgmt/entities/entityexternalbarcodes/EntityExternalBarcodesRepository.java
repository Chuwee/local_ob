package es.onebox.mgmt.entities.entityexternalbarcodes;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EntityExternalBarcodesRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityExternalBarcodesRepository(MsEntityDatasource msEntityDatasource){
        this.msEntityDatasource = msEntityDatasource;
    }

    public ExternalBarcodeConfig getEntityExternalBarcodeConfig(Long entityId) {
        return msEntityDatasource.getEntityExternalBarcodeConfig(entityId);
    }
}
