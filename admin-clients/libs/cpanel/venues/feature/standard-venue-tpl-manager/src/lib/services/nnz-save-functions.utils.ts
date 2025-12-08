import { cloneObject } from '@admin-clients/shared/utility/utils';
import {
    BlockingReasonCounter, Counter, NotNumberedZone, QuotaCounter, Seat, SeatStatus, StatusCounter, VenueTemplateItemType
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';

//TODO: move this code to standard-venue-template-save.service

export function prepareNNZsStatusAndBlockingReasonCountersToSave(
    labelGroups: VenueTemplateLabelGroup[],
    modifiedItems: { seats: Seat[]; nnzs: NotNumberedZone[] }
): { seats: Seat[]; nnzs: NotNumberedZone[] } {
    const stateLabelGroup = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.state);
    if (stateLabelGroup.nnzPartialApply) {
        modifiedItems.nnzs = modifiedItems.nnzs.map(modifiedNNZ => prepareNNZStatusAndBlockingReasonCountersToSave(modifiedNNZ));
    }
    return modifiedItems;
}

function prepareNNZStatusAndBlockingReasonCountersToSave(nnz: NotNumberedZone): NotNumberedZone {
    const clonedNNZ: NotNumberedZone = Object.assign({}, nnz);
    const loserCounters: (StatusCounter | BlockingReasonCounter)[] = [];
    const winnerStatusCounters: StatusCounter[] = [];
    const resultStatusCounters: StatusCounter[] = [];
    const winnerBRCounters: BlockingReasonCounter[] = [];
    const resultBRCounters: BlockingReasonCounter[] = [];
    distributeStatusAndBlockingReasonCountersLosersAndWinners(clonedNNZ.statusCounters, winnerStatusCounters, loserCounters);
    distributeStatusAndBlockingReasonCountersLosersAndWinners(clonedNNZ.blockingReasonCounters, winnerBRCounters, loserCounters);
    setStatusAndBlockingReasonCountersWinnersGainings(winnerStatusCounters, loserCounters, resultStatusCounters);
    setStatusAndBlockingReasonCountersWinnersGainings(winnerBRCounters, loserCounters, resultBRCounters);
    clonedNNZ.statusCounters = resultStatusCounters;
    clonedNNZ.blockingReasonCounters = resultBRCounters;
    return clonedNNZ;
}

function distributeStatusAndBlockingReasonCountersLosersAndWinners(
    counters: (StatusCounter | BlockingReasonCounter)[],
    winnerCounters: (StatusCounter | BlockingReasonCounter)[],
    loserCounters: (StatusCounter | BlockingReasonCounter)[]
): void {
    counters.forEach(counter => {
        if (counter.initialCount !== undefined) {
            // promotor locked state is not used, must be setted or unsetted by blocking reason
            if (!(counter as StatusCounter).status || (counter as StatusCounter).status !== SeatStatus.promotorLocked) {
                if (counter.count < counter.initialCount) {
                    loserCounters.push(counter);
                } else if (counter.count > counter.initialCount) {
                    winnerCounters.push(counter);
                }
            }
        }
    });
}

function setStatusAndBlockingReasonCountersWinnersGainings(
    winnerCounters: (StatusCounter | BlockingReasonCounter)[],
    loserCounters: (StatusCounter | BlockingReasonCounter)[],
    resultCounters: (StatusCounter | BlockingReasonCounter)[]
): void {
    winnerCounters.forEach(counter => {
        while (counter.initialCount < counter.count) {
            const counterToEat = loserCounters[0];
            const availableCountToEat = counterToEat.initialCount - counterToEat.count;
            const requiredCountIncrease = counter.count - counter.initialCount;
            if (availableCountToEat >= requiredCountIncrease) {
                counterToEat.initialCount -= requiredCountIncrease;
                counter.initialCount += requiredCountIncrease;
                resultCounters.push(getStatusAndBlockingReasonCounterToSave(counter, counterToEat, requiredCountIncrease));
            } else if (availableCountToEat < requiredCountIncrease) {
                counterToEat.initialCount -= availableCountToEat;
                counter.initialCount += availableCountToEat;
                resultCounters.push(getStatusAndBlockingReasonCounterToSave(counter, counterToEat, availableCountToEat));
            }
            if (counterToEat.count === counterToEat.initialCount) {
                loserCounters.splice(0, 1);
            }
        }
    });
}

function getStatusAndBlockingReasonCounterToSave(
    baseCounter: StatusCounter | BlockingReasonCounter,
    sourceCounter: StatusCounter | BlockingReasonCounter,
    count: number
): StatusCounter | BlockingReasonCounter {
    const getCounterIdentifier = (counter: StatusCounter | BlockingReasonCounter): string => {
        if ((counter as StatusCounter).status) {
            return (counter as StatusCounter).status;
        } else {
            return (counter as BlockingReasonCounter).blocking_reason.toString();
        }
    };
    if ((baseCounter as StatusCounter).status) {
        return {
            itemType: VenueTemplateItemType.notNumberedZoneStatusCounter,
            status: getCounterIdentifier(baseCounter),
            source: getCounterIdentifier(sourceCounter),
            count
        } as StatusCounter;
    } else {
        return {
            itemType: VenueTemplateItemType.notNumberedZoneBlockingReasonCounter,
            blocking_reason: Number(getCounterIdentifier(baseCounter)),
            source: getCounterIdentifier(sourceCounter),
            count
        } as BlockingReasonCounter;
    }
}

