package es.onebox.event.surcharges.manager;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventChangeSeatDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventInvitationDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventPromotionDao;
import es.onebox.event.surcharges.dao.RangeSurchargeEventSecondaryMarketDao;
import es.onebox.event.surcharges.dto.Range;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SurchargeManagerFactory {
    private RangeDao rangeDao;
    private RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao;
    private RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao;
    private RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao;
    private RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao;
    private RangeSurchargeEventDao rangeSurchargeEventDao;
    private EventDao eventDao;

    public SurchargeManagerFactory() {
    }

    @Autowired
    public SurchargeManagerFactory(RangeDao rangeDao,
                                   RangeSurchargeEventInvitationDao rangeSurchargeEventInvitationDao,
                                   RangeSurchargeEventPromotionDao rangeSurchargeEventPromotionDao,
                                   RangeSurchargeEventChangeSeatDao rangeSurchargeEventChangeSeatDao,
                                   RangeSurchargeEventDao rangeSurchargeEventDao,
                                   RangeSurchargeEventSecondaryMarketDao rangeSurchargeEventSecMktDao,
                                   EventDao eventDao) {
        this.rangeDao = rangeDao;
        this.rangeSurchargeEventInvitationDao = rangeSurchargeEventInvitationDao;
        this.rangeSurchargeEventPromotionDao = rangeSurchargeEventPromotionDao;
        this.rangeSurchargeEventChangeSeatDao = rangeSurchargeEventChangeSeatDao;
        this.rangeSurchargeEventDao = rangeSurchargeEventDao;
        this.rangeSurchargeEventSecMktDao = rangeSurchargeEventSecMktDao;
        this.eventDao = eventDao;
    }

    public SurchargeManager create(SurchargesDTO surchargesDTO) {
        SurchargeManager surchargeManager;

        switch (surchargesDTO.getType()) {
            case GENERIC:
                surchargeManager = new SurchargeManagerGeneric(rangeDao, rangeSurchargeEventDao, eventDao);
                break;
            case INVITATION:
                surchargeManager = new SurchargeManagerInvitation(rangeDao, rangeSurchargeEventInvitationDao, eventDao);
                break;
            case PROMOTION:
                surchargeManager = new SurchargeManagerPromotion(rangeDao, rangeSurchargeEventPromotionDao, eventDao);
                break;
            case CHANGE_SEAT:
                surchargeManager = new SurchargeManagerChangeSeat(rangeDao, rangeSurchargeEventChangeSeatDao, eventDao);
                break;
            case SECONDARY_MARKET_PROMOTER:
                surchargeManager = new SurchargeManagerSecondaryMarketPromoter(rangeDao, rangeSurchargeEventSecMktDao, eventDao);
                break;
            case SECONDARY_MARKET_CHANNEL:
                surchargeManager = new SurchargeManagerSecondaryMarketChannel(rangeDao, rangeSurchargeEventSecMktDao, eventDao);
                break;
            default:
                throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
        }

        List<Range> ranges = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(surchargesDTO.getRanges())) {
            surchargesDTO.getRanges().stream()
                    .map(rangeDTO -> new Range(
                            rangeDTO.getFrom(),
                            rangeDTO.getValues().getFixed(),
                            rangeDTO.getValues().getPercentage(),
                            rangeDTO.getValues().getMin(),
                            rangeDTO.getValues().getMax(),
                            rangeDTO.getCurrencyId()))
                    .forEach(ranges::add);

            surchargeManager.setRanges(ranges);
        }
        surchargeManager.setLimit(surchargesDTO.getLimit());
        surchargeManager.setAllowChannelUseAlternativeCharges(surchargesDTO.getAllowChannelUseAlternativeCharges());

        return surchargeManager;
    }
}
