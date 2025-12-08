import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject } from 'rxjs';
import { filter, map, take, takeUntil } from 'rxjs/operators';
import { venueTplEditorSeatMatrixLimits } from '../../../models/venue-tpl-editor-seat-matrix-conf.model';
import { VenueTplEditorSeatMatrixService } from '../../../venue-tpl-editor-seat-matrix.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule,
        SelectSearchComponent
    ],
    selector: 'app-venue-tpl-editor-seat-matrix-form-matrix',
    templateUrl: './venue-tpl-editor-seat-matrix-form-matrix.component.html',
    styleUrls: ['../../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixFormMatrixComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);

    readonly form = this._fb.group({
        sector: [0, [Validators.required]],
        rows: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.rows)]],
        seats: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.seats)]],
        seatsSize: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.size)]],
        seatsDistance: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.seatsDistance)]],
        rowsDistance: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.rowsDistance)]]
    });

    readonly sectors$ = this._venueMapSrv.getVenueMap$().pipe(
        filter(Boolean),
        map(venueMap => venueMap.sectors.filter(sector => !sector.delete))
    );

    ngOnInit(): void {
        //form data incoming
        this._seatMatrixSrv.getSeatMatrixConf$().pipe(takeUntil(this._onDestroy))
            .subscribe(config => {
                this.form.setValue(config.matrix, { emitEvent: false });
                this.changeControlEnabled(this.form.controls.seats, config.seats.numTracks === 1);
            });
        // form data outgoing
        this.form.valueChanges.pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._seatMatrixSrv.fixMinMaxFormValues(this.form);
                if (this.form.valid) {
                    this._seatMatrixSrv.mergeSeatMatrixConf({ matrix: this.form.getRawValue() });
                }
            });
        // initial sector selection
        combineLatest([this.sectors$, this._viewsSrv.getViewData$()])
            .pipe(take(1))
            .subscribe(([sectors, viewData]) => {
                const viewSectors = sectors.filter(sector => {
                    const viewId = viewData.view.id;
                    return sector.rows.flatMap(row => row.seats).some(seat => seat.view === viewId)
                        || sector.notNumberedZones.some(nnz => nnz.view === viewId);
                });
                // if view has any sector, it sets the last sector from view.
                if (viewSectors.length) {
                    this.form.controls.sector.setValue(viewSectors[viewSectors.length - 1].id);
                } else {
                    const emptySectors = sectors.filter(sector => !sector.rows?.length && !sector.notNumberedZones?.length);
                    if (emptySectors.length) {
                        this.form.controls.sector.setValue(emptySectors[0].id);
                    } else if (sectors.length) {
                        this.form.controls.sector.setValue(sectors[sectors.length - 1].id);
                    }
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    private changeControlEnabled(control: AbstractControl, enabled = true): void {
        if (enabled) {
            if (control.disabled) {
                control.enable();
            }
        } else if (control.enabled) {
            control.disable();
        }
    }
}
