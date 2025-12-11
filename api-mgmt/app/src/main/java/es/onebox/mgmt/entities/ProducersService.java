package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoiceProviderInfo;
import es.onebox.mgmt.datasources.ms.entity.dto.MasterdataValue;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Producers;
import es.onebox.mgmt.datasources.ms.entity.enums.ProducerStatus;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.ProducerRepository;
import es.onebox.mgmt.entities.converter.ProducerConverter;
import es.onebox.mgmt.entities.dto.CreateProducerRequestDTO;
import es.onebox.mgmt.entities.dto.ProducerContactDTO;
import es.onebox.mgmt.entities.dto.ProducerDTO;
import es.onebox.mgmt.entities.dto.ProducerSearchFilter;
import es.onebox.mgmt.entities.dto.SearchProducersResponse;
import es.onebox.mgmt.entities.invoiceprovider.enums.RequestStatus;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.PRODUCER_NOT_FOUND;

@Service
public class ProducersService {

    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final ProducerRepository producerRepository;

    @Autowired
    public ProducersService(EntitiesRepository entitiesRepository, ProducerRepository producerRepository,
                            SecurityManager securityManager, MasterdataService masterdataService) {
        this.entitiesRepository = entitiesRepository;
        this.producerRepository = producerRepository;
        this.securityManager = securityManager;
        this.masterdataService = masterdataService;
    }

    public SearchProducersResponse getProducers(ProducerSearchFilter filter) {
        securityManager.checkEntityAccessible(filter);

        ProducerFilter producerFilter = ProducerConverter.toMs(SecurityUtils.getUserOperatorId(), filter);
        Producers producers = entitiesRepository.getProducers(producerFilter);

        SearchProducersResponse response = new SearchProducersResponse();
        response.setData(producers.getData().stream()
                .map(ProducerConverter::fromMsEntity)
                .collect(Collectors.toList())
        );
        response.setMetadata(producers.getMetadata());

        return response;
    }

    public ProducerDTO getProducer(Long producerId) {
        Producer producer = getAndCheckProducer(producerId);

        ProducerDTO producerDTO = ProducerConverter.fromMsEntity(producer);
        fillContactToDTO(producerDTO, producer);

        return producerDTO;
    }

    public Long create(CreateProducerRequestDTO producer) {
        if (SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_ENT_ADMIN) ) {
            if (producer.getEntityId() == null) {
                throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER,
                        "entity_id is mandatory", null);
            }
            securityManager.checkEntityAccessible(producer.getEntityId());
        } else {
            producer.setEntityId(SecurityUtils.getUserEntityId());
        }

        return entitiesRepository.createProducer(ProducerConverter.toMsEntity(producer));
    }

    public void update(Long producerId, ProducerDTO producerData) {
        validateUpdate(producerId, producerData);
        Producer producer = ProducerConverter.toMsEntity(producerData);
        producer.setId(producerId);

        fillContactFromDTO(producerData.getContact(), producer);


        entitiesRepository.updateProducer(producer);
    }

    public void delete(Long producerId) {
        Producer producer = getAndCheckProducer(producerId);

        producer.setStatus(ProducerStatus.DELETED);
        entitiesRepository.updateProducer(producer);
    }

    private Producer getAndCheckProducer(Long producerId) {
        Producer producer = entitiesRepository.getProducer(producerId);
        if (producer == null || producer.getStatus().equals(ProducerStatus.DELETED)) {
            throw new OneboxRestException(PRODUCER_NOT_FOUND, "No producer found with id: " + producerId, null);
        }
        securityManager.checkEntityAccessible(producer.getEntity().getId());
        return producer;
    }

    private void validateUpdate(Long producerId, ProducerDTO producerData) {
        Producer producer = getAndCheckProducer(producerId);
        InvoiceProviderInfo providerInfo = producerRepository.getProducerInvoiceProviderInfo(producerId);
        if (providerInfo.getStatus().equals(RequestStatus.COMPLETED.name())) {
            if (BooleanUtils.isFalse(producerData.getUseSimplifiedInvoice())) {
                throw new OneboxRestException(ApiMgmtErrorCode.SIMPLIFIED_INVOICE_CAN_NOT_BE_DISABLED, null);
            }
            if (producerData.getNif() != null && !producer.getNif().equals(producerData.getNif())) {
                throw new OneboxRestException(ApiMgmtErrorCode.NIF_MODIFICATION_DISABLED, null);
            }
        }
    }

    private void fillContactToDTO(ProducerDTO producerDTO, Producer producer) {
        ProducerConverter.fillAditionalData(producerDTO, producer);
        if (producer.getCountryId() != null) {
            MasterdataValue country = masterdataService.getCountry(producer.getCountryId().longValue());
            producerDTO.getContact().setCountry(new CodeNameDTO(country.getCode(), null));
        }
        if (producer.getCountrySubdivisionId() != null) {
            MasterdataValue country = masterdataService.getCountrySubdivision(producer.getCountrySubdivisionId().longValue());
            producerDTO.getContact().setCountrySubdivision(new CodeNameDTO(country.getCode(), null));
        }
    }

    private void fillContactFromDTO(ProducerContactDTO contact, Producer producer) {
        if (contact != null) {
            if (contact.getCountry() != null && contact.getCountry().getCode() != null) {
                producer.setCountryId(masterdataService.getCountryIdByCode(contact.getCountry().getCode()));
            }
            if (contact.getCountrySubdivision() != null && contact.getCountrySubdivision().getCode() != null) {
                producer.setCountrySubdivisionId(masterdataService.getCountrySubdivisionIdByCode(contact.getCountrySubdivision().getCode()));
            }
        }
    }


}
