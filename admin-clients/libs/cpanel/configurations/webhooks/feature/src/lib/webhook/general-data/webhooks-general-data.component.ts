import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    PutWebhook, Webhook, WebhookComponent, WebhookEvents, WebhookService, WebhookStatus
} from '@admin-clients/cpanel/shared/feature/webhook';
import { EphemeralMessageService, MessageType } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { take, tap } from 'rxjs';

@Component({
    selector: 'app-webhooks-general-data',
    imports: [
        WebhookComponent, FormContainerComponent, TranslatePipe, MatDividerModule, ReactiveFormsModule,
        MatProgressSpinnerModule, MatFormFieldModule, MatInputModule, FormControlErrorsComponent, FlexLayoutModule
    ],
    templateUrl: './webhooks-general-data.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class WebhooksGeneralDataComponent {
    readonly webhookRef = viewChild(WebhookComponent);
    readonly #destroyRef = inject(DestroyRef);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly form = this.#fb.group({
        internal_name: ['', Validators.required]
    });

    readonly $internalName = signal<string>('');
    readonly #webhookSrv = inject(WebhookService);
    readonly $webhook = toSignal<Webhook>(this.#webhookSrv.webhook.get$().pipe(
        tap(webhook => {
            this.form.get('internal_name')?.setValue(webhook?.internal_name);
            this.$internalName.set(webhook?.internal_name);
        })
    ));

    readonly $loading = toSignal(this.#webhookSrv.webhook.inProgress$());

    async cancel(): Promise<void> {
        this.#webhookSrv.webhook.load(this.$webhook().id);
    }

    async save(): Promise<void> {
        if (!this.form.valid) return;

        const webhookFormValue = this.webhookRef().form.value;
        if (webhookFormValue?.events) {
            webhookFormValue.events = Object.keys(webhookFormValue.events)
                .filter(event => !!webhookFormValue.events[event])
                .map(event => event as WebhookEvents);
        }

        const webhookReq = {
            id: this.$webhook().id,
            body: {
                status: webhookFormValue.enabled ? WebhookStatus.active : WebhookStatus.inactive,
                notification_url: webhookFormValue.url || this.$webhook()?.notification_url,
                events: webhookFormValue.events || this.$webhook()?.events
            } as PutWebhook
        };

        const currentInternalName = this.form.get('internal_name')?.value;
        if (!this.form.get('internal_name')?.pristine && currentInternalName !== this.$internalName()) {
            webhookReq.body.internal_name = currentInternalName;
        }

        this.#webhookSrv.webhook.update$(webhookReq.id, webhookReq.body)
            .pipe(take(1), takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#ephemeralMsgSrv.show({
                    type: MessageType.success,
                    msgKey: 'WEBHOOKS.TOOLTIP.SUCCESS'
                });
                this.#webhookSrv.webhook.load(this.$webhook().id);
                this.form.markAsPristine();
            });
    }
}
