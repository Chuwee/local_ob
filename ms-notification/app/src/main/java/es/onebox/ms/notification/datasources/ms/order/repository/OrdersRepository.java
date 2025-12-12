package es.onebox.ms.notification.datasources.ms.order.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.ms.notification.common.dto.MemberOrderDTO;
import es.onebox.ms.notification.datasources.ms.order.MsOrderDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class OrdersRepository {

    private final MsOrderDatasource msOrderDatasource;

    @Autowired
    public OrdersRepository(MsOrderDatasource msOrderDatasource) {
        this.msOrderDatasource = msOrderDatasource;
    }

    @Cached(key = "getOrderByCode", expires = 1, timeUnit = TimeUnit.MINUTES)
    public OrderDTO getOrderByCode(@CachedArg String code) {
        return msOrderDatasource.getOrderByCode(code);
    }

    @Cached(key = "getCouchbaseMemberOrderByCode", expires = 1, timeUnit = TimeUnit.MINUTES)
    public MemberOrderDTO getCouchbaseMemberOrderByCode(@CachedArg String code) {
        return msOrderDatasource.getCouchbaseMemberOrderByCode(code);
    }
}
