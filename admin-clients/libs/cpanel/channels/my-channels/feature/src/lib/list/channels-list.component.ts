import { Metadata } from '@OneboxTM/utils-state';
import { ChannelListElement, ChannelsService, GetChannelsRequest } from '@admin-clients/cpanel/channels/data-access';
import { User, AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    DialogSize, MessageDialogService, ObMatDialogConfig
    , ListFilteredComponent, ListFiltersService, SortFilterComponent, PaginatorComponent, FilterItem
    , SearchInputComponent,
    EphemeralMessageService
} from '@admin-clients/shared/common/ui/components';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, OnInit, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { NewChannelDialogComponent } from '../create/new-channel-dialog.component';
import { ChannelsListFilterComponent } from './filter/channels-list-filter.component';

@Component({
    selector: 'app-channels-list',
    templateUrl: './channels-list.component.html',
    styleUrls: ['./channels-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    private _isDeleteAllowed = new BehaviorSubject<boolean>(false);
    private _request = new GetChannelsRequest();
    private _sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(ChannelsListFilterComponent) private _filterComponent: ChannelsListFilterComponent;

    channels$: Observable<ChannelListElement[]>;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    channelsPageSize = 20;
    isDeleteAllowed$: Observable<boolean> = this._isDeleteAllowed.asObservable();
    channelsMetadata$: Observable<Metadata>;
    channelsLoading$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly $canCreateChannels = toSignal(this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]));
    readonly $canReadMultipleEntities = toSignal(this._auth.canReadMultipleEntities$());
    readonly $canSelectOperator = toSignal(this._auth.hasLoggedUserSomeRoles$([UserRoles.SYS_MGR]));

    readonly $displayedColumns = computed(() => {
        const columns = ['name', 'status', 'type', 'actions'];
        if (this.$canReadMultipleEntities()) columns.splice(1, 0, 'entity_name');
        if (this.$canSelectOperator()) columns.unshift('operator');
        return columns;
    });

    constructor(
        private _channelsSrv: ChannelsService,
        private _auth: AuthenticationService,
        private _matDialog: MatDialog,
        private _msgDialogSrv: MessageDialogService,
        private _breakpointObserver: BreakpointObserver,
        private _ref: ChangeDetectorRef,
        private _ephemeralMessageService: EphemeralMessageService,
        private _route: ActivatedRoute,
        private _router: Router
    ) {
        super();
        this._auth.getLoggedUser$()
            .pipe(filter(user => user !== null), take(1))
            .subscribe(user => this.configureDeletePermissions(user));
    }

    trackByFn = (_: number, channel: ChannelListElement): number => channel.id;

    ngOnInit(): void {
        this.channelsMetadata$ = this._channelsSrv.channelsList.getMetadata$();
        this.channelsLoading$ = this._channelsSrv.isChannelsListLoading$();
        this.channels$ = this._channelsSrv.channelsList.getList$();
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this._request = new GetChannelsRequest();
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this._request.limit = values[0].value.limit;
                        this._request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this._request.name = values[0].value;
                        break;
                    case 'ENTITY':
                        this._request.entityId = values[0].value;
                        break;
                    case 'OPERATOR':
                        this._request.operatorId = values[0].value;
                        break;
                    case 'STATUS':
                        this._request.status = values.map(val => val.value);
                        break;
                    case 'TYPE':
                        this._request.type = values[0].value;
                        break;
                }
            }
        });

        this.loadChannels();
    }

    openNewChannelDialog(): void {
        this._matDialog.open(NewChannelDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(channelId => {
                if (channelId) {
                    this._router.navigate([channelId, 'general-data'], { relativeTo: this._route });
                }
            });
    }

    openDeleteChannelDialog(channel: ChannelListElement): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_CHANNEL',
            message: 'CHANNELS.DELETE_WARNING',
            messageParams: { channelName: channel.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._channelsSrv.deleteChannel(channel.id.toString())
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'CHANNELS.DELETE_SUCCESS',
                                msgParams: { channelName: channel.name }
                            });
                            this.loadChannels();
                        });
                }
            });
    }

    private loadChannels(): void {
        this._channelsSrv.channelsList.load(this._request);
        this._ref.detectChanges();
    }

    private configureDeletePermissions(user: User): void {
        const isDeleteAllowed = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR, UserRoles.CNL_MGR]);
        this._isDeleteAllowed.next(isDeleteAllowed);
    }

}
