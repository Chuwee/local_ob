import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { StdVenueTplService, VENUE_MAP_SERVICE, VenueMapService } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit, Optional } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, startWith, take, takeUntil } from 'rxjs/operators';
import { VenueTemplateLabel } from '../../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateLabelGroupType } from '../../models/label-group/venue-template-label-group-type.enum';
import { NnzPartialApplyData } from '../../models/nnz-partial-apply-data.model';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { StandardVenueTemplateFilterService } from '../../services/standard-venue-template-filter.service';
import { StandardVenueTemplatePartialChangesService } from '../../services/standard-venue-template-partial-changes.service';
import { StandardVenueTemplateSelectionService } from '../../services/standard-venue-template-selection.service';
import { SourceLabelCounter } from './source-label-counter.model';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule
    ],
    selector: 'app-nnz-partial-apply-dialog',
    templateUrl: './nnz-partial-apply-dialog.component.html',
    styleUrls: ['./nnz-partial-apply-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NnzPartialApplyDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    form: UntypedFormGroup;
    targetLabels$: Observable<SourceLabelCounter[]>;

    constructor(
        private _dialogRef: MatDialogRef<NnzPartialApplyDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: NnzPartialApplyData,
        private _fb: UntypedFormBuilder,
        private _translateSrv: TranslateService,
        private _stdVenueTplSrv: StdVenueTplService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        private _standardVenueTemplateFilterSrv: StandardVenueTemplateFilterService,
        private _standardVenueTemplateSelectionSrv: StandardVenueTemplateSelectionService,
        private _standardVenueTemplatePartialChangesSrv: StandardVenueTemplatePartialChangesService
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.createForm();
        this.targetLabels$ = this.getAvailableTargetLabels();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    commit(): void {
        this._standardVenueTemplatePartialChangesSrv.partialApplyToNNZ(
            this.data.nnz,
            (this.form.get('sourceStatus').value as SourceLabelCounter).label,
            this.data.label,
            Number(this.form.get('count').value)
        );
        this._standardVenueTemplateFilterSrv.filterLabel();
        this._standardVenueTemplateSelectionSrv.checkFilteredSelectedItems();
        this.close(true);
    }

    close(success = false): void {
        this._dialogRef.close(success);
    }

    private getAvailableTargetLabels(): Observable<SourceLabelCounter[]> {
        return this._standardVenueTemplateSrv.getLabelGroups$()
            .pipe(
                take(1),
                map(labelGroups => {
                    const sourceLabelCounters: SourceLabelCounter[] = [];
                    const statusLabels = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.state).labels;
                    this.data.nnz.statusCounters.forEach(statusCounter =>
                        sourceLabelCounters.push(this.getLabelCounter(statusCounter.status, statusCounter.count, statusLabels))
                    );
                    const brLabels = labelGroups.find(lg => lg.id === VenueTemplateLabelGroupType.blockingReason)?.labels || [];
                    if (this.data.nnz.blockingReasonCounters) {
                        this.data.nnz.blockingReasonCounters.forEach(brCounter =>
                            sourceLabelCounters.push(this.getLabelCounter(brCounter.blocking_reason.toString(), brCounter.count, brLabels))
                        );
                    }
                    return sourceLabelCounters.filter(labelCounter => labelCounter !== null);
                })
            );
    }

    private getLabelCounter(labelId: string, count: number, labels: VenueTemplateLabel[]): SourceLabelCounter {
        let resultCounter: SourceLabelCounter = null;
        if (this.data.label.id !== labelId) {
            const newCounterLabel = labels.find(label => label.id === labelId);
            if (newCounterLabel && !newCounterLabel.disabled && newCounterLabel.count > 0) {
                resultCounter = {
                    label: newCounterLabel,
                    count
                };
            }
        }
        return resultCounter;
    }

    private createForm(): void {
        this.form = this._fb.group({
            sector: null,
            nnz: this.data.nnz.name,
            nnzCapacity: this.data.nnz.capacity,
            targetStatus: this.data.label.literal || this._translateSrv.instant(this.data.label.literalKey),
            sourceStatus: [null, Validators.required],
            allTickets: [true, Validators.required],
            count: { value: null, disabled: true }
        });
        // sector name set
        this._venueMapSrv.getVenueMap$()
            .pipe(filter(venueMap => venueMap !== null), take(1))
            .subscribe(vm => this.form.get('sector').setValue(vm.sectors.find(sector => sector.id === this.data.nnz.sector).name));
        // count input mechanisms
        combineLatest([
            this.form.get('sourceStatus').valueChanges.pipe(startWith(null as SourceLabelCounter)),
            this.form.get('allTickets').valueChanges.pipe(startWith([this.form.get('allTickets').value]))
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([sourceCounter, allTickets]) => {
                const countControl = this.form.get('count');
                const labelCount = (sourceCounter && (sourceCounter as SourceLabelCounter).count) || 0;
                if (allTickets) {
                    countControl.disable();
                    countControl.setValidators(null);
                    countControl.setValue(labelCount);
                } else {
                    countControl.enable();
                    if (sourceCounter) {
                        countControl.setValidators([Validators.required, Validators.min(1), Validators.max(labelCount || 1)]);
                    }
                    countControl.setValue(0);
                }
            });
    }
}
