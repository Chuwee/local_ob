import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge, noDuplicateValuesValidatorFn } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs';
import { PriceTypesDataMode, PriceTypesDialogData } from './price-types-dialog-data';

@Component({
    imports: [
        FlexLayoutModule, TranslatePipe, MatIconModule, MatButtonModule, MatDialogModule, ReactiveFormsModule,
        MatFormFieldModule, FormControlErrorsComponent, MatInputModule, ObFormFieldLabelDirective, MatProgressSpinnerModule
    ],
    selector: 'app-activity-venue-template-price-types-dialog',
    templateUrl: './activity-venue-template-price-types-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ActivityVenueTemplatePriceTypesDialogComponent implements OnInit {
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #data: PriceTypesDialogData = inject(MAT_DIALOG_DATA);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<ActivityVenueTemplatePriceTypesDialogComponent>);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly priceTypesDataMode = PriceTypesDataMode;

    readonly $loading = toSignal(booleanOrMerge([
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#venueTemplatesSrv.isVenueTemplatePriceTypeSaving$()
    ]));

    readonly $priceTypesMode = signal<PriceTypesDataMode>(PriceTypesDataMode.creation);

    readonly $existingPriceTypes = toSignal<{ codes: string[]; names: string[] }>(
        this.#venueTemplatesSrv.getVenueTemplatePriceTypes$().pipe(
            map((priceTypes: VenueTemplatePriceType[]) => ({
                codes: priceTypes.map(pt => pt.code),
                names: priceTypes.map(pt => pt.name)
            }))
        )
    );

    readonly form = this.#fb.group({
        name: ['', [
            Validators.required,
            control => noDuplicateValuesValidatorFn(control, this.$existingPriceTypes().names.filter(n => n !== this.#data.name))]],
        code: ['', [
            Validators.required,
            control => noDuplicateValuesValidatorFn(control, this.$existingPriceTypes().codes.filter(c => c !== this.#data.code))
        ]]
    });

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    ngOnInit(): void {
        this.$priceTypesMode.set(this.#data.mode);
        if (this.$priceTypesMode() !== PriceTypesDataMode.edition) return;
        if (this.#data.isNameReadOnly) this.form.get('name')?.disable();
        this.form.patchValue({ name: this.#data.name, code: this.#data.code });
    }

    close(): void {
        this.#dialogRef.close();
    }

    newPriceType(): void {
        const price = {
            name: this.form.get('name').value,
            code: this.form.get('code').value
        };
        this.#venueTemplatesSrv.addVenueTemplatePriceType(this.#data.templateId, price)
            .subscribe(() => this.#showSuccessMessage(PriceTypesDataMode.creation));
    }

    editPriceType(): void {
        const price = {
            id: this.#data.id,
            name: this.form.get('name').value,
            code: this.form.get('code').value
        };
        this.#venueTemplatesSrv.updateVenueTemplatePriceType(this.#data.templateId, price)
            .subscribe(() => this.#showSuccessMessage(PriceTypesDataMode.edition));
    }

    #showSuccessMessage(mode: PriceTypesDataMode): void {
        this.#ephemeralMessageService.showSuccess({
            msgKey: mode === PriceTypesDataMode.edition ? 'VENUE_TPLS.EDIT_TICKET_TYPE_SUCCESS' : 'VENUE_TPLS.CREATE_TICKET_TYPE_SUCCESS',
            msgParams: { name: this.form.get('name').value }
        });
        this.#dialogRef.close(true);
    }
}
