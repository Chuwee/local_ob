package es.onebox.mgmt.channels.forms.converter;

import es.onebox.core.order.utils.common.BuyerDataUtils;
import es.onebox.mgmt.channels.forms.dto.ChannelAgreementDTO;
import es.onebox.mgmt.channels.forms.dto.CreateChannelAgreementDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelAgreementDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAgreement;
import org.apache.commons.collections.MapUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelAgreementConverter {

    private ChannelAgreementConverter() {
    }

    public static List<ChannelAgreementDTO> toDTO(List<ChannelAgreement> in) {
        return in.stream().map(ChannelAgreementConverter::toDTO).sorted(Comparator.comparing(ChannelAgreementDTO::getPosition))
                .collect(Collectors.toList());
    }

    public static ChannelAgreement toDTO(CreateChannelAgreementDTO in) {
        ChannelAgreement out = new ChannelAgreement();
        out.setName(in.getName());
        out.setTexts(in.getTexts().entrySet().stream()
                 .collect(Collectors.toMap(entry -> ConverterUtils.toLocale(entry.getKey()), Map.Entry::getValue)));
        return out;
    }

    public static ChannelAgreement toDTO(UpdateChannelAgreementDTO in) {
        ChannelAgreement out = new ChannelAgreement();
        out.setName(in.getName());
        out.setMandatory(in.getMandatory());
        out.setEnabled(in.getEnabled());
        out.setPosition(in.getPosition());
        if (MapUtils.isNotEmpty(in.getTexts())) {
            out.setTexts(in.getTexts().entrySet().stream()
                    .collect(Collectors.toMap(entry -> ConverterUtils.toLocale(entry.getKey()), Map.Entry::getValue)));
        }
        return out;
    }

    private static ChannelAgreementDTO toDTO(ChannelAgreement in) {
        ChannelAgreementDTO out = new ChannelAgreementDTO();
        out.setId(in.getId());
        out.setExternalKey(BuyerDataUtils.getChannelAgreementKey(in.getId(), in.getItemId()));
        out.setName(in.getName());
        out.setMandatory(in.getMandatory());
        out.setEnabled(in.getEnabled());
        out.setTexts(in.getTexts().entrySet().stream()
                .collect(Collectors.toMap(entry -> ConverterUtils.toLanguageTag(entry.getKey()), Map.Entry::getValue)));
        out.setPosition(in.getPosition());
        return out;
    }

}
