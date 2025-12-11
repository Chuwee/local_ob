package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.ChannelEventInvitationSurchargeRangeDao;
import es.onebox.event.events.dao.ChannelEventPromotionSurchargeRangeDao;
import es.onebox.event.events.dao.ChannelEventSurchargeRangeDao;
import es.onebox.event.events.dto.EventChannelSurchargesDTO;
import es.onebox.event.events.dto.EventChannelSurchargesListDTO;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.manager.ChannelEventSurchargeManager;
import es.onebox.event.events.manager.ChannelEventSurchargeManagerFactory;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.common.converters.CommonRangeConverter;
import es.onebox.event.surcharges.dto.Range;
import es.onebox.event.surcharges.dto.SurchargeLimitDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static es.onebox.core.utils.common.CommonUtils.isNull;
import static es.onebox.event.common.utils.RangesUtils.defaultRanges;

@Service
public class EventChannelSurchargesService {

    private final ChannelEventDao channelEventDao;
    private final ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;
    private final ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao;
    private final ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao;
    private final ChannelEventSurchargeManagerFactory channelEventSurchargeManagerFactory;

    @Autowired
    public EventChannelSurchargesService(ChannelEventDao channelEventDao, ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao,
                                         ChannelEventPromotionSurchargeRangeDao channelEventPromotionSurchargeRangeDao,
                                         ChannelEventInvitationSurchargeRangeDao channelEventInvitationSurchargeRangeDao,
                                         ChannelEventSurchargeManagerFactory channelEventSurchargeManagerFactory){
        this.channelEventDao = channelEventDao;
        this.channelEventSurchargeRangeDao = channelEventSurchargeRangeDao;
        this.channelEventPromotionSurchargeRangeDao = channelEventPromotionSurchargeRangeDao;
        this.channelEventInvitationSurchargeRangeDao = channelEventInvitationSurchargeRangeDao;
        this.channelEventSurchargeManagerFactory = channelEventSurchargeManagerFactory;
    }

    public List<EventChannelSurchargesDTO> getEventChannelSurcharges (Long eventId, Long channelId,  List<SurchargeTypeDTO> types){
        EventChannelRecord channelEvent = getChannelEvent(eventId, channelId);

        List<EventChannelSurchargesDTO> surcharges = new ArrayList<>();
        if(types == null || types.isEmpty()) {
           getSurchages(channelEvent, SurchargeTypeDTO.GENERIC, surcharges);
           getSurchages(channelEvent, SurchargeTypeDTO.PROMOTION, surcharges);
           if (channelEvent.getChannelType().equals(ChannelSubtype.BOX_OFFICE_ONEBOX.getIdSubtipo())) {
               getSurchages(channelEvent, SurchargeTypeDTO.INVITATION, surcharges);
           }
        } else {
            for(SurchargeTypeDTO type : types) {
                if(SurchargeTypeDTO.GENERIC.equals(type)) {
                    getSurchages(channelEvent, SurchargeTypeDTO.GENERIC, surcharges);
                } else if (SurchargeTypeDTO.PROMOTION.equals(type)) {
                    getSurchages(channelEvent, SurchargeTypeDTO.PROMOTION, surcharges);
                } else if (SurchargeTypeDTO.INVITATION.equals(type) &&
                        channelEvent.getChannelType().equals(ChannelSubtype.BOX_OFFICE_ONEBOX.getIdSubtipo()))  {
                    getSurchages(channelEvent, SurchargeTypeDTO.INVITATION, surcharges);
                }
            }
        }
        return surcharges;
    }

    public void setEventChannelSurcharges(Long eventId, Long channelId, EventChannelSurchargesListDTO surchargesRequest){
        if(surchargesRequest.stream().anyMatch(surchargeDTO -> surchargeDTO.getType() == null)){
            throw new OneboxRestException(MsEventErrorCode.TYPE_MANDATORY);
        }
        if (hasDuplicatedTypes(surchargesRequest)){
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_DUPLICATED);
        }

        EventChannelRecord channelEvent = getChannelEvent(eventId, channelId);

