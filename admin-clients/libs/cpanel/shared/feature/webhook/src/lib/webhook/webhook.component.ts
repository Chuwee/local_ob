import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CopyTextComponent, EphemeralMessageService, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { atLeastOneRequiredInFormGroup } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AbstractControl, FormGroupDirective, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, switchMap, tap } from 'rxjs';
import { GetWebhooksOptions, Webhook, WebhookEvents, WebhookForm, WebhookStatus } from '../webhook.model';
import { WebhookService } from '../webhook.service';

// eslint-disable-next-line max-len
const URL_PATTERN = '((https?:\\/\\/)?(www\\.)?)?[a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9.](([a-zA-Z0-9][a-zA-Z0-9-]{1,61}[a-zA-Z0-9.])*)?[a-zA-Z0-9]{2,}(:[0-9]{1,5})?(\\/[\\S]*)?';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    selector: 'app-webhook',
    templateUrl: './webhook.component.html',
    styleUrls: ['./webhook.component.scss'],
    imports: [
        CommonModule, FlexLayoutModule, ReactiveFormsModule, TranslatePipe, MaterialModule,
        FormControlErrorsComponent, CopyTextComponent, HelpButtonComponent
    ]
})
export class WebhookComponent implements OnInit, AfterViewInit {
    readonly #fb = inject(UntypedFormBuilder);
    readonly #form = inject(FormGroupDirective);
    readonly #webhookSrv = inject(WebhookService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    $id = input<string>('', { alias: 'id' });
    $options = input<GetWebhooksOptions>(null, { alias: 'options' });
    $isSysAdminForm = input<boolean>(true, { alias: 'isSysAdminForm' });

    #webhook: Webhook;
    static formGroupName = 'webhook';
    webhook$: Observable<Webhook>;
    loading$: Observable<boolean>;
    form: UntypedFormGroup;
    webhookEvents = Object.values(WebhookEvents);

    ngOnInit(): void {
        this.#webhookSrv.webhook.clear();
        this.webhookEvents = this.$options()?.events || this.webhookEvents;

        this.form = this.#fb.group({
            enabled: null,
            url: [{ value: null, disabled: this.$isSysAdminForm() }, [Validators.required, Validators.pattern(URL_PATTERN)]],
            events: this.#fb.group(
                this.webhookEvents.reduce((acc, event) =>
                    (acc[event] = { value: null, disabled: this.$isSysAdminForm() }, acc), {}
                ),
                { validators: [atLeastOneRequiredInFormGroup()] }
            ),
            api_key: ''
        });

        if (this.$isSysAdminForm()) {
            this.form.get('enabled').valueChanges.pipe(
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(enabled => {
                if (enabled) {
                    this.form.get('url').enable();
                    this.form.get('events').enable();
                } else {
                    this.form.get('url').disable();
                    this.form.get('events').disable();
                }
            });
        }

        this.#form.control.setControl(WebhookComponent.formGroupName, this.form);

        this.webhook$ = this.#webhookSrv.webhook.get$();
        this.loading$ = this.#webhookSrv.webhook.inProgress$();

        this.webhook$.pipe(
            filter(webhook => !!webhook),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(webhook => {
            this.form?.reset({
                enabled: webhook.status === WebhookStatus.active,
                url: webhook.notification_url,
                events: webhook.events?.reduce((acc, event) => (acc[event] = true, acc), {}),
                api_key: webhook.api_key
            } as WebhookForm);
            this.#webhook = webhook;
        });

    }

    ngAfterViewInit(): void {
        if (this.$id()) {
            this.#webhookSrv.webhook.load(this.$id());
        }
    }

    get apiKeyCtrl(): AbstractControl {
        return this.form.get('api_key');
    }

    refresh(): void {
        this.#webhookSrv.webhook.refreshApiKey$(this.#webhook.id)
            .pipe(
                tap(updatedWebhook => this.apiKeyCtrl.setValue(updatedWebhook.api_key)),
                switchMap(updatedWebhook => this.#webhookSrv.getUpdatedWebhooksList(updatedWebhook))
            )
            .subscribe(listResponse => {
                if (listResponse) {
                    this.#webhookSrv.webhooks.applyWebhooksUpdate(listResponse);
                }
                this.#ephemeralMsgSrv.showSuccess({ msgKey: `WEBHOOKS.EDIT.SUCCESS` });
            });
    }
}
