import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    MappedInfo, SeasonTicketRenewalAvailableSeat, SeasonTicketRenewalMappingStatus, SeasonTicketRenewalSeat,
    SeasonTicketRenewalsListEditDialogLocationService, SeasonTicketRenewalsListEditDialogRenewalsService,
    SeasonTicketRenewalsListEditDialogState, SeasonTicketRenewalsService, VmRowNnzGroupsToShow, VmSeasonTicketRenewal,
    VmSeasonTicketRenewalAvailableNnz, VmSeasonTicketRenewalAvailableRow, VmSeasonTicketRenewalAvailableSeat,
    VmSeasonTicketRenewalAvailableSector, VmSeasonTicketRenewalEdit, seasonTicketRenewalsProviders
} from '@admin-clients/cpanel/promoters/season-tickets/renewals/data-access';
import { TicketAllocationType } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatOptgroup, MatOption } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatFormField, MatSelect, MatSelectChange } from '@angular/material/select';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, of } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, take } from 'rxjs/operators';
import { GroupSelectSearchComponent } from './group-select-search/group-select-search.component';

interface RowNnzGroupNames {
    row: string;
    nnz: string;
}

@Component({
    selector: 'app-season-ticket-renewals-list-edit-dialog',
    templateUrl: 'season-ticket-renewals-list-edit-dialog.component.html',
    styleUrls: ['season-ticket-renewals-list-edit-dialog.component.scss'],
    providers: [
        seasonTicketRenewalsProviders,
        SeasonTicketRenewalsListEditDialogState,
        SeasonTicketRenewalsListEditDialogRenewalsService,
        SeasonTicketRenewalsListEditDialogLocationService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormsModule, TranslatePipe, MatIcon, MatIconButton, MatDialogTitle, MatDialogContent, MatTable, AsyncPipe, MatHeaderCell,
        MatHeaderCellDef, MatCell, MatCellDef, EllipsifyDirective, MatTooltip, MatFormField, MatSelect, MatOption, SelectSearchComponent,
        MatDivider, GroupSelectSearchComponent, MatOptgroup, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatProgressSpinner,
        MatButton, MatColumnDef, MatDialogActions
    ]
})
export class SeasonTicketRenewalsListEditDialogComponent implements OnDestroy {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    readonly #dialogRef = inject(MatDialogRef<SeasonTicketRenewalsListEditDialogComponent>);
    readonly #renewalsSrv = inject(SeasonTicketRenewalsListEditDialogRenewalsService);
    readonly #locationSrv = inject(SeasonTicketRenewalsListEditDialogLocationService);
    readonly #translate = inject(TranslateService);
    readonly #data = inject<{ renewals: VmSeasonTicketRenewal[] }>(MAT_DIALOG_DATA);
    readonly #onDestroy = inject(DestroyRef);
    readonly #breakpointObserver = inject(BreakpointObserver);

