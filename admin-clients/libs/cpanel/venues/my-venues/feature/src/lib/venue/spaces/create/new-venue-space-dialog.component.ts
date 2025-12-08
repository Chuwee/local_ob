import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Observable, Subject } from 'rxjs';
import { first, switchMap, takeUntil } from 'rxjs/operators';
import { PostVenueSpaceRequest, VenueSpaceCapacityType, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { NewVenueDialogComponent } from '../../../create/new-venue-dialog.component';

@Component({
    selector: 'app-new-venue-space-dialog',
    templateUrl: './new-venue-space-dialog.component.html',
    styleUrls: ['./new-venue-space-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class NewVenueSpaceDialogComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();

    readonly venueSpaceCapacityType = VenueSpaceCapacityType;

    form: UntypedFormGroup;
    isInProgress$: Observable<boolean>;

    constructor(
        private _dialogRef: MatDialogRef<NewVenueDialogComponent, number>,
        private _fb: UntypedFormBuilder,
        private _venuesService: VenuesService
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.initForm();
        this.initComponentModels();
        this.initFormHandlers();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createVenueSpace(): void {
        if (this.form.valid) {
            this._venuesService.getVenue$()
                .pipe(
                    first(venue => !!venue),
                    switchMap(venue => {
                        const formValue = this.form.value;
                        const data: PostVenueSpaceRequest = {
                            name: formValue.name,
                            capacity: { type: formValue.capacityType }
                        };
                        if (this.form.get('capacityType').value === this.venueSpaceCapacityType.fixed) {
                            data.capacity = {
                                ...data.capacity,
                                value: formValue.capacityValue
                            };
                        }
                        return this._venuesService.createVenueSpace(venue.id, data);
                    })
                ).subscribe(venueSpaceId => this.close(venueSpaceId));
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(venueSpaceId: number = null): void {
        this._dialogRef.close(venueSpaceId);
    }

    private initForm(): void {
        this.form = this._fb.group({
            name: [null, Validators.required],
            capacityType: [this.venueSpaceCapacityType.unlimited, Validators.required],
            capacityValue: [{ value: null, disabled: true }, [Validators.required, Validators.min(1)]]
        });
    }

    private initComponentModels(): void {
        this.isInProgress$ = this._venuesService.isVenueSpaceSaving$();
    }

    private initFormHandlers(): void {
        this.form.get('capacityType').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (value === this.venueSpaceCapacityType.unlimited) {
                    this.form.get('capacityValue').disable();
                } else {
                    this.form.get('capacityValue').enable();
                }
            });
    }
}
