import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { GetProducersRequest, Producer, ProducersService } from '@admin-clients/cpanel/promoters/producers/data-access';
import {
    DialogSize, MessageDialogService, ObMatDialogConfig, ListFilteredComponent, ListFiltersService,
    SortFilterComponent, PaginatorComponent, FilterItem, SearchInputComponent, EphemeralMessageService,
    PopoverComponent, PopoverFilterDirective, ChipsFilterDirective, ChipsComponent, ContextNotificationComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { NgIf, NgClass, AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, ViewChild } from '@angular/core';
import { ExtendedModule } from '@angular/flex-layout/extended';
import { FlexModule } from '@angular/flex-layout/flex';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, map, shareReplay } from 'rxjs/operators';
import { NewProducerDialogComponent } from '../create/new-producer-dialog.component';
import { ProducersListFilterComponent } from './filter/producers-list-filter.component';

@Component({
    selector: 'app-producers-list',
    templateUrl: './producers-list.component.html',
    styleUrls: ['./producers-list.component.scss'],
    providers: [
        ListFiltersService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexModule, PopoverComponent, MaterialModule, NgClass,
        PopoverFilterDirective, ProducersListFilterComponent, SearchInputComponent, NgIf,
        PaginatorComponent, ChipsComponent, ChipsFilterDirective,
        ExtendedModule, RouterLink, ContextNotificationComponent, AsyncPipe, TranslatePipe
    ]
})
export class ProducersListComponent extends ListFilteredComponent implements AfterViewInit, OnDestroy {
    private _request: GetProducersRequest;
    private _sortFilterComponent: SortFilterComponent;

    private readonly _onDestroy: Subject<void> = new Subject();

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(ProducersListFilterComponent) private readonly _filterComponent: ProducersListFilterComponent;

    readonly initSortCol = 'name';
    readonly initSortDir: SortDirection = 'asc';
    readonly producersPageSize = 20;

    readonly isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly producers$ = this._producersService.getProducersListData$().pipe(filter(Boolean));
    readonly producersMetadata$ = this._producersService.getProducersListMetadata$().pipe(shareReplay(1));
    readonly isLoading$ = booleanOrMerge([this._producersService.isProducersListLoading$()]);
    readonly canSelectEntity$ = this._auth.canReadMultipleEntities$();
    readonly displayedColumns$ = this.canSelectEntity$
        .pipe(
            map(canSelectEntity => {
                if (canSelectEntity) {
                    return ['name', 'entity', 'status', 'default', 'actions'];
                } else {
                    return ['name', 'status', 'default', 'actions'];
                }
            })
        );

    constructor(
        private _producersService: ProducersService,
        private _auth: AuthenticationService,
        private _breakpointObserver: BreakpointObserver,
        private _ref: ChangeDetectorRef,
        private _router: Router,
        private _route: ActivatedRoute,
        private _msgDialogSrv: MessageDialogService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _matDialog: MatDialog
    ) {
        super();
    }

    trackByFn = (_, item: Producer): number => item.id;

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this._onDestroy.next(null);
        this._onDestroy.complete();
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

    loadData(filters: FilterItem[]): void {
        this._request = new GetProducersRequest();
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
                    case 'ENTITY':
                        this._request.entityId = values[0].value;
                        break;
                    case 'STATUS':
                        this._request.status = values.map(val => val.value);
                        break;
                }
            }
        });

        this.loadProducers();
    }

    openDeleteProducerDialog(producer: Producer): void {
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_PRODUCER',
            message: 'PRODUCER.DELETE_PRODUCER_WARNING',
            messageParams: { producerName: producer.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._producersService.deleteProducer(producer.id.toString())
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'PRODUCER.DELETE_PRODUCER_SUCCESS',
                                msgParams: { producerName: producer.name }
                            });
                            this.loadProducers();
                        });
                }
            });
    }

    openNewProducerDialog(): void {
        this._matDialog.open(NewProducerDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .subscribe(producerId => {
                if (producerId) {
                    this._router.navigate([producerId], { relativeTo: this._route });
                }
            });
    }

    private loadProducers(): void {
        this._producersService.loadProducersList(
            this._request.limit,
            this._request.offset,
            this._request.sort,
            this._request.q,
            this._request.fields,
            this._request.entityId,
            this._request.status
        );
        this._ref.detectChanges();
    }

}
