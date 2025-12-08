import {
    type PostWebhook, type Webhook, type WebhookActionType, WebhookComponent, WebhookEvents, type WebhookForm, WebhookService, WebhookStatus
} from '@admin-clients/cpanel/shared/feature/webhook';
import type { Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { throwError, Observable } from 'rxjs';

type WebhookDialogData = { entity: Entity; actionType: WebhookActionType; webhookId: string };
type WebhookFormControl = 'webhook';
@Component({
    templateUrl: './entity-webhook-dialog.component.html',
    imports: [MaterialModule, WebhookComponent, ReactiveFormsModule, TranslatePipe],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityWebhookDialogComponent {
    readonly #dialogRef = inject(MatDialogRef<EntityWebhookDialogComponent>);
    readonly #data: WebhookDialogData = inject(MAT_DIALOG_DATA);
    readonly #fb = inject(FormBuilder);
    readonly #webhookSrv = inject(WebhookService);

    readonly #entityId = this.#data.entity.id;
    readonly #webhookEvents = this.#webhooksEventsByEntity(this.#data.entity?.settings?.allow_avet_integration);
    readonly $isInProgress = toSignal(this.#webhookSrv.webhook.inProgress$());
    readonly actionType = this.#data.actionType;
    readonly webhookId = this.#data.webhookId;
    readonly options = { scope: 'ENTITY', entity_id: this.#entityId, events: this.#webhookEvents };
    readonly form = this.#fb.group<Partial<Record<WebhookFormControl, WebhookForm>>>({});

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
    }

    save(): void {
        this.save$().subscribe(() => this.close(true));
    }

    close(reloadAfterClose = false): void {
        this.#dialogRef.close(reloadAfterClose);
    }

    save$(): Observable<Webhook | void> {
        if (this.form.dirty && this.form.valid) {
            const { webhook } = this.form.getRawValue();
            const webhookFormValues = {
                status: webhook.enabled ? WebhookStatus.active : WebhookStatus.inactive,
                notification_url: webhook.url,
                events: this.#getEnabledWebhookEvents(webhook.events)
            };

            if (this.actionType === 'NEW') {
                const postWebhook = {
                    ...webhookFormValues,
                    status: WebhookStatus.inactive, scope: this.options.scope, entity_id: this.options.entity_id
                } as PostWebhook;

                return this.#webhookSrv.webhook.create$(postWebhook);
            } else {
                return this.#webhookSrv.webhook.update$(this.webhookId, webhookFormValues);
            }
        } else {
            this.form.markAllAsTouched();
            return throwError(() => 'invalid form');
        }
    }

    #getEnabledWebhookEvents(events: { [key in WebhookEvents]?: boolean; }): WebhookEvents[] {
        return Object.entries(events).filter(([_, value]) => !!value).map(([key]) => key as WebhookEvents);
    }

    #webhooksEventsByEntity(isAvetEntity: boolean): WebhookEvents[] {
        const allWebhookEvents = Object.values(WebhookEvents);
        const membersWebhookEvents = [WebhookEvents.memberOrderPurchase, WebhookEvents.memberOrderUpdate];

        return isAvetEntity ? allWebhookEvents : allWebhookEvents.filter(ev => !membersWebhookEvents.includes(ev));
    }
}
