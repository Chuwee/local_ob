import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntityFilterButtonComponent, EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import {
    CustomersService, PostCustomersToImport,
    WsCustomersImportMessage, CustomerListItem, GetCustomersRequest
} from '@admin-clients/cpanel-viewers-customers-data-access';
import { EntitiesBaseService, GetEntitiesRequest, TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ChipsComponent,
    ChipsFilterDirective,
    ContextNotificationComponent,
    DialogSize, EmptyStateComponent, EphemeralMessageService, ExportDialogComponent,
    FilterItem, IconManagerService, ListFilteredComponent, ListFiltersService,
    MessageDialogService, newPassword, ObMatDialogConfig, PaginatorComponent,
    PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { Topic, WebsocketsService, WsCustomerMsgType, WsMsgStatus } from '@admin-clients/shared/core/data-access';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgClass } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatSort, MatSortModule, SortDirection } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, firstValueFrom, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, startWith, switchMap, takeUntil } from 'rxjs/operators';
import { NewCustomerDialogComponent } from '../create/new-customer-dialog.component';
import { ImportCustomerDialogComponent } from '../import/import-dialog/import-customer-dialog.component';
import { ImportCustomerResultDialogComponent } from '../import/result-dialog/import-customer-result-dialog.component';
import { SetPasswordDialogComponent } from '../password/set-password-dialog.component';
import {
    customerTypesFields, exportDataCustomers, exportDataCustomersPoints,
    externalVendorFields, getAdditionalFields
} from './customers-export-data';
import { CustomersListFilterComponent } from './list-filter/customers-list-filter.component';

