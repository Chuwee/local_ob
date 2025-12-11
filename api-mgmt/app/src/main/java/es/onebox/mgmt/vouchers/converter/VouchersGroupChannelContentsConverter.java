package es.onebox.mgmt.vouchers.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.vouchers.dto.VoucherChannelContentTextFilter;
import es.onebox.mgmt.vouchers.enums.VoucherChannelContentTextType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.toLocale;

public class VouchersGroupChannelContentsConverter {

    private VouchersGroupChannelContentsConverter(){}

    public static CommunicationElementFilter<VoucherChannelContentTextType> fromVoucherFilter(VoucherChannelContentTextFilter original,
                                                                                              MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<VoucherChannelContentTextType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() != null) {
            commFilter.setTags(new HashSet<>(Collections.singletonList(VoucherChannelContentTextType.valueOf(original.getType().name()))));
        } else {
            commFilter.setTags(Arrays.stream(VoucherChannelContentTextType.values()).collect(Collectors.toSet()));
        }
        return commFilter;
    }

    public static ChannelContentTextListDTO<VoucherChannelContentTextType> fromMsChannelText(List<BaseCommunicationElement> elements) {
        return new ChannelContentTextListDTO<>(elements.stream()
                .map(e -> {
                    ChannelContentTextDTO<VoucherChannelContentTextType> dto = new ChannelContentTextDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
                    dto.setType(VoucherChannelContentTextType.valueOf(e.getTag()));
                    dto.setValue(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }


    public static List<BaseCommunicationElement> toMsChannelText(List<ChannelContentTextDTO<VoucherChannelContentTextType>> elements) {
        return elements.stream()
                .map(element -> {
                    BaseCommunicationElement dto = new BaseCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTag(element.getType().name());
                    dto.setValue(element.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
