import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import {
    DialogSize, FilterComponent, FilterItem, FilterItemValue, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { Params } from '@angular/router';
import { filter, firstValueFrom, Observable, of } from 'rxjs';
import { BiImpersonationDialogComponent } from './dialog/bi-impersonation-dialog.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, MatButtonModule, MatIconModule, FlexLayoutModule, ReactiveFormsModule
    ],
    selector: 'app-bi-impersonation',
    templateUrl: './bi-impersonation.component.html'
})
export class BiImpersonationComponent extends FilterComponent implements OnDestroy {
    private readonly _auth = inject(AuthenticationService);
    private readonly _matDialog = inject(MatDialog);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _entityUsersSrv = inject(EntityUsersService);

    private readonly _filterItem = new FilterItem('USER_ID', null);
    private _userId: number;

    readonly selectedUser$ = this._entityUsersSrv.getEntityUser$().pipe(filter(Boolean));

    @Input() onlyIcon = false;

    async openImpersonationDialog(): Promise<void> {
        const loggedUser = await firstValueFrom(this._auth.getLoggedUser$());
        const selectedUser = await firstValueFrom(this.selectedUser$);
        this._matDialog.open<BiImpersonationDialogComponent, { userId: number; entityId: number }, { userId: number }>(
            BiImpersonationDialogComponent, new ObMatDialogConfig({ userId: selectedUser.id, entityId: selectedUser.entity.id }))
            .beforeClosed()
            .subscribe(response => {
                if (!response) return;

                const { userId } = response;

                if (this._userId === userId) return;

                if (userId !== loggedUser.id && loggedUser.reports?.mstr_user_has_subscriptions) {
                    this._msgDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'TITLES.WARNING',
                        message: 'BI_REPORTS.IMPERSONATION.SUBSCRIPTIONS_WARN',
                        actionLabel: 'FORMS.ACTIONS.AGREED',
                        showCancelButton: true
                    })
                        .subscribe(response => {
                            if (!response) return;

                            this._userId = userId;
                            this.filtersSubject.next(this.getFilters());
                            this._entityUsersSrv.loadEntityUser(userId);
                        });
                } else {
                    this._userId = userId;
                    this.filtersSubject.next(this.getFilters());
                    this._entityUsersSrv.loadEntityUser(userId);
                }
            });
    }

    applyFiltersByUrlParams$(params: Params): Observable<FilterItem[]> {
        if (params['userId']) {
            if (Number(params['userId']) !== this._userId) {
                this._entityUsersSrv.loadEntityUser(params['userId']);
            }
        }
        this._userId = Number(params['userId']);
        return of(this.getFilters());
    }

    getFilters(): FilterItem[] {
        if (this._userId) {
            this._filterItem.values = [new FilterItemValue(this._userId, null)];
            this._filterItem.urlQueryParams['userId'] = this._userId.toString();
        } else {
            this._filterItem.values = null;
            this._filterItem.urlQueryParams = {};
        }
        return [this._filterItem];
    }

    removeFilter(): void {
        // noop
    }

    resetFilters(): void {
        // noop
    }
}
