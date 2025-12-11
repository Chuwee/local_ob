package es.onebox.mgmt.channels.notifications.converter;

import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTemplateDTO;
import es.onebox.mgmt.channels.notifications.dto.ChannelEmailTemplatesDTO;
import es.onebox.mgmt.channels.notifications.enums.ChannelEmailTemplateType;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplate;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplates;

import java.util.stream.Collectors;

public class ChannelEmailTemplateConverter {

    public static ChannelEmailTemplatesDTO fromMs(ChannelEmailTemplates source) {
        return source.stream()
                .map(ChannelEmailTemplateConverter::fromMs)
                .collect(Collectors.toCollection(ChannelEmailTemplatesDTO::new));
    }

    public static ChannelEmailTemplateDTO fromMs(ChannelEmailTemplate source) {
        ChannelEmailTemplateDTO target = new ChannelEmailTemplateDTO();

        target.setFrom(source.getFrom());
        target.setCco(source.getCco());
        target.setAlias(source.getAlias());
        target.setType(fromMs(source.getType()));
        return target;
    }

    public static ChannelEmailTemplateType fromMs(
            es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplateType source) {
        return ChannelEmailTemplateType.valueOf(source.name());
    }

    public static ChannelEmailTemplates toMs(ChannelEmailTemplatesDTO source) {
        return source.stream()
                .map(ChannelEmailTemplateConverter::toMs)
                .collect(Collectors.toCollection(ChannelEmailTemplates::new));
    }

    public static ChannelEmailTemplate toMs(ChannelEmailTemplateDTO source) {
        ChannelEmailTemplate target = new ChannelEmailTemplate();

        target.setFrom(source.getFrom());
        target.setAlias(source.getAlias());
        target.setType(toMs(source.getType()));
        if (source.getCco() != null) {
            target.setCco(source.getCco().replaceAll(",\\s*", ", "));
        }
        return target;
    }

    public static es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplateType toMs(ChannelEmailTemplateType source) {
        return es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplateType.valueOf(source.name());
    }
}
