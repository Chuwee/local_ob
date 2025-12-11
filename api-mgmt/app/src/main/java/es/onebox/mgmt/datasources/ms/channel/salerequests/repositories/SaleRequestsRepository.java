package es.onebox.mgmt.datasources.ms.channel.salerequests.repositories;

import es.onebox.mgmt.common.IdNameListWithLimited;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestAllowRefundResponse;
import es.onebox.mgmt.datasources.ms.channel.salerequests.MsSaleRequestsDatasource;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestPromotionsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSubscriptionListSalesRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestDelivery;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.UpdateSaleRequestDelivery;
import es.onebox.mgmt.salerequests.dto.CategoryIdRequestDTO;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequestExtended;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestSessionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SaleRequestsRepository {

    private final MsSaleRequestsDatasource msSaleRequestsDatasource;

    @Autowired
    public SaleRequestsRepository(MsSaleRequestsDatasource msSaleRequestsDatasource) {
        this.msSaleRequestsDatasource = msSaleRequestsDatasource;
    }

    public MsSaleRequestsResponseDTO searchSaleRequests(MsSaleRequestsFilter filter) {
        return msSaleRequestsDatasource.searchSaleRequests(filter);
    }

    public MsSaleRequestDTO getSaleRequestDetail(Long saleRequestId) {
        return msSaleRequestsDatasource.getSaleRequestDetail(saleRequestId);
    }

    public MsSessionSaleRequestResponseDTO getSessions(Long userOperatorId, Long saleRequestId, SearchSaleRequestSessionsFilter filter) {
        return msSaleRequestsDatasource.getSessions(userOperatorId, saleRequestId, filter);
    }

    public MsSaleRequestPromotionsResponseDTO getSaleRequestPromotion(Long saleRequestId) {
        return msSaleRequestsDatasource.getSaleRequestPromotions(saleRequestId);
    }

    public IdNameListWithLimited filtersSaleRequests(String filterType, FiltersSalesRequestExtended filter) {
        return msSaleRequestsDatasource.filtersSaleRequests(filterType, filter);
    }

    public SaleRequestAllowRefundResponse getAllowRefund(Long saleRequestId) {
        return msSaleRequestsDatasource.getAllowRefund(saleRequestId);
    }

    public void updateAllowRefund(Long saleRequestId, SaleRequestAllowRefundResponse request) {
        msSaleRequestsDatasource.updateAllowRefund(saleRequestId, request);
    }

    public MsUpdateSaleRequestResponseDTO updateSaleRequestStatus(Long saleRequestId, MsUpdateSaleRequestDTO msUpdateSaleRequestDTO){
        return msSaleRequestsDatasource.updateSaleRequestStatus(saleRequestId, msUpdateSaleRequestDTO);
    }

    public void updateSaleRequestSubscriptionList(Long saleRequestId, MsSubscriptionListSalesRequestDTO msSubscriptionListSalesRequestDTO){
        msSaleRequestsDatasource.updateSaleRequestSubscriptionList(saleRequestId, msSubscriptionListSalesRequestDTO);
    }

    public void updateEventCategorySaleRequest(Long saleRequestId, CategoryIdRequestDTO categoryId) {
        msSaleRequestsDatasource.updateEventCategorySaleRequest(saleRequestId, categoryId);
    }

    public SaleRequestDelivery getDelivery(Long saleRequestId) {
        return msSaleRequestsDatasource.getDelivery(saleRequestId);
    }

    public void updateDelivery(Long saleRequestId, UpdateSaleRequestDelivery request) {
        msSaleRequestsDatasource.updateDelivery(saleRequestId, request);
    }
}
