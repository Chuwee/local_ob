import {
    SeasonTicket, SeasonTicketGenerationStatus, SeasonTicketsService, SeasonTicketStatus
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    GetSeasonTicketRenewalsRequest, PostSeasonTicketRenewals, PurgeSeasonTicketsRenewalsRequest, RenewalCandidateToImport,
    RenewalCandidateTypeEnum, RenewalsGenerationStatus, SeasonTicketRenewal, SeasonTicketRenewalMappingStatus,
    SeasonTicketRenewalsAction, SeasonTicketRenewalsListActionsService, SeasonTicketRenewalsListState,
    SeasonTicketRenewalsSaveService, SeasonTicketRenewalsService, SeasonTicketRenewalStatus, VmSeasonTicketRenewal,
    getLocationInfo, VmSeasonTicketRenewalEdit, PostSeasonTicketRenewalsGeneration,
    AutomaticRenewalStatus, PutSeasonTicketRenewals
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    ChipsComponent, ChipsFilterDirective, ContextNotificationComponent, DialogSize, EphemeralMessageService,
    ExportDialogComponent, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService, ObDialog, ObMatDialogConfig,
    openDialog, PaginatorComponent, PopoverComponent, PopoverFilterDirective, SearchInputComponent, SortFilterComponent
} from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, ExportDialogData, ExportFormat, ExportRequest } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ComponentType } from '@angular/cdk/portal';
import { AsyncPipe } from '@angular/common';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, output, signal, ViewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatSelect, MatSelectTrigger } from '@angular/material/select';
import { MatSort, SortDirection } from '@angular/material/sort';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { distinctUntilChanged, filter, first, map, pairwise, shareReplay, switchMap, take, tap } from 'rxjs/operators';
import { NewSeasonTicketRenewalDialogComponent } from '../create/new-season-ticket-renewal-dialog.component';
import { SeasonTicketRenewalsListEditDialogComponent } from '../edit/season-ticket-renewals-list-edit-dialog.component';
import { RenewalsExportXMLSepaDialogComponent } from './export-xml-sepa/renewals-export-xml-sepa-dialog.component';
import { SeasonTicketRenewalsListFilterComponent } from './filter/season-ticket-renewals-list-filter.component';
import {
    RenewalsGenerateCsvImportComponent
} from './generate/csv-import/renewals-generate-csv-import.component';
import { RenewalsGenerateXmlSepaDialogComponent } from './generate/xml-sepa-import/renewals-generate-xml-sepa-dialog.component';
import { exportDataSeasonTicketRenewal } from './season-ticket-renewals-export-data';
import { SeasonTicketRenewalsListSummaryComponent } from './summary/season-ticket-renewals-list-summary.component';

