import { mergeObjects } from '@admin-clients/shared/utility/utils';
import { inject, Injectable } from '@angular/core';
import { AbstractControl, FormArray, FormControl, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTplEditorSeatMatrixConf } from './models/venue-tpl-editor-seat-matrix-conf.model';
import { EdRow } from './models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';

@Injectable()
export class VenueTplEditorSeatMatrixService {

    private readonly _alphabeticNames = this.generateAlphabeticNames();

    private readonly _venueTplEdState = inject(VenueTplEditorState);

    getSeatMatrixConf$(): Observable<VenueTplEditorSeatMatrixConf> {
        return this._venueTplEdState.seatMatrixConf.getValue$();
    }

    commitSeatMatrix(): void {
        this.mergeSeatMatrixConf({ commitConfiguration: true });
    }

    cancelSeatMatrix(): void {
        this.mergeSeatMatrixConf({ continueRows: false });
    }

    endCommit(): void {
        this.mergeSeatMatrixConf({ commitConfiguration: false, continueRows: false });
    }

    mergeSeatMatrixConf(newConf: Partial<VenueTplEditorSeatMatrixConf>): void {
        this._venueTplEdState.seatMatrixConf.getValue$().pipe(take(1))
            .subscribe(config =>  {
                const resultConfig = mergeObjects(config, newConf);
                if (resultConfig.seats.numTracks === 1) {
                    resultConfig.seats.track1.seats = resultConfig.matrix.seats;
                } else {
                    resultConfig.matrix.seats = resultConfig.seats.track1.seats + resultConfig.seats.track2.seats;
                }
                this._venueTplEdState.seatMatrixConf.setValue(resultConfig);
            });
    }

    fixMinMaxFormValues(control: AbstractControl): void {
        if (control.invalid) {
            if (control instanceof FormControl) {
                if (control.hasError('max')) {
                    control.setValue(control.getError('max').max, { emitEvent: false });
                } else if (control.hasError('min')){
                    control.setValue(control.getError('min').min, { emitEvent: false });
                }
            } else if (control instanceof FormGroup || control instanceof FormArray) {
                Object.values(control.controls).forEach(subControl => this.fixMinMaxFormValues(subControl));
            }
        }
    }

    setTempIds(rows: EdRow[], tempIdGenerator: () => number): void {
        rows.forEach(row => {
            row.id ??= tempIdGenerator();
            row.seats.forEach(seat => {
                seat.id = tempIdGenerator();
                seat.rowId = row.id;
            });
        });
    }

    getAlphabeticName(index: number, firstValue: string): string {
        return this._alphabeticNames[(this._alphabeticNames.indexOf(firstValue) + index) % this._alphabeticNames.length];
    }

    // Generates a list of strings from 'A' to 'Z'
    private generateAlphabeticNames(): string[] {
        const firstCharCode = 'A'.charCodeAt(0);
        const lastCharCode = 'Z'.charCodeAt(0);
        return [...Array(lastCharCode - firstCharCode + 1).keys()].map(index => String.fromCharCode(firstCharCode + index));
    }
}
