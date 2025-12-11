package es.onebox.mgmt.salerequests.delivery;

import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestDelivery;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.salerequests.delivery.dto.SaleRequestDeliveryDTO;
import es.onebox.mgmt.salerequests.delivery.dto.UpdateSaleRequestDeliveryDTO;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleRequestDeliveryService {


    private final SaleRequestsRepository saleRequestsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public SaleRequestDeliveryService(SaleRequestsRepository saleRequestsRepository, SecurityManager securityManager) {
        this.saleRequestsRepository = saleRequestsRepository;
        this.securityManager = securityManager;
    }


    public SaleRequestDeliveryDTO getDelivery(Long saleRequestId) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);
        SaleRequestDelivery delivery = this.saleRequestsRepository.getDelivery(saleRequestId);
        return SaleRequestDeliveryConverter.toDTO(delivery);
    }

    public void updateDelivery(Long saleRequestId, UpdateSaleRequestDeliveryDTO request) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId,
                saleRequestsRepository::getSaleRequestDetail, securityManager::checkEntityAccessible);
        saleRequestsRepository.updateDelivery(saleRequestId, SaleRequestDeliveryConverter.toEntity(request));
    }

}
