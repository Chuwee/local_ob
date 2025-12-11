package es.onebox.mgmt.integrations.barcodes;

import es.onebox.mgmt.datasources.ms.entity.dto.ExternalBarcodeConfig;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityExternalBarcodeConfigRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BarcodesService {

    @Autowired
    private EntityExternalBarcodeConfigRepository entityExternalBarcodeConfigRepository;

    public List<ExternalBarcodeConfigDTO> getBarcodes() {
        List<ExternalBarcodeConfig> externalBarcodes = entityExternalBarcodeConfigRepository.getExternalBarcodes();
        if (CollectionUtils.isNotEmpty(externalBarcodes)) {
            return externalBarcodes.stream()
                    .map(barcodes -> new ExternalBarcodeConfigDTO(barcodes.getId()))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

}
