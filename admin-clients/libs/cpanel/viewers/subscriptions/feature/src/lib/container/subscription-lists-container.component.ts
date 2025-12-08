import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    SubscriptionListLoadCase, SubscriptionListsService, SubscriptionListsApi
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { EphemeralMessageService, ObDialogService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { first, map, shareReplay } from 'rxjs/operators';
import { NewSubscriptionListsDialogComponent } from '../create/new-subscription-lists-dialog.component';
import { SubscriptionListsStateMachine } from '../subscription-lists-state-machine';

@Component({
    selector: 'app-subscription-lists-container',
    templateUrl: './subscription-lists-container.component.html',
    styleUrls: ['./subscription-lists-container.component.scss'],
    providers: [
        SubscriptionListsApi,
        SubscriptionListsService,
        SubscriptionListsStateMachine
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionListsContainerComponent {
    private readonly _subscriptionListSrv = inject(SubscriptionListsService);
    private readonly _obDialogSrv = inject(ObDialogService);
    private readonly _ephemeralMessageService = inject(EphemeralMessageService);
    private readonly _subscriptionListSM = inject(SubscriptionListsStateMachine);

    readonly smallDevice$ = inject(BreakpointObserver)
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(result => result.matches));

    readonly isLoading$ = booleanOrMerge([
        this._subscriptionListSrv.isSubscriptionListLoading$(),
        this._subscriptionListSrv.isSubscriptionListsListLoading$()
    ]);

    readonly subscriptionLists$ = this._subscriptionListSrv.getSubscriptionListsListData$();
    readonly canLoggedUserWrite$ = inject(AuthenticationService).getLoggedUser$().pipe(
        first(user => user !== null),
        map(user => AuthenticationService.isSomeRoleInUserRoles(
            user, [UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR, UserRoles.CNL_MGR, UserRoles.CRM_MGR]
        )),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    openNewSubscriptionListDialog(): void {
        this._obDialogSrv.open(NewSubscriptionListsDialogComponent).beforeClosed()
            .subscribe(subscriptionListId => {
                if (subscriptionListId) {
                    this._ephemeralMessageService.showSuccess({ msgKey: 'SUBSCRIPTION_LIST.CREATE_SUCCESS' });
                    this._subscriptionListSM.setCurrentState({
                        state: SubscriptionListLoadCase.loadSubscriptionList,
                        idPath: String(subscriptionListId)
                    });
                }
            });
    }
}
