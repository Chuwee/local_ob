package es.onebox.common.datasources.ms.order.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.order.MsOrderDatasource;
import es.onebox.common.datasources.ms.order.dto.OrderActionResponse;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderGroup;
import es.onebox.common.datasources.ms.order.dto.OrderProductRequest;
import es.onebox.common.datasources.ms.order.dto.OrderSearchResponse;
import es.onebox.common.datasources.ms.order.dto.PreOrderDTO;
import es.onebox.common.datasources.ms.order.dto.VisitorGroupParam;
import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceDTO;
import es.onebox.common.datasources.ms.order.request.InvoiceSearchParam;
import es.onebox.common.datasources.orders.dto.OrderSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MsOrderRepository {

    private final MsOrderDatasource msOrderDatasource;

    @Autowired
    MsOrderRepository (MsOrderDatasource msOrderDatasource) {
        this.msOrderDatasource = msOrderDatasource;
    }

    public OrderActionResponse getOrderAction(String orderCode) {
        return msOrderDatasource.getOrderAction(orderCode);
    }

    public void upsertOrderAction(String orderCode, OrderProductRequest actions) {
        msOrderDatasource.upsertOrderAction(orderCode, actions);
    }

    public OrderDTO getOrderInfo(String code, Integer entityId) {
        return msOrderDatasource.getOrderInfo(code, entityId);
    }

    public PreOrderDTO getPreOrderInfo(String code, Integer entityId) {
        return msOrderDatasource.getPreOrderInfo(code, entityId);
    }

    public PreOrderDTO getPreOrderInfo(String token) {
        return msOrderDatasource.getPreOrderInfo(token);
    }

    public List<OrderGroup> searchGroups(VisitorGroupParam param) {
        return msOrderDatasource.searchGroups(param);
    }

    public OrderGroup getGroup(Long groupId) {
        return msOrderDatasource.getGroup(groupId);
    }

    public OrderDTO getOrderByCode(String code) {
        return msOrderDatasource.getOrderByCode(code);
    }

    @Cached(key = "getOrder", expires = 10 * 60)
    public OrderDTO getOrderByCodeCached(@CachedArg String code) {
        return msOrderDatasource.getOrderByCode(code);
    }

    public List<InvoiceDTO> searchInvoices(InvoiceSearchParam param) {
        return msOrderDatasource.searchInvoices(param);
    }

    public OrderSearchResponse searchOrders(OrderSearchRequest orderSearchRequest) {
        return msOrderDatasource.searchOrders(orderSearchRequest);
    }
}
