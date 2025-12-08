import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { takeUntil, withLatestFrom } from 'rxjs/operators';
import {
    SeatMatrixConfNumerationType, SeatMatrixConfRangeType, SeatMatrixConfSeatDirection, venueTplEditorSeatMatrixLimits
} from '../../../models/venue-tpl-editor-seat-matrix-conf.model';
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
    selector: 'app-venue-tpl-editor-seat-matrix-form-seats',
    templateUrl: './venue-tpl-editor-seat-matrix-form-seats.component.html',
    styleUrls: ['../../venue-tpl-editor-properties-forms-common-styles.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSeatMatrixFormSeatsComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();

    private readonly _fb = inject(FormBuilder);
    private readonly _seatMatrixSrv = inject(VenueTplEditorSeatMatrixService);

    readonly rangeTypes = Object.values(SeatMatrixConfRangeType);
    readonly numerationTypes = Object.values(SeatMatrixConfNumerationType);
    readonly seatDirections = Object.values(SeatMatrixConfSeatDirection);

    readonly form = this._fb.group({
        numTracks: 1 as 1 | 2,
        seatsRange: null as SeatMatrixConfRangeType,
        track1: this._fb.group({
            numeration: null as SeatMatrixConfNumerationType,
            direction: null as SeatMatrixConfSeatDirection,
            numericStartsWith: [0, [Validators.required, Validators.min(0)]],
            alphabeticStartsWith: ['', Validators.required],
            seats: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.seats)]]
        }),
        track2: this._fb.group({
            numeration: null as SeatMatrixConfNumerationType,
            direction: null as SeatMatrixConfSeatDirection,
            numericStartsWith: [0, [Validators.required, Validators.min(0)]],
            alphabeticStartsWith: ['', Validators.required],
            seats: [0, [Validators.required, Validators.min(1), Validators.max(venueTplEditorSeatMatrixLimits.seats)]]
        })
    });

    ngOnInit(): void {
        //form data incoming
        this._seatMatrixSrv.getSeatMatrixConf$().pipe(takeUntil(this._onDestroy)).subscribe(config => {
            const controls = this.form.controls;
            // config.matrix.seats change reaction
            this.form.setValue(config.seats, { emitEvent: false });
            this.changeControlEnabled(
                controls.track1.controls.numericStartsWith, config.seats.seatsRange === SeatMatrixConfRangeType.numeric
            );
            this.changeControlEnabled(
                controls.track1.controls.alphabeticStartsWith, config.seats.seatsRange === SeatMatrixConfRangeType.alphabetic
            );
            this.changeControlEnabled(
                controls.track2.controls.numericStartsWith, config.seats.seatsRange === SeatMatrixConfRangeType.numeric
            );
            this.changeControlEnabled(
                controls.track2.controls.alphabeticStartsWith, config.seats.seatsRange === SeatMatrixConfRangeType.alphabetic
            );
            this.changeControlEnabled(
                controls.track1.controls.numeration, config.seats.seatsRange === SeatMatrixConfRangeType.numeric
            );
            this.changeControlEnabled(
                controls.track2.controls.numeration, config.seats.seatsRange === SeatMatrixConfRangeType.numeric
            );
        });
        // form data outgoing
        this.form.valueChanges
            .pipe(
                withLatestFrom(this._seatMatrixSrv.getSeatMatrixConf$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([_, conf]) => {
                this._seatMatrixSrv.fixMinMaxFormValues(this.form);
                if (this.form.valid) {
                    const value = this.form.getRawValue();
                    if (value.numTracks === 2) {
                        if (conf.seats.numTracks !== value.numTracks) {
                            value.track1.seats = Math.ceil(conf.matrix.seats / 2);
                            value.track2.seats = Math.floor(conf.matrix.seats / 2);
                            value.track1.direction = SeatMatrixConfSeatDirection.left;
                            value.track2.direction = SeatMatrixConfSeatDirection.right;
                        }
                        if (conf.seats.numTracks !== value.numTracks || conf.seats.seatsRange !== value.seatsRange) {
                            if (value.seatsRange === SeatMatrixConfRangeType.numeric) {
                                const typicalConf = value.track1.numeration !== SeatMatrixConfNumerationType.even;
                                value.track1.numeration
                                    = typicalConf ? SeatMatrixConfNumerationType.odd : SeatMatrixConfNumerationType.even;
                                value.track2.numeration
                                    = typicalConf ? SeatMatrixConfNumerationType.even : SeatMatrixConfNumerationType.odd;
                                value.track1.numericStartsWith = typicalConf ? 1 : 2;
                                value.track2.numericStartsWith = typicalConf ? 2 : 1;
                            } else {
                                let resultCharCode = String(value.track1.alphabeticStartsWith).charCodeAt(0) + value.track1.seats;
                                //searches a character between A(65) and Z(90) starting with the track1 character and looping.
                                while (resultCharCode > 90) {
                                    resultCharCode -= 26;
                                }
                                value.track2.alphabeticStartsWith = String.fromCharCode(resultCharCode);
                            }
                        }
                    }
                    this._seatMatrixSrv.mergeSeatMatrixConf({ seats: value });
                }
            }
            );
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