export function prepareNNZsQuotaCountersToSave(
    labelGroups: VenueTemplateLabelGroup[],
    modifiedItems: { seats: Seat[]; nnzs: NotNumberedZone[] }
): { seats: Seat[]; nnzs: NotNumberedZone[] } {
    const quotaLabelGroup = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.quota);
    if (quotaLabelGroup?.nnzPartialApply) {
        modifiedItems.nnzs = modifiedItems.nnzs.map(modifiedNNZ => prepareNNZQuotaCountersToSave(modifiedNNZ));
    }
    return modifiedItems;
}

function prepareNNZQuotaCountersToSave(nnz: NotNumberedZone): NotNumberedZone {
    const clonedNNZ: NotNumberedZone = Object.assign({}, nnz);
    const loserCounters: QuotaCounter[] = [];
    const winnerCounters: QuotaCounter[] = [];
    const resultCounters: QuotaCounter[] = [];
    nnz.quotaCounters;
    distributeQuotaCountersLosersAndWinners(nnz.quotaCounters, winnerCounters, loserCounters);
    setQuotaCountersWinnersGainings(winnerCounters, loserCounters, resultCounters);
    clonedNNZ.quotaCounters = resultCounters;
    return clonedNNZ;
}

function distributeQuotaCountersLosersAndWinners(
    counters: QuotaCounter[],
    winnerCounters: QuotaCounter[],
    loserCounters: QuotaCounter[]
): void {
    counters.forEach(counter => {
        if (counter.initialCount !== undefined) {
            if (counter.count < counter.initialCount) {
                loserCounters.push(counter);
            } else if (counter.count > counter.initialCount) {
                winnerCounters.push(counter);
            }
        }
    });
}

function setQuotaCountersWinnersGainings(
    winnerCounters: QuotaCounter[],
    loserCounters: QuotaCounter[],
    resultCounters: QuotaCounter[]
): void {
    winnerCounters.forEach(counter => {
        while (counter.initialCount < counter.count) {
            const counterToEat = loserCounters[0];
            const availableCountToEat = counterToEat.initialCount - counterToEat.count;
            const requiredCountIncrease = counter.count - counter.initialCount;
            if (availableCountToEat >= requiredCountIncrease) {
                counterToEat.initialCount -= requiredCountIncrease;
                counter.initialCount += requiredCountIncrease;
                resultCounters.push(getQuotaCounterToSave(counter, counterToEat, requiredCountIncrease));
            } else if (availableCountToEat < requiredCountIncrease) {
                counterToEat.initialCount -= availableCountToEat;
                counter.initialCount += availableCountToEat;
                resultCounters.push(getQuotaCounterToSave(counter, counterToEat, availableCountToEat));
            }
            if (counterToEat.count === counterToEat.initialCount) {
                loserCounters.splice(0, 1);
            }
        }
    });
}

function getQuotaCounterToSave(
    baseCounter: QuotaCounter,
    sourceCounter: QuotaCounter,
    count: number
): QuotaCounter {
    return {
        itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters,
        quota: baseCounter.quota,
        source: sourceCounter.quota.toString(),
        count
    };
}

// makes a copy of the not numbered zones only with the fields that has changed, optimizing the save process
export function prepareNNZsToSave(modifiedItems: { seats: Seat[]; nnzs: NotNumberedZone[] }): { seats: Seat[]; nnzs: NotNumberedZone[] } {
    // cleans not modified values from the not numbered zones (makes a copy)
    return {
        seats: modifiedItems.seats,
        nnzs: modifiedItems.nnzs?.length
            && modifiedItems.nnzs.map(nnz => {
                const resultNNZ: NotNumberedZone = cloneObject(nnz);
                resultNNZ.statusCounters = getCounterToSave(nnz, resultNNZ.statusCounters);
                resultNNZ.blockingReasonCounters = getCounterToSave(nnz, resultNNZ.blockingReasonCounters);
                resultNNZ.quotaCounters = getCounterToSave(nnz, resultNNZ.quotaCounters);
                resultNNZ.priceType = resultNNZ.priceType !== resultNNZ.record?.initialState.priceType ? resultNNZ.priceType : undefined;
                resultNNZ.visibility =
                    resultNNZ.visibility !== resultNNZ.record?.initialState.visibility ? resultNNZ.visibility : undefined;
                resultNNZ.accessibility =
                    resultNNZ.accessibility !== resultNNZ.record?.initialState.accessibility ? resultNNZ.accessibility : undefined;
                resultNNZ.gate = resultNNZ.gate !== resultNNZ.record?.initialState.gate ? resultNNZ.gate : undefined;
                resultNNZ.record = undefined;
                return resultNNZ;
            })
    };
}

function getCounterToSave<T extends Counter>(nnz: NotNumberedZone, counters: T[]): T[] {
    if (counters?.length) {
        const singleCounter = counters?.length === 1 && counters[0];
        if (singleCounter?.count === nnz.capacity && !singleCounter.source) {
            // this configuration of the nnz is given when partial assignment is not available, venue, promoter and event template
            return counters;
        } else { // the required counters to save on partial assignments always have source
            return counters.filter(sc => !!sc.source);
        }
    } else {
        return undefined;
    }
}

