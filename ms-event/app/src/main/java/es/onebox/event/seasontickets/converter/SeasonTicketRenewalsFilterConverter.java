package es.onebox.event.seasontickets.converter;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.event.seasontickets.amqp.renewals.purge.PurgeRenewalSeatsMessage;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SeasonTicketRenewalsFilterConverter {

    private SeasonTicketRenewalsFilterConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    private static final Map<String, Operator> MAP_OF_OPERATORS = Arrays.stream(Operator.values())
            .collect(Collectors.toMap(
                    Operator::getKey,
                    Function.identity()
            ));

    public static RenewalSeatsPurgeFilter convertToRenewalSeatsPurgeFilter(PurgeRenewalSeatsMessage purgeRenewalSeatsMessage) {
        if(purgeRenewalSeatsMessage == null) {
            return null;
        }

        RenewalSeatsPurgeFilter purgeFilter = new RenewalSeatsPurgeFilter();
        purgeFilter.setMappingStatus(purgeRenewalSeatsMessage.getMappingStatus());
        purgeFilter.setRenewalStatus(purgeRenewalSeatsMessage.getRenewalStatus());
        purgeFilter.setFreeSearch(purgeRenewalSeatsMessage.getFreeSearch());
        purgeFilter.setBirthday(SeasonTicketRenewalsFilterConverter.fromMessage(purgeRenewalSeatsMessage.getBirthday()));
        return purgeFilter;
    }

    private static List<FilterWithOperator<ZonedDateTime>> fromMessage(List<String> birthday) {
        if(CollectionUtils.isEmpty(birthday)) {
            return null;
        }
        return birthday.stream().map(SeasonTicketRenewalsFilterConverter::fromMessage).toList();
    }

    private static FilterWithOperator<ZonedDateTime> fromMessage(String birthday) {
        if(birthday == null) {
            return null;
        }

        String[] filterParts = birthday.split(":");
        String operatorString = filterParts[0];
        StringBuilder valueString = new StringBuilder();
        for (int i = 1; i < filterParts.length; i++) {
            if(i != 1) {
                valueString.append(":");
            }
            valueString.append(filterParts[i]);
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(valueString.toString(), DateTimeFormatter.ISO_DATE_TIME);

        FilterWithOperator<ZonedDateTime> filter = new FilterWithOperator<>();
        filter.setOperator(getOperator(operatorString));
        filter.setValue(zonedDateTime);
        return filter;
    }

    private static Operator getOperator(String operatorString) {
        return MAP_OF_OPERATORS.get(operatorString);
    }

    public static SeasonTicketRenewalSeatsFilter convertToSeasonTicketRenewalSeatsFilter(Long seasonTicketId,
                                                                                         RenewalSeatsPurgeFilter purgeFilter) {
        if(purgeFilter == null) {
            return null;
        }

        SeasonTicketRenewalSeatsFilter renewalSeatsFilter = new SeasonTicketRenewalSeatsFilter();
        renewalSeatsFilter.setSeasonTicketId(seasonTicketId);
        renewalSeatsFilter.setMappingStatus(purgeFilter.getMappingStatus());
        renewalSeatsFilter.setRenewalStatus(purgeFilter.getRenewalStatus());
        renewalSeatsFilter.setFreeSearch(purgeFilter.getFreeSearch());
        renewalSeatsFilter.setBirthday(purgeFilter.getBirthday());
        return renewalSeatsFilter;
    }
}