    readonly $isHandsetOrTablet = toSignal(this.#breakpointObserver.observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches),
            shareReplay(1)
        ));

    readonly $isAutoRenewalAvailable = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(
        first(Boolean),
        map(seasonTicket => seasonTicket?.settings?.operative?.renewal?.automatic)
    ));

    readonly $renewalType = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(
        first(Boolean),
        map(seasonTicket => seasonTicket?.settings?.operative?.renewal?.renewal_type)
    ));

    readonly displayedColumns$ = computed(() => {
        const baseColumns = [
            'selection', 'name', 'historic_seat', 'actual_seat', 'sector', 'row_nnz', 'seat', 'rate'
        ];
        if (this.$isAutoRenewalAvailable()) {
            baseColumns.push('renewal_type');
        }
        baseColumns.push('actions_renewal_edit');
        return baseColumns;
    });

    readonly mappingStatus = SeasonTicketRenewalMappingStatus;
    readonly rates$ = this.#seasonTicketSrv.getSeasonTicketRates$();
    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(first(Boolean)));
    readonly rowGroupName = this.#translate.instant('SEASON_TICKET.RENEWALS.LIST.ROW_GROUP');
    readonly nnzGroupName = this.#translate.instant('SEASON_TICKET.RENEWALS.LIST.NNZ_GROUP');
    readonly rowNnzGroupNames: RowNnzGroupNames = {
        row: this.rowGroupName,
        nnz: this.nnzGroupName
    };

    readonly $isLoading = toSignal(booleanOrMerge([
        this.#seasonTicketRenewalsSrv.renewalsCapacityTree.inProgress$(),
        this.#seasonTicketRenewalsSrv.availableRowSeats.inProgress$(),
        this.#seasonTicketRenewalsSrv.availableNnzSeats.inProgress$(),
        this.#seasonTicketSrv.isSeasonTicketRatesInProgress$(),
        this.#locationSrv.getNumberOfMappedInfoBeingAssigned$()
            .pipe(map(value => value !== 0))
    ]));

    readonly isSaveDisabled$ = combineLatest([
        toObservable(this.$isLoading),
        this.#renewalsSrv.hasAnyValidChanges$(),
        this.#renewalsSrv.hasAnyInvalidLocationChanges$()
    ]).pipe(
        map(([isLoading, hasValidChanges, hasInvalidLocationChanges]) =>
            isLoading || !hasValidChanges || hasInvalidLocationChanges
        ),
        distinctUntilChanged()
    );

    readonly $renewalsEdits = toSignal(this.#renewalsSrv.getRenewalEdits$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        ));

    readonly searchGroupFields: Record<string, keyof VmSeasonTicketRenewalAvailableRow | keyof VmSeasonTicketRenewalAvailableNnz> = {
        [this.rowGroupName]: 'row_name',
        [this.nnzGroupName]: 'not_numbered_zone_name'
    };

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.FULL_SCREEN);
        effect(() => {
            const renewalEdits = this.#data.renewals.map((renewal, index) => this.#getRenewalEditFromMatData(renewal, index));
            this.#renewalsSrv.setRenewalEdits(renewalEdits);
            this.#setRenewalEditsWithMappedInfo(renewalEdits);
            this.#seasonTicketSrv.loadSeasonTicketRates(this.$seasonTicket().id.toString());
        });
    }

    readonly hasAnyDirtyChanges = (renewalEdit: VmSeasonTicketRenewalEdit): boolean => this.#renewalsSrv.hasAnyDirtyChanges(renewalEdit);
    readonly isRenewalTypeEditable = (renewalEdit: VmSeasonTicketRenewalEdit): boolean =>
        this.$renewalType() === 'XML_SEPA' ? renewalEdit.auto_renewal : true;

    ngOnDestroy(): void {
        this.#seasonTicketSrv.clearSeasonTicketRates();
    }

    closeHandler(): void {
        this.#dialogRef.close();
    }

    saveHandler(): void {
        combineLatest([
            this.#renewalsSrv.getRenewalEdits$(),
            this.#locationSrv.getAvailableSectors$(),
            this.#locationSrv.getRecordOfAvailableRows$(),
            this.#locationSrv.getRecordOfAvailableNnzs$(),
            this.#locationSrv.getRecordOfAvailableRowSeats$(),
            this.#locationSrv.getRecordOfAvailableNnzSeats$(),
            this.#seasonTicketSrv.getSeasonTicketRates$()
        ]).pipe(
            take(1)
        ).subscribe(([
            renewalEdits,
            availableSectors,
            recordOfAvailableRows,
            recordOfAvailableNnz,
            recordOfAvailableRowSeats,
            recordOfAvailableNnzSeats,
            rates
        ]) => {
            const isAutoRenewalAvailable = this.$isAutoRenewalAvailable();
            const renewalEditsToSave = renewalEdits
                .filter(renewalEdit => this.#renewalsSrv.hasValidChangesToSave(renewalEdit, isAutoRenewalAvailable))
                .map(renewalEdit => this.#renewalsSrv.completeLocationInfo(
                    renewalEdit,
                    availableSectors,
                    recordOfAvailableRows,
                    recordOfAvailableNnz,
                    recordOfAvailableRowSeats,
                    recordOfAvailableNnzSeats
                ))
                .map(renewalEdit => {
                    const renewalEditToSave = {
                        ...renewalEdit,
                        assignedRateId: rates.find(renewalRate => renewalRate.name === renewalEdit.assignedRate).id
                    };

                    if (isAutoRenewalAvailable && this.#renewalsSrv.isAutoRenewalDirty(renewalEdit)) {
                        renewalEditToSave.auto_renewal = renewalEdit.assignedAutoRenewal;
                    }

                    return renewalEditToSave;
                });
            this.#dialogRef.close(renewalEditsToSave);
        });
    }

    changeSectorHandler(sectorId: number, renewalEdit: VmSeasonTicketRenewalEdit, renewalEditIndex: number): void {
        this.#resetLocation(renewalEdit, renewalEditIndex);
        this.#assignSector(sectorId, renewalEditIndex);
    }

    changeRowOrNnzHandler(
        id: number,
        renewalEdit: VmSeasonTicketRenewalEdit,
        renewalEditIndex: number,
        matSelectChange: MatSelectChange
    ): void {
        const matOption = matSelectChange.source.selected as MatOption;
        const previousSeatId = renewalEdit.assignedSeatId;
        if (matOption.group.label === this.rowNnzGroupNames.nnz) {
            if (this.#renewalsSrv.isNnzAssigned(renewalEdit) && this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
                this.#resetNnzSeat(previousSeatId, renewalEditIndex);
            } else if (this.#renewalsSrv.isRowAssigned(renewalEdit)) {
                if (this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
                    this.#resetRowSeat(previousSeatId, renewalEditIndex);
                }
                this.#resetRow(renewalEditIndex);
            }
            this.#assignNnz(id, renewalEditIndex);
        } else if (matOption.group.label === this.rowNnzGroupNames.row) {
            if (this.#renewalsSrv.isRowAssigned(renewalEdit) && this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
                this.#resetRowSeat(previousSeatId, renewalEditIndex);
            } else if (this.#renewalsSrv.isNnzAssigned(renewalEdit) && this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
                this.#resetNnzSeat(previousSeatId, renewalEditIndex);
                this.#resetNnz(renewalEditIndex);
            }
            this.#assignRow(id, renewalEditIndex);
        }
    }

    changeRowSeatHandler(seatId: number, renewalEdit: VmSeasonTicketRenewalEdit, renewalEditIndex: number): void {
        const previousSeatId = renewalEdit.assignedSeatId;
        if (this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
            this.#changeRowSeat(seatId, renewalEditIndex, previousSeatId);
        } else {
            this.#assignRowSeat(seatId, renewalEditIndex);
        }
    }

    resetHandler(renewalEdit: VmSeasonTicketRenewalEdit, renewalEditIndex: number): void {
        if (this.#renewalsSrv.isLocationNotMappedDirty(renewalEdit)) {
            this.#resetLocation(renewalEdit, renewalEditIndex);
            this.#resetSector(renewalEditIndex);
        } else if (this.#renewalsSrv.isLocationMappedDirty(renewalEdit)) {
            this.#resetLocation(renewalEdit, renewalEditIndex);

            if (renewalEdit.actual_seat.seat_type === TicketAllocationType.notNumbered) {
                this.#assignSector(renewalEdit.actual_seat.sector_id, renewalEditIndex);
                this.#assignNnz(renewalEdit.actual_seat.not_numbered_zone_id, renewalEditIndex);
            } else if (renewalEdit.actual_seat.seat_type === TicketAllocationType.numbered) {
                this.#assignSector(renewalEdit.actual_seat.sector_id, renewalEditIndex);
                this.#assignRow(renewalEdit.actual_seat.row_id, renewalEditIndex);
                this.#assignRowSeat(renewalEdit.actual_seat.seat_id, renewalEditIndex);
            }
        }

        if (this.#renewalsSrv.isRateDirty(renewalEdit)) {
            this.#renewalsSrv.resetRate(renewalEditIndex);
        }

        if (this.#renewalsSrv.isAutoRenewalDirty(renewalEdit)) {
            this.#renewalsSrv.resetAutoRenewal(renewalEditIndex);
        }
    }

    changeRateHandler(rateName: string, renewalEditIndex: number): void {
        this.#renewalsSrv.changeRate(renewalEditIndex, rateName);
    }

    changeAutoRenewalHandler(autoRenewal: boolean, renewalEditIndex: number): void {
        this.#renewalsSrv.changeAutoRenewal(renewalEditIndex, autoRenewal);
    }

    getLocationInfo(location: SeasonTicketRenewalSeat): string {
        if (location.seat_type === TicketAllocationType.numbered) {
            return `${location.sector} | ${location.row} | ${location.seat}`;
        } else if (location.seat_type === TicketAllocationType.notNumbered) {
            return `${location.sector} | ${location.not_numbered_zone}`;
        } else {
            return undefined;
        }
    }

    isSeatSelectDisabled(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return this.#renewalsSrv.isNnzAssigned(renewalEdit);
    }

    #resetLocation(renewalEdit: VmSeasonTicketRenewalEdit, renewalEditIndex: number): void {
        const previousSeatId = renewalEdit.assignedSeatId;

        if (this.#renewalsSrv.isNnzAssigned(renewalEdit) && this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
            this.#resetNnzSeat(previousSeatId, renewalEditIndex);
            this.#resetNnz(renewalEditIndex);
        } else if (this.#renewalsSrv.isRowAssigned(renewalEdit)) {
            if (this.#renewalsSrv.isSeatAssigned(renewalEdit)) {
                this.#resetRowSeat(previousSeatId, renewalEditIndex);
            }
            this.#resetRow(renewalEditIndex);
        }
    }

    #getRenewalEditFromMatData(renewal: VmSeasonTicketRenewal, index: number): VmSeasonTicketRenewalEdit {
        const renewalEdit = this.#getRenewalEditInitialization(renewal);
        return {
            ...renewalEdit,
            sectorsToShow$: this.#getSectorsToShow$(index),
            rowsRecordKey: this.#locationSrv.initRowsRecordKey(renewalEdit),
            nnzsRecordKey: this.#locationSrv.initNnzsRecordKey(renewalEdit),
            rowNnzGroupsToShow$: this.#getRowNnzGroupsToShow$(this.#getRowsToShow$(index), this.#getNnzToShow$(index)),
            rowSeatsRecordKey: this.#locationSrv.initRowSeatsRecordKey(renewalEdit),
            rowSeatsToShow$: this.#getRowSeatsToShow$(index),
            nnzSeatsRecordKey: this.#locationSrv.initNnzsRecordKey(renewalEdit),
            assignedRate: renewalEdit.actual_rate,
            assignedAutoRenewal: renewalEdit.auto_renewal
        };
    }

    #getRenewalEditInitialization(renewal: VmSeasonTicketRenewal): VmSeasonTicketRenewalEdit {
        const { isSelected, isSelectable, ...restOfRenewal } = renewal;
        return {
            ...restOfRenewal,
            sectorsToShow$: of(null),
            rowsRecordKey: '',
            nnzsRecordKey: '',
            rowNnzGroupsToShow$: of(null),
            rowSeatsRecordKey: '',
            rowSeatsToShow$: of(null),
            nnzSeatsRecordKey: ''
        };
    }

    #getRowsToShow$(index: number): Observable<VmSeasonTicketRenewalAvailableRow[]> {
        return this.#locationSrv.getRecordOfAvailableRows$()
            .pipe(
                map(recordOfAvailableRows => {
                    const renewalEdits = this.#renewalsSrv.getRenewalEdits();
                    return recordOfAvailableRows[renewalEdits[index].rowsRecordKey]?.filter(row => {
                        const renewalEdits = this.#renewalsSrv.getRenewalEdits();
                        if (this.#locationSrv.isRowShown(renewalEdits[index], row)) {
                            return true;
                        } else {
                            this.#renewalsSrv.setRowIdToNullWhenNotShownButIsAssigned(row, index);
                            return false;
                        }
                    });
                })
            );
    }

    #getNnzToShow$(index: number): Observable<VmSeasonTicketRenewalAvailableNnz[]> {
        return this.#locationSrv.getRecordOfAvailableNnzs$()
            .pipe(
                map(recordOfAvailableNnzs => {
                    const renewalEdits = this.#renewalsSrv.getRenewalEdits();
                    return recordOfAvailableNnzs[renewalEdits[index].nnzsRecordKey]?.filter(nnz => {
                        const renewalEdits = this.#renewalsSrv.getRenewalEdits();
                        if (this.#locationSrv.isNnzShown(renewalEdits[index], nnz)) {
                            return true;
                        } else {
                            this.#renewalsSrv.setNnzIdToNullWhenNotShownButIsAssigned(nnz, index);
                            return false;
                        }
                    });
                })
            );
    }

    #getSectorsToShow$(index: number): Observable<VmSeasonTicketRenewalAvailableSector[]> {
        return this.#locationSrv.getAvailableSectors$()
            .pipe(
                map(availableSectors => availableSectors.filter(sector => {
                    const renewalEdits = this.#renewalsSrv.getRenewalEdits();
                    if (this.#locationSrv.isSectorShown(renewalEdits[index], sector)) {
                        return true;
                    } else {
                        this.#renewalsSrv.setSectorIdToNullWhenNotShownButIsAssigned(sector, index);
                        return false;
                    }
                }))
            );
    }

    #getRowNnzGroupsToShow$(
        rowsToShow$: Observable<VmSeasonTicketRenewalAvailableRow[]>,
        nnzToShow$: Observable<VmSeasonTicketRenewalAvailableNnz[]>
    ): Observable<VmRowNnzGroupsToShow> {
        return combineLatest([rowsToShow$, nnzToShow$])
            .pipe(
                map(([rowsToShow, nnzToShow]) => {
                    const groups = [
                        rowsToShow?.length && { name: this.rowNnzGroupNames.row, data: rowsToShow },
                        nnzToShow?.length && { name: this.rowNnzGroupNames.nnz, data: nnzToShow }
                    ].filter(Boolean);

                    return groups as VmRowNnzGroupsToShow;
                })
            );
    }

    #getRowSeatsToShow$(index: number): Observable<VmSeasonTicketRenewalAvailableSeat[]> {
        return this.#locationSrv.getRecordOfAvailableRowSeats$()
            .pipe(
                map(recordOfAvailableSeats => {
                    const renewalEdits = this.#renewalsSrv.getRenewalEdits();
                    return recordOfAvailableSeats[renewalEdits[index].rowSeatsRecordKey]?.filter(seat =>
                        this.#locationSrv.isRowSeatShown(renewalEdits[index], seat));
                })
            );
    }

    #setRenewalEditsWithMappedInfo(renewalEdits: VmSeasonTicketRenewalEdit[]): void {
        this.#seasonTicketRenewalsSrv.renewalsCapacityTree.load(this.$seasonTicket().id);
        this.#seasonTicketRenewalsSrv.renewalsCapacityTree.get$()
            .pipe(first(Boolean))
            .subscribe(renewalsCapacityTree => {
                const availableSectorsWithMappedInfo = this.#locationSrv.getAvailableSectorsWithMappedInfo(
                    renewalEdits,
                    renewalsCapacityTree
                );
                this.#locationSrv.setAvailableSectors(availableSectorsWithMappedInfo);

                const mappedInfos = this.#locationSrv.getMappedInfos(
                    renewalEdits,
                    availableSectorsWithMappedInfo
                );

                if (mappedInfos.length !== 0) {
                    this.#renewalsSrv.assignRowsAndNnzsWithMappedInfo(mappedInfos);
                    const renewalEditsWithMappedInfo = this.#renewalsSrv.getRenewalEdits();
                    this.#loadRowsAndNnzsWithMappedInfo(
                        renewalEditsWithMappedInfo,
                        mappedInfos
                    );

                    const mappedInfosRecordForRows = this.#locationSrv.getMappedInfoRecordForRows(
                        renewalEditsWithMappedInfo,
                        mappedInfos
                    );
                    const mappedInfosRecordForNnz = this.#locationSrv.getMappedInfoRecordForNnz(
                        renewalEditsWithMappedInfo,
                        mappedInfos
                    );
                    const numberOfMappedInfoRowsBeingAssigned = Object.keys(mappedInfosRecordForRows).length;
                    const numberOfMappedInfoNnzsBeingAssigned = Object.keys(mappedInfosRecordForNnz).length;
                    const numberOfMappedInfoBeingAssigned = numberOfMappedInfoRowsBeingAssigned + numberOfMappedInfoNnzsBeingAssigned;
                    this.#locationSrv.initNumberOfMappedInfoBeingAssigned(numberOfMappedInfoBeingAssigned);

                    if (numberOfMappedInfoRowsBeingAssigned) {
                        this.#loadAndAssignRowSeatsWithMappedInfo(renewalEditsWithMappedInfo, mappedInfosRecordForRows);
                    }

                    if (numberOfMappedInfoNnzsBeingAssigned) {
                        this.#loadAndAssignNnzSeatsWithMappedInfo(renewalEditsWithMappedInfo, mappedInfosRecordForNnz);
                    }
                }
            });
    }

    #loadRowsAndNnzsWithMappedInfo(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        mappedInfos: MappedInfo[]
    ): void {
        mappedInfos.forEach(mappedInfo => {
            this.#locationSrv.loadRowsFromASector(renewalEdits[mappedInfo.renewalIndex]);
            this.#locationSrv.loadNnzFromASector(renewalEdits[mappedInfo.renewalIndex]);
        });
    }

    #loadAndAssignRowSeatsWithMappedInfo(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        mappedInfosRecordForNumberedSeats: Record<string, MappedInfo[]>
    ): void {
        Object.keys(mappedInfosRecordForNumberedSeats).forEach(recordKey => {
            const renewalEditIndex = mappedInfosRecordForNumberedSeats[recordKey][0].renewalIndex;
            if (renewalEdits[renewalEditIndex].actual_seat.seat_type === TicketAllocationType.numbered) {
                this.#seasonTicketRenewalsSrv.availableRowSeats.load(
                    this.$seasonTicket().id,
                    renewalEdits[renewalEditIndex].actual_seat.sector_id,
                    renewalEdits[renewalEditIndex].actual_seat.row_id
                );
                this.#seasonTicketRenewalsSrv.availableRowSeats.get$(
                    renewalEdits[renewalEditIndex].actual_seat.sector_id,
                    renewalEdits[renewalEditIndex].actual_seat.row_id
                ).pipe(
                    first(Boolean)
                ).subscribe(availableRowSeats => {
                    this.#assignRowSeatsWithMappedInfo(mappedInfosRecordForNumberedSeats, recordKey, availableRowSeats);
                    this.#locationSrv.decrementNumberOfMappedInfoBeingAssigned();
                });
            }
        });
    }

    #assignRowSeatsWithMappedInfo(
        mappedInfoRecord: Record<string, MappedInfo[]>,
        recordKey: string,
        availableRowSeats: SeasonTicketRenewalAvailableSeat[]
    ): void {
        mappedInfoRecord[recordKey].forEach(mappedInfo => {
            availableRowSeats.push(mappedInfo.seat);
        });
        this.#locationSrv.setRecordOfAvailableRowSeats(availableRowSeats, recordKey);
        mappedInfoRecord[recordKey].forEach(mappedInfo => {
            this.#assignRowSeat(mappedInfo.seat.seat_id, mappedInfo.renewalIndex);
        });
    }

    #loadAndAssignNnzSeatsWithMappedInfo(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        mappedInfosRecordForNnz: Record<string, MappedInfo[]>
    ): void {
        Object.keys(mappedInfosRecordForNnz).forEach(recordKey => {
            const renewalEditIndex = mappedInfosRecordForNnz[recordKey][0].renewalIndex;
            if (renewalEdits[renewalEditIndex].actual_seat.seat_type === TicketAllocationType.notNumbered) {
                this.#seasonTicketRenewalsSrv.availableNnzSeats.load(
                    this.$seasonTicket().id,
                    renewalEdits[renewalEditIndex].actual_seat.sector_id,
                    renewalEdits[renewalEditIndex].actual_seat.not_numbered_zone_id
                );
                this.#seasonTicketRenewalsSrv.availableNnzSeats.get$(
                    renewalEdits[renewalEditIndex].actual_seat.sector_id,
                    renewalEdits[renewalEditIndex].actual_seat.not_numbered_zone_id
                ).pipe(first(Boolean))
                    .subscribe(availableNnzSeats => {
                        this.#assignNnzSeatsWithMappedInfo(mappedInfosRecordForNnz, recordKey, availableNnzSeats);
                        this.#locationSrv.decrementNumberOfMappedInfoBeingAssigned();
                    });
            }
        });
    }

    #assignNnzSeatsWithMappedInfo(
        mappedInfoRecord: Record<string, MappedInfo[]>,
        recordKey: string,
        availableNnzSeats: SeasonTicketRenewalAvailableSeat[]
    ): void {
        mappedInfoRecord[recordKey].forEach(mappedInfo => {
            availableNnzSeats.push(mappedInfo.seat);
        });
        this.#locationSrv.setRecordOfAvailableNnzSeats(availableNnzSeats, recordKey);
        mappedInfoRecord[recordKey].forEach(mappedInfo => {
            this.#assignNnzSeat(mappedInfo.seat.seat_id, mappedInfo.renewalIndex);
        });
    }

    #assignSector(sectorId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.assignSector(sectorId, renewalEditIndex);
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        this.#locationSrv.loadRowsFromASector(newRenewalEdits[renewalEditIndex]);
        this.#locationSrv.loadNnzFromASector(newRenewalEdits[renewalEditIndex]);
    }

    #assignRow(rowId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.assignRow(rowId, renewalEditIndex);
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        this.#loadRowSeats(newRenewalEdits[renewalEditIndex]);
    }

    #loadRowSeats(renewalEdit: VmSeasonTicketRenewalEdit): void {
        this.#seasonTicketRenewalsSrv.availableRowSeats.load(
            this.$seasonTicket().id,
            renewalEdit.assignedSectorId,
            renewalEdit.assignedRowId
        );
        this.#seasonTicketRenewalsSrv.availableRowSeats.get$(
            renewalEdit.assignedSectorId,
            renewalEdit.assignedRowId
        ).pipe(
            first(Boolean)
        ).subscribe(availableSeats => {
            this.#locationSrv.setRecordOfAvailableRowSeats(availableSeats, renewalEdit.rowSeatsRecordKey);
        });
    }

    #assignNnz(nnzId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.assignNnz(nnzId, renewalEditIndex);
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        this.#loadAndAssignNnzSeats(newRenewalEdits[renewalEditIndex], renewalEditIndex);
    }

    #loadAndAssignNnzSeats(renewalEdit: VmSeasonTicketRenewalEdit, renewalEditIndex: number): void {
        this.#seasonTicketRenewalsSrv.availableNnzSeats.load(
            this.$seasonTicket().id,
            renewalEdit.assignedSectorId,
            renewalEdit.assignedNnzId
        );
        this.#seasonTicketRenewalsSrv.availableNnzSeats.get$(
            renewalEdit.assignedSectorId,
            renewalEdit.assignedNnzId
        ).pipe(
            first(Boolean)
        ).subscribe(availableNnzSeats => {
            this.#locationSrv.setRecordOfAvailableNnzSeats(availableNnzSeats, renewalEdit.nnzSeatsRecordKey);
            const seat = this.#locationSrv.getNnzSeatToAssign(renewalEdit);
            this.#assignNnzSeat(seat.seat_id, renewalEditIndex);
        });
    }

    #changeRowSeat(seatId: number, renewalEditIndex: number, previousSeatId: number): void {
        this.#renewalsSrv.assignRowSeat(seatId, renewalEditIndex);
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        this.#locationSrv.updateRowSeatsWhenChangeRowSeat(newRenewalEdits[renewalEditIndex], previousSeatId);
    }

    #assignSeat(seatId: number, renewalEditIndex: number, assignMethod: 'assignRowSeat' | 'assignNnzSeat'): void {
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        const renewalEdit = newRenewalEdits[renewalEditIndex];

        if (assignMethod === 'assignRowSeat') {
            this.#locationSrv.updateRowSeatWhenAssignRowSeat(renewalEdit);
            this.#locationSrv.updateRowsWhenAssignRowSeat(renewalEdit);
        } else {
            this.#locationSrv.updateNnzSeatsWhenAssignNnzSeat(renewalEdit);
            this.#locationSrv.updateNnzWhenAssignNnzSeat(renewalEdit);
        }

        this.#locationSrv.updateSectorsWhenAssignSeat(renewalEdit);
    }

    #assignRowSeat(seatId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.assignRowSeat(seatId, renewalEditIndex);
        this.#assignSeat(seatId, renewalEditIndex, 'assignRowSeat');
    }

    #assignNnzSeat(seatId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.assignNnzSeat(seatId, renewalEditIndex);
        this.#assignSeat(seatId, renewalEditIndex, 'assignNnzSeat');
    }

    #resetSector(renewalEditIndex: number): void {
        this.#renewalsSrv.resetSector(renewalEditIndex);
        this.#locationSrv.updateRowsWhenResetSector();
        this.#locationSrv.updateNnzsWhenResetSector();
    }

    #resetRow(renewalEditIndex: number): void {
        this.#renewalsSrv.resetRow(renewalEditIndex);
        this.#locationSrv.updateRowSeatsWhenResetRow();
    }

    #resetNnz(renewalEditIndex: number): void {
        this.#renewalsSrv.resetNnz(renewalEditIndex);
        this.#locationSrv.updateNnzSeatsWhenResetNnz();
    }

    #resetRowSeat(previousSeatId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.resetSeat(renewalEditIndex);
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        this.#locationSrv.updateRowSeatsWhenResetSeat(newRenewalEdits[renewalEditIndex], previousSeatId);
        this.#locationSrv.updateRowsWhenResetSeat(newRenewalEdits[renewalEditIndex]);
        this.#locationSrv.updateSectorsWhenResetSeat(newRenewalEdits[renewalEditIndex]);
    }

    #resetNnzSeat(previousSeatId: number, renewalEditIndex: number): void {
        this.#renewalsSrv.resetSeat(renewalEditIndex);
        const newRenewalEdits = this.#renewalsSrv.getRenewalEdits();
        this.#locationSrv.updateNnzSeatsWhenResetSeat(newRenewalEdits[renewalEditIndex], previousSeatId);
        this.#locationSrv.updateNnzWhenResetSeat(newRenewalEdits[renewalEditIndex]);
        this.#locationSrv.updateSectorsWhenResetSeat(newRenewalEdits[renewalEditIndex]);
    }
}