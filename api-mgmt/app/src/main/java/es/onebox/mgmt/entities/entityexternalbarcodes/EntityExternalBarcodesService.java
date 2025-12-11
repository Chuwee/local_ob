package es.onebox.mgmt.entities.entityexternalbarcodes;

import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityExternalBarcodesService {

    private final EntityExternalBarcodesRepository entityExternalBarcodesRepository;

    @Autowired
    public EntityExternalBarcodesService(EntityExternalBarcodesRepository entityExternalBarcodesRepository){
        this.entityExternalBarcodesRepository = entityExternalBarcodesRepository;
    }


    public ExternalBarcodeConfig getEntityExternalBarcodeConfig(Long entityId){
        return entityExternalBarcodesRepository.getEntityExternalBarcodeConfig(entityId);
    }
}
