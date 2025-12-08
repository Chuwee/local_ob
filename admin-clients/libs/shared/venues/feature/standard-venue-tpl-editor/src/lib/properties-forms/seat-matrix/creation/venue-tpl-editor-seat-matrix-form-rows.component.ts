import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {
    SeatMatrixConfNumerationType, SeatMatrixConfRangeType, SeatMatrixConfRowDirection, SeatMatrixConfRowLabelPosition
} from '../../../models/venue-tpl-editor-seat-matrix-conf.model';
import { EdSector } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorSeatMatrixService } from '../../../venue-tpl-editor-seat-matrix.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SharedUtilityDirectivesModule
    ],
    selector: 'app-venue-tpl-editor-seat-matrix-form-rows',
    templateUrl: './venue-tpl-editor-seat-matrix-form-rows.component.html',
    styleUrls: ['../../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixFormRowsComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);

    @ViewChild('sectorsSelectSearch')
    readonly sectorsSelectSearchComponent: SelectSearchComponent<EdSector>;

    readonly rangeTypes = Object.values(SeatMatrixConfRangeType);
    readonly numerationTypes = Object.values(SeatMatrixConfNumerationType);
    readonly rowDirections = Object.values(SeatMatrixConfRowDirection);
    readonly rowLabelPositions = Object.values(SeatMatrixConfRowLabelPosition);

    readonly form = this._fb.group({
        range: null as SeatMatrixConfRangeType,
        numeration: null as SeatMatrixConfNumerationType,
        direction: null as SeatMatrixConfRowDirection,
        numericStartsWith: [0, [Validators.required, Validators.min(0)]],
        alphabeticStartsWith: ['', [Validators.required]],
        prefix: '',
        show: false,
        position: null as SeatMatrixConfRowLabelPosition
    });

    ngOnInit(): void {
        //form data incoming
        this._seatMatrixSrv.getSeatMatrixConf$().pipe(takeUntil(this._onDestroy)).subscribe(config => {
            this.form.setValue(config.rows, { emitEvent: false });
            const controls = this.form.controls;
            this.changeControlEnabled(controls.numericStartsWith, config.rows.range === SeatMatrixConfRangeType.numeric);
            this.changeControlEnabled(controls.numeration, config.rows.range === SeatMatrixConfRangeType.numeric);
            this.changeControlEnabled(controls.alphabeticStartsWith, config.rows.range === SeatMatrixConfRangeType.alphabetic);
            this.changeControlEnabled(controls.position, config.rows.show);
            this.changeControlEnabled(controls.prefix, config.rows.show);
        });
        // form data outgoing
        this.form.valueChanges.pipe(takeUntil(this._onDestroy)).subscribe(() => {
            this._seatMatrixSrv.fixMinMaxFormValues(this.form);
            if (this.form.valid) {
                this._seatMatrixSrv.mergeSeatMatrixConf({ rows: this.form.getRawValue() });
            }
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    private changeControlEnabled(control: AbstractControl, enabled = true): void {
        if (enabled) {
            control.enable({ emitEvent: false });
        } else {
            control.disable({ emitEvent: false });
        }
    }
}
