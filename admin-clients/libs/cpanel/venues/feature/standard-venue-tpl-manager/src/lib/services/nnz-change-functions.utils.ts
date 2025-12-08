import {
    BlockingReasonCounter, NotNumberedZone, QuotaCounter, SeatStatus, StatusCounter, VenueTemplateItemType
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateLabel, VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';

//TODO: move this code to standard-venue-template-partial-changes.service

export function statusBlockingReasonPartialApply(
    nnz: NotNumberedZone, sourceLabel: VenueTemplateLabel, targetLabel: VenueTemplateLabel, count: number,
    labelGroups: VenueTemplateLabelGroup[]
): void {
    const alterCounterCount = (element: StatusCounter | BlockingReasonCounter, countChange: number): void => {
        if (element.initialCount === undefined) {
            element.initialCount = element.count;
        }
        element.count += countChange;
    };
    const stLabelGroup = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.state);
    const brLabelGroup = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.blockingReason);
    // update selected labels, simple
    targetLabel.count += count;
    sourceLabel.count -= count;
    // update nnz counters
    alterCounterCount(getNNZStatusOrBlockingReasonCounter(nnz, targetLabel.id), count);
    alterCounterCount(getNNZStatusOrBlockingReasonCounter(nnz, sourceLabel.id), count * -1);
    // crazy stuff, changes from locked to other state and vice versa requires changes to locked status counter too, hold on
    let lockedStateChangeCount = 0;
    if (stLabelGroup.labels.indexOf(targetLabel) !== -1) {
        if (stLabelGroup.labels.indexOf(sourceLabel) === -1) { // blocking reason source to state target
            lockedStateChangeCount = count * -1;
        }
    } else if (brLabelGroup.labels.indexOf(sourceLabel) === -1) { // state source to blocking reason target
        lockedStateChangeCount = count;
    }
    alterCounterCount(getNNZStatusOrBlockingReasonCounter(nnz, SeatStatus.promotorLocked), lockedStateChangeCount);
    labelGroups
        .find(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.state) // finds state label group
        .labels // state label group labels
        .find(lg => lg.id === SeatStatus.promotorLocked) // finds promotor locked status label
        .count += lockedStateChangeCount;
}

function getNNZStatusOrBlockingReasonCounter(nnz: NotNumberedZone, labelId: string): (StatusCounter | BlockingReasonCounter) {
    const seatStatus = Object.values(SeatStatus).find(ss => ss === labelId);
    let result: StatusCounter | BlockingReasonCounter;
    if (seatStatus) {
        result = nnz.statusCounters.find(sc => sc.status === labelId);
        if (!result) {
            result = {
                itemType: VenueTemplateItemType.notNumberedZoneStatusCounter,
                status: seatStatus,
                count: 0,
                initialCount: 0
            } as StatusCounter;
            nnz.statusCounters.push(result);
        }
    } else {
        if (!nnz.blockingReasonCounters) {
            nnz.blockingReasonCounters = [];
        }
        result = nnz.blockingReasonCounters.find(brc => brc.blocking_reason.toString() === labelId);
        if (!result) {
            result = {
                itemType: VenueTemplateItemType.notNumberedZoneBlockingReasonCounter,
                blocking_reason: Number(labelId),
                count: 0,
                initialCount: 0
            } as BlockingReasonCounter;
            nnz.blockingReasonCounters.push(result);
        }
    }
    return result;
}

export function quotaPartialApply(
    targetLabelCounter: LabelCounter,
    sourceLabelCounters: LabelCounter[],
    labelGroups: VenueTemplateLabelGroup[],
    nnz: NotNumberedZone
): void {
    modifyQuotaLabels(targetLabelCounter, sourceLabelCounters, labelGroups);
    modifyQuotaCounters(targetLabelCounter, sourceLabelCounters, nnz);
}

function modifyQuotaLabels(
    targetLabel: LabelCounter,
    sources: LabelCounter[],
    labelGroups: VenueTemplateLabelGroup[]
): void {
    const foundLabelGroup = labelGroups.find(labelGroup => labelGroup.id === targetLabel.label.labelGroupId);
    sources.forEach(modifiedCounterSource => {
        const sourceLabel = foundLabelGroup.labels.find(label => label.id === modifiedCounterSource.label.id);
        targetLabel.label.count += modifiedCounterSource.count;
        sourceLabel.count -= modifiedCounterSource.count;
    });
}

function modifyQuotaCounters(
    targetLabel: LabelCounter,
    sources: LabelCounter[],
    nnz: NotNumberedZone
): void {
    const alterCounterCount = (element: QuotaCounter, countChange: number): void => {
        if (element.initialCount === undefined) {
            element.initialCount = element.count;
        }
        element.count += countChange;
        element.available += countChange;
    };
    alterCounterCount(getNNZQuotaCounter(nnz, targetLabel.label.id), sources.map(source => source.count).reduce((a, b) => a + b, 0));
    sources.forEach(modifiedCounterSource => {
        alterCounterCount(getNNZQuotaCounter(nnz, modifiedCounterSource.label.id), modifiedCounterSource.count * -1);
    });
}

function getNNZQuotaCounter(nnz: NotNumberedZone, labelId: string): QuotaCounter {
    let quotaCounter = nnz.quotaCounters.find(quotaCounter => quotaCounter.quota.toString() === labelId);
    if (!quotaCounter) {
        quotaCounter = {
            itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters,
            quota: +labelId,
            count: 0,
            initialCount: 0,
            available: 0
        };
        nnz.quotaCounters.push(quotaCounter);
    }
    return quotaCounter;
}

interface LabelCounter {
    label: VenueTemplateLabel;
    count: number;
}
