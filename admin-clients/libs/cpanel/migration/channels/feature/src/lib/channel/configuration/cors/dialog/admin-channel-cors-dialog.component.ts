import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AdminChannelsService, type CorsSettings } from '@admin-clients/cpanel/migration/channels/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { unique } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';

type CorsDialogData = { channelId: number; settings: CorsSettings; originIndex: number | null };

@Component({
    templateUrl: './admin-channel-cors-dialog.component.html',
    imports: [
        MatDialogTitle, MatDialogContent, MatDialogActions, MatLabel, MatInput, MatFormField, MatError,
        MatIcon, MatIconButton, MatButton, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent
    ],
    providers: [AdminChannelsService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminChannelCorsDialogComponent extends ObDialog<AdminChannelCorsDialogComponent, CorsDialogData, boolean> {
    readonly #fb = inject(NonNullableFormBuilder);
    readonly #adminChannelsSrv = inject(AdminChannelsService);
    readonly #dialogRef = inject(MatDialogRef<AdminChannelCorsDialogComponent>);
    readonly #data: CorsDialogData = inject(MAT_DIALOG_DATA);

    readonly #channelId = this.#data.channelId;
    readonly #settings = this.#data.settings;
    readonly #originIndex = this.#data.originIndex;

    readonly #originValue = this.#settings?.allowed_origins?.[this.#originIndex] ?? '';
    readonly #filteredOrigins = this.#settings?.allowed_origins?.filter((_, i) => i !== this.#originIndex) ?? [];

    readonly $isInProgress = toSignal(this.#adminChannelsSrv.channelCorsSettings.loading$());
    readonly form = this.#fb.group({
        origin: this.#fb.control<string>(
            this.#originValue,
            [Validators.required, Validators.maxLength(255), unique(this.#filteredOrigins)]
        )
    });

    constructor() {
        super(DialogSize.MEDIUM);
    }

    close(reloadAfterClose = false): void {
        this.#dialogRef.close(reloadAfterClose);
    }

    save(): void {
        this.save$().subscribe(() => this.close(true));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const { origin } = this.form.getRawValue();
            const updatedOrigins = this.#settings?.allowed_origins?.map((value, i) => (i === this.#originIndex) ? origin : value) ?? [];

            if (this.isDialogTypeCreate) {
                updatedOrigins.unshift(origin);
            }

            return this.#adminChannelsSrv.channelCorsSettings.upsert$(
                this.#channelId,
                { enabled: this.#settings.enabled, allowed_origins: updatedOrigins }
            );
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    get isDialogTypeCreate(): boolean {
        return this.#originIndex === null;
    }
}
