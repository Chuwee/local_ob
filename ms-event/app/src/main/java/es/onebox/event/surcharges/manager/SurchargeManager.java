package es.onebox.event.surcharges.manager;

import es.onebox.event.events.dao.EventDao;
import es.onebox.event.common.converters.CommonRangeConverter;
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

public abstract class SurchargeManager {
    protected List<Range> ranges = new ArrayList<>();
    protected SurchargeLimitDTO limit;
    protected Boolean allowChannelUseAlternativeCharges;
    protected RangeDao rangeDao;
    protected EventDao eventDao;

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    public SurchargeManager(RangeDao rangeDao, EventDao eventDao) {
        this.rangeDao = rangeDao;
        this.eventDao = eventDao;
    }

    public SurchargeLimitDTO getLimit() {
        return limit;
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

    protected abstract void insertSurchages(Long eventId, List<Integer> rangeIds);
    protected abstract void insertLimits(Long eventId);

    public abstract void deleteSurchargesAndRanges(Long eventId);

    public void insert(Long eventId) {
        this.generateMaxRange();

        List<Integer> insertedRangesIds = this.insertRanges().stream()
                .map(CpanelRangoRecord::getIdrango)
                .collect(Collectors.toList());

        this.insertSurchages(eventId, insertedRangesIds);
        this.insertLimits(eventId);
    }

    public List<CpanelRangoRecord> insertRanges() {
        List<CpanelRangoRecord> insertedRangoRecords = new ArrayList<>();

        CommonRangeConverter.fromSurchargeManager(this)
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
