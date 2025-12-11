package es.onebox.mgmt.datasources.ms.order.repository;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.dal.dto.couch.enums.ProductType;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.mgmt.common.cache.enums.OrdersCachedMappingsType;
import es.onebox.mgmt.common.cache.repository.orders.OrdersCachedRepository;
import es.onebox.mgmt.datasources.ms.order.MsOrderDatasource;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchRequest;
import es.onebox.mgmt.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.mgmt.datasources.ms.order.dto.SearchOrderRequest;
import es.onebox.mgmt.datasources.ms.order.dto.SearchOrderResponse;
import es.onebox.mgmt.datasources.ms.order.dto.SeasonTicketReleasesExportRequest;
import es.onebox.mgmt.export.enums.ExportType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrdersRepository {

    private final MsOrderDatasource msOrderDatasource;
    private final OrdersCachedRepository ordersCachedRepository;

    @Autowired
    public OrdersRepository(MsOrderDatasource msOrderDatasource, OrdersCachedRepository ordersCachedRepository) {
        this.msOrderDatasource = msOrderDatasource;
        this.ordersCachedRepository = ordersCachedRepository;
    }

    public boolean eventHasOrders(Long eventId) {
        if (ordersCachedRepository.contains(eventId, OrdersCachedMappingsType.EVENTS_WITH_SALES)) {
            return true;
        }

        SearchOrderRequest request = new SearchOrderRequest();
        request.setEventId(eventId);
        request.setLimit(0L);
        SearchOrderResponse response = msOrderDatasource.searchOrders(request);
        Long eventSales = response.getMetadata().getTotal();

        if (eventSales > 0) {
            ordersCachedRepository.create(eventId, eventSales, OrdersCachedMappingsType.EVENTS_WITH_SALES);
            return true;
        }
        return false;
    }

    public boolean sessionHasOrders(Long sessionId) {
        if (ordersCachedRepository.contains(sessionId, OrdersCachedMappingsType.SESSIONS_WITH_SALES)) {
            return true;
        }

        SearchOrderRequest request = new SearchOrderRequest();
        request.setSessionId(sessionId);
        request.setLimit(0L);
        SearchOrderResponse response = msOrderDatasource.searchOrders(request);
        Long sessionSales = response.getMetadata().getTotal();

        if (sessionSales > 0) {
            ordersCachedRepository.create(sessionId, sessionSales, OrdersCachedMappingsType.SESSIONS_WITH_SALES);
            return true;
        }
        return false;
    }

    public boolean productHasOrders(Long productId) {
        if (ordersCachedRepository.contains(productId, OrdersCachedMappingsType.PRODUCTS_WITH_SALES)) {
            return true;
        }

        ProductSearchRequest request = new ProductSearchRequest();
        request.setProductIds(List.of(productId));
        request.setProductTypes(List.of(ProductType.PRODUCT));
        request.setLimit(0L);
        ProductSearchResponse response = msOrderDatasource.searchProducts(request);
        Long productSales = response.getMetadata().getTotal();

        if (productSales > 0) {
            ordersCachedRepository.create(productId, productSales, OrdersCachedMappingsType.PRODUCTS_WITH_SALES);
            return true;
        }
        return false;
    }

    public void invalidateCachedSessionsWithSales(Long id, OrdersCachedMappingsType mappingType) {
        if(ordersCachedRepository.contains(id, mappingType)){
            ordersCachedRepository.delete(id, mappingType);
        }
    }

    public OrderDTO getOrder(String orderCode) {
        return msOrderDatasource.getOrder(orderCode);
    }

    public ExportProcess exportSeasonTicketReleases(SeasonTicketReleasesExportRequest filter) {
        return msOrderDatasource.exportSeasonTicketReleases(filter);
    }

    public ExportProcess getReleaseExportStatus(String exportId, Long userId, ExportType type) {
        return msOrderDatasource.getExportStatus(exportId, userId, type);
    }

}
