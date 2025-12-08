import { NotNumberedZone, occupiedStatus, Seat } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Injectable } from '@angular/core';
import { combineLatest, distinctUntilChanged, filter, map, Observable, switchMap, withLatestFrom } from 'rxjs';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';
import {
    prepareNNZsQuotaCountersToSave, prepareNNZsStatusAndBlockingReasonCountersToSave, prepareNNZsToSave
} from './nnz-save-functions.utils';

@Injectable()
export class StandardVenueTemplateSaveService {

    constructor(
        private _standardVenueTemplateState: StandardVenueTemplateState,
        private _venueTemplatesSrv: VenueTemplatesService
    ) { }

    isDirty$(): Observable<boolean> {
        return combineLatest([
            this._venueTemplatesSrv.venueTpl.get$(),
            this._standardVenueTemplateState.getModifiedItems$(),
            this._standardVenueTemplateState.getTemplateImage$()
        ])
            .pipe(
                filter(([venueTemplate, , ,]) => venueTemplate !== null),
                map(([venueTemplate, modifiedItems, modifiedImage]) =>
                    modifiedItems?.seats?.size > 0
                    || modifiedItems?.nnzs?.size > 0
                    || modifiedItems?.sectors?.size > 0
                    || (modifiedImage || null) !== (venueTemplate.image_url || null)
                ),
                distinctUntilChanged()
            );
    }

    getModifiedItemsToSave$(): Observable<{ seats: Seat[]; nnzs: NotNumberedZone[] }> {
        return this._standardVenueTemplateState.getLabelGroups$()
            .pipe(
                switchMap(labelGroups => this._standardVenueTemplateState.getModifiedItems$()
                    .pipe(
                        withLatestFrom(this._standardVenueTemplateState.getVenueItems$()),
                        map(([modifiedItems, venueItems]) => ({
                            seats: Array.from(modifiedItems?.seats || [], id => venueItems.seats.get(id)),
                            nnzs: Array.from(modifiedItems?.nnzs || [], id => venueItems.nnzs.get(id))
                        })),
                        map(modifiedItems => {
                            /* gate from sold items can be modified, but api breaks if sold status is sent,
                                so we set it to undefined in that case, please don't judge */
                            modifiedItems.seats.forEach(seat =>
                                seat.status = occupiedStatus.includes(seat.status) && seat.gateUpdateType ? undefined : seat.status);
                            modifiedItems = prepareNNZsStatusAndBlockingReasonCountersToSave(labelGroups, modifiedItems);
                            modifiedItems = prepareNNZsQuotaCountersToSave(labelGroups, modifiedItems);
                            modifiedItems = prepareNNZsToSave(modifiedItems);
                            return modifiedItems;
                        })
                    ))
            );
    }
}
