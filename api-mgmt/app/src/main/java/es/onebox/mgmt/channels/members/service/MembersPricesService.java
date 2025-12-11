package es.onebox.mgmt.channels.members.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.members.eip.MembersBatchPricesMessage;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MembersPricesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembersPricesService.class);

    private final ChannelsHelper channelsHelper;
    private final AvetConfigRepository avetConfigRepository;
    private final DefaultProducer batchPricesProducer;
    @Autowired
    public MembersPricesService(ChannelsHelper channelsHelper, AvetConfigRepository avetConfigRepository,
                                @Qualifier("batchPricesProducer") DefaultProducer batchPricesProducer) {
        this.channelsHelper = channelsHelper;
        this.avetConfigRepository = avetConfigRepository;
        this.batchPricesProducer = batchPricesProducer;
    }

    public void runMembersBatchPrices(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }
        MembersBatchPricesMessage membersBatchPricesMessage = new MembersBatchPricesMessage();
        membersBatchPricesMessage.setChannelId(channelId);
        try {
            batchPricesProducer.sendMessage(membersBatchPricesMessage);
            LOGGER.error("[MEMBERS BATCH PRICES][{}] Running batch prices", channelId);
        } catch (Exception e) {
            LOGGER.error("[MEMBERS BATCH PRICES][{}] Error enqueueing batch prices", channelId, e);
        }
    }

}
