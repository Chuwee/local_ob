package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInoivcePrefixFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateProducerInvoicePrefix;
import es.onebox.mgmt.datasources.ms.entity.enums.ProducerStatus;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.converter.InvoicePrefixConverter;
import es.onebox.mgmt.entities.dto.CreateProducerInvoicePrefixRequestDTO;
import es.onebox.mgmt.entities.dto.ProducerInvoicePrefixesDTO;
import es.onebox.mgmt.entities.dto.UpdateProducerInvoicePrefixRequestDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.PRODUCER_NOT_FOUND;

@Service
public class ProducersInvoicePrefixesService {

    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;

    @Autowired
    public ProducersInvoicePrefixesService(final EntitiesRepository entitiesRepository,
                                           final SecurityManager securityManager) {
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public ProducerInvoicePrefixesDTO getProducersInvoicePrefixes(Long producerId, ProducerInoivcePrefixFilter filter) {
        Producer producer = getAndCheckProducer(producerId);

        ProducerInvoicePrefix producerInvoicePrefix = entitiesRepository.getProducerInvoicePrefixes(producerId, filter);
        return InvoicePrefixConverter.fromMsEntity(producerInvoicePrefix, producer);
    }

    public IdDTO createInvoicePrefix(Long producerId, CreateProducerInvoicePrefixRequestDTO createProducerInvoicePrefixRequestDTO) {
        getAndCheckProducer(producerId);
        CreateProducerInvoicePrefix createProducerInvoicePrefix = InvoicePrefixConverter.toMsEntity(createProducerInvoicePrefixRequestDTO);
        return entitiesRepository.createProducerInvoicePrefix(producerId, createProducerInvoicePrefix);
    }

    public void updateInvoicePrefix(Long producerId, Long invoicePrefixId,
                                    UpdateProducerInvoicePrefixRequestDTO updateProducerInvoicePrefixRequestDTO) {
        getAndCheckProducer(producerId);
        UpdateProducerInvoicePrefix updateProducerInvoicePrefix = InvoicePrefixConverter.toMsEntity(updateProducerInvoicePrefixRequestDTO);
        entitiesRepository.updateProducerInvoicePrefix(producerId, invoicePrefixId, updateProducerInvoicePrefix);
    }

    private Producer getAndCheckProducer(Long producerId) {
        Producer producer = entitiesRepository.getProducer(producerId);
        if (producer == null || producer.getStatus().equals(ProducerStatus.DELETED)) {
            throw new OneboxRestException(PRODUCER_NOT_FOUND, "No producer found with id: " + producerId, null);
        }
        securityManager.checkEntityAccessible(producer.getEntity().getId());
        return producer;
    }

}
