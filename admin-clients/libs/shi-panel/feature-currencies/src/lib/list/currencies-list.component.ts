import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import {
    ListFilteredComponent, ListFiltersService, SortFilterComponent, FilterItem, ContextNotificationComponent, ObMatDialogConfig,
    EphemeralMessageService
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions, UserRoles } from '@admin-clients/shi-panel/utility-models';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, inject, OnDestroy, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { CurrenciesService } from '../currencies.service';
import { Currency } from '../models/currency.model';
import { GetCurrenciesRequest } from '../models/get-currencies-request.model';
import { CurrencyHistoricComponent } from './historic/currency-historic.component';
import { ModifyCurrencyDialogComponent } from './modify/modify-currency-dialog.component';

const PAGE_SIZE = 20;

@Component({
    imports: [CommonModule, TranslatePipe, MaterialModule, ContextNotificationComponent, FlexLayoutModule, EllipsifyDirective],
    providers: [ListFiltersService],
    selector: 'app-currencies-list',
    templateUrl: './currencies-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CurrenciesListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    private readonly _authService = inject(AuthenticationService);
    private readonly _currenciesService = inject(CurrenciesService);
    private readonly _breakpointObserver = inject(BreakpointObserver);
    private readonly _matDialog = inject(MatDialog);
    private readonly _ephemeralMsg = inject(EphemeralMessageService);
    private readonly _detailOverlayService = inject(DetailOverlayService);
    private readonly _onDestroy = new Subject<void>();

    private readonly _clickedRow = new BehaviorSubject<Currency>(null);
    private _request: GetCurrenciesRequest;
    private _sortFilterComponent: SortFilterComponent;

    @ViewChild(MatSort) private readonly _matSort: MatSort;

    readonly displayedColumns = [
        'supplier',
        'source',
        'target',
        'rate',
        'last_update',
        'modifier',
        'actions'
    ];

    readonly initSortCol = 'supplier';
    readonly initSortDir: SortDirection = 'desc';
    readonly currenciesPageSize = PAGE_SIZE;
    readonly userRoles = UserRoles;

    readonly currencies$ = this._currenciesService.list.getData$();
    readonly currenciesMetadata$ = this._currenciesService.list.getMetadata$();
    readonly loadingData$ = this._currenciesService.list.loading$();
    readonly clickedRow$ = this._clickedRow.asObservable();

    readonly hasWritePermissions$: Observable<boolean> = this._authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.exchangeRateWrite])));

    readonly isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._sortFilterComponent
        ]);
    }

    override ngOnDestroy(): void {
        this._detailOverlayService.close();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    loadData(filters: FilterItem[]): void {
        this._request = {
            limit: this.currenciesPageSize,
            aggs: true,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values && values.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this._request.sort = values[0].value;
                        break;
                }
            }
        });
        this.loadCurrencies();
    }

    openModifyExchangeRateDialog(currency: Currency): void {
        this._matDialog.open(ModifyCurrencyDialogComponent, new ObMatDialogConfig({
            currency
        })).beforeClosed()
            .subscribe(changed => {
                if (changed) {
                    this._ephemeralMsg.showSuccess({
                        msgKey: 'CURRENCIES.MODIFY_CURRENCY.SUCCESS'
                    });
                    this.loadCurrencies();
                }
            });
    }

    open(row: Currency): void {
        if (row.has_transitions) {
            this._clickedRow.next(row);

            const data = new DetailOverlayData(row, row.supplier + ': ' + row.source + ' to ' + row.target);
            this._detailOverlayService.open(CurrencyHistoricComponent, data).subscribe(changed => {
                if (changed) {
                    this._ephemeralMsg.showSuccess({
                        msgKey: 'CURRENCIES.MODIFY_CURRENCY.SUCCESS'
                    });
                    this.loadCurrencies();
                }
                this._clickedRow.next(null);
            });
        }
    }

    override refresh(): void {
        this.loadData(this.listFiltersService.getFilters());
    }

    private loadCurrencies(): void {
        this._currenciesService.list.load(this._request);
    }
}
