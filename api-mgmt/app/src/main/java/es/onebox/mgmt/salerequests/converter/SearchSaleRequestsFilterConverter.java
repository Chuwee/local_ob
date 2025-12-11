package es.onebox.mgmt.salerequests.converter;

import es.onebox.mgmt.common.FiltrableField;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequest;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequestExtended;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestsFilter;
import es.onebox.mgmt.salerequests.enums.SaleRequestField;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.core.security.Roles.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.ROLE_OPR_MGR;

public class SearchSaleRequestsFilterConverter {

    private SearchSaleRequestsFilterConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static MsSaleRequestsFilter convertToMsSaleRequestsFilter(SearchSaleRequestsFilter requestFilter, List<Currency> currencies) {
        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        fill(filter, requestFilter,currencies);
        return filter;
    }

    private static void fill(MsSaleRequestsFilter filter, SearchSaleRequestsFilter requestFilter, List<Currency> currencies) {
        if (requestFilter == null) {
            return;
        }

        filter.setLimit(requestFilter.getLimit());
        filter.setOffset(requestFilter.getOffset());

        filter.setOperatorId(SecurityUtils.getUserOperatorId());

        if (SecurityUtils.hasAnyRole(ROLE_OPR_MGR)) {
            filter.setChannelEntityId(requestFilter.getChannelEntityId());
        } else if (SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN)) {
            filter.setChannelEntityId(requestFilter.getChannelEntityId());
            filter.setEntityAdminId(SecurityUtils.getUserEntityId());
        } else if (!SecurityUtils.hasAnyRole(ROLE_OPR_MGR, ROLE_ENT_ADMIN) && CollectionUtils.isEmpty(requestFilter.getChannelEntityId())) {
            filter.setChannelEntityId(Collections.singletonList(SecurityUtils.getUserEntityId()));
        } else {
            throw new AccessDeniedException("Can't access resources from other entities");
        }

        filter.setEventEntityId(requestFilter.getEventEntityId());
        filter.setChannelId(requestFilter.getChannelId());
        filter.setDate(requestFilter.getDate());
        filter.setIncludeArchived(requestFilter.getIncludeArchived());
        filter.setIncludeAllowedChannelPromotion(requestFilter.getIncludeThirdPartyEntityEvents());
        filter.setSort(requestFilter.getSort());
        filter.setQ(requestFilter.getQ());
        if(requestFilter.getCurrency() != null) {
            filter.setCurrencyId(CurrenciesUtils.getCurrencyId(currencies, requestFilter.getCurrency()).intValue());
        }
        if (CollectionUtils.isNotEmpty(requestFilter.getStatus())) {
            filter.setStatus(requestFilter.getStatus().stream()
                    .map(SaleRequestsStatus::toMsChannelEnum)
                    .collect(Collectors.toList()));
        } else {
            filter.setStatus(Arrays.stream(SaleRequestsStatus.values())
                    .map(SaleRequestsStatus::toMsChannelEnum)
                    .collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(requestFilter.getFields())) {
            filter.setFields(new ArrayList<>());
            for (String field : requestFilter.getFields()) {
                FiltrableField filterField = SaleRequestField.byName(field);
                if (filterField != null) {
                    filter.getFields().add(filterField.getDtoName());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(requestFilter.getEventStatus())) {
            filter.setEventStatus(requestFilter.getEventStatus());
        }
    }

    public static FiltersSalesRequestExtended convertToFiltersSaleRequest(FiltersSalesRequest filter) {
        FiltersSalesRequestExtended filterExtended = new FiltersSalesRequestExtended();

        if (SecurityUtils.hasAnyRole(ROLE_OPR_MGR)) {
            filterExtended.setOperatorId(SecurityUtils.getUserOperatorId());
        } else if (SecurityUtils.hasAnyRole(ROLE_ENT_ADMIN)) {
           filterExtended.setEntityAdminId(SecurityUtils.getUserEntityId());
        } else if (SecurityUtils.hasAnyRole(ROLE_OPR_ANS, ROLE_CNL_MGR, ROLE_ENT_ANS)) {
            filterExtended.setEntityId(SecurityUtils.getUserEntityId());
        } else {
            throw new AccessDeniedException("Can't access resources from other entities");
        }
        filterExtended.setQ(filter.getQ());
        filterExtended.setLimit(filter.getLimit());

        return filterExtended;
    }
}
