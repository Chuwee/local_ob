import { NotNumberedZone, Seat, VmItemsSet } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable } from '@angular/core';
import { combineLatest, take } from 'rxjs';
import { VenueTemplateLabel } from '../models/label-group/venue-template-label-group-list.model';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';
import { quotaPartialApply, statusBlockingReasonPartialApply } from './nnz-change-functions.utils';

@Injectable()
export class StandardVenueTemplatePartialChangesService {

    constructor(
        private _standardVenueTemplateState: StandardVenueTemplateState
    ) {
    }

    partialApplyToNNZ(nnz: NotNumberedZone, sourceLabel: VenueTemplateLabel, targetLabel: VenueTemplateLabel, count: number): void {
        combineLatest([
            this._standardVenueTemplateState.getLabelGroups$(),
            this._standardVenueTemplateState.getModifiedItems$(),
            this._standardVenueTemplateState.getVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([labelGroups, modifiedItems, venueItems]) => {
                statusBlockingReasonPartialApply(nnz, sourceLabel, targetLabel, count, labelGroups);
                this.setModifiedItems(modifiedItems, venueItems, nnz);
            });
    }

    quotaPartialApply(
        targetLabelSource: LabelCounter,
        modifiedCounterSources: LabelCounter[],
        nnz: NotNumberedZone
    ): void {
        combineLatest([
            this._standardVenueTemplateState.getLabelGroups$(),
            this._standardVenueTemplateState.getModifiedItems$(),
            this._standardVenueTemplateState.getVenueItems$()
        ]).pipe(take(1))
            .subscribe(([labelGroups, modifiedItems, venueItems]) => {
                quotaPartialApply(targetLabelSource, modifiedCounterSources, labelGroups, nnz);
                this.setModifiedItems(modifiedItems, venueItems, nnz);
            });
    }

    private setModifiedItems(
        modifiedItems: VmItemsSet,
        venueItems: { seats: Map<number, Seat>; nnzs: Map<number, NotNumberedZone> },
        nnz: NotNumberedZone
    ): void {
        const newModifiedNNZs = new Map<number, NotNumberedZone>();

        if (modifiedItems?.nnzs?.size) {
            Array.from((modifiedItems.nnzs)).forEach(nnzId => newModifiedNNZs.set(nnzId, venueItems.nnzs.get(nnzId)));
        }

        if (!newModifiedNNZs.has(nnz.id)) {
            newModifiedNNZs.set(nnz.id, nnz);
        }

        if (newModifiedNNZs.size > 0 || modifiedItems?.nnzs?.size) {
            const resultModifiedNNZs = new Set<number>();
            newModifiedNNZs.forEach(modifiedNNZ => resultModifiedNNZs.add(modifiedNNZ.id));
            this._standardVenueTemplateState.setModifiedItems({
                seats: modifiedItems?.seats,
                nnzs: resultModifiedNNZs,
                sectors: modifiedItems?.sectors
            });
        }
    }
}

interface LabelCounter {
    label: VenueTemplateLabel;
    count: number;
}
