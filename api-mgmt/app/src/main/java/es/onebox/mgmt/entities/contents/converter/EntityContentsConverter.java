package es.onebox.mgmt.entities.contents.converter;

import es.onebox.mgmt.channels.contents.converter.ChannelContentsConverter;
import es.onebox.mgmt.channels.contents.dto.ChannelProfiledTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlockLabelDTO;
import es.onebox.mgmt.channels.contents.dto.ChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlockDTO;
import es.onebox.mgmt.channels.contents.dto.UpdateChannelTextBlocksDTO;
import es.onebox.mgmt.channels.contents.enums.ChannelBlockType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlockLabel;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlocks;
import es.onebox.mgmt.datasources.ms.entity.contents.EntityLiteral;
import es.onebox.mgmt.datasources.ms.entity.contents.EntityLiterals;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlock;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTextBlockLabel;
import es.onebox.mgmt.datasources.ms.entity.enums.EntityBlockType;
import es.onebox.mgmt.entities.contents.dto.EntityLiteralDTO;
import es.onebox.mgmt.entities.contents.dto.EntityLiteralsDTO;
import es.onebox.mgmt.entities.contents.dto.EntityProfiledTextBlockDTO;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlockDTO;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlockLabelDTO;
import es.onebox.mgmt.entities.contents.dto.EntityTextBlocksDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlock;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlockDTO;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocks;
import es.onebox.mgmt.entities.contents.dto.UpdateEntityTextBlocksDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EntityContentsConverter {

    private EntityContentsConverter() {
    }

    public static EntityLiteralsDTO toDTO(EntityLiterals in) {
        List<EntityLiteralDTO> out = in.stream()
                .map(el -> new EntityLiteralDTO(el.getKey(), el.getValue()))
                .sorted(Comparator.comparing(EntityLiteralDTO::getKey)).toList();
        return new EntityLiteralsDTO(out);
    }

    public static EntityLiterals toDTO(EntityLiteralsDTO in) {
        List<EntityLiteral> out = in.stream().map(el -> new EntityLiteral(el.getKey(), el.getValue())).toList();
        return new EntityLiterals(out);
    }

    public static EntityTextBlocksDTO toDTO(List<EntityTextBlock> in) {
        List<EntityTextBlockDTO> out = in.stream().map(EntityContentsConverter::toDTO).collect(Collectors.toList());
        return new EntityTextBlocksDTO(out);
    }

    private static EntityTextBlockDTO toDTO(EntityTextBlock in) {
        EntityTextBlockDTO out = new EntityTextBlockDTO();
        out.setId(in.getBlockId());
        out.setSubject(in.getSubject());
        out.setValue(in.getValue());
        out.setLanguage(ConverterUtils.toLanguageTag(in.getLanguage()));
        out.setType(in.getType() != null ? EntityBlockType.valueOf(in.getType().name()) : null);
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

    private static List<EntityTextBlockLabelDTO> toLabelsDTO(List<EntityTextBlockLabel> in) {
        return in.stream().map(EntityContentsConverter::toDTO).collect(Collectors.toList());
    }

    private static List<EntityProfiledTextBlockDTO> toProfilesDTO(List<EntityProfiledTextBlock> in) {
        return in.stream().map(EntityContentsConverter::toDTO).collect(Collectors.toList());
    }

    private static EntityTextBlockLabelDTO toDTO(EntityTextBlockLabel in) {
        EntityTextBlockLabelDTO out = new EntityTextBlockLabelDTO();
        out.setCode(in.getCode());
        out.setName(in.getName());
        return out;
    }

    private static EntityProfiledTextBlockDTO toDTO(EntityProfiledTextBlock in) {
        EntityProfiledTextBlockDTO out = new EntityProfiledTextBlockDTO();
        out.setId(in.getProfileId().longValue());
        out.setName(in.getProfileName());
        out.setValue(in.getValue());
        return out;
    }

    public static UpdateEntityTextBlocks toDTO(UpdateEntityTextBlocksDTO in) {
        List<UpdateEntityTextBlock> blocks = in.stream().map(EntityContentsConverter::toDTO)
                .collect(Collectors.toList());
        UpdateEntityTextBlocks out = new UpdateEntityTextBlocks();
        out.setUserId(SecurityUtils.getUserId());
        out.setValues(blocks);
        return out;
    }

    private static UpdateEntityTextBlock toDTO(UpdateEntityTextBlockDTO in) {
        UpdateEntityTextBlock out = new UpdateEntityTextBlock();
        out.setId(in.getId());
        out.setValue(in.getValue());
        out.setSubject(in.getSubject());
        out.setUseFreeText(in.getUseFreeText());
        out.setLanguage(ConverterUtils.toLocale(in.getLanguage()));
        return out;
    }
}
