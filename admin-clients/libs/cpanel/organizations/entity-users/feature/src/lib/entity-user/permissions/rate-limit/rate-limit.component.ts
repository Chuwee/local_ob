import { EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import {
    DialogSize, EmptyStateComponent, EphemeralMessageService, MessageDialogService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, finalize, map, switchMap, tap } from 'rxjs';
import { type DialogType, RateLimitDialogComponent } from './dialog/rate-limit-dialog.component';

@Component({
    selector: 'ob-rate-limit',
    templateUrl: './rate-limit.component.html',
    styleUrl: './rate-limit.component.scss',
    imports: [
        FormContainerComponent, TranslatePipe, MatProgressSpinner, MatDivider, MatExpansionPanel, MatIconButton, MatIcon,
        MatExpansionPanelTitle, MatExpansionPanelHeader, MatAccordion, MatSlideToggle, MatButton, EmptyStateComponent, MatTooltip
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RateLimitComponent {
    readonly #userSrv = inject(EntityUsersService);
    readonly #dialogSrv = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly $rateLimit = toSignal(this.#userSrv.userRateLimit.get$());
    readonly $loading = toSignal(this.#userSrv.userRateLimit.inProgress$());
    readonly $userId = toSignal(
        this.#userSrv.getEntityUser$()
            .pipe(
                filter(Boolean),
                map(user => user.id),
                tap(userId => this.#userSrv.userRateLimit.load(userId))
            )
    );

    updateUnlimited(toggleChange: MatSlideToggleChange): void {
        const unlimited = !toggleChange.checked;
        const updatedRateLimit = { ...this.$rateLimit(), unlimited };

        this.#userSrv.userRateLimit.update(this.$userId(), updatedRateLimit)
            .pipe(finalize(() => this.#userSrv.userRateLimit.load(this.$userId())))
            .subscribe({
                next: () => this.#ephemeralMsgSrv.showSaveSuccess()
            });
    }

    openCreateDialog(): void {
        return this.#openRuleDialog('CREATE');
    }

    openEditDialog(index: number): void {
        return this.#openRuleDialog('EDIT', index);
    }

    openDeleteDialog(path: string, index: number): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'USER.RATE_LIMIT.ACTIONS.DELETE_TITLE',
                message: 'USER.RATE_LIMIT.ACTIONS.DELETE_MESSAGE',
                messageParams: { path },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .pipe(
                filter(Boolean),
                switchMap(() => {
                    const updatedRules = [...this.$rateLimit()?.rules ?? []];
                    updatedRules.splice(index, 1);
                    const updatedRateLimit = {
                        unlimited: updatedRules.length === 0 || this.$rateLimit()?.unlimited,
                        rules: updatedRules
                    };
                    return this.#userSrv.userRateLimit.update(this.$userId(), updatedRateLimit);
                })
            )
            .subscribe(() => {
                this.#userSrv.userRateLimit.load(this.$userId());
                this.#ephemeralMsgSrv.showDeleteSuccess();
            });
    }

    #openRuleDialog(type: DialogType, index: number = null): void {
        openDialog(this.#dialogSrv, RateLimitDialogComponent, { index, userId: this.$userId(), type })
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => {
                this.#userSrv.userRateLimit.load(this.$userId());
                this.#ephemeralMsgSrv.showSaveSuccess();
            });
    }
}
