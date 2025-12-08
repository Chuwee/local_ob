import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Subject } from 'rxjs';
import { filter, map, take, takeUntil } from 'rxjs/operators';
import { EditorMode } from '../../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorSeatMatrixService } from '../../venue-tpl-editor-seat-matrix.service';
import { VenueTplEditorVenueMapService } from '../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../venue-tpl-editor.service';
import { VenueTplEditorSeatMatrixFormMatrixComponent } from './creation/venue-tpl-editor-seat-matrix-form-matrix.component';
import { VenueTplEditorSeatMatrixFormRowsComponent } from './creation/venue-tpl-editor-seat-matrix-form-rows.component';
import { VenueTplEditorSeatMatrixFormSeatsComponent } from './creation/venue-tpl-editor-seat-matrix-form-seats.component';
import { VenueTplEditorSeatMatrixFormIncreaseComponent } from './increase/venue-tpl-editor-seat-matrix-form-increase.component';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule,
        VenueTplEditorSeatMatrixFormMatrixComponent,
        VenueTplEditorSeatMatrixFormRowsComponent,
        VenueTplEditorSeatMatrixFormSeatsComponent,
        VenueTplEditorSeatMatrixFormIncreaseComponent
    ],
    selector: 'app-venue-tpl-editor-seat-matrix-form',
    templateUrl: './venue-tpl-editor-seat-matrix-form.component.html',
    styleUrls: ['../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixFormComponent implements OnInit {
    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);

    readonly form = this._fb.group({ continueRows: false });

    readonly newMatrix$ = this._seatMatrixSrv.getSeatMatrixConf$().pipe(map(conf => !conf.continueRows));

    ngOnInit(): void {
        //form data incoming
        this._seatMatrixSrv.getSeatMatrixConf$().pipe(takeUntil(this._onDestroy))
            .subscribe(config => this.form.setValue({ continueRows: config.continueRows }, { emitEvent: false }));
        // form data outgoing
        this.form.valueChanges.pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this._seatMatrixSrv.mergeSeatMatrixConf(this.form.getRawValue());
                this._editorSrv.modes.setEditorMode(
                    this.form.getRawValue().continueRows ? EditorMode.seatMatrixIncrease : EditorMode.seatMatrixCreate
                );
            });
        combineLatest([
            this._venueMapSrv.getVenueMap$().pipe(map(venueMap => venueMap.sectors.filter(sector => !sector.delete))),
            this._viewsSrv.getViewData$().pipe(map(viewData => viewData.view.id))
        ])
            .pipe(
                take(1),
                filter(([sectors, viewId]) =>
                    !sectors.some(sector => sector.rows.flatMap(row => row.seats).some(seat => seat.view === viewId))
                )
            )
            .subscribe(() => this.form.controls.continueRows.disable());
    }

}
