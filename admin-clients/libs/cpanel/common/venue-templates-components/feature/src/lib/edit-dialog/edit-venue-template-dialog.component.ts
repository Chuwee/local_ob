import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { notEmpty } from '@admin-clients/shared/utility/utils';
import { VenueTemplate, VenueTemplateFieldsRestrictions, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, inject, Inject, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';

@Component({
    selector: 'app-edit-venue-template-dialog',
    templateUrl: './edit-venue-template-dialog.component.html',
    styleUrls: ['./edit-venue-template-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, FlexLayoutModule, MaterialModule, ReactiveFormsModule,
        FormControlErrorsComponent
    ]
})
export class EditVenueTemplateDialogComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #onDestroy = new Subject<void>();

    form = this.#fb.group({
        name: [null as string, [
            Validators.required,
            Validators.maxLength(VenueTemplateFieldsRestrictions.nameLength),
            notEmpty()
        ]]
    });

    constructor(
        private _dialogRef: MatDialogRef<EditVenueTemplateDialogComponent>,
        private _venueTemplatesService: VenueTemplatesService,
        @Inject(MAT_DIALOG_DATA) private _data: VenueTemplate
    ) {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.form.patchValue({ name: this._data.name });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    updateVenueTemplate(): void {
        if (this.isValid()) {
            const data = this.form.value;

            this._venueTemplatesService.updateVenueTemplate(this._data.id, data)
                .subscribe(() =>
                    this.close({ id: this._data.id, name: data.name } as VenueTemplate)
                );
        }
    }

    close(venueTpl: VenueTemplate = null): void {
        if (venueTpl) {
            this._venueTemplatesService.venueTpl.load(this._data.id);
        }
        this._dialogRef.close(venueTpl);
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            return false;
        }
    }

}
