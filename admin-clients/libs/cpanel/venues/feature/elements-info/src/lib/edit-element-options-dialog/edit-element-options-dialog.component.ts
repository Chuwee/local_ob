import { DialogSize, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { ElementInfoContentOption, VenueTemplateElementInfoContents } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import {
    MAT_DIALOG_DATA,
    MatDialogModule,
    MatDialogRef
} from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-edit-element-options-dialog',
    imports: [
        TranslatePipe, ReactiveFormsModule, MaterialModule,
        MatDialogModule, SearchablePaginatedSelectionModule, EllipsifyDirective
    ],
    templateUrl: './edit-element-options-dialog.component.html',
    styleUrls: ['./edit-element-options-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditElementOptionsDialogComponent {
    private readonly _dialogRef = inject(MatDialogRef<EditElementOptionsDialogComponent>);
    private readonly _fb = inject(FormBuilder);

    readonly options: ElementInfoContentOption[] = Object.values(inject(MAT_DIALOG_DATA).contents || {});

    readonly form = this._fb.group({
        option: [null as VenueTemplateElementInfoContents, [Validators.required]]
    });

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.LARGE);
        this._dialogRef.disableClose = false;
    }

    close(): void {
        this._dialogRef.close();
    }

    saveOption(): void {
        if (this.form.valid) {
            this._dialogRef.close(this.form.value.option[0]);
        }
    }
}
