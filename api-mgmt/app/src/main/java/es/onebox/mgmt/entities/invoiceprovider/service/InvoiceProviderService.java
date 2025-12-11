package es.onebox.mgmt.entities.invoiceprovider.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoiceProviderInfo;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInoivcePrefixFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.ProducerRepository;
import es.onebox.mgmt.entities.invoiceprovider.converter.InvoiceProviderConverter;
import es.onebox.mgmt.entities.invoiceprovider.dto.InvoiceProviderInfoDTO;
import es.onebox.mgmt.entities.invoiceprovider.dto.RequestInvoiceProviderDTO;
import es.onebox.mgmt.entities.invoiceprovider.enums.InvoiceProvider;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class InvoiceProviderService {

    private final ProducerRepository producerRepository;
    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;

    @Autowired
    public InvoiceProviderService(ProducerRepository producerRepository, SecurityManager securityManager,
                                  EntitiesRepository entitiesRepository) {
        this.producerRepository = producerRepository;
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public InvoiceProviderInfoDTO getProducerInvoiceProviderInfo(Long producerId) {
        validateProducer(producerId);
        InvoiceProviderInfo info = producerRepository.getProducerInvoiceProviderInfo(producerId);
        return InvoiceProviderConverter.toDTO(info);
    }

    public InvoiceProviderInfoDTO requestInvoiceProvider(Long producerId, RequestInvoiceProviderDTO request) {
        validateRequest(producerId, request);
        InvoiceProviderInfo info = producerRepository.requestInvoiceProvider(producerId, InvoiceProviderConverter.toMs(request));
        return InvoiceProviderConverter.toDTO(info);
    }

    private void validateRequest(Long producerId, RequestInvoiceProviderDTO request) {
        validateProducer(producerId);
        ProducerInoivcePrefixFilter filter = new ProducerInoivcePrefixFilter();
        filter.setDefult(Boolean.TRUE);
        ProducerInvoicePrefix prefixes = entitiesRepository.getProducerInvoicePrefixes(producerId, filter);
        if (CollectionUtils.isEmpty(prefixes.getData())) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCER_MUST_HAVE_A_DEFAULT_INVOICE_PREFIX, null);
        }
        if (request != null && InvoiceProvider.FEVER.equals(request.getProvider())) {
            throw new OneboxRestException(ApiMgmtErrorCode.UNSUPPORTED_OPERATION);
        }
    }

    private void validateProducer(Long producerId) {
        Producer producer = producerRepository.getProducer(producerId);
        securityManager.checkEntityAccessible(producer.getEntity().getId());

        Entity entity = entitiesRepository.getEntity(producer.getEntity().getId());
        if (BooleanUtils.isNotTrue(entity.getAllowExternalInvoiceNotification())) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCER_DOES_NOT_ALLOW_EXTERNAL_INVOICE, null);
        }

    }
}
