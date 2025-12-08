import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelListElement, ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { MatIcon } from '@angular/material/icon';
import { MatButton, MatIconButton } from '@angular/material/button';

@Component({
    selector: 'app-customer-private-area-dialog',
    standalone: true,
    imports: [
        MatDialogContent, ReactiveFormsModule, MatDialogTitle, MatIcon, MatIconButton,
        MatLabel, TranslatePipe, MatFormField, MatSelect, MatOption,
        FormControlErrorsComponent, MatProgressSpinner, MatButton, MatDialogActions
    ],
    templateUrl: './customer-private-area-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerPrivateAreaDialogComponent extends ObDialog<CustomerPrivateAreaDialogComponent, null, ChannelListElement> {
    readonly #channelsSrv = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        channel: [null as ChannelListElement, Validators.required]
    })

    readonly $isLoading = toSignal(this.#channelsSrv.channelsList.loading$());
    readonly $channels = toSignal(this.#channelsSrv.channelsList.getList$());

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    close(): void {
        this.dialogRef.close();
    }

    access(): void {
        this.dialogRef.close(this.form.controls.channel.value);
    }
}
