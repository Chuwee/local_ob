package es.onebox.event.events.manager;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.ChannelEventInvitationSurchargeRangeDao;
import es.onebox.event.events.dao.ChannelEventPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.ChannelEventSurchargeRangeDao;
import es.onebox.event.events.dto.EventChannelSurchargesDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dto.Range;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChannelEventSurchargeManagerFactory {
    private RangeDao rangeDao;
    private ChannelEventDao channelEventDao;
    private ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;
    private ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao;
    private ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao;

    public ChannelEventSurchargeManagerFactory() {
    }

    @Autowired
    public ChannelEventSurchargeManagerFactory(RangeDao rangeDao,
                                               ChannelEventDao channelEventDao,
                                               ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao,
                                               ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao,
                                               ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao) {
        this.rangeDao = rangeDao;
        this.channelEventDao = channelEventDao;
        this.channelEventSurchargeRangeDao = channelEventSurchargeRangeDao;
        this.channelEventPromotionSurchargeRangeDao = channelEventPromotionSurchargeRangeDao;
        this.channelEventInvitationSurchargeRangeDao = channelEventInvitationSurchargeRangeDao;
    }

    public ChannelEventSurchargeManager create(EventChannelSurchargesDTO surcharge){
        ChannelEventSurchargeManager channelEventSurchargeManager;

        switch (surcharge.getType()){
            case GENERIC:
                channelEventSurchargeManager = new ChannelEventSurchargeManagerGeneric(rangeDao, channelEventDao, channelEventSurchargeRangeDao);
                channelEventSurchargeManager.setEnabledRanges(surcharge.getEnabledRanges());
                break;
            case PROMOTION:
                channelEventSurchargeManager = new ChannelEventSurchargeManagerPromotion(rangeDao, channelEventDao,channelEventPromotionSurchargeRangeDao);
                channelEventSurchargeManager.setEnabledRanges(surcharge.getEnabledRanges());
                break;
            case INVITATION:
                channelEventSurchargeManager = new ChannelEventSurchargeManagerInvitation(rangeDao, channelEventDao,channelEventInvitationSurchargeRangeDao);
                break;
            default:
                throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
        }

        List<Range> ranges = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(surcharge.getRanges())) {
            surcharge.getRanges().stream()
                    .map(rangeDTO -> new Range(
                            rangeDTO.getFrom(),
                            rangeDTO.getValues().getFixed(),
                            rangeDTO.getValues().getPercentage(),
                            rangeDTO.getValues().getMin(),
                            rangeDTO.getValues().getMax(),
                            rangeDTO.getCurrencyId()))
                    .forEach(ranges::add);

            channelEventSurchargeManager.setRanges(ranges);
        }
        channelEventSurchargeManager.setLimit(surcharge.getLimit());
        channelEventSurchargeManager.setAllowChannelUseAlternativeCharges(surcharge.getAllowChannelUseAlternativeCharges());

        return channelEventSurchargeManager;
    }
}
