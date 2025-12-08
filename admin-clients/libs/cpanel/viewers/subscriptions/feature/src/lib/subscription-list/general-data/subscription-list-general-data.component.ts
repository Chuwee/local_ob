import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    PutSubscriptionList, SubscriptionListLoadCase, SubscriptionListStatus, SubscriptionListFieldsRestrictions, SubscriptionListsService,
    SubscriptionList
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { distinctUntilChanged, filter, map, shareReplay, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { SubscriptionListsStateMachine } from '../../subscription-lists-state-machine';

@Component({
    selector: 'app-subscription-list-general-data',
    templateUrl: './subscription-list-general-data.component.html',
    styleUrls: ['./subscription-list-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionListGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    private _onDestroy: Subject<void> = new Subject();
    form: UntypedFormGroup;
    isLoadingOrSaving$: Observable<boolean>;
    subscriptionList$: Observable<SubscriptionList>;
    textRestrictions = SubscriptionListFieldsRestrictions;
    canLoggedUserWrite$: Observable<boolean>;
    isSaveCancelDisabled$: Observable<boolean>;

    constructor(
        private _auth: AuthenticationService,
        private _fb: UntypedFormBuilder,
        private _ephemeralMessageService: EphemeralMessageService,
        private _subscriptionListsSrv: SubscriptionListsService,
        private _subscriptionListsSM: SubscriptionListsStateMachine,
        private _msgDialogService: MessageDialogService
    ) {
    }

    ngOnInit(): void {
        this.initForms();
        this.model();
        this.formChangeHandler();
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this.subscriptionList$
            .pipe(
                take(1),
                tap(subscriptionList => {
                    this._subscriptionListsSM.setCurrentState({
                        state: SubscriptionListLoadCase.loadSubscriptionList,
                        idPath: subscriptionList.id.toString()
                    });
                    this.form.markAsPristine();
                    this.form.markAsUntouched();
                })
            ).subscribe();
    }

    save(): void {
        this.save$(true).subscribe(() => {
            this.form.markAsPristine();
            this.form.markAsUntouched();
        });
    }

    save$(...args: unknown[]): Observable<void> {
        const [loadWithNavigation]
            = args as boolean[]; // loadWithNavigation will be true if the save method is executed by the component instead of by the guard.

        return this.subscriptionList$
            .pipe(
                take(1),
                switchMap(subscriptionList => {
                    if (this.form.valid) {
                        const putSubscriptionList: PutSubscriptionList = {
                            ...this.form.value,
                            name: this.form.value.name,
                            description: this.form.value.description,
                            status: this.form.value.status ? SubscriptionListStatus.active : SubscriptionListStatus.inactive,
                            default: this.form.value.default
                        };
                        return this._subscriptionListsSrv.saveSubscriptionList(subscriptionList.id, putSubscriptionList).pipe(
                            tap(() => {
                                this._ephemeralMessageService.showSuccess({ msgKey: 'SUBSCRIPTION_LIST.UPDATE_SUCCESS' });
                                this._subscriptionListsSM.setCurrentState({
                                    state: loadWithNavigation ? SubscriptionListLoadCase.loadSubscriptionList :
                                        SubscriptionListLoadCase.loadWithoutNavigating,
                                    idPath: subscriptionList.id.toString()
                                });
                            })
                        );
                    } else {
                        this.form.markAllAsTouched();
                        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
                        return of(null);
                    }
                })
            );
    }

    private initForms(): void {
        this.form = this._fb.group({
            name: [null, [Validators.required, Validators.maxLength(SubscriptionListFieldsRestrictions.subscriptionListNameLength)]],
            description: [null, [Validators.maxLength(SubscriptionListFieldsRestrictions.subscriptionListDescriptionLength)]],
            status: [null, [Validators.required]],
            default: [false, [Validators.required]]
        });
    }

    private model(): void {
        // Loading
        this.isLoadingOrSaving$ = booleanOrMerge([
            this._subscriptionListsSrv.isSubscriptionListLoading$(),
            this._subscriptionListsSrv.isSubscriptionListsListLoading$()
        ]);

        // Subscription List
        this.subscriptionList$ = this._subscriptionListsSrv.getSubscriptionList$()
            .pipe(
                filter(subscriptionList => subscriptionList !== null),
                shareReplay(1)
            );

        // Logged user
        const loggedUser$ = this._auth.getLoggedUser$()
            .pipe(
                filter(user => user !== null),
                shareReplay(1)
            );

        const writingRoles = [UserRoles.OPR_MGR, UserRoles.ENT_MGR];

        // check if logged user has write permissions
        this.canLoggedUserWrite$ = loggedUser$
            .pipe(
                map(user => AuthenticationService.isSomeRoleInUserRoles(user, writingRoles)),
                shareReplay(1)
            );

        // Save/Cancel condition
        this.isSaveCancelDisabled$ = combineLatest([
            this.isLoadingOrSaving$,
            this.canLoggedUserWrite$,
            this.form.valueChanges
        ]).pipe(
            map(([isLoading, canLoggedUserWrite]) =>
                isLoading || !canLoggedUserWrite || !this.form?.dirty
            ),
            distinctUntilChanged(),
            shareReplay(1)
        );
    }

    private refreshFormDataHandler(): void {
        this.subscriptionList$
            .pipe(
                takeUntil(this._onDestroy)
            ).subscribe(subscriptionList => this.form.patchValue({
                ...subscriptionList,
                status: subscriptionList.status === SubscriptionListStatus.active
            }));

        this.canLoggedUserWrite$
            .pipe(
                take(1)
            ).subscribe(canLoggedUserWrite => {
                if (!canLoggedUserWrite) {
                    this.form.disable();
                }
            });
    }

    private formChangeHandler(): void {
        combineLatest([
            this.subscriptionList$,
            this.form.valueChanges
        ]).pipe(
            filter(([subscriptionList]) => !!subscriptionList),
            tap(([subscriptionList]) => {
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('name'), subscriptionList.name);
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('description'), subscriptionList.description);
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('status'), subscriptionList.status);
                FormControlHandler.checkAndRefreshDirtyState(this.form.get('default'), subscriptionList.default);
            }),
            takeUntil(this._onDestroy)
        ).subscribe();
    }
}