@Component({
    selector: 'app-customers-list',
    imports: [TranslatePipe, MatButtonModule, MatIconModule, FlexLayoutModule, EllipsifyDirective,
        EmptyStateComponent, PopoverComponent, PopoverFilterDirective, AsyncPipe, NgClass,
        EntityFilterModule, MatTooltipModule, MatMenuModule, CustomersListFilterComponent, SearchInputComponent,
        PaginatorComponent, MatDivider, ChipsComponent, ChipsFilterDirective, MatSpinner, MatTableModule, MatSortModule,
        LocalDateTimePipe, RouterLink, ContextNotificationComponent, MatProgressBar
    ],
    templateUrl: './customers-list.component.html',
    styleUrls: ['./customers-list.component.scss'],
    providers: [
        ListFiltersService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomersListComponent extends ListFilteredComponent implements OnInit, OnDestroy, AfterViewInit {
    readonly #onDestroy: Subject<void> = new Subject();
    readonly #importProgressBS = new BehaviorSubject<WsMsgStatus>(null);
    readonly #router = inject(Router);
    readonly #route = inject(ActivatedRoute);

    readonly #tableSrv = inject(TableColConfigService);
    readonly #matDialog = inject(MatDialog);
    readonly #auth = inject(AuthenticationService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #translate = inject(TranslateService);
    readonly #customersSrv = inject(CustomersService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #ws = inject(WebsocketsService);
    readonly #iconManagerSrv = inject(IconManagerService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #$entityId = toSignal(this.#auth.canReadMultipleEntities$()
        .pipe(
            switchMap(canReadMultipleEntities =>
                canReadMultipleEntities ?
                    this.#entitiesSrv.getEntity$().pipe(map(entity => entity?.id))
                    : this.#auth.getLoggedUser$().pipe(first(Boolean), map(user => user.entity.id))
            )
        ));

    #customersListRequest: GetCustomersRequest;
    #importReference: number;
    #wsImportMsg: WsCustomersImportMessage;

    @ViewChild(MatSort) private _matSort: MatSort;
    @ViewChild(PaginatorComponent) private _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private _searchInputComponent: SearchInputComponent;
    @ViewChild(CustomersListFilterComponent) private _filterComponent: CustomersListFilterComponent;
    @ViewChild(PopoverComponent) private _popover: PopoverComponent;
    @ViewChild('entityFilterButton') private _entityFilterButton: EntityFilterButtonComponent;

    readonly wsgStatus = WsMsgStatus;
    readonly dateTimeFormats = DateTimeFormats;
    readonly initSortCol = 'name';
    readonly initSortDir: SortDirection = 'asc';
    readonly customersPageSize = 20;

    readonly entitiesRequest: GetEntitiesRequest = { limit: 999 };

    readonly isHandsetOrTablet$ = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly canReadMultipleEntities$ = this.#auth.canReadMultipleEntities$();

    readonly entitySelected$ = this.canReadMultipleEntities$
        .pipe(
            switchMap(canReadMultEnt => {
                if (canReadMultEnt) {
                    return this.#entitiesSrv.getEntity$().pipe(map(entity => !!entity));
                } else {
                    return of(true);
                }
            }),
            startWith(false),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly showEntityButton$ = combineLatest([
        this.canReadMultipleEntities$,
        this.entitySelected$
    ])
        .pipe(map(([canRead, entitySelected]) => canRead && entitySelected));

    readonly showNoEntityEntityButton$ = combineLatest([
        this.canReadMultipleEntities$,
        this.entitySelected$
    ]).pipe(map(([canRead, entitySelected]) => canRead && !entitySelected));

    readonly $entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean)), { initialValue: null });

    readonly displayedColumns$ = this.canReadMultipleEntities$
        .pipe(
            first(),
            map(canReadMultipleEntities => {
                const cols = ['name', 'member_id', 'entity', 'phone', 'sign_up_date', 'type', 'status', 'management', 'actions'];
                return canReadMultipleEntities ? cols : cols.filter(col => col !== 'entity');
            })
        );

    readonly isLoading$ = booleanOrMerge([
        this.#customersSrv.customer.loading$(),
        this.#customersSrv.customersList.loading$(),
        this.#customersSrv.importReference.loading$(),
        this.#customersSrv.customer.forms.adminCustomer.loading$(),
        this.#entitiesSrv.entityCustomerTypes.inProgress$()
    ]);

    readonly importProgress$ = this.#importProgressBS.asObservable();
    readonly canLoggedUserCreateOrDelete$ = this.#auth.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user =>
                AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.CRM_MGR, UserRoles.OPR_MGR, UserRoles.ENT_MGR])
            )
        );

    readonly $showActions = toSignal(combineLatest([
        this.canLoggedUserCreateOrDelete$,
        this.entitySelected$
    ])
        .pipe(map(([canCreate, selected]) => canCreate && selected)));

    readonly $hasEntityCustomerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$().pipe(map(cts => !!cts?.length)));
    readonly $haveLoyaltyPointsSettings = toSignal(
        this.#entitiesSrv.getEntity$().pipe(filter(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    readonly $adminCustomerFields = toSignal(this.#customersSrv.customer.forms.adminCustomer.get$().pipe(
        filter(Boolean),
        map(fields => fields.flat())
    ));

    readonly $hasEntityExternalVendorWithInternalAccess = toSignal(this.#entitiesSrv.authConfig.get$().pipe(
        map(authConfig => authConfig?.authenticators?.some(auth => auth.type === 'VENDOR' && auth.customer_creation === 'ENABLED'))
    ));

    readonly $hasEntityExternalVendor = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => (
            entity.settings?.external_integration?.auth_vendor?.enabled &&
            entity.settings?.external_integration?.auth_vendor?.vendor_id?.length
        )))
    );

    readonly customers$ = this.#customersSrv.customersList.getData$();
    readonly customersMetadata$ = this.#customersSrv.customersList.getMetadata$();

    constructor() {
        super();
        this.#iconManagerSrv.addIconDefinition(newPassword);
        this.#entitiesSrv.getEntity$().pipe(filter(Boolean), takeUntilDestroyed()).subscribe(entity => {
            this.#entitiesSrv.entityCustomerTypes.load(entity.id);
            this.#customersSrv.customer.forms.adminCustomer.load(entity.id);
        });
        this.#loadEntityAuthConfig();
    }

    ngOnInit(): void {
        this.importProgress$
            .pipe(
                filter(Boolean),
                takeUntil(this.#onDestroy)
            )
            .subscribe(importProgress => {
                if (importProgress === WsMsgStatus.done) {
                    this.showImportDone();
                    this.clearWsImportSubscription();
                    this.loadCustomers();
                } else if (importProgress === WsMsgStatus.error) {
                    this.showImportError();
                    this.clearWsImportSubscription();
                }
            });
        this.checkSomeImportInProgress();
    }

    override ngOnDestroy(): void {
        super.ngOnDestroy();
        this.clearWsImportSubscription();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    ngAfterViewInit(): void {
        super.initListFilteredComponent([
            this._paginatorComponent,
            new SortFilterComponent(this._matSort),
            this._searchInputComponent,
            this._filterComponent,
            this._entityFilterButton
        ]);
    }

    // Load data for filters - in base class -.
    async loadData(filters: FilterItem[]): Promise<void> {
        const canReadMultipleEntities = await firstValueFrom(this.canReadMultipleEntities$);
        this.#customersListRequest = {
            limit: this.customersPageSize,
            offset: 0
        };
        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values?.length > 0) {
                switch (filterItem.key) {
                    case 'SORT':
                        this.#customersListRequest.sort = values[0].value;
                        break;
                    case 'PAGINATION':
                        this.#customersListRequest.limit = values[0].value.limit;
                        this.#customersListRequest.offset = values[0].value.offset;
                        break;
                    case 'SEARCH_INPUT':
                        this.#customersListRequest.q = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#customersListRequest.entityId = values[0].value;
                        break;
                    case 'STATUS':
                        this.#customersListRequest.status = values.map(val => val.value);
                        break;
                    case 'START_DATE':
                        this.#customersListRequest.startDate = values[0].value;
                        break;
                    case 'END_DATE':
                        this.#customersListRequest.endDate = values[0].value;
                        break;
                }
            }
        });
        if (!canReadMultipleEntities || this.#customersListRequest.entityId) {
            this.loadCustomers();
        }
    }

    openDeleteCustomerDialog(customer: CustomerListItem): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_CUSTOMER',
            message: 'CUSTOMER.DELETE_CUSTOMER_WARNING',
            messageParams: { customerEmail: customer.email },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(isSuccess => {
                if (isSuccess) {
                    this.deleteCustomer(customer);
                }
            });

    }

    openNewCustomerDialog(): void {
        const entityId = this.#$entityId();
        this.#matDialog.open<NewCustomerDialogComponent, { entityId: number }, {
            customerId?: string;
            entityId?: number;
        }>(NewCustomerDialogComponent, new ObMatDialogConfig({ entityId }))
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(result => {
                const { customerId } = result;
                if (customerId) {
                    this.#router.navigate([customerId, 'general-data'], {
                        relativeTo: this.#route,
                        queryParams: { entityId }
                    });
                }
            });
    }

    openCustomerListImportDialog(): void {
        this.#matDialog.open(
            ImportCustomerDialogComponent,
            new ObMatDialogConfig({
                currentEntity: this.$entity()
            })
        ).beforeClosed()
            .subscribe((postCustomersToImport: PostCustomersToImport) => {
                if (postCustomersToImport) {
                    this.#customersSrv.importReference.importCustomers(postCustomersToImport);
                }
            });
    }

    openCustomerListExportDialog(): void {
        const baseExportData = this.$haveLoyaltyPointsSettings() ? exportDataCustomersPoints : exportDataCustomers;
        const exportData = baseExportData.map(group => ({
            ...group,
            fields: [...group.fields]
        }));
        if (this.$hasEntityCustomerTypes()) exportData.find(field => field.field === 'member')?.fields?.push(...customerTypesFields);
        if (this.$hasEntityExternalVendor()) exportData.find(field => field.field === 'member')?.fields?.push(...externalVendorFields);
        const additionalFields = getAdditionalFields(this.$adminCustomerFields());
        if (additionalFields.fields.length) exportData.push(additionalFields);
        this.#matDialog.open(
            ExportDialogComponent,
            new ObMatDialogConfig({
                exportData,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_CUSTOMERS')
            })
        ).beforeClosed()
            .pipe(filter(hasExportList => !!hasExportList))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_CUSTOMERS', exportList.fields.map(resultData => resultData.field));
                this.#customersSrv.exportCustomers.exportCustomers(this.#customersListRequest, exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageService.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });
    }

    openSetPasswordDialog(customer: CustomerListItem): void {
        this.#matDialog.open(
            SetPasswordDialogComponent,
            new ObMatDialogConfig({ customer })
        ).beforeClosed()
            .subscribe(saved => {
                if (saved) {
                    this.loadCustomers();
                    this.#ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' });
                }
            });
    }

    private showImportDone(): void {
        this.#matDialog.open(
            ImportCustomerResultDialogComponent,
            new ObMatDialogConfig({
                created: this.#wsImportMsg.data.created,
                updated: this.#wsImportMsg.data.updated,
                errors: this.#wsImportMsg.data.errors,
                products: this.#wsImportMsg.data.products
            })
        ).beforeClosed()
            .subscribe();
    }

    private showImportError(): void {
        const title = this.#translate.instant('TITLES.ERROR_DIALOG');
        const message = this.#translate.instant('CUSTOMER.IMPORT_ERROR');
        this.#msgDialogSrv.showAlert({ size: DialogSize.SMALL, title, message });
    }

    private async checkSomeImportInProgress(): Promise<void> {
        const canReadMultipleEntities = await firstValueFrom(this.canReadMultipleEntities$);
        const canLoggedUserCreateOrDelete = await firstValueFrom(this.canLoggedUserCreateOrDelete$);
        if (!canReadMultipleEntities && canLoggedUserCreateOrDelete) {
            this.#customersSrv.importReference.load();
        }
        this.#customersSrv.importReference.get$()
            .pipe(
                filter(value => !!value),
                takeUntil(this.#onDestroy)
            )
            .subscribe(({ import_process_code: importReference }) => {
                this.#importReference = importReference;
                this.subscribeToWsImport();
            });
    }

    private subscribeToWsImport(): void {
        this.#importProgressBS.next(WsMsgStatus.inProgress);
        this.#ws.getMessages$<WsCustomersImportMessage>(Topic.customer, this.#importReference)
            .pipe(
                filter(wsMsg => wsMsg?.type === WsCustomerMsgType.import),
                takeUntil(this.#onDestroy)
            )
            .subscribe(wsMsg => {
                this.#wsImportMsg = wsMsg;
                switch (wsMsg.status) {
                    case WsMsgStatus.done:
                        this.#importProgressBS.next(WsMsgStatus.done);
                        break;
                    case WsMsgStatus.error:
                        this.#importProgressBS.next(WsMsgStatus.error);
                        break;
                }
            });
    }

    private loadCustomers(): void {
        this.#customersSrv.customersList.load(this.#customersListRequest);
        this.#ref.detectChanges();
    }

    private deleteCustomer(customer: CustomerListItem): void {
        this.#customersSrv.customer.delete(customer.id, customer.entity?.id?.toString())
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: 'CUSTOMER.DELETE_CUSTOMER_SUCCESS',
                    msgParams: { customerEmail: customer.email }
                });
                this.loadCustomers();
            });
    }

    private clearWsImportSubscription(): void {
        if (this.#importReference) {
            this.#ws.unsubscribeMessages(Topic.customer, this.#importReference);
            this.#importReference = null;
            this.#customersSrv.importReference.clear();
        }
    }

    #loadEntityAuthConfig(): void {
        this.#entitiesSrv.getEntity$().pipe(
            filter(Boolean),
            takeUntilDestroyed()
        ).subscribe(entity => {
            this.#entitiesSrv.authConfig.load(entity.id);
        });

    }
}
