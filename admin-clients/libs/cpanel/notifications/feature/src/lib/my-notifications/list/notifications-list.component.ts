import { getRangeParam } from '@OneboxTM/utils-http';
import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetNotificationsRequest, NotificationEmails, NotificationStatus, NotificationsService, aggDataNotifications
} from '@admin-clients/cpanel/notifications/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    DateRangeShortcut, DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, ListFilteredComponent,
    ListFiltersService, SortFilterComponent, PaginatorComponent, FilterItem,
    SearchInputComponent, PopoverFilterDirective, PopoverComponent,
    EmptyStateComponent, AggregatedDataComponent, ChipsFilterDirective, ChipsComponent,
    ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, AggregationMetrics, AggregatedData } from '@admin-clients/shared/data-access/models';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgIf, AsyncPipe, NgClass } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ExtendedModule } from '@angular/flex-layout/extended';
import { FlexModule } from '@angular/flex-layout/flex';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, combineLatest, first, map } from 'rxjs';
import { NewNotificationDialogComponent } from '../create/new-notification-dialog.component';
import { NotificationsListFilterComponent } from './filter/notifications-list-filter.component';

const PAGE_SIZE = 20;
const writeRoles = [UserRoles.OPR_MGR, UserRoles.ENT_MGR];
@Component({
    selector: 'app-notifications-list',
    templateUrl: './notifications-list.component.html',
    styleUrls: ['./notifications-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, NgIf, MaterialModule, NgClass,
        PopoverComponent, PopoverFilterDirective,
        NotificationsListFilterComponent, SearchInputComponent, PaginatorComponent,
        ChipsComponent, ChipsFilterDirective, AggregatedDataComponent, ContextNotificationComponent,
        ExtendedModule, RouterLink, EmptyStateComponent, TranslatePipe, AsyncPipe, LocalDateTimePipe
    ]
})
export class NotificationsListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    private _onDestroy = new Subject<void>();
    private _request: GetNotificationsRequest;
    private _sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(NotificationsListFilterComponent) private _filterComponent: NotificationsListFilterComponent;

    initSortCol = 'summary.sent_date';
    initSortDir: SortDirection = 'desc';
    notificationsPageSize = PAGE_SIZE;
    dateTimeFormats = DateTimeFormats;
    dateRangeShortcut = DateRangeShortcut;
    aggDataNotification: AggregationMetrics = aggDataNotifications;
    notificationStatus = NotificationStatus;
    hasAppliedFilters = false;
    notifications$: Observable<NotificationEmails[]>;
    notificationsMetadata$: Observable<Metadata>;
    notificationsAggregatedData$: Observable<AggregatedData>;
    loadingData$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly canSelectEntity$ = this._authSrv.canReadMultipleEntities$().pipe(first());
    readonly canWrite$ = this._authSrv.hasLoggedUserSomeRoles$(writeRoles).pipe(first());

    readonly displayedColumns$ = combineLatest([this.canSelectEntity$, this._authSrv.getLoggedUser$()])
        .pipe(
            first(),
            map(([canSelectEntity, user]) => {
                if (canSelectEntity) {
                    return ['name', 'entity', 'created_date', 'summary.sent_date', 'summary.total', 'status', 'actions'];
                } else {
                    this._entitiesService.loadEntity(user.entity.id);
                    return ['name', 'created_date', 'summary.sent_date', 'summary.total', 'status', 'actions'];
                }
            })
        );

    constructor(
        private _notificationsService: NotificationsService,
        private _ref: ChangeDetectorRef,
        private _breakpointObserver: BreakpointObserver,
        private _authSrv: AuthenticationService,
        private _entitiesService: EntitiesBaseService,
        private _matDialog: MatDialog,
        private _router: Router,
        private _msgDialogService: MessageDialogService,
        private _ephemeralMsg: EphemeralMessageService
    ) {
        super();
    }

    ngOnInit(): void {
        this.notifications$ = this._notificationsService.getNotificationsListData$();
        this.notificationsMetadata$ = this._notificationsService.getNotificationsListMetadata$();
        this.notificationsAggregatedData$ = this._notificationsService.getNotificationsListAggregatedData$();
        this.loadingData$ = this._notificationsService.isNotificationsListLoading$();
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    override ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesService.clearEntity();
    }

    loadData(filters: FilterItem[]): void {
        this.hasAppliedFilters = false;
        this._request = {
            limit: this.notificationsPageSize,
            aggs: true,
            offset: 0
        };
        let start, end;
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                this.hasAppliedFilters = true;
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        this.hasAppliedFilters = false;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        this.hasAppliedFilters = false;
                        break;
                    case 'SEARCH_INPUT':
                        this._request.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this._request.entity_id = values[0].value;
                        break;
                    case 'STATUS':
                        this._request.status = values.map(val => val.value);
                        break;
                    case 'START_DATE':
                        start = values[0].value;
                        break;
                    case 'END_DATE':
                        end = values[0].value;
                        break;
                }
            }
        });
        this._request.sent_date = getRangeParam(start, end);
        this.loadNotifications();
    }

    openNewNotificationDialog(): void {
        this._matDialog.open(NewNotificationDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(notificationCode => {
                if (notificationCode) {
                    this._router.navigate(['/notifications/', notificationCode.code]);
                }
            });
    }

    openDeleteNotificationDialog(notification: NotificationEmails): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_EMAIL_NOTIFICATION',
            message: 'NOTIFICATIONS.EMAIL_NOTIFICATION.DELETE_WARNING_MESSAGE',
            messageParams: { notificationName: notification.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._notificationsService.deleteNotification(notification.code)
                        .subscribe(() => {
                            this._ephemeralMsg.showSuccess({
                                msgKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.DELETE_SUCCESS',
                                msgParams: { notificationName: notification.name }
                            });
                            this.loadNotifications();
                        });
                }
            });
    }

    private loadNotifications(): void {
        this._notificationsService.loadNotificationsList(this._request);
        this._ref.detectChanges();
    }
}
