package es.onebox.event.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.dao.ChannelCurrenciesDao;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventPackErrorCode;
import es.onebox.event.packs.converter.PackChannelConverter;
import es.onebox.event.packs.dao.PackChannelDao;
import es.onebox.event.packs.dto.PackChannelDetailDTO;
import es.onebox.event.packs.dto.PackChannelSearchFilter;
import es.onebox.event.packs.dto.PackChannelsDTO;
import es.onebox.event.packs.dto.PackDetailDTO;
import es.onebox.event.packs.dto.UpdatePackChannelDTO;
import es.onebox.event.packs.enums.PackChannelStatus;
import es.onebox.event.packs.record.PackChannelRecord;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackChannelService {

    private final PackChannelDao packChannelDao;
    private final EntityDao entityDao;
    private final ChannelDao channelDao;
    private final ChannelCurrenciesDao channelCurrencyDao;
    private final EventDao eventDao;
    private final PackService packService;
    private String s3Url;

    public PackChannelService(PackChannelDao packChannelDao,
                              ChannelDao channelDao,
                              EventDao eventDao,
                              EntityDao entityDao,
                              ChannelCurrenciesDao channelCurrencyDao,
                              PackService packService,
                              @Value("${onebox.repository.S3SecureUrl}") String s3domain,
                              @Value("${onebox.repository.fileBasePath}") String fileBasePath) {
        this.packChannelDao = packChannelDao;
        this.channelDao = channelDao;
        this.eventDao = eventDao;
        this.entityDao = entityDao;
        this.channelCurrencyDao = channelCurrencyDao;
        this.packService = packService;
        this.s3Url = s3domain + fileBasePath;
    }

    @MySQLRead
    public PackChannelsDTO getPackChannels(Long packId, @Valid PackChannelSearchFilter filter) {
        List<PackChannelRecord> packChannels = packChannelDao.findPackChannels(packId, filter);
        Long total = packChannelDao.countByFilter(packId, filter);

        PackChannelsDTO dto = new PackChannelsDTO();
        dto.setData(PackChannelConverter.convertList(packChannels, s3Url));
        dto.setMetadata(MetadataBuilder.build(filter, total));
        return dto;
    }

    @MySQLRead
    public PackChannelDetailDTO getPackChannel(Long packId, Long channelId) {
        PackChannelRecord packChannel = packChannelDao.getPackChannelDetailed(packId, channelId);
        if (packChannel == null) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_CHANNEL_NOT_FOUND);
        }
        return PackChannelConverter.toDTO(packChannel, s3Url);
    }

    @MySQLWrite
    public void createPackChannels(Long packId, List<Long> channelIds) {
        PackDetailDTO pack = packService.getPackById(packId);
        if (pack == null) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_NOT_FOUND);
        }

        List<CpanelPackCanalRecord> packChannels = packChannelDao.getPackChannels(packId);
        List<Long> packChannelIds = packChannels.stream().map(pc -> pc.getIdcanal().longValue()).toList();
        if (packChannelIds.stream().anyMatch(channelIds::contains)) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_CHANNEL_EXISTS);
        }

        List<ChannelInfo> channels = channelDao.getByIds(channelIds);
        if (CollectionUtils.isEmpty(channels) || channels.size() != channelIds.size()) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND);
        }
        for (ChannelInfo channel : channels) {
            validateEntityCurrency(packId, channel.getId());

            CpanelPackCanalRecord record = new CpanelPackCanalRecord();
            record.setIdpack(packId.intValue());
            record.setIdcanal(channel.getId().intValue());
            record.setEstado(PackChannelStatus.PENDING_REQUEST.getId());
            packChannelDao.insert(record);
        }
    }

    @MySQLWrite
    public void updatePackChannel(Long packId, Long channelId, UpdatePackChannelDTO updatePackChannel) {
        CpanelPackCanalRecord packCanalRecord = getAndCheckPackChannel(packId, channelId);
        PackChannelConverter.toRecord(packCanalRecord, updatePackChannel);
        packChannelDao.update(packCanalRecord);
    }

    @MySQLWrite
    public void deletePackChannel(Long packId, Long channelId) {
        CpanelPackCanalRecord packChannel = getAndCheckPackChannel(packId, channelId);
        packChannelDao.delete(packChannel);
    }

    @MySQLWrite
    public void requestChannelApproval(Long packId, Long channelId, Long userId) {
        CpanelPackCanalRecord packChannel = getAndCheckPackChannel(packId, channelId);

        validateEntityCurrency(packId, channelId);

        if (!PackChannelStatus.PENDING_REQUEST.getId().equals(packChannel.getEstado())) {
            throw new OneboxRestException(MsEventErrorCode.REQUEST_NOT_PENDING);
        }

        packChannel.setEstado(PackChannelStatus.PENDING.getId());
        packChannelDao.update(packChannel);

        //TODO notification of request to user
    }

    private CpanelPackCanalRecord getAndCheckPackChannel(Long packId, Long channelId) {
        return packChannelDao.getPackChannel(packId, channelId)
                .orElseThrow(() -> new OneboxRestException(MsEventPackErrorCode.PACK_CHANNEL_NOT_FOUND));
    }

    private void validateEntityCurrency(Long packId, Long channelId) {
        PackDetailDTO pack = packService.getPackById(packId);
        Integer operatorId = entityDao.getEntityInfo(pack.getEntityId().intValue()).operatorId();
        CpanelEntidadRecord operator = entityDao.getById(operatorId);
        if (CommonUtils.isTrue(operator.getUsemulticurrency()) && isNotValidCurrency(channelId, pack)) {
            throw new OneboxRestException(MsEventPackErrorCode.PACK_CHANNEL_CURRENCY_NOT_MATCH);
        }
    }

    private boolean isNotValidCurrency(Long channelId, PackDetailDTO pack) {
        Long packMainItemEventId = packService.getPackMainItemEventId(pack.getId().intValue());
        CpanelEventoRecord mainEvent = eventDao.getById(packMainItemEventId.intValue());

        List<Long> currencies = channelCurrencyDao.getCurrenciesByChannelId(channelId);
        if (CollectionUtils.isEmpty(currencies)) {
            CpanelCanalRecord channel = channelDao.getById(channelId.intValue());
            return !channel.getCurrency().equals(mainEvent.getIdcurrency());
        }

        return currencies.stream()
                .noneMatch(currency -> currency == mainEvent.getIdcurrency().longValue());
    }

}
