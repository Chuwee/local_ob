package es.onebox.mgmt.seasontickets.converter;



import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketFilter;
import es.onebox.mgmt.events.enums.EventField;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSearchFilter;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SeasonTicketFilterConverter {

    private SeasonTicketFilterConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    private static List<String> toMs(List<SeasonTicketStatusDTO> listSeasonTicketStatusDTO) {
        if (CollectionUtils.isNotEmpty(listSeasonTicketStatusDTO)) {
            return listSeasonTicketStatusDTO.stream().map(Enum::name).toList();
        }
        return null;
    }

    public static SeasonTicketFilter toMs(Long operatorId, SeasonTicketSearchFilter filter, Long countryId, List<Currency> currencies) {
        if (filter == null) {
            return null;
        }

        if (operatorId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Operator is mandatory field", null);
        }

        SeasonTicketFilter seasonTicketFilter = new SeasonTicketFilter();

        seasonTicketFilter.setEntityId(filter.getEntityId());
        seasonTicketFilter.setOperatorId(operatorId);
        seasonTicketFilter.setStatus(toMs(filter.getStatus()));
        seasonTicketFilter.setProducerId(filter.getProducerId());
        seasonTicketFilter.setVenueId(filter.getVenueId());
        seasonTicketFilter.setCountryId(countryId);
        seasonTicketFilter.setCity(filter.getCity());
        seasonTicketFilter.setIncludeArchived(CommonUtils.isTrue(filter.getIncludeArchived()));
        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            seasonTicketFilter.setFreeSearch(filter.getFreeSearch());
        }
        seasonTicketFilter.setEntityAdminId(filter.getEntityAdminId());
        seasonTicketFilter.setStartDate(filter.getStartDate());
        seasonTicketFilter.setEndDate(filter.getEndDate());
        if (filter.getCurrencyCode() != null) {
            seasonTicketFilter.setCurrencyId(currencies.stream().filter(currency -> currency.getCode().equals(filter.getCurrencyCode())).map(Currency::getId).findFirst().orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND)));
        }
        seasonTicketFilter.setSort(ConverterUtils.checkSortFields(filter.getSort(), EventField::byName));
        seasonTicketFilter.setFields(ConverterUtils.checkFilterFields(filter.getFields(), EventField::byName));
        seasonTicketFilter.setOffset(filter.getOffset());
        seasonTicketFilter.setLimit(filter.getLimit());
        return seasonTicketFilter;
    }
}
