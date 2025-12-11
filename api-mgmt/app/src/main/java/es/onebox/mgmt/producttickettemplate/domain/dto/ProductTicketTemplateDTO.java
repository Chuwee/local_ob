package es.onebox.mgmt.producttickettemplate.domain.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;

public record ProductTicketTemplateDTO(
        Long id,
        String name,
        IdNameDTO entity,
        ProductTicketModelDTO model,
        @JsonProperty("default")
        Boolean isDefault,
        Long defaultLanguage,
        List<Long> selectedLanguageIds) implements Serializable {

}
