import { Metadata } from '@OneboxTM/utils-state';
import { B2bClientUser, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, openDialog, SearchTableChangeEvent, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, switchMap, takeUntil } from 'rxjs/operators';
import { ApiKeyB2bClientUserDialogComponent } from './api-key-b2b-client-user-dialog/api-key-b2b-client-user-dialog.component';
import {
    CreateUpdateB2bClientUserDialogComponent
} from './create-update-b2b-client-user-dialog/create-update-b2b-client-user-dialog.component';
import {
    CreateOrUpdateB2bClientUserDialogActions, CreateOrUpdateB2bClientUserDialogData, CreateOrUpdateB2bClientUserDialogReturnData
} from './models/create-update-b2b-client-user-dialog-data.model';

const PAGE_SIZE = 20;

@Component({
    selector: 'app-b2b-client-users-management',
    templateUrl: './b2b-client-users-management.component.html',
    styleUrls: ['./b2b-client-users-management.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class B2bClientUsersManagementComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _isOperatorUser: boolean;
    private _entityId: number;
    private _b2bClientId: number;
    @ViewChild('clientUsers')
    private _clientUsersTable: SearchTableComponent<IdName>;

    b2bClientUsers$: Observable<B2bClientUser[]>;
    metadata$: Observable<Metadata>;
    isInProgress$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly columns = ['name', 'email', 'username', 'creation_date', 'type', 'actions'];
    readonly pageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly dialogActions = CreateOrUpdateB2bClientUserDialogActions;

    constructor(
        private _b2bSrv: B2bService,
        private _auth: AuthenticationService,
        private _breakpointObserver: BreakpointObserver,
        private _matDialog: MatDialog,
        private _msgDialogService: MessageDialogService,
        private _ephemeralMsg: EphemeralMessageService
    ) { }

    ngOnInit(): void {
        combineLatest([
            this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]),
            this._b2bSrv.getB2bClient$().pipe(filter(b2bClient => !!b2bClient))
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([isOperator, b2bClient]) => {
                this._isOperatorUser = isOperator;
                this._entityId = b2bClient.entity?.id;
                this._b2bClientId = b2bClient.id;
            });
        this.initComponentModels();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._b2bSrv.clearB2bClientUsersList();
    }

    openCreateOrUpdateB2bClientUserDialog(action: CreateOrUpdateB2bClientUserDialogActions, b2bClientUser?: B2bClientUser): void {
        const dialogData: CreateOrUpdateB2bClientUserDialogData = {
            action,
            b2bClientUserId: b2bClientUser?.id,
            b2bClientId: this._b2bClientId
        };
        this._matDialog.open<CreateUpdateB2bClientUserDialogComponent, CreateOrUpdateB2bClientUserDialogData,
            CreateOrUpdateB2bClientUserDialogReturnData>(
                CreateUpdateB2bClientUserDialogComponent, new ObMatDialogConfig(dialogData)
            )
            .beforeClosed()
            .subscribe(dialogReturnData => {
                if (dialogReturnData?.actionPerformed) {
                    if (dialogReturnData?.newB2bClientUserId) {
                        this._msgDialogService.showSuccess({
                            size: DialogSize.SMALL,
                            title: 'TITLES.CREDENTIALS_SENT',
                            message: 'B2B_CLIENTS.USERS_MANAGEMENT.USER_CREATION_SUCCESS',
                            actionLabel: 'FORMS.ACTIONS.OK'
                        });
                    } else {
                        this._ephemeralMsg.showSuccess({ msgKey: 'B2B_CLIENTS.USERS_MANAGEMENT.EDIT_USER_SUCCESS' });
                    }

                    this._clientUsersTable.changePage({
                        pageIndex: dialogReturnData?.newB2bClientUserId ? 0 : this._clientUsersTable.page
                    }, true);
                }
            });
    }

    openDeleteB2bClientUserDialog(b2bClientUser: B2bClientUser): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_B2B_CLIENT_USER',
            message: 'B2B_CLIENTS.USERS_MANAGEMENT.DELETE_USER_WARNING',
            messageParams: { b2bClientUserName: b2bClientUser.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._isOperatorUser
                    ? this._b2bSrv.deleteB2bClientUser(this._b2bClientId, b2bClientUser.id, this._entityId)
                    : this._b2bSrv.deleteB2bClientUser(this._b2bClientId, b2bClientUser.id))
            )
            .subscribe(() => {
                this._ephemeralMsg.showSuccess({ msgKey: 'B2B_CLIENTS.USERS_MANAGEMENT.DELETE_USER_SUCCESS' });
                this._clientUsersTable.changePage({ pageIndex: this._clientUsersTable.page }, true);
            });
    }

    openRegeneratePasswordDialog(b2bClientUser: B2bClientUser): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.REGENERATE_PASSWORD',
            message: 'B2B_CLIENTS.USERS_MANAGEMENT.REGENERATE_PASSWORD_WARNING',
            actionLabel: 'FORMS.ACTIONS.OK',
            showCancelButton: true
        })
            .pipe(
                filter(accepted => !!accepted),
                switchMap(_ => this._isOperatorUser
                    ? this._b2bSrv.regenerateB2bClientUserPassword(this._b2bClientId, b2bClientUser.id, this._entityId)
                    : this._b2bSrv.regenerateB2bClientUserPassword(this._b2bClientId, b2bClientUser.id))
            )
            .subscribe(() => {
                this._ephemeralMsg.showSuccess({ msgKey: 'B2B_CLIENTS.USERS_MANAGEMENT.REGENERATE_PASSWORD_SUCCESS' });
            });
    }

    openViewApiKeyDialog(b2bClientUser: B2bClientUser): void {
        const { client_id: clientId, id: clientUserId, api_key: apiKey } = b2bClientUser;
        const operatorEntityId = this._isOperatorUser ? this._entityId : null;

        openDialog(this._matDialog, ApiKeyB2bClientUserDialogComponent, { clientId, clientUserId, apiKey, operatorEntityId })
            .afterClosed()
            .pipe(filter(Boolean))
            .subscribe(() => this._clientUsersTable.changePage({ pageIndex: this._clientUsersTable.page }, true));
    }

    loadB2bClientUsers({ limit, offset, q }: SearchTableChangeEvent): void {
        const request: Record<string, unknown> = { limit, offset, q };
        if (this._isOperatorUser) {
            request['entity_id'] = this._entityId;
        }
        this._b2bSrv.loadB2bClientUsersList(this._b2bClientId, request);
    }

    private initComponentModels(): void {
        this.b2bClientUsers$ = this._b2bSrv.getB2bClientUsersListData$().pipe(filter(b2bClientUsers => !!b2bClientUsers));
        this.metadata$ = this._b2bSrv.getB2bClientUsersListMetadata$().pipe(filter(metadata => !!metadata));
        this.isInProgress$ = booleanOrMerge([
            this._b2bSrv.isB2bClientUsersListLoading$(),
            this._b2bSrv.isB2bClientUserInProgress$(),
            this._b2bSrv.isB2bClientUserPasswordInProgress$()
        ]);
    }

}
