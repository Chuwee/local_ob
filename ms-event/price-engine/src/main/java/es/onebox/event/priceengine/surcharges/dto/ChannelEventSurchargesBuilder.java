/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.priceengine.surcharges.dto;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ignasi
 */
public class ChannelEventSurchargesBuilder {

    private List<CpanelRangoRecord> promoterMainSurcharges;
    private List<CpanelRangoRecord> promoterPromotionSurcharges;
    private List<CpanelRangoRecord> promoterInvitationSurcharges;
    private List<CpanelRangoRecord> promoterSecondaryMarketSurcharges;
    private List<CpanelRangoRecord> channelMainSurcharges;
    private List<CpanelRangoRecord> channelPromotionSurcharges;
    private List<CpanelRangoRecord> channelInvitationSurcharges;
    private List<CpanelRangoRecord> channelSecondaryMarketSurcharges;

    private ChannelEventSurchargesBuilder() {
    }

    public static ChannelEventSurchargesBuilder builder() {
        return new ChannelEventSurchargesBuilder();
    }

    public ChannelEventSurcharges build() {
        return buildSurcharges();
    }

    private ChannelEventSurcharges buildSurcharges() {
        ChannelEventSurcharges surcharges = new ChannelEventSurcharges();

        surcharges.setPromoter(new SurchargeRanges());
        surcharges.getPromoter().setMain(this.buildSurchargeRanges(this.promoterMainSurcharges));
        surcharges.getPromoter().setPromotion(this.buildSurchargeRanges(this.promoterPromotionSurcharges));
        surcharges.getPromoter().setInvitation(this.buildSurchargeRanges(this.promoterInvitationSurcharges));
        surcharges.getPromoter().setSecondaryMarket(this.buildSurchargeRanges(this.promoterSecondaryMarketSurcharges));

        surcharges.setChannel(new SurchargeRanges());
        surcharges.getChannel().setMain(this.buildSurchargeRanges(this.channelMainSurcharges));
        surcharges.getChannel().setPromotion(this.buildSurchargeRanges(this.channelPromotionSurcharges));
        surcharges.getChannel().setInvitation(this.buildSurchargeRanges(this.channelInvitationSurcharges));
        surcharges.getChannel().setSecondaryMarket(this.buildSurchargeRanges(this.channelSecondaryMarketSurcharges));

        return surcharges;
    }

    private List<SurchargeRange> buildSurchargeRanges(List<CpanelRangoRecord> rangeRecords) {
        if (CollectionUtils.isNotEmpty(rangeRecords)) {
            List<SurchargeRange> ranges = new ArrayList<>();
            for (CpanelRangoRecord rangeRecord : rangeRecords) {
                SurchargeRange range = new SurchargeRange();
                range.setFrom(rangeRecord.getRangominimo());
                range.setTo(NumberUtils.isZero(rangeRecord.getRangomaximo()) ? Double.MAX_VALUE : rangeRecord.getRangomaximo());
                range.setFixedValue(rangeRecord.getValor());
                range.setPercentageValue(rangeRecord.getPorcentaje());
                range.setMinimumValue(rangeRecord.getValorminimo());
                range.setMaximumValue(rangeRecord.getValormaximo());

                ranges.add(range);
            }
            return ranges;
        } else {
            return Collections.emptyList();
        }
    }

    public ChannelEventSurchargesBuilder promoterMainSurcharges(final List<CpanelRangoRecord> value) {
        this.promoterMainSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder promoterPromotionSurcharges(final List<CpanelRangoRecord> value) {
        this.promoterPromotionSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder promoterInvitationSurcharges(final List<CpanelRangoRecord> value) {
        this.promoterInvitationSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder promoterSecondaryMarketSurcharges(final List<CpanelRangoRecord> value) {
        this.promoterSecondaryMarketSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder channelMainSurcharges(final List<CpanelRangoRecord> value) {
        this.channelMainSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder channelPromotionSurcharges(final List<CpanelRangoRecord> value) {
        this.channelPromotionSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder channelInvitationSurcharges(final List<CpanelRangoRecord> value) {
        this.channelInvitationSurcharges = value;
        return this;
    }

    public ChannelEventSurchargesBuilder channelSecondaryMarketSurcharges(final List<CpanelRangoRecord> value) {
        this.channelSecondaryMarketSurcharges = value;
        return this;
    }

}
