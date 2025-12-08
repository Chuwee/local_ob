import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { type Webhook, type WebhookActionType, WebhookService, WebhookStatus } from '@admin-clients/cpanel/shared/feature/webhook';
import { CopyTextComponent, DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, inject, input, output, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { catchError, finalize, map, of, switchMap } from 'rxjs';
import { EntityWebhookDialogComponent } from '../webhook-dialog/entity-webhook-dialog.component';

@Component({
    selector: 'app-entity-webhook-table',
    templateUrl: './entity-webhook-table.component.html',
    styleUrl: './entity-webhook-table.component.scss',
    imports: [
        CopyTextComponent, MatTable, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatHeaderCell, MatIconButton,
        MatHeaderCellDef, MatCell, MatCellDef, MatColumnDef, MatIcon, MatTooltip, MatSlideToggle, TranslatePipe, MatProgressSpinner
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityWebhookTableComponent {
    readonly #matDialog = inject(MatDialog);
    readonly #webhookSrv = inject(WebhookService);
    readonly #entitySrv = inject(EntitiesService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly $webhooks = input<Webhook[]>([], { alias: 'webhooks' });
    readonly $reload = output<WebhookActionType>({ alias: 'reload' });

    readonly #$entity = toSignal(this.#entitySrv.getEntity$());
    readonly $isInProgress = toSignal(this.#webhookSrv.webhook.inProgress$());
    readonly $isOpenEditDialog = signal<boolean>(false);
    readonly tableColumns = ['notification-url', 'api-key', 'id', 'enabled', 'actions'];
    readonly $isHandsetOrTablet = toSignal(this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            takeUntilDestroyed(),
            map(result => result.matches)
        ));

    openEditWebhookDialog(webhookId: string): void {
        const actionType: WebhookActionType = 'EDIT';
        this.$isOpenEditDialog.set(true);

        this.#matDialog.open(EntityWebhookDialogComponent, new ObMatDialogConfig({
            entity: this.#$entity(),
            actionType,
            webhookId
        }))
            .afterClosed()
            .pipe(finalize(() => this.$isOpenEditDialog.set(false)))
            .subscribe(result => {
                if (result) this.$reload.emit(actionType);

            });
    }

    openDeleteWebhookDialog(webhook: Webhook): void {
        this.#msgDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'WEBHOOKS.DELETE.TITLE',
                message: 'WEBHOOKS.DELETE.MESSAGE',
                messageParams: { eventName: webhook.notification_url },
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
            .pipe(
                switchMap(success =>
                    success ? this.#webhookSrv.webhook.delete$(webhook.id).pipe(map(() => true)) : of(false)
                )
            )
            .subscribe(result => {
                if (result) this.$reload.emit('DELETE');
            });
    }

    updateStatusWebhook(event: MatSlideToggleChange, webhook: Webhook): void {
        const status = event.checked ? WebhookStatus.active : WebhookStatus.inactive;

        this.#updateWebhook({ ...webhook, status });
    }

    refreshApiKey(webhookId: string): void {
        this.#webhookSrv.webhook.refreshApiKey$(webhookId)
            .pipe(
                switchMap(updatedWebhook => this.#webhookSrv.getUpdatedWebhooksList(updatedWebhook))
            )
            .subscribe(listResponse => {
                if (listResponse) {
                    this.#webhookSrv.webhooks.applyWebhooksUpdate(listResponse);
                }
                this.#ephemeralMsgSrv.showSuccess({ msgKey: `WEBHOOKS.TOOLTIP.SUCCESS` });
            });
    }

    #updateWebhook(updatedWebhook: Webhook): void {
        const { id, ...putWebhook } = updatedWebhook;

        this.#webhookSrv.webhook.update$(id, putWebhook)
            .pipe(
                catchError(error => {
                    this.$reload.emit('ERROR');
                    throw error;
                })
            )
            .subscribe(() => this.$reload.emit('EDIT'));
    }
}
