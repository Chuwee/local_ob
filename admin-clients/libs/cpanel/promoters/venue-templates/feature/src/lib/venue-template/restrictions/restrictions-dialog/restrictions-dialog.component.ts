import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { GetPriceTypeRestricion, PostPriceTypeRestriction } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { Id } from '@admin-clients/shared/data-access/models';
import { atLeastOneRequiredInFormGroup } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { catchError, filter, first, takeUntil, tap } from 'rxjs/operators';
import { PriceTypeWithRestriction } from '../price-type-restrictions.component';

enum RestrictionType {
    requiredTicketsNumber = 'required_tickets_number',
    lockedTicketsNumber = 'locked_tickets_number'
}

@Component({
    selector: 'app-restrictions-dialog',
    templateUrl: './restrictions-dialog.component.html',
    styleUrls: ['./restrictions-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDialogModule, MatIcon, TranslatePipe, ReactiveFormsModule, MatCheckbox, MatFormFieldModule,
        MatRadioButton, MatRadioGroup, FlexLayoutModule, FormControlErrorsComponent, MatProgressSpinner,
        AsyncPipe, MatButtonModule, MatInput
    ]
})
export class PriceTypeRestrictionDialogComponent implements OnInit, OnDestroy {

    private _onDestroy = new Subject<void>();

    priceTypes: VenueTemplatePriceType[];
    form: UntypedFormGroup;
    priceType: PriceTypeWithRestriction;
    isLoadingOrSaving$ = new BehaviorSubject<boolean>(false);

    get selectedZones(): string {
        const selectedZonesIds = Object.entries(this.form.get('required_price_type_ids').value as Record<number, boolean>)
            ?.filter(([, value]) => !!value).map(([id]) => id);
        return selectedZonesIds?.reduce((acc, id) => acc.concat(this.priceTypes.find(elem => elem.id === +id)?.name), []).join(', ');
    }

    constructor(
        private _dialogRef: MatDialogRef<PriceTypeRestrictionDialogComponent>,
        private _fb: UntypedFormBuilder,
        @Inject(MAT_DIALOG_DATA) private _data: {
            priceType: PriceTypeWithRestriction;
            priceTypes: VenueTemplatePriceType[];
            restriction$: (id: number) => Observable<GetPriceTypeRestricion>;
        }
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        this.priceType = this._data.priceType;
        this.priceTypes = this._data.priceTypes?.filter(priceType => priceType.id !== this.priceType.id);
    }

    ngOnInit(): void {
        this.form = this._fb.group({
            required_price_type_ids: this._fb.group(
                this.mapToKeyObject(this.priceTypes as Id[]),
                { validators: [atLeastOneRequiredInFormGroup()] }
            ),
            [RestrictionType.requiredTicketsNumber]: [null, [Validators.min(1), Validators.required]],
            [RestrictionType.lockedTicketsNumber]: [null, [Validators.min(1), Validators.required]],
            type: [null, Validators.required]
        });

        this.form.get('type').valueChanges.pipe(
            filter(val => !!val),
            takeUntil(this._onDestroy)
        ).subscribe(value => {
            if (value === RestrictionType.requiredTicketsNumber) {
                this.form.get(RestrictionType.lockedTicketsNumber).disable();
                this.form.get(RestrictionType.requiredTicketsNumber).enable();
            } else {
                this.form.get(RestrictionType.lockedTicketsNumber).enable();
                this.form.get(RestrictionType.requiredTicketsNumber).disable();
            }
        });

        if (this.priceType.hasRestrictions) {
            this.isLoadingOrSaving$.next(true);
            this._data.restriction$(this.priceType.id).pipe(
                first(elem => !!elem),
                tap(() => this.isLoadingOrSaving$.next(false)),
                catchError(() => {
                    this.close();
                    return of(null);
                }),
                takeUntil(this._onDestroy)
            ).subscribe(restriction => {
                this.form.reset({
                    required_price_type_ids: this.mapToKeyObject(restriction.required_price_types, true),
                    [RestrictionType.requiredTicketsNumber]: restriction.required_tickets_number,
                    [RestrictionType.lockedTicketsNumber]: restriction.locked_tickets_number,
                    type: restriction.required_tickets_number ? RestrictionType.requiredTicketsNumber : RestrictionType.lockedTicketsNumber
                });
            });
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    close(result = null): void {
        this._dialogRef.close(result);
    }

    save(): void {
        if (this.form.valid) {
            const data: PostPriceTypeRestriction = {
                required_price_type_ids: Object.keys(this.form.value.required_price_type_ids)
                    ?.filter(id => !!this.form.value.required_price_type_ids[id])
                    ?.map(id => +id),
                required_tickets_number: this.form.value.required_tickets_number,
                locked_tickets_number: this.form.value.locked_tickets_number
            };
            this.close(data);
        } else {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    private mapToKeyObject(data: Id[], value = false): Record<number, boolean> {
        return data?.map(elem => elem.id)
            .reduce((acc, curr) => (acc[curr] = value, acc), {});
    }

}
