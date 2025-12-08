import { Metadata } from '@OneboxTM/utils-state';
import { GetOperatorsRequest, Operator, OperatorsService } from '@admin-clients/cpanel-configurations-operators-data-access';
import { ContextNotificationComponent, FilterItem, ListFilteredComponent, ListFiltersService, ObMatDialogConfig, PaginatorComponent, SearchInputComponent, SortFilterComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, mapTo, switchMap } from 'rxjs/operators';
import { NewOperatorDialogComponent } from '../create/new-operator-dialog.component';
import { ShowPasswordDialogComponent } from '../create/show-password/show-password-dialog.component';

@Component({
    selector: 'app-operators-list',
    templateUrl: './operators-list.component.html',
    styleUrls: ['./operators-list.component.scss'],
    providers: [ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        CommonModule,
        ContextNotificationComponent,
        RouterModule,
        SearchInputComponent,
        PaginatorComponent
    ]
})
export class OperatorsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    private _request: GetOperatorsRequest;
    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    private _sortFilterComponent: SortFilterComponent;
    displayedColumns = ['name', 'short_name', 'language', 'currency', 'timezone'];
    operatorsPageSize = 20;
    initSortCol = 'name';
    initSortDir: SortDirection = 'asc';
    operatorsMetadata$: Observable<Metadata>;
    operatorsLoading$: Observable<boolean>;
    operators$: Observable<Operator[]>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    constructor(
        private _operatorsSrv: OperatorsService,
        private _breakpointObserver: BreakpointObserver,
        private _ref: ChangeDetectorRef,
        private _matDialog: MatDialog,
        private _router: Router,
        private _route: ActivatedRoute
    ) {
        super();
    }

    ngOnInit(): void {
        this.operatorsMetadata$ = this._operatorsSrv.operators.getMetadata$();
        this.operatorsLoading$ = this._operatorsSrv.operators.loading$();
        this.operators$ = this._operatorsSrv.operators.getData$()
            .pipe(filter(operators => !!operators));
    }

    ngAfterViewInit(): void {
        this._sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this._sortFilterComponent,
            this._searchInputComponent]);
    }

    loadData(filters: FilterItem[]): void {
        this._request = {} as GetOperatorsRequest;
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
                        this._request.q = values[0].value;
                        break;
                }
            }
        });
        this.loadOperators();
    }

    openNewOperatorDialog(): void {
        this._matDialog.open<NewOperatorDialogComponent, null, { id: number; password: string }>(
            NewOperatorDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .pipe(
                filter(operator => !!operator),
                switchMap(({ password, id }) =>
                    this._matDialog.open<ShowPasswordDialogComponent, { password: string }, void>(
                        ShowPasswordDialogComponent, new ObMatDialogConfig({ password })
                    )
                        .beforeClosed().pipe(mapTo(id))),
                filter(id => !!id)
            )
            .subscribe(operatorId => {
                this._router.navigate([operatorId, 'general-data'], { relativeTo: this._route });
            });
    }

    private loadOperators(): void {
        this._operatorsSrv.operators.load(this._request);
        this._ref.detectChanges();
    }
}
