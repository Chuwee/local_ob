package es.onebox.mgmt.packsalerequest;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.request.FilterPackSalesRequests;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSaleRequestResponse;
import es.onebox.mgmt.datasources.ms.channel.packsalerequests.dto.response.PackSalesRequestBase;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.packsalerequest.dto.request.PackSaleRequestUpdateStatusDTO;
import es.onebox.mgmt.packsalerequest.dto.request.PackSaleRequestsSearchFilterDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestBaseResponseDTO;
import es.onebox.mgmt.packsalerequest.dto.response.PackSaleRequestResponseDTO;
import es.onebox.mgmt.packsalerequest.utils.PackSaleRequestConverter;
import es.onebox.mgmt.packsalerequest.utils.PackSaleValidationUtils;
import es.onebox.mgmt.packsalerequest.enums.PackSaleRequestStatus;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackSaleRequestService {

    private final SecurityManager securityManager;
    private final PackSaleRequestRepository packSaleRequestRepository;

    @Autowired
    public PackSaleRequestService(SecurityManager securityManager, PackSaleRequestRepository packSaleRequestRepository) {
        this.securityManager = securityManager;
        this.packSaleRequestRepository = packSaleRequestRepository;
    }

    public PackSaleRequestResponseDTO search(PackSaleRequestsSearchFilterDTO filterDTO) {
        securityManager.checkEntityAccessible(filterDTO);
        FilterPackSalesRequests filterRequest = PackSaleRequestConverter.buildSearchFilter(filterDTO);
        PackSaleRequestResponse response = packSaleRequestRepository.search(filterRequest);
        return PackSaleRequestConverter.convertToPackSaleRequestResponseDTO(response);
    }



    public void updateStatus(Long saleRequestId, PackSaleRequestUpdateStatusDTO updateStatus) {
        PackSalesRequestBase detail = getAndCheckPackSalesRequest(saleRequestId);
        PackSaleValidationUtils.checkValidStatusTransition(detail, updateStatus.getStatus());
        if (PackSaleValidationUtils.isValidProductStatusChange(PackSaleRequestStatus.getById(detail.getState().getId()),updateStatus.getStatus() )) {
            packSaleRequestRepository.updateStatus(saleRequestId, updateStatus.getStatus());
        }
    }

    private PackSalesRequestBase getAndCheckPackSalesRequest(Long saleRequestId) {
        PackSalesRequestBase detail = packSaleRequestRepository.getDetail(saleRequestId);
        if (detail == null) {
            //We can only delete products that does not have associated purchases
            throw new OneboxRestException(ApiMgmtErrorCode.SALE_REQUEST_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(detail.getChannelEntityId().longValue());
        return detail;
    }


    public PackSaleRequestBaseResponseDTO getDetail(Long saleRequestId) {
        PackSalesRequestBase detail = getAndCheckPackSalesRequest(saleRequestId);
        return PackSaleRequestConverter.convertToPackSaleRequestBaseResponseDTO(detail);
    }


}
