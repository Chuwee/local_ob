package es.onebox.mgmt.datasources.ms.order.repository;

import es.onebox.mgmt.datasources.ms.order.MsOrderDatasource;
import es.onebox.mgmt.datasources.ms.order.dto.ProductBarcodesResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SessionExternalBarcodeRepository {

    private final MsOrderDatasource msOrderDatasource;

    @Autowired
    public SessionExternalBarcodeRepository(MsOrderDatasource msOrderDatasource) {
        this.msOrderDatasource = msOrderDatasource;
    }

    public ProductBarcodesResponseDTO getExternalBarcodes(Long eventId, Long sessionId, String barcode, Long limit, Long offset) {
        return msOrderDatasource.getExternalBarcodes(eventId, sessionId, barcode, limit, offset);
    }

}
