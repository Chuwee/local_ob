package es.onebox.event.products.dao;

import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.jooq.cpanel.tables.CpanelIdioma;
import es.onebox.jooq.cpanel.tables.CpanelProductLanguage;
import es.onebox.jooq.cpanel.tables.records.CpanelProductLanguageRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_LANGUAGE;

@Repository
public class ProductLanguageDao extends DaoImpl<CpanelProductLanguageRecord, Integer> {

    protected ProductLanguageDao() {
        super(CPANEL_PRODUCT_LANGUAGE);
    }

    private static final CpanelProductLanguage productLanguage = CPANEL_PRODUCT_LANGUAGE;
    private static final CpanelIdioma language = CPANEL_IDIOMA;
    private static final Field<String> JOIN_LANGUAGE_CODE = language.CODIGO.as("languageCode");
    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_LANGUAGE_CODE
    };

    public List<ProductLanguageRecord> findByProductId(Long productId) {
        List<ProductLanguageRecord> result = new ArrayList<>();
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(productLanguage.fields(), JOIN_FIELDS);

        SelectJoinStep query = dsl
                .select(fields)
                .from(productLanguage)
                .innerJoin(language).on(productLanguage.LANGUAGEID.eq(language.IDIDIOMA));

        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(productLanguage.PRODUCTID.eq(productId.intValue()));
        query.where(conditions);

        List<Record> records = query.fetch();
        for (Record record : records) {
            ProductLanguageRecord productLanguageRecord = buildProductLanguageRecord(record, fields.length);
            result.add(productLanguageRecord);
        }
        return result;
    }

    public ProductLanguageRecord findDefaultByProductId(Long productId) {
        SelectConditionStep query = dsl
                .select(productLanguage.PRODUCTID, productLanguage.DEFAULTLANGUAGE, JOIN_LANGUAGE_CODE)
                .from(productLanguage)
                .innerJoin(language).on(productLanguage.LANGUAGEID.eq(language.IDIDIOMA))
                .where(productLanguage.PRODUCTID.eq(productId.intValue()))
                .and(productLanguage.DEFAULTLANGUAGE.eq((byte) 1));

        Record record = query.fetchOne();
        if(record == null) {
            return null;
        }
        ProductLanguageRecord productLanguageRecord = record.into(ProductLanguageRecord.class);
        productLanguageRecord.setCode(record.getValue(JOIN_LANGUAGE_CODE));

        return productLanguageRecord;
    }

    private static ProductLanguageRecord buildProductLanguageRecord(Record record, int fields) {
        ProductLanguageRecord productLanguageRecord = record.into(ProductLanguageRecord.class);
        //Add join fields only if has been added to base event fields
        if (fields > productLanguageRecord.fields().length) {
            productLanguageRecord.setCode(record.getValue(JOIN_LANGUAGE_CODE));
        }
        return productLanguageRecord;
    }

    public void deleteByProduct(Long productId) {
        dsl.delete(productLanguage).
                where(productLanguage.PRODUCTID.eq(productId.intValue())).
                execute();
    }

}
