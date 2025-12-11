package es.onebox.mgmt.salerequests.configuration.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestAllowRefundResponse;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSubscriptionListSalesRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.configuration.dto.SaleRequestAllowRefundDTO;
import es.onebox.mgmt.salerequests.dto.CategoryIdRequestDTO;
import es.onebox.mgmt.salerequests.dto.SubscriptionListSalesRequestDTO;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
public class SaleRequestConfigurationService {

    private final SaleRequestsRepository saleRequestsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public SaleRequestConfigurationService(SaleRequestsRepository saleRequestsRepository,
                               SecurityManager securityManager) {
        this.saleRequestsRepository = saleRequestsRepository;
        this.securityManager = securityManager;
    }

    public SaleRequestAllowRefundDTO getAllowRefund(Long saleRequestId) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        SaleRequestAllowRefundResponse msResponse = saleRequestsRepository.getAllowRefund(saleRequestId);

        if (nonNull(msResponse)) {
            return fromMsSaleRequestAllowRefundResponse(msResponse);
        }

        return null;
    }

    public void updateAllowRefund(Long saleRequestId, SaleRequestAllowRefundDTO request) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);

        if (nonNull(request) && nonNull(request.getAllowRefund())) {
            saleRequestsRepository.updateAllowRefund(saleRequestId, toMsSaleRequestAllowRefundResponse(request));
        } else {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

    private SaleRequestAllowRefundDTO fromMsSaleRequestAllowRefundResponse(SaleRequestAllowRefundResponse response) {
        SaleRequestAllowRefundDTO result = new SaleRequestAllowRefundDTO();
        result.setAllowRefund(response.getAllowRefund());
        return result;
    }

    private SaleRequestAllowRefundResponse toMsSaleRequestAllowRefundResponse(SaleRequestAllowRefundDTO request) {
        SaleRequestAllowRefundResponse result = new SaleRequestAllowRefundResponse();
        result.setAllowRefund(request.getAllowRefund());
        return result;
    }

    public void updateEventCategorySaleRequest(Long saleRequestId, CategoryIdRequestDTO categoryId) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);
        saleRequestsRepository.updateEventCategorySaleRequest(saleRequestId, categoryId);
    }

    public void updateSaleRequestSubscriptionList(Long saleRequestId, SubscriptionListSalesRequestDTO subscriptionListSalesRequestDTO) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId, saleRequestsRepository::getSaleRequestDetail,
                securityManager::checkEntityAccessible);
        SaleRequestsValidations.validateUpdatableSaleRequestSubscriptionList(subscriptionListSalesRequestDTO);

        MsSubscriptionListSalesRequestDTO msSubscriptionListSalesRequestDTO = new MsSubscriptionListSalesRequestDTO();
        msSubscriptionListSalesRequestDTO.setEnableSubscriptionList(subscriptionListSalesRequestDTO.getEnable());
        msSubscriptionListSalesRequestDTO.setSubscriptionListId(subscriptionListSalesRequestDTO.getId());

        saleRequestsRepository.updateSaleRequestSubscriptionList(saleRequestId, msSubscriptionListSalesRequestDTO);
    }

}
