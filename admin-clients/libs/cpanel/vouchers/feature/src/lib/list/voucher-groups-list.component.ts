import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, first, map, switchMap } from 'rxjs/operators';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetVoucherGroupsRequest, VoucherGroupStatus,
    VoucherGroupType, VoucherGroup, VouchersService
} from '@admin-clients/cpanel-vouchers-data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, ListFilteredComponent, ListFiltersService,
    SortFilterComponent, PaginatorComponent, SearchInputComponent, FilterItem
} from '@admin-clients/shared/common/ui/components';
import { isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { NewVoucherGroupDialogComponent } from '../create/new-voucher-groups-dialog.component';
import { VoucherGroupsFilterComponent } from './filter/voucher-groups-filter.component';

const writeRoles = [UserRoles.OPR_MGR, UserRoles.CNL_MGR];

@Component({
    selector: 'app-vouchers-list',
    templateUrl: './voucher-groups-list.component.html',
    styleUrls: ['./voucher-groups-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherGroupsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    readonly #voucherService = inject(VouchersService);
    readonly #auth = inject(AuthenticationService);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(VoucherGroupsFilterComponent) private _filterComponent: VoucherGroupsFilterComponent;

    #request: GetVoucherGroupsRequest;
    #sortFilterComponent: SortFilterComponent;
    pageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    voucherGroupStatus = VoucherGroupStatus;
    voucherGroupType = VoucherGroupType;

    readonly metadata$ = this.#voucherService.getVoucherGroupsListMetadata$();
    readonly isLoading$ = this.#voucherService.isVoucherGroupsListLoading$();
    readonly voucherGroups$ = this.#voucherService.getVoucherGroupsListData$().pipe(filter(voucherGroups => !!voucherGroups));
    readonly userCanWrite$ = this.#auth.hasLoggedUserSomeRoles$(writeRoles);
    readonly isHandsetOrTablet$ = isHandsetOrTablet$();

    updateStatus: (id: number, status: VoucherGroupStatus) => Observable<void>;

    readonly canSelectEntity$ = this.#auth.canReadMultipleEntities$();

    readonly displayedColumns$ = this.#auth.getLoggedUser$()
        .pipe(
            first(),
            map(AuthenticationService.operatorCurrencyCodes),
            switchMap(currencies => this.canSelectEntity$
                .pipe(
                    map(canSelectEntity => {
                        const displayedColumns = ['name', 'description', 'entity', 'type', 'validation_method', 'status', 'actions'];
                        if (canSelectEntity) {
                            displayedColumns.splice(1, 0, 'entity');
                        }
                        if (currencies?.length > 1) {
                            displayedColumns.splice(displayedColumns.length - 2, 0, 'currency');
                        }
                        return displayedColumns;
                    })
                ))
        );

    trackByFn = (_: number, item: VoucherGroup): number => item.id;

    ngOnInit(): void {
        this.updateStatus = (id, status) => this.#voucherService.updateVoucherGroupStatus(id, status);
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this.#request = {};
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#request.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#request.limit = values[0].value.limit;
                        this.#request.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#request.q = values[0].value;
                        break;
                    case 'TYPE':
                        this.#request.type = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entity_id = values[0].value;
                        break;
                    case 'CURRENCY':
                        this.#request.currency_code = values[0].value;
                        break;
                }
            }
        });
        this.loadVoucherGroups();
    }

    openNewVoucherGroupDialog(): void {
        this.#matDialog.open(NewVoucherGroupDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(created => !!created))
            .subscribe(id => {
                this.#voucherService.loadVoucherGroup(id);
                this.#voucherService.getVoucherGroup$()
                    .pipe(first(group => !!group))
                    .subscribe(group => {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'VOUCHER_GROUP.CREATE_VOUCHER_GROUP_SUCCESS' });
                        this.#router.navigate(
                            [id, group.type === VoucherGroupType.giftCard ? 'gift-card' : 'group', 'configuration', 'principal-info'],
                            { relativeTo: this.#route }
                        );
                    });
            });
    }

    openDeleteVoucherGroupDialog(voucherGroup: VoucherGroup): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_VOUCHER_GROUP',
            message: 'VOUCHER_GROUP.DELETE_WARNING',
            messageParams: { name: voucherGroup.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#voucherService.deleteVoucherGroup(voucherGroup.id))
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'VOUCHER_GROUP.DELETE_VOUCHER_GROUP_SUCCESS',
                    msgParams: { name: voucherGroup.name }
                });
                this.loadVoucherGroups();
            });
    }

    private loadVoucherGroups(): void {
        this.#voucherService.loadVoucherGroupsList(this.#request);
        this.#ref.detectChanges();
    }

}
