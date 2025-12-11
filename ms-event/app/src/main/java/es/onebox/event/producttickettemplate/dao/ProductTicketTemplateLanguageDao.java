package es.onebox.event.producttickettemplate.dao;

import static es.onebox.event.producttickettemplate.dao.TableConstants.LANGUAGE;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA;

import java.util.ArrayList;
import java.util.List;

import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguageRecord;
import es.onebox.jooq.cpanel.tables.CpanelIdioma;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateLanguageRecord;
import es.onebox.jooq.dao.DaoImpl;

@Repository
public class ProductTicketTemplateLanguageDao extends DaoImpl<CpanelProductTicketTemplateLanguageRecord, Integer> {

    protected ProductTicketTemplateLanguageDao() {
        super(LANGUAGE);
    }

    private static final CpanelIdioma language = CPANEL_IDIOMA;
    private static final Field<String> JOIN_LANGUAGE_CODE = language.CODIGO.as("languageCode");
    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_LANGUAGE_CODE
    };

    public void deleteByProductTicketTemplateId(Integer templateId) {
        dsl.delete(LANGUAGE)
                .where(LANGUAGE.TEMPLATEID.eq(templateId)).execute();
    }

    public List<ProductTicketTemplateLanguageRecord> findByProductTicketTemplateId(Integer templateId) {
        List<ProductTicketTemplateLanguageRecord> result = new ArrayList<>();
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(LANGUAGE.fields(), JOIN_FIELDS);

        SelectJoinStep query = dsl
                .select(fields)
                .from(LANGUAGE)
                .innerJoin(language).on(LANGUAGE.LANGUAGEID.eq(language.IDIDIOMA));

        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(LANGUAGE.TEMPLATEID.eq(templateId));
        query.where(conditions);

        List<Record> records = query.fetch();
        for (Record ptRecord : records) {
            ProductTicketTemplateLanguageRecord productTicketTemplateLanguageRecord =
                    buildProductTicketTemplateLanguageRecord(ptRecord, fields.length);
            result.add(productTicketTemplateLanguageRecord);
        }
        return result;
    }

    private static ProductTicketTemplateLanguageRecord buildProductTicketTemplateLanguageRecord(Record ptRecord, int fields) {
        ProductTicketTemplateLanguageRecord productTicketTemplateLanguageRecord = ptRecord.into(ProductTicketTemplateLanguageRecord.class);
        //Add join fields only if has been added to base event fields
        if (fields > productTicketTemplateLanguageRecord.fields().length) {
            productTicketTemplateLanguageRecord.setCode(ptRecord.getValue(JOIN_LANGUAGE_CODE));
        }
        return productTicketTemplateLanguageRecord;
    }
}
