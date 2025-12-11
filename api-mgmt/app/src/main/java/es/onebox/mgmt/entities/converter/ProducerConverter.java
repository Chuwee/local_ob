package es.onebox.mgmt.entities.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerFilter;
import es.onebox.mgmt.entities.dto.CreateProducerRequestDTO;
import es.onebox.mgmt.entities.dto.ProducerContactDTO;
import es.onebox.mgmt.entities.dto.ProducerDTO;
import es.onebox.mgmt.entities.dto.ProducerSearchFilter;
import es.onebox.mgmt.entities.enums.ProducerStatus;
import es.onebox.mgmt.events.enums.EventField;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class ProducerConverter {

    private ProducerConverter() {
    }

    public static ProducerDTO fromMsEntity(Producer source) {
        if (source == null) {
            return null;
        }

        ProducerDTO producerDTO = new ProducerDTO(source.getId());
        producerDTO.setName(source.getName());
        producerDTO.setNif(source.getNif());
        producerDTO.setSocialReason(source.getSocialReason());
        producerDTO.setDefault(source.getDefault());
        producerDTO.setUseSimplifiedInvoice(source.getUseSimplifiedInvoice());
        if (source.getStatus() != null) {
            producerDTO.setStatus(ProducerStatus.valueOf(source.getStatus().name()));
        }
        if (source.getEntity() != null) {
            producerDTO.setEntity(new IdNameDTO(source.getEntity().getId(), source.getEntity().getName()));
        }


        return producerDTO;
    }

    public static void fillAditionalData(ProducerDTO producerDTO, Producer source) {
        if (ObjectUtils.anyNotNull(source.getAddress(), source.getCity(), source.getPostalCode(), source.getEmail(),
                source.getPhone(), source.getContactName(), source.getCountryId(), source.getCountrySubdivisionId())) {
            ProducerContactDTO contact = new ProducerContactDTO();
            contact.setAddress(source.getAddress());
            contact.setCity(source.getCity());
            contact.setPostalCode(source.getPostalCode());
            contact.setEmail(source.getEmail());
            contact.setPhone(source.getPhone());
            contact.setName(source.getContactName());
            producerDTO.setContact(contact);
        }
    }

    public static Producer toMsEntity(CreateProducerRequestDTO source) {
        Producer producer = new Producer();
        producer.setName(source.getName());
        producer.setNif(source.getNif());
        producer.setSocialReason(source.getSocialReason());
        producer.setEntity(new Entity(source.getEntityId()));
        return producer;
    }

    public static Producer toMsEntity(ProducerDTO source) {
        Producer producer = new Producer();
        producer.setName(source.getName());
        producer.setNif(source.getNif());
        producer.setSocialReason(source.getSocialReason());
        producer.setDefault(source.getDefault());
        producer.setUseSimplifiedInvoice(source.getUseSimplifiedInvoice());
        if (source.getStatus() != null) {
            producer.setStatus(es.onebox.mgmt.datasources.ms.entity.enums.ProducerStatus.get(source.getStatus().getState()));
        }
        ProducerContactDTO contact = source.getContact();
        if (contact != null) {
            producer.setAddress(contact.getAddress());
            producer.setCity(contact.getCity());
            producer.setPostalCode(contact.getPostalCode());
            producer.setEmail(contact.getEmail());
            producer.setPhone(contact.getPhone());
            producer.setContactName(contact.getName());
        }
        return producer;
    }

    public static ProducerFilter toMs(Long operatorId, ProducerSearchFilter filter) {
        ProducerFilter producerFilter = new ProducerFilter();
        producerFilter.setEntityId(filter.getEntityId());
        producerFilter.setEntityAdminId(filter.getEntityAdminId());
        producerFilter.setOperatorId(operatorId);

        if (filter.getStatus() != null) {
            FilterWithOperator<ProducerStatus> filterOperator = new FilterWithOperator<>();
            filterOperator.setOperator(Operator.EQUALS);
            filterOperator.setValue(filter.getStatus());
            producerFilter.setStatus(filterOperator);
        }

        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            producerFilter.setFreeSearch(filter.getFreeSearch());
        }
        producerFilter.setSort(ConverterUtils.checkSortFields(filter.getSort(), EventField::byName));
        producerFilter.setFields(ConverterUtils.checkFilterFields(filter.getFields(), EventField::byName));
        producerFilter.setOffset(filter.getOffset());
        producerFilter.setLimit(filter.getLimit());

        return producerFilter;
    }

}
