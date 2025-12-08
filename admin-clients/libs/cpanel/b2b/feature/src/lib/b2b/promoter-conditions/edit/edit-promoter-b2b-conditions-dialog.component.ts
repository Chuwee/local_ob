import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    B2bConditions, B2bService,
    PutB2bConditionsClients,
    VmB2bConditionsClient,
    VmEditPromoterConditionsData
} from '@admin-clients/cpanel/b2b/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, viewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { B2bConditionsFormComponent } from '../../generic-conditions-form/b2b-conditions-form.component';

@Component({
    selector: 'app-edit-promoter-b2b-conditions-dialog',
    templateUrl: './edit-promoter-b2b-conditions-dialog.component.html',
    styleUrls: ['./edit-promoter-b2b-conditions-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, B2bConditionsFormComponent,
        MatProgressSpinner, MatIcon, MatButton, MatDialogModule, MatIconButton
    ]
})
export class EditPromoterB2bConditionsDialogComponent implements OnInit, OnDestroy {

    readonly #dialogRef = inject(MatDialogRef<EditPromoterB2bConditionsDialogComponent, boolean>);
    readonly #fb = inject(FormBuilder);
    readonly #b2bSrv = inject(B2bService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #data = inject<VmEditPromoterConditionsData>(MAT_DIALOG_DATA);

    private readonly _$conditionsFormComponent = viewChild(B2bConditionsFormComponent);

    form = this.#fb.group({});
    inProgress$: Observable<boolean>;
    selectedClients: VmB2bConditionsClient[];
    hasCustomConditions: boolean;
    noEventConditionsFound$: Observable<boolean>;
    getConditions$: () => Observable<B2bConditions>;
    currencies: string[];

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.currencies = [this.#data.contextCurrency];
    }

    ngOnInit(): void {
        this.#model();
        this.#refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this.#b2bSrv.clearConditions();
    }

    saveConditions(): void {
        if (this.form.valid) {
            const message = this.selectedClients ?
                'PROFESSIONAL_SELLING.EDIT_B2B_CLIENTS_CONDITIONS_CONFIRM_MSG' :
                'PROFESSIONAL_SELLING.EDIT_EVENT_B2B_CONDITIONS_CONFIRM_MSG';
            this.#msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.ALERT',
                message,
                actionLabel: 'FORMS.ACTIONS.OK',
                showCancelButton: true
            })
                .pipe(
                    filter(accepted => !!accepted),
                    switchMap(() => {
                        const conditions = this._$conditionsFormComponent().getNormalizedConditions();
                        if (this.selectedClients) {
                            const clientsConditions: PutB2bConditionsClients = {
                                id: this.#data.contextId,
                                clients: this.selectedClients.map(({ id }) => ({ id, conditions }))
                            };
                            return this.#b2bSrv.saveConditionsClients('EVENT', clientsConditions);
                        } else {
                            return this.#b2bSrv.saveConditions('EVENT', {
                                id: this.#data.contextId,
                                conditions
                            });
                        }
                    })
                )
                .subscribe(() => this.close(true));
        } else {
            this.form.markAllAsTouched();
            this.form.patchValue(this.form.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    restoreInitialConditions(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.ALERT',
            message: 'PROFESSIONAL_SELLING.DELETE_B2B_CLIENTS_CONDITIONS_CONFIRM_MSG',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(() => this.#b2bSrv.deleteConditionsClients('EVENT', {
                    ...(this.#data.context === 'SEASON_TICKET' && { season_ticket_id: this.#data.contextId }),
                    ...(this.#data.context === 'EVENT' && { event_id: this.#data.contextId }),
                    clients_ids: this.selectedClients
                        .filter(client => client.condHierarchicalLevel === 'CLIENT_B2B_EVENT')
                        .map(client => client.id)
                }))
            )
            .subscribe(() => this.close(true));
    }

    deleteConditions(): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.ALERT',
            message: 'PROFESSIONAL_SELLING.DELETE_EVENT_B2B_CONDITIONS_CONFIRM_MSG',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(() => this.#b2bSrv.deleteConditions('EVENT', {
                    ...(this.#data.context === 'SEASON_TICKET' && { season_ticket_id: this.#data.contextId }),
                    ...(this.#data.context === 'EVENT' && { event_id: this.#data.contextId })
                }))
            )
            .subscribe(() => this.close(true));
    }

    close(actionPerformed = false): void {
        this.#dialogRef.close(actionPerformed);
    }

    #model(): void {
        this.getConditions$ = () => this.#b2bSrv.getConditions$();
        this.inProgress$ = booleanOrMerge([
            this.#b2bSrv.isConditionsInProgress$(),
            this.#b2bSrv.isConditionsClientsInProgress$()
        ]);
        this.selectedClients = this.#data.selectedClients;
        this.hasCustomConditions = this.selectedClients?.some(client => client.condHierarchicalLevel === 'CLIENT_B2B_EVENT');
        this.noEventConditionsFound$ = this.#b2bSrv.getConditions$()
            .pipe(map(conditions => !conditions));
    }

    #refreshFormDataHandler(): void {
        if (!this.selectedClients?.length) {
            this.#b2bSrv.loadConditions('EVENT', {
                ...(this.#data.context === 'SEASON_TICKET' && { season_ticket_id: this.#data.contextId }),
                ...(this.#data.context === 'EVENT' && { event_id: this.#data.contextId })
            });
        }
    }
}
