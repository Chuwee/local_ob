import { B2bService } from '@admin-clients/cpanel/b2b/data-access';
import {
    CopyTextComponent, DialogSize, EphemeralMessageService, MessageDialogService, ObDialog
} from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogTitle } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, switchMap } from 'rxjs';

type ApiKeyDialogData = {
    clientId: number; clientUserId: number; apiKey: string; operatorEntityId: number | null;
};

@Component({
    selector: 'app-api-key-b2b-client-user-dialog',
    templateUrl: './api-key-b2b-client-user-dialog.component.html',
    imports: [MatDialogTitle, MatDialogContent, MatDialogActions, MatIcon, MatIconButton, MatButton, TranslatePipe, CopyTextComponent],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApiKeyB2bClientUserDialogComponent extends ObDialog<ApiKeyB2bClientUserDialogComponent, ApiKeyDialogData, boolean> {
    #reloadAfterClose = false;

    readonly #dialogSrv = inject(MessageDialogService);
    readonly #data: ApiKeyDialogData = inject(MAT_DIALOG_DATA);
    readonly #b2bSrv = inject(B2bService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly #clientId = this.#data.clientId;
    readonly #clientUserId = this.#data.clientUserId;
    readonly #operatorEntityId = this.#data.operatorEntityId;

    readonly $isLoading = toSignal(this.#b2bSrv.b2bClientUserApiKey.loading$());
    readonly $apiKey = signal<string>(this.#data.apiKey);
    readonly $allowCopyApiKey = computed(() => !this.$apiKey().includes('*'));

    constructor() {
        super(DialogSize.MEDIUM, true);
    }

    openRegenerateApiKeyDialog(): void {
        this.#dialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'USER.API_KEY.REFRESH_WARNING_TITLE',
            message: 'USER.API_KEY.REFRESH_WARNING_MESSAGE',
            actionLabel: 'USER.API_KEY.REFRESH',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#b2bSrv.b2bClientUserApiKey.refresh$(this.#clientId, this.#clientUserId, this.#operatorEntityId))
            )
            .subscribe(refreshedApiKey => {
                this.#reloadAfterClose = true;
                this.$apiKey.set(refreshedApiKey);
                this.#ephemeralMsgSrv.showSuccess({ msgKey: 'USER.API_KEY.REFRESH_SUCCESS' });
            });
    }

    close(): void {
        this.dialogRef.close(this.#reloadAfterClose);
    }
}
