package es.onebox.mgmt.channels.commissions.converter;

import es.onebox.mgmt.channels.commissions.dto.CommissionDTO;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.common.RangeDTO;
import es.onebox.mgmt.common.RangeValueDTO;
import es.onebox.mgmt.datasources.common.dto.Range;
import es.onebox.mgmt.datasources.common.dto.RangeValue;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelCommissionType;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;

import java.util.List;
import java.util.stream.Collectors;

public class CommissionConverter {

    private CommissionConverter() {}

    public static List<CommissionDTO> fromMsChannelsCommissionRangeResponse(List<ChannelCommission> msResponse,
                                                                            List<Currency> currencies,
                                                                            Currency defaultCurrency) {
        return msResponse.stream()
                .map(mr -> fromMsChannelCommission(mr, currencies, defaultCurrency))
                .collect(Collectors.toList());
    }

    private static CommissionDTO fromMsChannelCommission(ChannelCommission msChannelCommission,
                                                         List<Currency> currencies, Currency defaultCurrency) {
        CommissionDTO commission = new CommissionDTO();

        List<RangeDTO> ranges = msChannelCommission.getRanges().stream()
                .map(r -> fromRange(r, currencies, defaultCurrency))
                .collect(Collectors.toList());

        commission.setRanges(ranges);
        commission.setType(CommissionTypeDTO.valueOf(msChannelCommission.getType().name()));
        commission.setEnabledRanges(msChannelCommission.getEnabledRanges());

        return commission;
    }

    private static RangeDTO fromRange(Range msRange, List<Currency> currencies, Currency defaultCurrency) {
        RangeDTO range = new RangeDTO();
        RangeValueDTO values = new RangeValueDTO();
        values.setFixed(msRange.getValues().getFixed());
        values.setMax(msRange.getValues().getMax());
        values.setMin(msRange.getValues().getMin());
        values.setPercentage(msRange.getValues().getPercentage());
        range.setFrom(msRange.getFrom());
        range.setTo(msRange.getTo());
        range.setValues(values);
        Currency rangeCurrency = currencies.stream()
                                .filter(cc-> cc.getId().equals(msRange.getCurrencyId()))
                                .findFirst().orElse(null);
        if(rangeCurrency!=null) {
            range.setCurrency(rangeCurrency.getCode());
        } else if(defaultCurrency != null) {
            range.setCurrency(defaultCurrency.getCode());
        }
        //TODO Add this after adding currencies to season ticket
        /*else {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
        }*/
        return range;
    }

    public static ChannelCommission fromDTO(CommissionDTO source, List<Currency> currencies, Currency defaultCurrency) {
        ChannelCommission target = new ChannelCommission();

        target.setType(ChannelCommissionType.valueOf(source.getType().name()));
        target.setEnabledRanges(source.getEnabledRanges());

        List<Range> targetRanges = source.getRanges().stream()
                .map(range -> fromRangeDTO(range, currencies, defaultCurrency))
                .collect(Collectors.toList());

        target.setRanges(targetRanges);

        return target;
    }

    public static Range fromRangeDTO(RangeDTO source, List<Currency> currencies, Currency defaultCurrency) {
        Range target = new Range();

        target.setFrom(source.getFrom());

        if(target.getValues() == null) {
            target.setValues(new RangeValue());
        }

        target.getValues().setPercentage(source.getValues().getPercentage());
        target.getValues().setMin(source.getValues().getMin());
        target.getValues().setMax(source.getValues().getMax());
        target.getValues().setFixed(source.getValues().getFixed());

        Currency rangeCurrency = currencies.stream()
                                .filter(cc -> cc.getCode().equals(source.getCurrency()))
                                .findFirst().orElse(null);
        if(rangeCurrency != null) {
            target.setCurrencyId(rangeCurrency.getId());
        } else if(defaultCurrency != null) {
            target.setCurrencyId(defaultCurrency.getId());
        }
        //TODO Add this after adding currencies to season ticket
        /*else {
            throw new OneboxRestException(ApiMgmtErrorCode.CURRENCY_NOT_FOUND);
        }*/
        return target;
    }
}
