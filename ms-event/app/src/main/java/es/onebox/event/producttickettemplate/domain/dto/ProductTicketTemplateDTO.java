package es.onebox.event.producttickettemplate.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;

public record ProductTicketTemplateDTO(
		Long id,
		String name,
		IdNameDTO entity,
		ProductTicketModelDTO model,
		Integer defaultLanguage,
		@JsonInclude(JsonInclude.Include.NON_EMPTY) List<Integer> selectedLanguageIds) implements Serializable {

	public ProductTicketTemplateDTO(Long id, String name, IdNameDTO entity, ProductTicketModelDTO model) {
		this(id, name, entity, model, null, Collections.emptyList());
	}

	private ProductTicketTemplateDTO withLanguages(Integer defaultLanguage, List<Integer> selectedLanguageIds) {
		return new ProductTicketTemplateDTO(this.id, this.name, this.entity, this.model,
				defaultLanguage, selectedLanguageIds);
	}

	public ProductTicketTemplateDTO withLanguages(List<ProductTicketTemplateLanguageDTO> languages) {
		if (languages == null || languages.isEmpty()) {
			return this;
		}

		List<Integer> languageIds = new ArrayList<>(languages.size());
		Integer defaultLang = null;

		for (ProductTicketTemplateLanguageDTO lang : languages) {
			Integer langId = lang.languageId();
			languageIds.add(langId);
			if (CommonUtils.isTrue(lang.isDefault())) {
				defaultLang = langId;
			}
		}

		if (defaultLang == null) {
			defaultLang = languageIds.get(0);
		}

		return withLanguages(defaultLang, languageIds);
	}
}
