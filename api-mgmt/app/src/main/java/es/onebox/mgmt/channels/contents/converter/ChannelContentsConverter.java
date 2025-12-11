package es.onebox.mgmt.channels.contents.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelAuditedTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelContentsCloneDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelLiteralDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelLiteralsDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelProfiledTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlockLabelDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelProfiledTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.enums.ChannelBlockType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAuditedTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelContentClone;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelLiteral;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelLiterals;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlockLabel;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlocks;
import es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates.WhatsappTemplates;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.whatsapptemplates.dto.WhatsappTemplateDTO;
import es.onebox.mgmt.whatsapptemplates.dto.WhatsappTemplatesDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelContentsConverter {

    private ChannelContentsConverter() {
    }

    public static ChannelLiteralsDTO toDTO(ChannelLiterals in) {
        List<ChannelLiteralDTO> out = in.stream().map(el -> new ChannelLiteralDTO(el.getKey(), el.getValue(), el.getRichText(), el.getAuditable()))
                .collect(Collectors.toList());
        out.sort(Comparator.comparing(ChannelLiteralDTO::getKey));
        return new ChannelLiteralsDTO(out);
    }

    public static ChannelLiterals toDTO(ChannelLiteralsDTO in) {
        List<ChannelLiteral> out = in.stream().map(el -> new ChannelLiteral(el.getKey(), el.getValue()))
                .collect(Collectors.toList());
        return new ChannelLiterals(out);
    }

    public static List<ChannelAuditedTextBlockDTO> toHistoricalDTO(List<ChannelAuditedTextBlock> in, Map<Long, String> users) {
        return in.stream().map(el -> toDTO(el, users)).sorted(Comparator.comparing(ChannelAuditedTextBlockDTO::getChangedDate))
                .collect(Collectors.toList());
    }

    public static List<UpdateChannelProfiledTextBlock> toDTO(UpdateChannelProfiledTextBlocksDTO body) {
        return body.stream()
                .map(el -> new UpdateChannelProfiledTextBlock(el.getId(), ConverterUtils.toLocale(el.getLanguage()), el.getValue()))
                .collect(Collectors.toList());
    }

    public static ChannelTextBlocksDTO toDTO(List<ChannelTextBlock> in) {
        List<ChannelTextBlockDTO> out = in.stream().map(ChannelContentsConverter::toDTO).collect(Collectors.toList());
        return new ChannelTextBlocksDTO(out);
    }

    public static UpdateChannelTextBlocks toDTO(UpdateChannelTextBlocksDTO in) {
        List<UpdateChannelTextBlock> blocks = in.stream().map(ChannelContentsConverter::toDTO)
                .collect(Collectors.toList());
        UpdateChannelTextBlocks out = new UpdateChannelTextBlocks();
        out.setUserId(SecurityUtils.getUserId());
        out.setValues(blocks);
        return out;
    }

    public static ChannelContentClone toMs(ChannelContentsCloneDTO in) {
        ChannelContentClone out = new ChannelContentClone();
        out.setChannelId(in.getChannelId());
        return out;
    }

    private static ChannelTextBlockDTO toDTO(ChannelTextBlock in) {
        ChannelTextBlockDTO out = new ChannelTextBlockDTO();
        out.setId(in.getBlockId());
        out.setSubject(in.getSubject());
        out.setValue(in.getValue());
        out.setLanguage(ConverterUtils.toLanguageTag(in.getLanguage()));
        out.setType(ChannelBlockType.valueOf(in.getType().name()));
        out.setAudited(in.getAudited());
        out.setUseFreeText(in.getUseFreeText());
        if (CollectionUtils.isNotEmpty(in.getProfiledTextsBlocks())) {
            out.setProfiledTextBlocks(toProfilesDTO(in.getProfiledTextsBlocks()));
        }
        if (CollectionUtils.isNotEmpty(in.getLabels())) {
            out.setLabels(toLabelsDTO(in.getLabels()));
        }
        return out;
    }

    private static ChannelAuditedTextBlockDTO toDTO(ChannelAuditedTextBlock in, Map<Long, String> users) {
        ChannelAuditedTextBlockDTO out = new ChannelAuditedTextBlockDTO();
        out.setChangedDate(in.getChangedDate());
        out.setLanguage(ConverterUtils.toLanguageTag(in.getLanguage()));
        out.setValue(in.getValue());
        out.setSubject(in.getSubject());
        IdNameDTO idName = new IdNameDTO();
        idName.setId(in.getUserId());
        String userName = users.get(in.getUserId());
        if (userName != null) {
            idName.setName(userName);
        }
        out.setAuthor(idName);
        return out;
    }

    private static List<ChannelTextBlockLabelDTO> toLabelsDTO(List<ChannelTextBlockLabel> in) {
        return in.stream().map(ChannelContentsConverter::toDTO).collect(Collectors.toList());
    }

    private static List<ChannelProfiledTextBlockDTO> toProfilesDTO(List<ChannelProfiledTextBlock> in) {
        return in.stream().map(ChannelContentsConverter::toDTO).collect(Collectors.toList());
    }

    private static ChannelProfiledTextBlockDTO toDTO(ChannelProfiledTextBlock in) {
        ChannelProfiledTextBlockDTO out = new ChannelProfiledTextBlockDTO();
        out.setId(in.getProfileId().longValue());
        out.setName(in.getProfileName());
        out.setValue(in.getValue());
        return out;
    }

    private static ChannelTextBlockLabelDTO toDTO(ChannelTextBlockLabel in) {
        ChannelTextBlockLabelDTO out = new ChannelTextBlockLabelDTO();
        out.setCode(in.getCode());
        out.setName(in.getName());
        return out;
    }

    private static UpdateChannelTextBlock toDTO(UpdateChannelTextBlockDTO in) {
        UpdateChannelTextBlock out = new UpdateChannelTextBlock();
        out.setId(in.getId());
        out.setValue(in.getValue());
        out.setSubject(in.getSubject());
        out.setUseFreeText(in.getUseFreeText());
        out.setLanguage(ConverterUtils.toLocale(in.getLanguage()));
        return out;
    }

    public static WhatsappTemplatesDTO toDTO(WhatsappTemplates in) {
        List<WhatsappTemplateDTO> out = in.stream()
                .map(el -> new WhatsappTemplateDTO(
                        el.getId(),
                        el.getName(),
                        el.getType(),
                        el.getPreview()))
                .sorted(Comparator.comparing(WhatsappTemplateDTO::getId))
                .collect(Collectors.toList());
        return new WhatsappTemplatesDTO(out);
    }
}
