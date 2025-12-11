package es.onebox.event.packs.converter;

import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.packs.dto.PackChannelDTO;
import es.onebox.event.packs.dto.PackChannelDetailDTO;
import es.onebox.event.packs.dto.PackChannelInfoDTO;
import es.onebox.event.packs.dto.PackChannelSettingsDTO;
import es.onebox.event.packs.dto.PackChannelStatusDTO;
import es.onebox.event.packs.dto.PackInfoDTO;
import es.onebox.event.packs.dto.UpdatePackChannelDTO;
import es.onebox.event.packs.enums.PackChannelStatus;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.record.PackChannelRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PackChannelConverter {

    private PackChannelConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<PackChannelDTO> convertList(List<PackChannelRecord> records, String s3Url) {
        if (records == null) {
            return new ArrayList<>();
        }
        return records.stream()
                .map(record -> convert(record, s3Url))
                .collect(Collectors.toList());
    }

    public static PackChannelDTO convert(PackChannelRecord record, String s3Url) {
        if (record == null) {
            return null;
        }
        PackChannelDTO dto = new PackChannelDTO();
        dto.setId(record.getId());
        fillPack(dto, record);
        fillChannel(dto, record, s3Url);
        fillStatus(dto, record);
        return dto;
    }

    public static PackChannelDetailDTO toDTO(PackChannelRecord record, String s3Url) {
        if (record == null) {
            return null;
        }
        PackChannelDetailDTO dto = new PackChannelDetailDTO();
        dto.setId(record.getId());
        fillPack(dto, record);
        fillChannel(dto, record, s3Url);
        fillStatus(dto, record);
        PackChannelSettingsDTO settings = new PackChannelSettingsDTO();
        settings.setSuggested(record.getSuggested());
        settings.setOnSaleForLoggedUsers(record.getOnSaleForLoggedUsers());
        dto.setSettings(settings);
        return dto;
    }

    private static void fillStatus(PackChannelDTO target, PackChannelRecord record) {
        target.setStatus(new PackChannelStatusDTO());
        target.getStatus().setRequest(PackChannelStatus.byId(record.getRequestStatus()));
    }

    private static void fillPack(PackChannelDTO target, PackChannelRecord record) {
        target.setPack(new PackInfoDTO());
        target.getPack().setId(record.getPackId());
        target.getPack().setName(record.getPackName());
        target.getPack().setStatus(PackStatus.getById(record.getPackStatus()));
    }

    private static void fillChannel(PackChannelDTO target, PackChannelRecord record, String s3Url) {
        target.setChannel(new PackChannelInfoDTO());
        target.getChannel().setId(record.getChannelId());
        target.getChannel().setName(record.getChannelName());
        target.getChannel().setType(ChannelSubtype.getById(record.getChannelType()));
        target.getChannel().setEntityId(record.getEntityId());
        target.getChannel().setEntityName(record.getEntityName());
        if (record.getEntityLogoPath() != null && record.getEntityId() != null && record.getOperatorId() != null) {
            target.getChannel().setEntityLogo(getLogoUrl(record, s3Url));
        }
    }

    private static String getLogoUrl(PackChannelRecord record, String s3url) {
        return S3URLResolver.builder()
                .withUrl(s3url)
                .withType(S3URLResolver.S3ImageType.ENTITY_IMAGE)
                .withEntityId(record.getEntityId())
                .withOperatorId(record.getOperatorId())
                .build()
                .buildPath(record.getEntityLogoPath());
    }


    public static void toRecord(CpanelPackCanalRecord record, UpdatePackChannelDTO updatePackChannel) {
        if (updatePackChannel.getSuggested() != null) {
            record.setSugerirpack(ConverterUtils.isTrueAsByte(updatePackChannel.getSuggested()));
        }
        if (updatePackChannel.getOnSaleForLoggedUsers() != null) {
            record.setOnsaleforloggedusers(ConverterUtils.isTrueAsByte(updatePackChannel.getOnSaleForLoggedUsers()));
        }
    }
}
