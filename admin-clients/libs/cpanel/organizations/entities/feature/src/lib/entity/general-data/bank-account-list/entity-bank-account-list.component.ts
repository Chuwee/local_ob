import { EntitiesService, EntityBankAccount } from '@admin-clients/cpanel/organizations/entities/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService, MessageType, ObMatDialogConfig, EmptyStateTinyComponent } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, switchMap } from 'rxjs';
import { BankAccountDialogComponent } from './bank-account-dialog/bank-account-dialog.component';

@Component({
    selector: 'app-entity-bank-account-list',
    templateUrl: './entity-bank-account-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatIcon, MatButton, FormContainerComponent, MatProgressSpinner, MatTable,
        MatHeaderRow, MatRow, MatCell, MatHeaderCell, MatColumnDef, MatHeaderRowDef, MatRowDef, MatHeaderCellDef, MatCellDef,
        MatIconButton, UpperCasePipe, EmptyStateTinyComponent
    ]
})
export class EntityBankAccountListComponent implements OnInit, OnDestroy {
    readonly #matDialog = inject(MatDialog);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #msgDialogService = inject(MessageDialogService);

    readonly $entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean)));
    readonly $bankAccounts = toSignal(this.#entitiesSrv.entityBankAccountList.get$().pipe(filter(Boolean)));
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#entitiesSrv.entityBankAccountList.inProgress$(),
        this.#entitiesSrv.entityBankAccount.inProgress$()
    ]));

    readonly displayedColumns = ['name', 'actions'];

    ngOnInit(): void {
        this.#entitiesSrv.entityBankAccountList.load(this.$entity().id);
    }

    openNewBankAccountDialog(): void {
        this.#matDialog.open(BankAccountDialogComponent, new ObMatDialogConfig({}))
            .afterClosed()
            .pipe(
                filter(Boolean),
                switchMap(bankAccountData =>
                    this.#entitiesSrv.entityBankAccountList.create(this.$entity().id, bankAccountData))
            )
            .subscribe(() => {
                this.#entitiesSrv.entityBankAccountList.load(this.$entity().id);
                this.#ephemeralMsgSrv.show({
                    type: MessageType.success,
                    msgKey: 'ENTITY.BANK_ACCOUNTS.CREATE_SUCCESS'
                });
            });
    }

    openEditBankAccountDialog(bankAccount: EntityBankAccount): void {
        this.#matDialog.open(BankAccountDialogComponent, new ObMatDialogConfig({ bankAccount }))
            .afterClosed()
            .pipe(
                filter(Boolean),
                switchMap(bankAccountData =>
                    this.#entitiesSrv.entityBankAccount.update(this.$entity().id, bankAccount.id, bankAccountData))
            )
            .subscribe(() => {
                this.#entitiesSrv.entityBankAccountList.load(this.$entity().id);
                this.#ephemeralMsgSrv.showSaveSuccess();
            });
    }

    deleteBankAccount(bankAccount: EntityBankAccount): void {
        this.#msgDialogService.showWarn({
            size: DialogSize.MEDIUM,
            title: 'ENTITY.BANK_ACCOUNTS.DELETE_TITLE',
            message: 'ENTITY.BANK_ACCOUNTS.DELETE_DESCRIPTION',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#entitiesSrv.entityBankAccount.delete(this.$entity().id, bankAccount.id))
            )
            .subscribe(() => {
                this.#entitiesSrv.entityBankAccountList.load(this.$entity().id);
                this.#ephemeralMsgSrv.show({
                    type: MessageType.success,
                    msgKey: 'ENTITY.BANK_ACCOUNTS.DELETE_SUCCESS'
                });
            });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityBankAccountList.clear();
    }
}
