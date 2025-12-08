import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    type GetWebhooksOptions, type WebhookActionType, WebhookModule, WebhookScope, WebhookService
} from '@admin-clients/cpanel/shared/feature/webhook';
import { EmptyStateComponent, EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, tap } from 'rxjs';
import { EntityWebhookDialogComponent } from './webhook-dialog/entity-webhook-dialog.component';
import { EntityWebhookTableComponent } from './webhook-table/entity-webhook-table.component';

@Component({
    selector: 'app-entity-webhooks',
    templateUrl: './entity-webhooks.component.html',
    imports: [
        EmptyStateComponent, TranslatePipe, MatButton, EntityWebhookTableComponent,
        FormContainerComponent, MatIcon, MatProgressSpinner, MatPaginator, WebhookModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityWebhooksComponent {
    readonly #matDialog = inject(MatDialog);
    readonly #entitySrv = inject(EntitiesService);
    readonly #webhookSrv = inject(WebhookService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    readonly matPaginator = viewChild<MatPaginator>(MatPaginator);

    #webhooksRequest: GetWebhooksOptions = { scope: WebhookScope.entity, offset: 0 };
    readonly #$entity = toSignal(this.#entitySrv.getEntity$().pipe(
        filter(Boolean),
        tap(entity => {
            this.#webhooksRequest = { ...this.#webhooksRequest, entity_id: entity.id };
            this.#webhookSrv.webhooks.load(this.#webhooksRequest);
        })
    ));

    readonly $webhooks = toSignal(this.#webhookSrv.webhooks.get$());
    readonly $metadata = toSignal(this.#webhookSrv.webhooks.getMetadata$());
    readonly $isInProgress = toSignal(this.#webhookSrv.webhooks.inProgress$());

    pageIndex(offset: number, limit: number): number {
        return Math.floor(offset / limit);
    }

    onPageChange(page: PageEvent): void {
        this.#webhooksRequest = { ...this.#webhooksRequest, offset: page.pageIndex * page.pageSize };
        this.#webhookSrv.webhooks.load(this.#webhooksRequest);
    }

    openCreateWebhookDialog(): void {
        const actionType: WebhookActionType = 'NEW';

        this.#matDialog.open(EntityWebhookDialogComponent, new ObMatDialogConfig({
            entity: this.#$entity(),
            actionType
        }))
            .afterClosed()
            .subscribe(result => {
                if (result) this.loadWebhooksAndShowMsg(actionType);
            });
    }

    loadWebhooksAndShowMsg(actionType: WebhookActionType): void {
        this.#webhookSrv.webhooks.load(this.#webhooksRequest);
        let msgKey: string;

        switch (actionType) {
            case 'EDIT':
                msgKey = 'WEBHOOKS.TOOLTIP.SUCCESS';
                break;
            case 'DELETE':
                msgKey = 'WEBHOOKS.DELETE.SUCCESS';
                break;
            case 'NEW':
                msgKey = 'WEBHOOKS.NEW.SUCCESS';
                break;
        }

        if (msgKey) {
            this.#ephemeralMsgSrv.showSuccess({ msgKey });
        }

    }
}