@Component({
    selector: 'app-season-ticket-renewals-list',
    templateUrl: './season-ticket-renewals-list.component.html',
    styleUrls: ['./season-ticket-renewals-list.component.scss'],
    providers: [
        ListFiltersService, SeasonTicketRenewalsListState, SeasonTicketRenewalsListActionsService, SeasonTicketRenewalsSaveService
    ],
    imports: [
        ContextNotificationComponent, MatButton, AsyncPipe, TranslatePipe, MatIcon, MatProgressBar, MatTooltip,
        MatMenu, MatMenuItem, EllipsifyDirective, SearchInputComponent, PopoverComponent, PopoverFilterDirective,
        PaginatorComponent, ChipsComponent, ChipsFilterDirective, MatTable, MatHeaderCell, MatHeaderCellDef,
        MatCheckbox, MatCell, MatCellDef, MatColumnDef, LocalCurrencyPipe, MatMenuTrigger, MatSort,
        MatIconButton, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, SeasonTicketRenewalsListFilterComponent,
        SeasonTicketRenewalsListSummaryComponent, MatFormField, MatOption, MatSelect, MatSelectTrigger,
        ObFormFieldLabelDirective, ReactiveFormsModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketRenewalsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit {
    readonly #tableSrv = inject(TableColConfigService);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    readonly #listState = inject(SeasonTicketRenewalsListState);
    readonly #actionsServ = inject(SeasonTicketRenewalsListActionsService);
    readonly #saveService = inject(SeasonTicketRenewalsSaveService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #router = inject(Router);
    readonly #onDestroy = inject(DestroyRef);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    @ViewChild(MatSort) private readonly _matSort: MatSort;
    @ViewChild(PaginatorComponent) private readonly _paginatorComponent: PaginatorComponent;
    @ViewChild(SearchInputComponent) private readonly _searchInputComponent: SearchInputComponent;
    @ViewChild(SeasonTicketRenewalsListFilterComponent) private readonly _filterComponent: SeasonTicketRenewalsListFilterComponent;

    #sortFilterComponent: SortFilterComponent;
    #isGenerationStatusReady: boolean;
    #request: GetSeasonTicketRenewalsRequest;
    #renewals: VmSeasonTicketRenewal[];

    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(first(Boolean)));
    readonly $isSeasonTicketReady = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$().pipe(
        first(Boolean),
        map(seasonTicketStatus => seasonTicketStatus.status === SeasonTicketStatus.ready)
    ));

    readonly $isSeasonTicketSetUp = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$()
        .pipe(
            first(Boolean),
            map(seasonTicketStatus => seasonTicketStatus.status === SeasonTicketStatus.setUp)
        ));

    readonly $updateSubstatusLoading = output<boolean>();
    readonly isLoading$ = booleanOrMerge([
        this.#seasonTicketRenewalsSrv.renewalsList.inProgress$(),
        this.#seasonTicketRenewalsSrv.renewalsExportXmlSepa.inProgress$(),
        this.#seasonTicketRenewalsSrv.renewalsSubstatus.inProgress$()
    ]);

    readonly seasonTicketCurrencyCode$ = this.#seasonTicketSrv.seasonTicket.get$().pipe(
        first(Boolean),
        map(seasonTicket => seasonTicket.currency_code)
    );

    readonly initSortCol = 'mapping_status';
    readonly initSortDir: SortDirection = 'desc';
    readonly displayedColumns = ['selection', 'mapping_status', 'member_id', 'name',
        'entity', 'historic_seat', 'actual_seat', 'rate', 'balance', 'type', 'renewal_status', 'actions'];

    readonly dateTimeFormats = DateTimeFormats;
    readonly pageSize = 20;
    readonly form = this.#fb.group({});

    readonly $automaticStatus = signal([SeasonTicketRenewalStatus.notRenewed.toString()]);
    readonly renewals$ = this.#listState.getRenewalsList$().pipe(tap(renewals => {
        this.#renewals = renewals;
        const renewalSubstatus = renewals.find(renewal => renewal.renewal_substatus)?.renewal_substatus;
        if (renewalSubstatus) {
            this.$automaticStatus.set([renewalSubstatus, ...this.$automaticStatus().filter(s => s !== renewalSubstatus)]);
            renewals.forEach(renewal => {
                this.form.setControl(renewal.id.toString(), new FormControl(renewalSubstatus));
            });
        }
    }));

    readonly renewalsMetadata$ = this.#seasonTicketRenewalsSrv.renewalsList.getMetadata$().pipe(shareReplay(1));
    readonly renewalStatus = SeasonTicketRenewalStatus;
    readonly mappingStatus = SeasonTicketRenewalMappingStatus;
    readonly isRenewalsGenerationInProgress$ = combineLatest([
        this.#seasonTicketRenewalsSrv.renewalsList.getSummary$(),
        this.#seasonTicketRenewalsSrv.renewalsList.inProgress$()
    ]).pipe(
        filter(([renewalsSummary]) => !!renewalsSummary),
        map(([renewalsSummary, isRenewalsListInProgress]) =>
            renewalsSummary.generation_status === RenewalsGenerationStatus.inProgress &&
            !isRenewalsListInProgress
        ),
        distinctUntilChanged()
    );

    readonly isRenewalEditingInProgress$ = this.#seasonTicketRenewalsSrv.renewalEdits.inProgress$();
    readonly isRenewalsPurgeInProgress$ = combineLatest([
        this.#seasonTicketRenewalsSrv.renewalsList.getSummary$(),
        this.#seasonTicketRenewalsSrv.renewalsList.inProgress$()
    ]).pipe(
        filter(([renewalsSummary]) => !!renewalsSummary),
        map(([renewalsSummary, isRenewalsListInProgress]) =>
            renewalsSummary.generation_status === RenewalsGenerationStatus.purgeInProgress
            && !isRenewalsListInProgress),
        distinctUntilChanged()
    );

    readonly isHandsetOrTablet$ = inject(BreakpointObserver)
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches),
            shareReplay(1)
        );

    readonly $isRenewalGenerationInProgress = toSignal(this.#seasonTicketRenewalsSrv.renewalsList.getSummary$().pipe(
        filter(Boolean),
        map(summary => summary?.automatic_renewal_status === AutomaticRenewalStatus.inProgress)
    ));

    isAllSelected: boolean;
    isEditDisabled: boolean;
    isDeleteDisabled: boolean;

    ngOnInit(): void {
        this.seasonTicketStatusChangeHandler();
        this.renewalsListChangeHandler();
        this.renewalsImportChangeHandler();
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(this._matSort);
        this.initListFilteredComponent([
            this._paginatorComponent,
            this.#sortFilterComponent,
            this._searchInputComponent,
            this._filterComponent
        ]);
    }

    updateRenewalStatus(renewalId: string, userId: string, status: string): void {
        if (status === SeasonTicketRenewalStatus.notRenewed.toString()) {
            this.#msgDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'SEASON_TICKETS.RENEWALS.LIST.RENEWAL_STATUS_CONFIRM_TITLE',
                message: 'SEASON_TICKETS.RENEWALS.LIST.RENEWAL_STATUS_CONFIRM_MSG',
                actionLabel: 'FORMS.ACTIONS.CHANGE_STATUS',
                showCancelButton: true
            }).pipe(
                filter(Boolean),
                switchMap(() =>
                    this.#seasonTicketRenewalsSrv.renewalsSubstatus.update(
                        this.$seasonTicket().id,
                        { items: [{ id: renewalId, user_id: userId, renewal_substatus: null }] } as PutSeasonTicketRenewals
                    )
                )
            )
                .subscribe(() => {
                    this.$updateSubstatusLoading.emit(true);
                    this.#ephemeralMsgSrv.showSuccess({ msgKey: 'SEASON_TICKETS.RENEWALS.LIST.RENEWAL_STATUS_SUCCESSFULLY_CHANGED' });
                    setTimeout(() => {
                        this.loadRenewalsList();
                        this.$updateSubstatusLoading.emit(false);
                    }, 2000);
                });
        }
        this.form.get(renewalId)?.patchValue(this.$automaticStatus().at(0));
    }

    loadData(filters: FilterItem[]): void {
        this.#actionsServ.setAction(SeasonTicketRenewalsAction.tableAction);
        this.setRequest(filters);
        this.loadRenewalsList();
    }

    masterToggle(): void {
        this.#actionsServ.setAction(SeasonTicketRenewalsAction.toggleTableRows);
        this.isAllSelected = !this.isAllSelected;
        let renewals: VmSeasonTicketRenewal[];
        if (this.isAllSelected) {
            renewals = this.#renewals.map(renewal => ({
                ...renewal,
                ...this.getAllSelectedSelectability(renewal)
            }));
        } else {
            renewals = this.#renewals.map(renewal => ({
                ...renewal,
                ...this.getDefaultSelectability(renewal)
            }));
        }
        this.#listState.setRenewalsList(renewals);
    }

    toggleRow(row: VmSeasonTicketRenewal): void {
        if (row.isSelectable) {
            this.#actionsServ.setAction(SeasonTicketRenewalsAction.toggleTableRows);
            const renewals = this.#renewals.map(renewal => {
                if (row.id === renewal.id) {
                    return { ...renewal, isSelected: !renewal.isSelected };
                } else {
                    return renewal;
                }
            });
            this.#listState.setRenewalsList(renewals);
        }
    }

    editRenewalsHandler(): void {
        const selectedRenewals = this.#renewals
            .filter(renewal => renewal.isSelected);
        const hasSelectedRenewalsFinalizedRenewals = selectedRenewals
            .some(renewal => renewal.renewal_status !== SeasonTicketRenewalStatus.notRenewed);
        if (hasSelectedRenewalsFinalizedRenewals) {
            this.#msgDialogSrv.showAlert({
                size: DialogSize.SMALL,
                title: 'SEASON_TICKET.RENEWALS.LIST.UPDATE_MULTIPLE_NOT_ALLOWED.TITLE',
                message: 'SEASON_TICKET.RENEWALS.LIST.UPDATE_MULTIPLE_NOT_ALLOWED.WARNING',
                actionLabel: 'OK',
                showCancelButton: false
            });
            return;
        }

        this.#actionsServ.setAction(SeasonTicketRenewalsAction.saveEdits);
        this.#matDialog.open(
            SeasonTicketRenewalsListEditDialogComponent,
            new ObMatDialogConfig({
                renewals: selectedRenewals
            })
        ).beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap((renewalEdits: VmSeasonTicketRenewalEdit[]) => this.#saveService.saveRenewalEdits(renewalEdits))
            )
            .subscribe(() => this.#ephemeralMsgSrv.showSaveSuccess());
    }

    deleteRenewalsHandler(): void {
        if (this.isAllSelected) {
            const { limit, offset, sort, aggs, ...request } = this.#request;
            this.#seasonTicketRenewalsSrv.renewalsList.getDeletableRenewalsNumber(this.$seasonTicket().id, request)
                .subscribe(({ deletable_renewals: deletableRenewals }) => {
                    this.#msgDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'SEASON_TICKET.RENEWALS.LIST.DELETE_ALL.TITLE',
                        message: 'SEASON_TICKET.RENEWALS.LIST.DELETE_ALL.WARNING',
                        messageParams: { deletableRenewalsNumber: deletableRenewals },
                        actionLabel: deletableRenewals !== 0 ? 'FORMS.ACTIONS.DELETE' : 'FORMS.ACTIONS.OK',
                        showCancelButton: deletableRenewals !== 0
                    })
                        .subscribe(success => {
                            if (success && deletableRenewals !== 0) {
                                this.isAllSelected = false;
                                this.deleteAllRenewals(request, this.$seasonTicket());
                            }
                        });
                });
        } else {
            this.#msgDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'SEASON_TICKET.RENEWALS.LIST.DELETE_MULTIPLE.TITLE',
                message: 'SEASON_TICKET.RENEWALS.LIST.DELETE_MULTIPLE.WARNING',
                actionLabel: 'FORMS.ACTIONS.DELETE',
                showCancelButton: true
            })
                .subscribe(success => {
                    if (success) {
                        this.deleteMultipleRenewals(this.$seasonTicket());
                    }
                });
        }
    }

    editRenewalHandler(row: VmSeasonTicketRenewal): void {
        this.#actionsServ.setAction(SeasonTicketRenewalsAction.saveEdits);
        this.#matDialog.open(
            SeasonTicketRenewalsListEditDialogComponent,
            new ObMatDialogConfig({
                renewals: [row]
            })
        ).beforeClosed()
            .pipe(
                filter(Boolean),
                switchMap((renewalEditSeats: VmSeasonTicketRenewalEdit[]) => this.#saveService.saveRenewalEdits(renewalEditSeats))
            )
            .subscribe(() => this.#ephemeralMsgSrv.showSaveSuccess());
    }

    deleteRenewalHandler(row: VmSeasonTicketRenewal): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'SEASON_TICKET.RENEWALS.LIST.DELETE.TITLE',
            message: 'SEASON_TICKET.RENEWALS.LIST.DELETE.WARNING',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this.deleteRenewal(row, this.$seasonTicket());
                }
            });
    }

    showTransaction(row: VmSeasonTicketRenewal): void {
        this.#router.navigate(['/transactions', row.order_code, 'general-data']);
    }

    openNewSeasonTicketRenewalDialog(): void {
        this.#matDialog.open(
            NewSeasonTicketRenewalDialogComponent,
            new ObMatDialogConfig({ hasPreviousRenewals: true })
        ).beforeClosed().pipe(
            tap((renewalCandidate: RenewalCandidateToImport) => {
                if (renewalCandidate) {
                    const { renewalCandidateId, renewalRates, type, includeBalance } = renewalCandidate;
                    let postRenewalCandidate: PostSeasonTicketRenewals;
                    if (type === RenewalCandidateTypeEnum.internal || type === RenewalCandidateTypeEnum.internalAllEntities) {
                        postRenewalCandidate = {
                            renewal_season_ticket: renewalCandidateId,
                            rates: renewalRates,
                            include_all_entities: type === RenewalCandidateTypeEnum.internalAllEntities,
                            include_balance: includeBalance
                        };
                    } else if (type === RenewalCandidateTypeEnum.external) {
                        postRenewalCandidate = {
                            renewal_external_event: renewalCandidateId,
                            is_external_event: true,
                            rates: renewalRates,
                            include_balance: includeBalance
                        };
                    }
                    this.#seasonTicketRenewalsSrv.renewalsImport.import(this.$seasonTicket().id, postRenewalCandidate);
                }
            })
        ).subscribe();
    }

    exportRenewals(): void {
        this.#matDialog.open<ExportDialogComponent, Partial<ExportDialogData>, ExportRequest>(
            ExportDialogComponent, new ObMatDialogConfig({
                exportData: exportDataSeasonTicketRenewal,
                exportFormat: ExportFormat.csv,
                selectedFields: this.#tableSrv.getColumns('EXP_SEASON_TICKET_RENEWAL')
            })
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#tableSrv.setColumns('EXP_SEASON_TICKET_RENEWAL', exportList.fields.map(resultData => resultData.field));
                this.#seasonTicketRenewalsSrv.renewalsList.export(this.$seasonTicket().id, this.#request, exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });
    }

    openExportXMLSepaDialog(): void {
        openDialog(this.#matDialog, RenewalsExportXMLSepaDialogComponent)
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(() => {
                this.#seasonTicketRenewalsSrv.renewalsExportXmlSepa.export(this.$seasonTicket().id)
                    .subscribe(() => {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'SEASON_TICKETS.RENEWALS.EXPORT_XML_SEPA.SUCCESS' });
                    });
            });
    }

    generateAutomaticRenewals(): void {
        let component: ComponentType<ObDialog<unknown, null, PostSeasonTicketRenewalsGeneration>>;
        const renewalType = this.$seasonTicket().settings.operative.renewal?.renewal_type;
        switch (renewalType) {
            case 'XML_SEPA':
                component = RenewalsGenerateXmlSepaDialogComponent;
                break;
            case 'CSV_IMPORT':
            default:
                component = RenewalsGenerateCsvImportComponent;
                break;
        }
        openDialog(this.#matDialog, component)
            .beforeClosed()
            .subscribe((postRenewalsToImport: PostSeasonTicketRenewalsGeneration) => {
                if (postRenewalsToImport) {
                    this.#seasonTicketRenewalsSrv.renewalsList.automaticRenewals.generate(this.$seasonTicket().id, postRenewalsToImport)
                        .subscribe(() => this.refresh());
                }
            });
    }

    private seasonTicketStatusChangeHandler(): void {
        this.#seasonTicketSrv.seasonTicketStatus.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(seasonTicketStatus =>
                this.#isGenerationStatusReady = seasonTicketStatus.status &&
                seasonTicketStatus.generation_status === SeasonTicketGenerationStatus.ready
            );
    }

    private setRequest(filters: FilterItem[]): void {
        this.#request = {
            limit: this.pageSize,
            offset: 0
        };

        filters.forEach(filterItem => {
            const values = filterItem.values;
            if (values?.length > 0) {
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
                    case 'MAPPING_STATUS':
                        this.#request.mapping_status = values[0].value;
                        break;
                    case 'RENEWAL_STATUS':
                        this.#request.renewal_status = values[0].value;
                        break;
                    case 'RENEWAL_SUBSTATUS':
                        this.#request.renewal_substatus = values[0].value;
                        break;
                    case 'AUTO_RENEWAL':
                        this.#request.auto_renewal = values[0].value;
                        break;
                    case 'START_DATE':
                        this.#request.startDate = values[0].value;
                        break;
                    case 'END_DATE':
                        this.#request.endDate = values[0].value;
                        break;
                    case 'ENTITY':
                        this.#request.entityId = values[0].value;
                        break;
                }
            }
        });
    }

    private renewalsListChangeHandler(): void {
        this.#seasonTicketRenewalsSrv.renewalsList.getData$()
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(renewals => {
                this.#listState.setRenewalsList(this.getVmRenewals(renewals));
            });

        this.#listState.getRenewalsList$()
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(renewals => {
                const isSomeRenewalSelected = renewals.some(renewal => renewal.isSelected);
                this.isEditDisabled = !isSomeRenewalSelected || this.isAllSelected;
                this.isDeleteDisabled = !(isSomeRenewalSelected || (this.isAllSelected && renewals.length));
            });
    }

    private getVmRenewals(renewals: SeasonTicketRenewal[]): VmSeasonTicketRenewal[] {
        renewals = renewals ?? [];
        if (this.isAllSelected) {
            return renewals.map(renewal => ({
                ...renewal,
                ...this.getAllSelectedSelectability(renewal),
                actualLocation: getLocationInfo(renewal.actual_seat),
                historicLocation: getLocationInfo(renewal.historic_seat)
            }));
        } else {
            return renewals.map(renewal => ({
                ...renewal,
                ...this.getDefaultSelectability(renewal),
                actualLocation: getLocationInfo(renewal.actual_seat),
                historicLocation: getLocationInfo(renewal.historic_seat)
            }));
        }
    }

    private getAllSelectedSelectability(
        renewal: SeasonTicketRenewal
    ): Pick<VmSeasonTicketRenewal, 'isSelectable' | 'isSelected'> {
        return {
            isSelected: this.getIsSelectable(renewal),
            isSelectable: false
        };
    }

    private getDefaultSelectability(
        renewal: SeasonTicketRenewal
    ): Pick<VmSeasonTicketRenewal, 'isSelectable' | 'isSelected'> {
        return {
            isSelected: false,
            isSelectable: this.getIsSelectable(renewal)
        };
    }

    private getIsSelectable(renewal: SeasonTicketRenewal): boolean {
        return renewal.renewal_status !== SeasonTicketRenewalStatus.renewed && !renewal.renewal_substatus;
    }

    private renewalsImportChangeHandler(): void {
        this.#seasonTicketRenewalsSrv.renewalsImport.inProgress$()
            .pipe(
                pairwise(),
                filter(([isInProgressOld, isInProgressCurr]) => isInProgressOld && !isInProgressCurr),
                switchMap(() => this.#seasonTicketRenewalsSrv.renewalsImport.error$()
                    .pipe(
                        take(1),
                        filter(isRenewalsImportError => !isRenewalsImportError),
                        tap(() => this.refresh())
                    )),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe();
    }

    private loadRenewalsList(): void {
        if (this.#request && this.#isGenerationStatusReady) {
            this.#seasonTicketRenewalsSrv.renewalsList.load(this.$seasonTicket().id, this.#request);
        }
    }

    private deleteAllRenewals(request: PurgeSeasonTicketsRenewalsRequest, seasonTicket: SeasonTicket): void {
        this.#seasonTicketRenewalsSrv.renewalsList.purge(seasonTicket.id, request)
            .subscribe(() => {
                this.loadRenewalsList();
            });
    }

    private deleteMultipleRenewals(seasonTicket: SeasonTicket): void {
        const selectedRenewals = this.#renewals.filter(renewal => renewal.isSelected);
        this.#actionsServ.setAction(SeasonTicketRenewalsAction.deleteAction);
        const deleteMultipleRenewals = {
            renewal_ids: selectedRenewals.map(selectedRenewal => selectedRenewal.id)
        };
        this.#seasonTicketRenewalsSrv.renewalsList.deleteMultiple(seasonTicket.id, deleteMultipleRenewals)
            .subscribe(response => {
                if (response.items.find(item => !item.result)) {
                    this.#msgDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: 'SEASON_TICKET.RENEWALS.LIST.DELETE_MULTIPLE_ERROR.TITLE',
                        message: 'SEASON_TICKET.RENEWALS.LIST.DELETE_MULTIPLE_ERROR.WARNING',
                        actionLabel: 'FORMS.ACTIONS.CLOSE',
                        showCancelButton: false
                    })
                        .subscribe(() => this.loadRenewalsList());
                } else {
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'SEASON_TICKET.RENEWALS.LIST.DELETE_MULTIPLE.SUCCESS',
                        msgParams: { seasonTicketName: seasonTicket.name }
                    });
                    this.loadRenewalsList();
                }
            });
    }

    private deleteRenewal(row: VmSeasonTicketRenewal, seasonTicket: SeasonTicket): void {
        this.#actionsServ.setAction(SeasonTicketRenewalsAction.deleteAction);
        this.#seasonTicketRenewalsSrv.renewalsList.deleteOne(seasonTicket.id, row.id)
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'SEASON_TICKET.RENEWALS.LIST.DELETE.SUCCESS',
                    msgParams: { seasonTicketName: seasonTicket.name }
                });
                this.loadRenewalsList();
            });
    }
}
