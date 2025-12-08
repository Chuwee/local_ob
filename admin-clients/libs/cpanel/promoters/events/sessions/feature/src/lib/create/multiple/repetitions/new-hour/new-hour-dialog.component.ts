
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { dateIsAfter, dateIsBefore, joinCrossValidations, timeValidator } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AddSessionsHourMode } from '../../../models/add-sessions-hour-mode.enum';

@Component({
    selector: 'app-new-hour-dialog',
    templateUrl: './new-hour-dialog.component.html',
    styleUrls: ['./new-hour-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewHourDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    form: UntypedFormGroup;
    addSessionsHourMode = AddSessionsHourMode;

    constructor(
        private _dialogRef: MatDialogRef<NewHourDialogComponent>,
        private _fb: UntypedFormBuilder
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            addHourMode: [null, Validators.required],
            fixedTime: [{ value: '10:00', disabled: true }, [Validators.required]],
            interval: this._fb.group({
                startTime: [{ value: '10:00', disabled: true }],
                endTime: [{ value: '11:00', disabled: true }],
                minuteInterval: [{ value: 1, disabled: true }, [Validators.required]]
            })
        });

        // INTERVAL DATE VALIDATORS
        this.form.get('interval.startTime').setValidators([
            Validators.required,
            timeValidator(dateIsBefore, 'startHourAfterEndHour', this.form.get('interval.endTime'))
        ]);
        this.form.get('interval.endTime').setValidators([
            Validators.required,
            timeValidator(dateIsAfter, 'endHourBeforeStartHour', this.form.get('interval.startTime'))
        ]);
        joinCrossValidations([
            this.form.get('interval.startTime'),
            this.form.get('interval.endTime')
        ], this._onDestroy);

        this.form.get('addHourMode').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe((mode: AddSessionsHourMode) => {
                const fixedTime = this.form.get('fixedTime');
                const interval = this.form.get('interval');
                if (mode === AddSessionsHourMode.fixed) {
                    fixedTime.enable();
                    interval.disable();
                } else {
                    fixedTime.disable();
                    interval.enable();
                }
                fixedTime.updateValueAndValidity();
                interval.updateValueAndValidity();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(result = null): void {
        this._dialogRef.close(result);
    }

    addHour(): void {
        this.close(this.form.value);
    }
}
