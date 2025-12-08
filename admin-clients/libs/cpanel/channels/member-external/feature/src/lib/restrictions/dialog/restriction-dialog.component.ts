import { Restriction, RestrictionType } from '@admin-clients/cpanel-channels-member-external-data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { notEmpty } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
    selector: 'app-restriction-dialog',
    templateUrl: './restriction-dialog.component.html',
    styleUrls: ['./restriction-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class MemberExternalRestrictionDialogComponent {

    private readonly _fb = inject(FormBuilder);

    readonly restriction = inject<Restriction>(MAT_DIALOG_DATA);
    readonly types = Object.values(RestrictionType);
    readonly form = this._fb.group({
        restriction_name: [
            { value: this.restriction?.restriction_name, disabled: false },
            [Validators.required, Validators.maxLength(75), notEmpty()]
        ],
        sid: [
            { value: this.restriction?.sid, disabled: !!this.restriction },
            [Validators.required, Validators.pattern('^[a-zA-Z0-9-_#]+$'), Validators.maxLength(75)]
        ],
        restriction_type: [
            { value: this.restriction?.restriction_type, disabled: !!this.restriction },
            [Validators.required]
        ]
    });

    constructor(
        private _dialogRef: MatDialogRef<MemberExternalRestrictionDialogComponent>
    ) {
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
