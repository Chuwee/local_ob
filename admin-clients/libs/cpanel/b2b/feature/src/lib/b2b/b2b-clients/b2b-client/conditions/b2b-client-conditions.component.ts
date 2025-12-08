import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    B2bConditionsClient, B2bService, PutB2bConditionsClients
} from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup } from '@angular/forms';
import { firstValueFrom, Observable, throwError } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';
import { B2bConditionsFormComponent } from '../../../generic-conditions-form/b2b-conditions-form.component';

@Component({
    selector: 'app-b2b-client-conditions',
    templateUrl: './b2b-client-conditions.component.html',
    styleUrls: ['./b2b-client-conditions.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientConditionsComponent implements OnInit, OnDestroy {
    #entityId: number;
    #clientId: number;

    readonly #fb = inject(FormBuilder);
    readonly #b2bSrv = inject(B2bService);
    readonly #entitySrv = inject(EntitiesBaseService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #authSrv = inject(AuthenticationService);

    @ViewChild(B2bConditionsFormComponent)
    private _conditionsFormComponent: B2bConditionsFormComponent;

    readonly $currencies = toSignal(this.#authSrv.getLoggedUser$()
        .pipe(
            filter(Boolean),
            map(user => AuthenticationService.operatorCurrencyCodes(user) ?? [user.currency])
        )
    );

    form: FormGroup;
    isInProgress$: Observable<boolean>;
    noClientConditionsFound$: Observable<boolean>;
    getConditions$: () => Observable<B2bConditionsClient>;

    async ngOnInit(): Promise<void> {
        this.initForm();
        await this.model();
        this.loadEntityConditionsClient();
    }

    ngOnDestroy(): void {
        this.#b2bSrv.clearConditions();
    }

    cancel(): void {
        this.loadEntityConditionsClient();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const conditions = this._conditionsFormComponent.getNormalizedConditions();
            const clientsConditions: PutB2bConditionsClients = {
                id: this.#entityId,
                clients: [{ id: this.#clientId, conditions }]
            };
            return this.#b2bSrv.saveConditionsClients('ENTITY', clientsConditions)
                .pipe(tap(() => this.#ephemeralMsgSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    saveConditions(): void {
        this.save$().subscribe(() => this.loadEntityConditionsClient());
    }

    deleteEntityClientConditions(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.ALERT',
            message: 'B2B_CLIENTS.CONDITIONS.DELETE_B2B_CLIENT_CONDITIONS_CONFIRM_MSG',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(() => this.#b2bSrv.deleteConditionsClients('ENTITY', {
                    entity_id: this.#entityId,
                    clients_ids: [this.#clientId]
                }))
            )
            .subscribe(() => {
                this.#ephemeralMsgSrv.showDeleteSuccess();
                this.loadEntityConditionsClient();
            });
    }

    private initForm(): void {
        this.form = this.#fb.group({});
    }

    private async model(): Promise<void> {
        this.getConditions$ = () => this.#b2bSrv.getConditionsClient$()
            .pipe(
                map(cond => cond?.condition_group_type === 'CLIENT_B2B' ? cond : null)
            );
        this.isInProgress$ = booleanOrMerge([
            this.#b2bSrv.isConditionsClientInProgress$(), // load
            this.#b2bSrv.isConditionsClientsInProgress$() // save || delete
        ]);
        this.noClientConditionsFound$ = this.#b2bSrv.getConditionsClient$()
            .pipe(map(conditions => conditions?.condition_group_type !== 'CLIENT_B2B'));

        const b2bClient = await firstValueFrom(this.#b2bSrv.getB2bClient$().pipe(first(b2bClient => !!b2bClient)));
        this.#entityId = b2bClient.entity?.id;
        this.#clientId = b2bClient.client_id;
    }

    private loadEntityConditionsClient(): void {
        this.#entitySrv.loadEntity(this.#entityId);
        this.#b2bSrv.loadConditionsClient('ENTITY', this.#clientId, { entity_id: this.#entityId });
    }
}
