package es.onebox.mgmt.salerequests.taxes;

import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsTaxesRepository;
import es.onebox.mgmt.salerequests.taxes.converter.SaleRequestTaxesConverter;
import es.onebox.mgmt.salerequests.taxes.dto.SaleRequestSurchargesTaxesDTO;
import es.onebox.mgmt.salerequests.taxes.dto.SaleRequestsSurchargesTaxesUpdateDTO;
import es.onebox.mgmt.salerequests.validation.SaleRequestsValidations;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleRequestsTaxesService {

    private final SaleRequestsRepository saleRequestsRepository;
    private final SaleRequestsTaxesRepository saleRequestsTaxesRepository;
    private final SecurityManager securityManager;

    @Autowired
    public SaleRequestsTaxesService(SaleRequestsRepository saleRequestsRepository, SaleRequestsTaxesRepository saleRequestsTaxesRepository, SecurityManager securityManager) {
        this.saleRequestsRepository = saleRequestsRepository;
        this.saleRequestsTaxesRepository = saleRequestsTaxesRepository;
        this.securityManager = securityManager;
    }

    public SaleRequestSurchargesTaxesDTO getSaleRequestSurchargesTaxes(Long saleRequestId) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId, saleRequestsRepository::getSaleRequestDetail,
                securityManager::checkEntityAccessible);
        return SaleRequestTaxesConverter.toDTO(saleRequestsTaxesRepository.getSaleRequestSurchargesTaxes(saleRequestId));
    }

    public void updateSaleRequestSurchargesTaxes(Long saleRequestId, SaleRequestsSurchargesTaxesUpdateDTO body) {
        SaleRequestsValidations.validateAnGetSaleRequestAndEntityAccess(saleRequestId, saleRequestsRepository::getSaleRequestDetail,
                securityManager::checkEntityAccessible);
        saleRequestsTaxesRepository.updateSaleRequestSurchargesTaxes(saleRequestId, SaleRequestTaxesConverter.toMS(body));
    }

}