        if (isNotSupportedType(surchargesRequest, channelEvent)) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
        }

        surchargesRequest.forEach(surcharge -> setSurcharge(channelEvent.getId().intValue(), surcharge));
    }

    @MySQLRead
    private EventChannelRecord getChannelEvent(Long eventId, Long channelId){
        if (isNull(eventId)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (isNull(channelId)) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_ID_MANDATORY);
        }

        EventChannelRecord eventChannelDetail =  channelEventDao.getChannelEventDetailed(channelId.intValue(), eventId.intValue());
        if (isNull(eventChannelDetail)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }
        return eventChannelDetail;
    }

    @MySQLRead
    private void getSurchages(EventChannelRecord channelEvent, SurchargeTypeDTO type, List<EventChannelSurchargesDTO> surcharges){
        if(SurchargeTypeDTO.GENERIC.equals(type)) {
            List<CpanelRangoRecord> genericRangeRecords = channelEventSurchargeRangeDao.getByChannelEventId(channelEvent.getId().intValue());
            if (CollectionUtils.isEmpty(genericRangeRecords)) {
                genericRangeRecords.add(defaultRanges(channelEvent.getEventCurrencyId()));
            }
                EventChannelSurchargesDTO surchargesDTO = new EventChannelSurchargesDTO();
                surchargesDTO.setRanges(CommonRangeConverter.fromRecords(genericRangeRecords));
                surchargesDTO.setLimit(getSurchargeLimit(channelEvent.getRecommendedChannelSurcharges(), channelEvent.getMinSurcharge(), channelEvent.getMaxSurcharge()));
                surchargesDTO.setType(type);
                surchargesDTO.setEnabledRanges(!channelEvent.getUseEventSurcharges());
                surcharges.add(surchargesDTO);
        } else if (SurchargeTypeDTO.PROMOTION.equals(type)) {
            List<CpanelRangoRecord> promotionRangeRecords = channelEventPromotionSurchargeRangeDao.getByChannelEventId(channelEvent.getId().intValue());
            if(CollectionUtils.isEmpty(promotionRangeRecords)){
                promotionRangeRecords.add(defaultRanges(channelEvent.getEventCurrencyId()));
            }
            EventChannelSurchargesDTO surchargesDTO = new EventChannelSurchargesDTO();
            surchargesDTO.setRanges(CommonRangeConverter.fromRecords(promotionRangeRecords));
            surchargesDTO.setLimit((getSurchargeLimit(channelEvent.getRecommendedPromotionChannelSurcharges(), channelEvent.getMinPromotionSurcharge(), channelEvent.getMaxPromotionSurcharge())));
            surchargesDTO.setType(type);
            surchargesDTO.setEnabledRanges(!channelEvent.getUsePromotionEventSurcharges());
            surchargesDTO.setAllowChannelUseAlternativeCharges
                        (channelEvent.getAllowChannelUseAlternativeCharges() != null &&
                                channelEvent.getAllowChannelUseAlternativeCharges());
            surcharges.add(surchargesDTO);
        } else {
            List<CpanelRangoRecord> invitationRangeRecords = channelEventInvitationSurchargeRangeDao.getByChannelEventId(channelEvent.getId().intValue());
            if(CollectionUtils.isEmpty(invitationRangeRecords)){
                invitationRangeRecords.add(defaultRanges(channelEvent.getEventCurrencyId()));
            }
            EventChannelSurchargesDTO surchargesDTO = new EventChannelSurchargesDTO();
            surchargesDTO.setRanges(CommonRangeConverter.fromRecords(invitationRangeRecords));
            surchargesDTO.setLimit(getSurchargeLimit(channelEvent.getRecommendedInvChannelSurcharges(), channelEvent.getMinInvSurcharge(), channelEvent.getMaxInvSurcharge()));
            surchargesDTO.setType(type);
            surchargesDTO.setEnabledRanges(!channelEvent.getUseEventSurcharges());
            surcharges.add(surchargesDTO);
        }
    }

    private SurchargeLimitDTO getSurchargeLimit(Boolean limit, Double min, Double max) {
        SurchargeLimitDTO surchargeLimitDTO = new SurchargeLimitDTO();
        surchargeLimitDTO.setEnabled(limit);
        if (surchargeLimitDTO.getEnabled()) {
            surchargeLimitDTO.setMin(min);
            surchargeLimitDTO.setMax(max);
        }
        return surchargeLimitDTO;
    }

    private boolean hasDuplicatedTypes(List<EventChannelSurchargesDTO> surchargeListDTO){
        return surchargeListDTO.stream()
                .anyMatch(surchargeDTO ->
                        surchargeListDTO.stream()
                                .filter(singleSurcharge -> singleSurcharge.getType().equals(surchargeDTO.getType()))
                                .count() > 1);
    }

    private boolean isNotSupportedType(List<EventChannelSurchargesDTO> surchargeListDTO, EventChannelRecord channelEvent) {
        return surchargeListDTO.stream()
                .anyMatch(surcharge ->
                        SurchargeTypeDTO.INVITATION.equals(surcharge.getType())
                                && !channelEvent.getChannelType().equals(ChannelSubtype.BOX_OFFICE_ONEBOX.getIdSubtipo())
                );
    }


    @MySQLWrite
    private void setSurcharge(Integer channelEventId, EventChannelSurchargesDTO surcharge){
        ChannelEventSurchargeManager channelEventSurchargeManager = channelEventSurchargeManagerFactory.create(surcharge);
        validateSetSurcharge(channelEventSurchargeManager);

        channelEventSurchargeManager.deleteChannelEventSurchargesAndRanges(channelEventId);
        channelEventSurchargeManager.insert(channelEventId);
    }

    private void validateSetSurcharge(ChannelEventSurchargeManager channelEventSurchargeManager){
        if (channelEventSurchargeManager.isEmpty()) {
            throw new OneboxRestException(MsEventErrorCode.AT_LEAST_ONE_RANGE);
        }

        if (channelEventSurchargeManager.getRanges().stream().anyMatch(range -> Objects.isNull(range.getFrom()))) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_FROM_RANGE_MANDATORY);
        }

        if (channelEventSurchargeManager.getRanges().stream().anyMatch(range -> Objects.isNull(range.getFixed()) && Objects.isNull(range.getPercentage()))) {
            throw new OneboxRestException(MsEventErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        }

        if (channelEventSurchargeManager.isInitialRangeDuplicated()) {
            throw new OneboxRestException(MsEventErrorCode.SURCHARGE_DUPLICATED_FROM_RANGE);
        }

        if (channelEventSurchargeManager.getRanges().stream().noneMatch(range -> range.getFrom() == 0)) {
            throw new OneboxRestException(MsEventErrorCode.FROM_RANGE_ZERO_MANDATORY);
        }

        if (isMinGreaterThanMax(channelEventSurchargeManager.getRanges())) {
            throw new OneboxRestException(MsEventErrorCode.MIN_SURCHARGE_GREATER_THAN_MAX);
        }
        if (channelEventSurchargeManager.getLimit() != null && channelEventSurchargeManager.getLimit().getEnabled()) {
            if (channelEventSurchargeManager.getLimit().getMin() == null || channelEventSurchargeManager.getLimit().getMax() == null) {
                throw new OneboxRestException(MsEventErrorCode.WHEN_LIMIT_ENABLED_IS_MANDATORY);
            } else if (channelEventSurchargeManager.getLimit().getMin().compareTo(channelEventSurchargeManager.getLimit().getMax()) >= 0) {
                throw new OneboxRestException(MsEventErrorCode.WHEN_LIMIT_ENABLED_MIN_LOWER_THAN_MAX);
            }
        }

        if (channelEventSurchargeManager.isAllowChannelUseAlternativeCharges() == null) {
            channelEventSurchargeManager.setAllowChannelUseAlternativeCharges(false);
        }
    }

    private boolean isMinGreaterThanMax(List<Range> ranges) {
        return ranges.stream().filter(range -> range.getMin() != null && range.getMax() != null)
                .anyMatch(range -> range.getMin() > range.getMax());
    }
}

