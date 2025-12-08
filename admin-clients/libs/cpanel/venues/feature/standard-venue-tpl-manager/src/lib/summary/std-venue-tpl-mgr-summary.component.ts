import { HeaderSummaryComponent } from '@admin-clients/cpanel/shared/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { map, startWith, withLatestFrom } from 'rxjs/operators';
import { VenueTemplateLabelGroupType } from '../models/label-group/venue-template-label-group-type.enum';
import { VenueTemplateEditorType } from '../models/venue-template-editor-type.model';
import { StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from '../services/standard-venue-template-changes.service';
import { StandardVenueTemplateFilterService } from '../services/standard-venue-template-filter.service';
import { StandardVenueTemplateSelectionService } from '../services/standard-venue-template-selection.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        HeaderSummaryComponent,
        LocalNumberPipe
    ],
    selector: 'app-std-venue-tpl-mgr-summary',
    templateUrl: './std-venue-tpl-mgr-summary.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class StdVenueTplMgrSummaryComponent {
    readonly #standardVenueTemplateSrv = inject(StandardVenueTemplateBaseService);
    readonly #standardVenueTemplateFilterSrv = inject(StandardVenueTemplateFilterService);
    readonly #standardVenueTemplateSelectionSrv = inject(StandardVenueTemplateSelectionService);
    readonly #standardVenueTemplateChangesSrv = inject(StandardVenueTemplateChangesService);

    readonly $editorType = input.required<VenueTemplateEditorType>({ alias: 'editorType' });

    readonly $showSoldColumn = computed(() =>
        this.$editorType() === VenueTemplateEditorType.sessionTemplate || this.$editorType() === VenueTemplateEditorType.sessionPackTemplate
    );

    readonly summaryData$ = combineLatest([
        this.#standardVenueTemplateSrv.getVenueItems$(),
        this.#standardVenueTemplateSrv.getLabelGroups$(),
        this.#standardVenueTemplateFilterSrv.getFilteredVenueItems$(), //trigger
        this.#standardVenueTemplateSelectionSrv.getSelectionQueue$().pipe(startWith(null)), //trigger
        this.#standardVenueTemplateChangesSrv.getModifiedItems$().pipe(startWith(null)) // triggers
    ]).pipe(
        map(([venueItems, labelGroups]) => {
            const nnzCapacity =
                (venueItems.nnzs &&
                    Array.from(venueItems.nnzs.values())
                        .map(nnz => nnz.capacity)
                        .reduce((previousValue, currentValue) => currentValue + previousValue, 0)
                ) || 0;
            const result = {
                total: nnzCapacity + venueItems.seats?.size || 0,
                selected: 0,
                filtered: 0,
                free: 0,
                sold: 0,
                locked: 0
            };
            labelGroups
                ?.filter(labelGroup => labelGroup.id === VenueTemplateLabelGroupType.state)
                .map(labelGroup => labelGroup?.labels)
                .find(labelGroup => !!labelGroup)
                .forEach(label => {
                    if (label.id === SeatStatus.free) {
                        result.free += label.count;
                    } else if (label.id === SeatStatus.promotorLocked) {
                        result.locked += label.count;
                    } else if (
                        label.id === SeatStatus.sold ||
                        label.id === SeatStatus.emitted ||
                        label.id === SeatStatus.gift ||
                        label.id === SeatStatus.booked
                    ) {
                        result.sold += label.count;
                    }
                });
            return result;
        }),
        withLatestFrom(this.#standardVenueTemplateSrv.getVenueItems$(), this.#standardVenueTemplateFilterSrv.getFilteredVenueItems$()),
        map(([result, venueItems, filteredVenueItems]) => {
            result.filtered = result.total;
            if (filteredVenueItems?.seats) {
                result.filtered -= filteredVenueItems.seats?.size;
            }
            if (filteredVenueItems?.nnzs) {
                Array.from(filteredVenueItems.nnzs)
                    .map(nnzIds => venueItems.nnzs.get(nnzIds))
                    .forEach(nnz => (result.filtered -= nnz.capacity));
            }
            return result;
        }),
        withLatestFrom(this.#standardVenueTemplateSrv.getVenueItems$(), this.#standardVenueTemplateSelectionSrv.getSelectedVenueItems$()),
        map(([result, venueItems, selectedVenueItems]) => {
            if (selectedVenueItems) {
                result.selected = selectedVenueItems?.seats?.size;
                Array.from(selectedVenueItems?.nnzs)
                    .map(nnzIds => venueItems.nnzs.get(nnzIds))
                    .forEach(nnz => (result.selected += nnz.capacity));
            }
            return result;
        })
    );
}
