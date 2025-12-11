package es.onebox.mgmt.packsalerequest;

import es.onebox.mgmt.datasources.ms.channel.packsalerequests.PackSaleRequestsDatasource;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request.FilterPackSalesRequests;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSaleRequestResponse;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSalesRequestBase;
import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PackSaleRequestRepository {

    private final PackSaleRequestsDatasource packSaleRequestsDatasource;

    @Autowired
    public PackSaleRequestRepository(PackSaleRequestsDatasource packSaleRequestsDatasource) {
        this.packSaleRequestsDatasource = packSaleRequestsDatasource;
    }

    public PackSaleRequestResponse search(FilterPackSalesRequests filter) {
        return packSaleRequestsDatasource.search(filter);
    }

    public PackSalesRequestBase getDetail(Long saleRequestId) {
        return packSaleRequestsDatasource.getDetail(saleRequestId);
    }

    public void updateStatus(Long saleRequestId, PackSaleRequestStatus status) {
        packSaleRequestsDatasource.updateStatus(saleRequestId, status);
    }
}
