import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    SubscriptionListFilter, SubscriptionListStatus, SubscriptionListsService
} from '@admin-clients/cpanel/viewers/subscriptions/data-access';
import { EntitiesBaseService, Entity, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import { AfterViewInit, ChangeDetectionStrategy, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { SatPopoverComponent } from '@ncstate/sat-popover';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import { first, map, shareReplay, switchMap, takeUntil } from 'rxjs/operators';
import { SubscriptionListsStateMachine } from '../../subscription-lists-state-machine';

@Component({
    selector: 'app-subscription-list-filter',
    templateUrl: './subscription-list-filter.component.html',
    styleUrls: ['./subscription-list-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SubscriptionListFilterComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('filterPopover')
    private _filterPopover: SatPopoverComponent;

    private _onDestroy: Subject<void> = new Subject();
    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();

    entities$: Observable<Entity[]>;
    entityId$: Observable<number>;
    statusList = Object.values(SubscriptionListStatus)
        .map(type => ({ id: type, name: `SUBSCRIPTION_LIST.STATUS_OPTS.${type.toUpperCase()}` }));

    filtersForm: UntypedFormGroup;

    constructor(
        private _fb: UntypedFormBuilder,
        private _subscriptionListSrv: SubscriptionListsService,
        private _subscriptionListSM: SubscriptionListsStateMachine,
        private _entitiesSrv: EntitiesBaseService,
        private _auth: AuthenticationService
    ) { }

    ngOnInit(): void {
        // Init reactive form:
        this.filtersForm = this._fb.group({
            entityId: null,
            status: [],
            q: null
        });
        // check if logged user has write permissions
        this.entityId$ = this._auth.getLoggedUser$().pipe(
            first(user => !!user),
            map(user => user.entity.id,
                shareReplay(1))
        );

        this.entities$ = combineLatest([this.canSelectEntity$, this._auth.getLoggedUser$().pipe(first(Boolean))]).pipe(
            switchMap(([canSelectEntity, user]) => {
                if (canSelectEntity) {
                    this._entitiesSrv.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name]
                    });
                    return this._entitiesSrv.entityList.getData$();
                }
                this.filtersForm.patchValue({ entity: user.entity });
                return of([]);
            }),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );
    }

    ngAfterViewInit(): void {
        this.onApplyBtnClick();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    onCloseBtnClick(): void {
        this._filterPopover.close();
    }

    onApplyBtnClick(): void {
        const filters: SubscriptionListFilter = {};

        if (this.filtersForm.value.entityId) {
            filters.entityId = this.filtersForm.value.entityId;
        }
        if (this.filtersForm.value.status) {
            filters.status = this.filtersForm.value.status;
        }
        if (this.filtersForm.value.q) {
            filters.q = this.filtersForm.value.q;
        }
        this._subscriptionListSrv.setSubscriptionListFilters(filters);
        this._subscriptionListSM.clearCurrentState();
        this.onCloseBtnClick();
    }

    onRemoveFiltersBtnClick(): void {
        this.filtersForm.reset();
        this.onApplyBtnClick();
    }

}
