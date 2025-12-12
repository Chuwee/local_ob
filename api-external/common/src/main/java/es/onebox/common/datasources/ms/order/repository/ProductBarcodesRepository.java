package es.onebox.common.datasources.ms.order.repository;

import es.onebox.common.datasources.ms.order.MsOrderDatasource;
import es.onebox.common.datasources.ms.order.dto.response.barcodes.ProductBarcodesResponse;
import es.onebox.common.datasources.ms.order.request.barcodes.ProductBarcodesSearchRequest;
import org.springframework.stereotype.Repository;

@Repository
public class ProductBarcodesRepository {

    private final MsOrderDatasource msOrderDatasource;

    public ProductBarcodesRepository(MsOrderDatasource msOrderDatasource) {
        this.msOrderDatasource = msOrderDatasource;
    }

    public ProductBarcodesResponse searchBarcodes(ProductBarcodesSearchRequest request) {
        return msOrderDatasource.getProductBarcodes(request);
    }
}
