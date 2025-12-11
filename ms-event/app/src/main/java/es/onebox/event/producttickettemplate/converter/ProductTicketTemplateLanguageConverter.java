package es.onebox.event.producttickettemplate.converter;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguageRecord;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguageDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguagesDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateLanguageRecord;

import java.util.List;

public class ProductTicketTemplateLanguageConverter {

	private ProductTicketTemplateLanguageConverter() {
	}

	public static CpanelProductTicketTemplateLanguageRecord toRecord(Integer templateId, Integer languageId) {

		CpanelProductTicketTemplateLanguageRecord templateLanguage = new CpanelProductTicketTemplateLanguageRecord();
		templateLanguage.setTemplateid(templateId);
		templateLanguage.setLanguageid(languageId);
		templateLanguage.setIsdefault((byte) 1);

		return templateLanguage;
	}

	public static CpanelProductTicketTemplateLanguageRecord toRecord(Integer templateId, Integer languageId,
			Integer defaultLanguageId) {
		CpanelProductTicketTemplateLanguageRecord templateLanguage = new CpanelProductTicketTemplateLanguageRecord();
		templateLanguage.setTemplateid(templateId);
		templateLanguage.setLanguageid(languageId);
		templateLanguage.setIsdefault(ConverterUtils.isTrueAsByte(languageId.equals(defaultLanguageId)));

		return templateLanguage;

	}

    public static ProductTicketTemplateLanguagesDTO toDto(
            List<ProductTicketTemplateLanguageRecord> productTicketTemplateLanguageRecordRecords) {

        ProductTicketTemplateLanguagesDTO dtos = new ProductTicketTemplateLanguagesDTO();

        if (productTicketTemplateLanguageRecordRecords == null || productTicketTemplateLanguageRecordRecords.isEmpty()) {
            return dtos;
        }

        for (ProductTicketTemplateLanguageRecord productTicketTemplateLanguageRecord : productTicketTemplateLanguageRecordRecords) {
            if (productTicketTemplateLanguageRecord == null) continue;

            Integer templateId = productTicketTemplateLanguageRecord.getTemplateid();
            Integer languageId = productTicketTemplateLanguageRecord.getLanguageid();
            String code = productTicketTemplateLanguageRecord.getCode();

            dtos.add(new ProductTicketTemplateLanguageDTO(
                    templateId,
                    languageId,
					code,
					ConverterUtils.isByteAsATrue(productTicketTemplateLanguageRecord.getIsdefault())));
        }

        return dtos;
    }

}
