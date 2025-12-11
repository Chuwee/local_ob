package es.onebox.mgmt.producttickettemplate.domain.dto;

import java.io.Serializable;

import es.onebox.core.serializer.dto.common.IdNameDTO;

public record ProductTicketTemplateSearchDTO(
        Long id,
        String name,
        IdNameDTO entity,
        ProductTicketModelDTO model) implements Serializable {

}

