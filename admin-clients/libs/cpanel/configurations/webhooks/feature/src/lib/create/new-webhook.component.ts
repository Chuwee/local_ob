import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { entitiesProviders, EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    PostWebhook, WebhookApi, WebhookScope, WebhookService, WebhookState, WebhookStatus
} from '@admin-clients/cpanel/shared/feature/webhook';
import { Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { EntitiesFilterFields, Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, OnInit } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { distinctUntilChanged, tap } from 'rxjs';

@Component({
    selector: 'app-new-webhook',
    providers: [entitiesProviders, WebhookApi, WebhookState, WebhookService],
    imports: [
        TranslatePipe, MatIconModule, ReactiveFormsModule, MatFormFieldModule, AsyncPipe,
        MatInputModule, MatSelectModule, MatProgressSpinnerModule, FormControlErrorsComponent,
        MatButtonModule, MatDialogModule, ObFormFieldLabelDirective, SelectSearchComponent
    ],
    templateUrl: './new-webhook.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewWebhookComponent implements OnInit {
    readonly #dialogRef = inject(MatDialogRef<NewWebhookComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #operatorsSrv = inject(OperatorsService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #webhookSrv = inject(WebhookService);
    readonly #messageDialogService = inject(MessageDialogService);

    readonly form = this.#fb.group({
        operator: [null as Operator, [Validators.required]],
        entity: [null as Entity, [Validators.required]],
        internalName: [null as string, [Validators.required]],
        scope: [null as WebhookScope, [Validators.required]]
    });

    readonly scopes = [WebhookScope.operator, WebhookScope.sysAdmin];

    readonly operators$ = this.#operatorsSrv.operators.getData$();
    readonly entities$ = this.#entitiesSrv.entityList.getData$();

    readonly #$operatorFormValue = toSignal(this.form.get('operator').valueChanges.pipe(distinctUntilChanged()));
    readonly #$entityFormValue = toSignal(this.form.get('entity').valueChanges.pipe(distinctUntilChanged()));

    readonly #$isOperatorScopeDisabled = computed(() => {
        const operator = this.#$operatorFormValue();
        const entity = this.#$entityFormValue();

        if (!operator || !entity) return true;

        return operator.id !== entity.id;
    });

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#entitiesSrv.entityList.inProgress$(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#operatorsSrv.operators.loading$()
    ]));

    constructor() {
        effect(() => {
            const operator = this.#$operatorFormValue();
            if (this.#$operatorFormValue()) {
                this.#loadEntitiesList(operator);
                this.form.get('entity').enable();
            } else {
                this.form.get('entity').disable();
            }
        });

        effect(() => {
            const isOperatorDisabled = this.#$isOperatorScopeDisabled();
            const currentScope = this.form.get('scope').value;

            if (currentScope === WebhookScope.operator && isOperatorDisabled) {
                this.form.get('scope').patchValue(null);
            }
        });
    }

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;

        this.#operatorsSrv.operators.load({ limit: 999, sort: 'name:asc' });
    }

    createWebhook(): void {
        if (this.form.valid) {
            this.#createWebhook();
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    isScopeDisabled(scope: WebhookScope): boolean {
        if (scope === WebhookScope.operator) return this.#$isOperatorScopeDisabled();
        return false;
    }

    close(): void {
        this.#dialogRef.close();
    }

    #createWebhook(): void {
        const { internalName, entity, operator, scope } = this.form.value;
        this.#webhookSrv.webhook.create$({
            status: WebhookStatus.active,
            notification_url: '',
            events: [],
            scope,
            entity_id: entity.id,
            operator_id: operator.id,
            internal_name: internalName
        } as PostWebhook)
            .pipe(tap((webhook => this.#webhookSrv.webhook.load(webhook.id))))
            .subscribe({
                next: webhook => this.#dialogRef.close(webhook.id),
                error: error => {
                    if (error.error.code === 'OPERATOR_SCOPE_WEBHOOK_ALREADY_EXISTS') {
                        this.#messageDialogService.showAlert({
                            size: DialogSize.SMALL,
                            title: 'WEBHOOKS.OPERATOR_SCOPE_ERROR_TITLE',
                            message: 'WEBHOOKS.OPERATOR_SCOPE_ERROR_MESSAGE'
                        });
                    }
                }
            });
    }

    #loadEntitiesList(operator: Operator): void {
        this.#entitiesSrv.entityList.load({
            limit: 999,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            include_entity_admin: true,
            operator_id: operator.id
        });
    }
}
