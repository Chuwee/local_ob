import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { EventRestriction, EventRestrictionType } from '@admin-clients/cpanel/promoters/events/restrictions/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { notEmpty } from '@admin-clients/shared/utility/utils';
import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    imports: [
        NgIf, NgFor,
        ReactiveFormsModule,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        FormControlErrorsComponent
    ],
    selector: 'app-event-restriction-dialog',
    templateUrl: './event-restriction-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventRestrictionDialogComponent {
    private readonly _fb = inject(FormBuilder);
    private readonly _dialogRef = inject(MatDialogRef<EventRestrictionDialogComponent>);

    readonly restriction = inject<EventRestriction>(MAT_DIALOG_DATA);
    readonly types = Object.values(EventRestrictionType);
    readonly form = this._fb.group({
        name: [
            { value: this.restriction?.name, disabled: false },
            [Validators.required, Validators.maxLength(75), notEmpty()]
        ],
        type: [
            { value: this.restriction?.type, disabled: !!this.restriction },
            [Validators.required]
        ]
    });

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    close(): void {
        this._dialogRef.close();
    }

    submit(): void {
        if (this.form.valid) {
            this._dialogRef.close({ sid: this.restriction?.sid, ...this.form.value });
        }
    }
}
