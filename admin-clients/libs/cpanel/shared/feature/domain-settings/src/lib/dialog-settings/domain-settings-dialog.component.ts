import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AdminChannelsService } from '@admin-clients/cpanel/migration/channels/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { unique } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatError, MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    standalone: true,
    templateUrl: './domain-settings-dialog.component.html',
    imports: [
        MatDialogTitle, MatDialogContent, MatDialogActions, MatLabel, MatInput, MatFormField, MatError,
        MatIcon, MatIconButton, MatButton, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent
    ],
    providers: [AdminChannelsService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DomainSettingsDialogComponent extends
    ObDialog<DomainSettingsDialogComponent, { existingValues: string[]; domain?: string }, string> {
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<DomainSettingsDialogComponent>);

    readonly #existingValues = this.data.existingValues.filter(domain => domain !== (this.data.domain || ''));
    readonly editMode = !!this.data?.domain;
    readonly domainCtrl = this.#fb.control<string>(
        this.data.domain || '',
        [Validators.required, unique(this.#existingValues)]
    );

    constructor() {
        super(DialogSize.MEDIUM);
    }

    close(): void {
        this.#dialogRef.close();
    }

    save(): void {
        this.#dialogRef.close(this.domainCtrl.value);
    }
}
