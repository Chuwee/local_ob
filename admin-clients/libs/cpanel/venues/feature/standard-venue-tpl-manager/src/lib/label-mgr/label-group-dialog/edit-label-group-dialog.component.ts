import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { VenueTemplateFieldsRestrictions, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { EditLabelGroupDialogData, EditLabelGroupDialogResponse } from './edit-label-group-dialog.data';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent
    ],
    selector: 'app-edit-label-group-dialog',
    templateUrl: './edit-label-group-dialog.component.html',
    styleUrls: ['./edit-label-group-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditLabelGroupDialogComponent
    extends ObDialog<EditLabelGroupDialogComponent, EditLabelGroupDialogData, EditLabelGroupDialogResponse> {

    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #venueTemplateService = inject(VenueTemplatesService);

    readonly form = this.#fb.group({
        name: [
            this.data.currentName ? this.data.currentName : null,
            [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.labelGroupNameLength)]
        ],
        code: [
            this.data.currentCode ? this.data.currentCode : null,
            [Validators.required, Validators.maxLength(VenueTemplateFieldsRestrictions.labelGroupCodeLength)]
        ]
    });

    loading$ = this.#venueTemplateService.venueTplCustomTagGroups.loading$();

    constructor() {
        super(DialogSize.MEDIUM);
        this.loading$ = this.#venueTemplateService.venueTplCustomTagGroups.loading$();
        if (this.data.isCreation) {
            if (this.data.isCreation && this.form.controls.code.enabled) {
                FormControlHandler.reflectControlValue(
                    this.form.controls.name,
                    this.form.controls.code,
                    VenueTemplateFieldsRestrictions.labelGroupCodeLength
                )
                    .pipe(takeUntilDestroyed(this.#destroyRef))
                    .subscribe();
            }
        }
    }

    save(): void {
        if (this.form.valid) {
            if (this.data.isCreation) {
                this.#venueTemplateService.venueTplCustomTagGroups.create(
                    this.data.templateId,
                    {
                        name: this.form.value.name,
                        code: this.form.value.code
                    })
                    .subscribe(({ id }) => this.dialogRef.close({ saved: true, name: this.form.value.name, id }));
            } else {
                this.#venueTemplateService.venueTplCustomTagGroups.update(
                    this.data.templateId,
                    {
                        id: this.data.id,
                        name: this.form.value.name,
                        code: this.form.value.code
                    })
                    .subscribe(() => this.dialogRef.close({ saved: true, name: this.form.value.name }));
            }
        }
    }

    close(): void {
        this.dialogRef.close({ saved: false });
    }
}
