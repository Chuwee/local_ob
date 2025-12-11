package es.onebox.mgmt.events.promotiontemplates.converter;

import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateFilter;
import es.onebox.mgmt.events.promotiontemplates.dto.EventPromotionTemplateFilter;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class EventPromotionTemplateFilterConverter {

    private EventPromotionTemplateFilterConverter () {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static PromotionTemplateFilter convertToMsPromotionTemplateFilter(EventPromotionTemplateFilter filterIn, List<Currency> currencies) {
        PromotionTemplateFilter filterOut = new PromotionTemplateFilter();
        if (filterIn.getCurrencyCode() != null) {
            filterOut.setCurrencyId(CurrenciesUtils.getCurrencyId(currencies, filterIn.getCurrencyCode()));
        }
        fill(filterOut, filterIn);
        return filterOut;
    }

    private static void fill(PromotionTemplateFilter out, EventPromotionTemplateFilter in) {
        if (in == null) {
            return;
        }
        out.setLimit(in.getLimit());
        out.setOffset(in.getOffset());
        out.setOperatorId(SecurityUtils.getUserOperatorId());
        out.setName(in.getName());
        out.setEntityAdminId(in.getEntityAdminId());
        out.setEntityId(in.getEntityId());
        out.setSort(in.getSort());
        out.setQ(in.getQ());

        if (CollectionUtils.isNotEmpty(in.getType())) {
            out.setType(in.getType());
        }

    }
}
