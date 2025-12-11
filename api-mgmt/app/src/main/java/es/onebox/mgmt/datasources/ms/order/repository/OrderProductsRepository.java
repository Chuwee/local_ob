package es.onebox.mgmt.datasources.ms.order.repository;

import es.onebox.mgmt.datasources.ms.order.MsOrderDatasource;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class OrderProductsRepository {

    private final MsOrderDatasource msOrderDatasource;

    @Autowired
    public OrderProductsRepository(MsOrderDatasource msOrderDatasource) {
        this.msOrderDatasource = msOrderDatasource;
    }

    public ProductSearchResponse searchProducts(ProductSearchRequest request) {
        return msOrderDatasource.searchProducts(request);
    }
}
