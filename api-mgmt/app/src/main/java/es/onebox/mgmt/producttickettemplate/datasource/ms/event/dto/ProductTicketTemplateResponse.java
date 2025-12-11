package es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import jakarta.validation.constraints.NotNull;

public record ProductTicketTemplateResponse(
        Long id,
        String name,
        IdNameDTO entity,
        ProductTicketModelResponse model,
        @NotNull
        @JsonProperty("default")
        Boolean isDefault,
        Long defaultLanguage,
        List<Long> selectedLanguageIds) implements Serializable {
}
