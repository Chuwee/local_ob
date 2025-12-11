package es.onebox.mgmt.producttickettemplate.domain.dto;

import java.io.Serializable;
import java.util.List;

import es.onebox.core.serializer.dto.common.IdNameDTO;

public record ProductTicketTemplateDetailDTO(
        Long id,
        String name,
        IdNameDTO entity,
        ProductTicketModelDTO model,
        Long defaultLanguage,
        List<Long> selectedLanguageIds) implements Serializable {

}

