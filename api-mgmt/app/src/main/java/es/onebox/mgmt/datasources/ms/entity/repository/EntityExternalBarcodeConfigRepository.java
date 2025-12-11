package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeEntityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntityExternalBarcodeConfigRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityExternalBarcodeConfigRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public ExternalBarcodeEntityConfig getExternalBarcodeEntityConfig(Long entityId) {
        return msEntityDatasource.getExternalBarcodeEntityConfig(entityId);
    }

    public void putExternalBarcodeEntityConfig(Long entityId, ExternalBarcodeEntityConfig externalBarcodeEntityConfig) {
        msEntityDatasource.putExternalBarcodeEntityConfig(entityId, externalBarcodeEntityConfig);
    }

    public List<ExternalBarcodeConfig> getExternalBarcodes() {
        return msEntityDatasource.getExternalBarcodes();
    }

}
