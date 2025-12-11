package es.onebox.event.events.manager;

import es.onebox.event.events.converter.ChannelEventRangeConverter;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.surcharges.dao.RangeDao;
import es.onebox.event.surcharges.dto.Range;
import es.onebox.event.surcharges.dto.SurchargeLimitDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ChannelEventSurchargeManager {
    protected List<Range> ranges = new ArrayList<>();
    protected SurchargeLimitDTO limit;
    protected Boolean enabledRanges;
    protected RangeDao rangeDao;
    protected ChannelEventDao channelEventDao;
    protected Boolean allowChannelUseAlternativeCharges;

    public ChannelEventSurchargeManager(RangeDao rangeDao, ChannelEventDao channelEventDao){
        this.rangeDao = rangeDao;
        this.channelEventDao = channelEventDao;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    public SurchargeLimitDTO getLimit() {
        return limit;
    }

    public Boolean getEnabledRanges() {
        return enabledRanges;
    }

    public void setEnabledRanges(Boolean enabledRanges) {
        this.enabledRanges = enabledRanges;
    }

    public void setLimit(SurchargeLimitDTO limit) {
        this.limit = limit;
    }

    public Boolean isAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
    }

    protected abstract void insertChannelEventSurcharges(Integer channelEventId, List<Integer> rangeIds);
    protected abstract void insertChannelEventConfiguration(Integer channelEventId);

    public abstract void deleteChannelEventSurchargesAndRanges(Integer channelEventId);

    public void insert(Integer channelEventId) {
        this.generateMaxRange();

        List<Integer> insertedRangesIds = this.insertChannelEventRanges().stream().map(CpanelRangoRecord::getIdrango).collect(Collectors.toList());
        this.insertChannelEventSurcharges(channelEventId,insertedRangesIds);
        this.insertChannelEventConfiguration(channelEventId);
    }

    public List<CpanelRangoRecord> insertChannelEventRanges() {
        List<CpanelRangoRecord> insertedRangoRecords = new ArrayList<>();
        ChannelEventRangeConverter.fromChannelEventSurchargeManager(this)
                .forEach(cpanelRangoRecord -> {
                    CpanelRangoRecord record = rangeDao.insert(cpanelRangoRecord);
                    insertedRangoRecords.add(record);
                });

        return insertedRangoRecords;
    }

    public void sortRanges() {
        Collections.sort(ranges);
    }

    public void generateMaxRange() {
        this.sortRanges();
        int totalRanges = ranges.size() - 1;
        int index = 0;

        for (Range range : this.ranges) {
            if (index == totalRanges) {
                range.setTo(0D);
            } else {
                range.setTo(this.ranges.get(++index).getFrom());
            }
        }
    }

    public String getRangeName(int rangeIndex) {

        if(rangeIndex == 0) {
            return String.format(Locale.ENGLISH, "0.0-%.1f", this.ranges.get(rangeIndex).getTo());
        }

        if (rangeIndex == this.ranges.size() - 1) {
            return String.format(Locale.ENGLISH, "%.1f-0.0", this.ranges.get(rangeIndex).getFrom());
        }

        int nextRangeIndex = rangeIndex + 1;

        return String.format(Locale.ENGLISH, "%.1f-%.1f",
                this.ranges.get(rangeIndex).getFrom(),
                this.ranges.get(nextRangeIndex).getFrom());
    }

    public boolean isEmpty() {
        return this.ranges == null || this.ranges.isEmpty();
    }

    public boolean isInitialRangeDuplicated() {
        Set<Double> uniqueRanges = new HashSet<>();
        for (Range range : this.ranges) {
            if (!uniqueRanges.add(range.getFrom())) {
                return true;
            }
        }
        return false;
    }
}
