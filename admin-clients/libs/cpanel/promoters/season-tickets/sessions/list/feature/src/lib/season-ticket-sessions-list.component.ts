import { Metadata } from '@OneboxTM/utils-state';
import {
    SeasonTicketGenerationStatus, SeasonTicketsService, SeasonTicketStatus
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    GetSeasonTicketSessionsRequest, SeasonTicketSession, seasonTicketSessionsProviders, SeasonTicketSessionsService,
    SeasonTicketSessionStatus
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import {
    SeasonTicketSessionsListState, SeasonTicketSessionsListAssignService, SeasonTicketSessionsListRemoveService,
    SeasonTicketSessionsListSaveService, SeasonTicketSessionsListActionsService, SeasonTicketSessionsListService,
    SeasonTicketSessionsAction, VmSeasonTicketSession
} from '@admin-clients/cpanel-promoters-season-tickets-sessions-list-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, FilterItem, ListFilteredComponent, ListFiltersService, MessageDialogService,
    ObMatDialogConfig, PaginatorComponent, SearchInputComponent, SortFilterComponent, UnsavedChangesDialogResult
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal, viewChild } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSort, SortDirection } from '@angular/material/sort';
import { BehaviorSubject, combineLatest, Observable, of } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, startWith, switchMap, take } from 'rxjs/operators';
import { SeasonTicketSessionsListDialogComponent } from './dialog/season-ticket-sessions-list-dialog.component';
import { SeasonTicketSessionsListFilterComponent } from './filter/season-ticket-sessions-list-filter.component';

@Component({
    selector: 'app-season-ticket-sessions-list',
    templateUrl: './season-ticket-sessions-list.component.html',
    styleUrls: ['./season-ticket-sessions-list.component.scss'],
    providers: [
        ListFiltersService, seasonTicketSessionsProviders, SeasonTicketSessionsListState, SeasonTicketSessionsListAssignService,
        SeasonTicketSessionsListRemoveService, SeasonTicketSessionsListSaveService, SeasonTicketSessionsListActionsService,
        SeasonTicketSessionsListService
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SeasonTicketSessionsListComponent extends ListFilteredComponent implements OnInit, AfterViewInit, WritingComponent {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #sessionsSrv = inject(SeasonTicketSessionsService);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #assignSrv = inject(SeasonTicketSessionsListAssignService);
    readonly #removeSrv = inject(SeasonTicketSessionsListRemoveService);
    readonly #saveSrv = inject(SeasonTicketSessionsListSaveService);
    readonly #actionsServ = inject(SeasonTicketSessionsListActionsService);
    readonly #sessionsListSrv = inject(SeasonTicketSessionsListService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #fb = inject(UntypedFormBuilder);

    private readonly _matSort = viewChild<MatSort>(MatSort);
    private readonly _paginatorComponent = viewChild<PaginatorComponent>(PaginatorComponent);
    private readonly _searchInputComponent = viewChild<SearchInputComponent>(SearchInputComponent);
    private readonly _filterComponent = viewChild<SeasonTicketSessionsListFilterComponent>(SeasonTicketSessionsListFilterComponent);

    readonly $showLoyaltyPointsSettings = toSignal(
        this.#entitiesSrv.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    readonly #$seasonTicketStatus = toSignal(this.#seasonTicketSrv.seasonTicketStatus.get$()
        .pipe(
            filter(value => value !== null),
            takeUntilDestroyed(this.#destroyRef)
        ));

    readonly #isStatusSetup = this.#$seasonTicketStatus().status === SeasonTicketStatus.setUp;
    readonly #seasonTicketStatusBS = new BehaviorSubject(this.#$seasonTicketStatus);
    readonly #isGenerationStatusInProgressBS = new BehaviorSubject<boolean>(this.#$seasonTicketStatus().status &&
        this.#$seasonTicketStatus().generation_status === SeasonTicketGenerationStatus.inProgress);

    readonly #isGenerationStatusReady = this.#$seasonTicketStatus().status &&
        this.#$seasonTicketStatus().generation_status === SeasonTicketGenerationStatus.ready;

    readonly #seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$()
        .pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#destroyRef)
        ));

    #request: GetSeasonTicketSessionsRequest;
    #isStatusSetupBS = new BehaviorSubject<boolean>(this.#isStatusSetup);
    #sortFilterComponent: SortFilterComponent;
    $isSetUpUnderstood = signal(false);
    isMasterToggleChecked$: Observable<boolean>;
    isMasterToggleIndeterminated$: Observable<boolean>;
    isSaveEnabled$: Observable<boolean>;
    isValidateSessionsEnabled$: Observable<boolean>;
    isRemoveValidationsEnabled$: Observable<boolean>;
    isReAssignSessionsEnabled$: Observable<boolean>;
    isUnAssignSessionsEnabled$: Observable<boolean>;
    isCancelChangesEnabled$: Observable<boolean>;
    canChangeFilters$: Observable<boolean>;

    displayedColumns = ['selection', 'session_name', 'status', 'session_starting_date', 'event_name'];
    readonly seasonTicketSessionsPageSize = 20;
    readonly initSortCol = 'session_name';
    readonly initSortDir: SortDirection = 'asc';
    readonly dateTimeFormats = DateTimeFormats;
    readonly seasonTicketSessionStatus = SeasonTicketSessionStatus;
    readonly form = this.#fb.group({});
    readonly sessionsList$ = this.#sessionsListSrv.getSessionsList$();
    readonly $seasonTicketLoyaltyPoints = toSignal(this.#seasonTicketSrv.seasonTicketLoyaltyPoint.get$());
    readonly isValidationInProgress$ = this.#assignSrv.isValidationInProgress();
    readonly isSaveChangesInProgress$ = this.#saveSrv.isSavingChangesInProgress$();
    readonly isGenerationStatusInProgress$ = this.#isGenerationStatusInProgressBS.asObservable();
    readonly isStatusSetUp$ = this.#isStatusSetupBS.asObservable();
    readonly $isLoyaltyPointsLoading = toSignal(this.#seasonTicketSrv.seasonTicketLoyaltyPoint.loading$());
    readonly $isLoading = toSignal(booleanOrMerge([
        this.#sessionsSrv.sessions.loading$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$()
    ]));

    readonly seasonTicketSessionsMetadata$: Observable<Metadata> = this.#sessionsSrv.sessions.getMetadata$()
        .pipe(
            filter(Boolean),
            shareReplay(1)
        );

    readonly isHandsetOrTablet$ = isHandsetOrTablet$().pipe(shareReplay(1));

    ngOnInit(): void {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.init);
        this.#loadSessions();
        this.#setSessionsList();
        this.#setInteractionConditions();
        this.#savingFinishedHandler();
        this.#assigningFinishedHandler();
        this.#removingFinishHandler();
        this.#initForm();
    }

    ngAfterViewInit(): void {
        this.#sortFilterComponent = new SortFilterComponent(
            this._matSort(),
            this.isCancelChangesEnabled$
                .pipe(
                    take(1),
                    switchMap(isCancel => isCancel ?
                        this.#msgDialogService
                            .defaultUnsavedChangesWarn({ message: 'FILTER.ACTIONS.UNSAVED_CHANGES', actionLabel: 'FORMS.ACTIONS.NO' })
                        : of(true))
                ));
        this.initListFilteredComponent([
            this._paginatorComponent(),
            this.#sortFilterComponent,
            this._searchInputComponent(),
            this._filterComponent()
        ]);
    }

    loadData(filters: FilterItem[]): void {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.tableAction);
        this.#setRequest(filters);
        this.#loadSessions();
    }

    masterToggle(): void {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.toggleTableRows);
        this.#sessionsListSrv.toggleAllSessionRows();
    }

    toggleRow(row: SeasonTicketSession): void {
        if (this.#isStatusSetup) {
            this.#toggleSelection(row);
        }
    }

    assign(): void {
        if (this.#seasonTicket()?.has_sales) {
            this.#displaySeasonWithSalesWarning()
                .pipe(take(1))
                .subscribe((res: boolean) => {
                    if (res) {
                        this.#assign();
                    }
                });
        } else {
            this.#assign();
        }
    }

    remove(): void {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.unassign);
        this.#removeSessionsStatus();
    }

    save(): void {
        this.#save$().subscribe();
    }

    cancelChanges(): void {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.cancel);
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#loadLoyaltyPoints();
        this.#loadSessions();
    }

    // warn message when navigating to another link
    canDeactivate(): Observable<boolean> {
        return this.isCancelChangesEnabled$
            .pipe(
                take(1),
                switchMap(isCancel => {
                    if (!isCancel) return of(true);

                    return this.#msgDialogService.openRichUnsavedChangesWarn()
                        .pipe(
                            switchMap(result => {
                                if (result === UnsavedChangesDialogResult.continue) {
                                    return of(true);
                                } else if (result === UnsavedChangesDialogResult.save) {

                                    let obsCond: Observable<boolean>;
                                    if (this.#someSessionStateToSave() && this.#isFormStateToSave()) {
                                        obsCond = combineLatest([
                                            this.isSaveChangesInProgress$.pipe(first(value => !value)),
                                            this.#seasonTicketSrv.seasonTicketLoyaltyPoint.loading$().pipe(first(value => !value))
                                        ]).pipe(map(() => true));
                                    } else if (this.#someSessionStateToSave()) {
                                        obsCond = this.isSaveChangesInProgress$.pipe(first(value => !value));
                                    } else {
                                        obsCond = this.#seasonTicketSrv.seasonTicketLoyaltyPoint.loading$()
                                            .pipe(
                                                first(value => !value),
                                                map(() => true)
                                            );
                                    }

                                    return this.#save$()
                                        .pipe(
                                            switchMap(isSave => {
                                                if (isSave) return obsCond;
                                                return of(true);
                                            })
                                        );
                                }
                                return of(false); // cancel
                            })
                        );
                })
            );
    }

    #save$(): Observable<unknown> {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.save);
        if (this.#someSessionStateToSave()) {
            this.#sessionsListSrv.deselectAllSessions();

            if (this.#sessionsListSrv.isSomeSessionToBeUnassigned()) {
                return this.#unAssignmentSaveMessageDialog()
                    .pipe(
                        switchMap(isSave => {
                            if (isSave) this.#saveSessionsStatus();
                            return of(isSave);
                        })
                    );
            } else if (this.#sessionsListSrv.isSomeSessionValid()) {
                this.#saveSessionsStatus();
            }
        } else {
            this.#saveFormData();
        }

        return of(true);
    }

    #someSessionStateToSave(): boolean {
        return this.#sessionsListSrv.isSomeSessionToBeUnassigned() || this.#sessionsListSrv.isSomeSessionValid();
    }

    #isFormStateToSave(): boolean {
        return this.$showLoyaltyPointsSettings() && this.form.valid;
    }

    #setRequest(filters: FilterItem[]): void {
        this.#request = {
            limit: this.seasonTicketSessionsPageSize,
            offset: 0
        };

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
                    case 'EVENT':
                        this.#request.event_id = values[0].value;
                        break;
                    case 'STATUS':
                        this.#request.status = values[0].value;
                        break;
                    case 'START_DATE':
                        this.#request.startDate = values[0].value;
                        break;
                    case 'END_DATE':
                        this.#request.endDate = values[0].value;
                        break;
                }
            }
        });
    }

    #setInteractionConditions(): void {
        // Set enabled states
        const isAllowedToSaveCancelOrAssign$ = this.#seasonTicketStatusBS.asObservable()
            .pipe(
                map(() => !this.$isLoading() && this.#isGenerationStatusReady && this.#isStatusSetup),
                distinctUntilChanged(),
                takeUntilDestroyed(this.#destroyRef),
                shareReplay(1)
            );

        const createEnabledState$ = (conditionFn: () => boolean): Observable<boolean> =>
            combineLatest([isAllowedToSaveCancelOrAssign$, this.sessionsList$])
                .pipe(
                    map(([isAllowed]) => isAllowed && conditionFn()),
                    distinctUntilChanged()
                );

        this.isValidateSessionsEnabled$ = createEnabledState$(() => this.#sessionsListSrv.isSomeSessionToValidate());
        this.isReAssignSessionsEnabled$ = createEnabledState$(() => this.#sessionsListSrv.isSomeSessionToReassign());
        this.isRemoveValidationsEnabled$ = createEnabledState$(() => this.#sessionsListSrv.isSomeSessionValidationToRemove());
        this.isUnAssignSessionsEnabled$ = createEnabledState$(() => this.#sessionsListSrv.isSomeSessionToUnAssign());

        // Set toggle states
        const createToggleState$ = (toggleConditionFn: (allSelected: boolean, someSelected: boolean) => boolean): Observable<boolean> =>
            combineLatest([
                this.sessionsList$.pipe(map(() => this.#sessionsListSrv.isAllSelectableSessionRowsSelected())),
                this.sessionsList$.pipe(map(() => this.#sessionsListSrv.isSomeSessionSelected()))
            ]).pipe(
                map(([allSelected, someSelected]) => toggleConditionFn(allSelected, someSelected)),
                distinctUntilChanged()
            );

        this.isMasterToggleChecked$ = createToggleState$((allSelected, someSelected) => allSelected && someSelected);
        this.isMasterToggleIndeterminated$ = createToggleState$((allSelected, someSelected) => !allSelected && someSelected);

        // Set buttons status
        const isSomeSessionValid$ = this.sessionsList$.pipe(map(() => this.#sessionsListSrv.isSomeSessionValid()));
        const isSomeSessionToBeUnassigned$ = this.sessionsList$.pipe(map(() => this.#sessionsListSrv.isSomeSessionToBeUnassigned()));

        this.isSaveEnabled$ = combineLatest([
            isAllowedToSaveCancelOrAssign$,
            isSomeSessionValid$,
            isSomeSessionToBeUnassigned$,
            this.form.valueChanges.pipe(startWith(this.form.value), map(() => this.form.dirty))
        ]).pipe(
            map(([isAllowed, isValid, isToBeUnAssigned, isFormDirty]) => isAllowed && (isValid || isToBeUnAssigned || isFormDirty)),
            distinctUntilChanged()
        );

        this.isCancelChangesEnabled$ = this.isSaveEnabled$.pipe(
            distinctUntilChanged(),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

        this.canChangeFilters$ = this.isCancelChangesEnabled$
            .pipe(
                take(1),
                switchMap(isCancel => isCancel ?
                    this.#msgDialogService
                        .defaultUnsavedChangesWarn({ message: 'FILTER.ACTIONS.UNSAVED_CHANGES', actionLabel: 'FORMS.ACTIONS.NO' })
                    : of(true)
                )
            );
    }

    #loadSessions(): void {
        if (this.#request && this.#isGenerationStatusReady) {
            this.#sessionsSrv.sessions.load(this.#seasonTicket().id.toString(), this.#request);
        }
    }

    #toggleSelection(row: SeasonTicketSession): void {
        if (this.#sessionsListSrv.isSessionRowSelectable(row)) {
            this.#actionsServ.setAction(SeasonTicketSessionsAction.toggleTableRows);
            this.#sessionsListSrv.toggleSessionRowSelection(row);
        }
    }

    #assign(): void {
        this.#actionsServ.setAction(SeasonTicketSessionsAction.assign);
        this.#assignSessions();
    }

    #assignSessions(): void {
        this.#assignSrv.initAssigningProgress();
        this.#assignSrv.assign();
    }

    #removeSessionsStatus(): void {
        this.#removeSrv.removeStatus();
    }

    #unAssignmentSaveMessageDialog(): Observable<boolean> {
        return this.#msgDialogService.showWarn({
            size: DialogSize.MEDIUM,
            title: 'TITLES.NOTICE',
            message: 'SEASON_TICKET.ACTIONS.SAVE_UNASSIGNMENTS',
            actionLabel: 'FILTER.ACTIONS.YES'
        });
    }

    #displaySeasonWithSalesWarning(): Observable<boolean> {
        return this.#msgDialogService.showWarn({
            actionLabel: 'SEASON_TICKET.SESSION.SESSION_WITH_SALES_MODAL_CONTINUE',
            cancelLabel: 'SEASON_TICKET.SESSION.SESSION_WITH_SALES_MODAL_CANCEL',
            title: 'SEASON_TICKET.SESSION.SESSION_WITH_SALES_MODAL_TITLE',
            message: 'SESSION.SESSION_WITH_SALES_MODAL_MESSAGE',
            showCancelButton: true,
            invertSuccess: false,
            size: DialogSize.MEDIUM
        });
    }

    #saveSessionsStatus(): void {
        this.#saveSrv.initSavingProgress();
        this.#saveFormData();
        this.#saveSrv.saveSessionStatus();
    }

    #setSessionsList(): void {
        this.#sessionsSrv.sessions.getData$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(sessions => {
                const vmSessions = this.#getVmSessions(sessions);
                this.#sessionsListSrv.setSessionsList(vmSessions);
            });
    }

    #getVmSessions(sessions: SeasonTicketSession[]): VmSeasonTicketSession[] {
        sessions = sessions ?? [];
        return sessions.map(session =>
        ({
            ...session,
            is_session_row_selectable: session.session_assignable.assignable ||
                session.status === SeasonTicketSessionStatus.assigned,
            is_selected: false,
            is_process_session_assignment_done: false,
            is_session_validated: false,
            is_session_valid: false,
            is_session_not_valid: false,
            session_not_valid_reason: null,
            sessions_not_unassigned_reason: null,
            is_validation_in_progress: false,
            is_assignment_in_progress: false,
            is_process_session_unassignment_done: false,
            is_unassignment_in_progress: false,
            is_session_assignment_to_be_unassigned: false
        })
        );
    }

    #savingFinishedHandler(): void {
        this.#saveSrv.savingValidationsFinish$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() =>
                this.#sessionsListSrv.isSomeSessionToBeUnassigned() ? this.#saveSessionsStatus() : this.#updateBarcodes()
            );

        this.#saveSrv.savingUnAssignmentsFinish$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.#updateBarcodes());
    }

    #updateBarcodes(): void {
        if (this.#sessionsListSrv.isSomeBarcodeToBeUpdated()) {
            this.#sessionsSrv.updateSessionsBarcodes(this.#seasonTicket().id)
                .subscribe(() => this.#finishSaving());
        } else {
            this.#finishSaving();
        }
    }

    #finishSaving(): void {
        if (this.#sessionsListSrv.hasSomeSessionNotBeenAssigned() || this.#sessionsListSrv.hasSomeSessionNotBeenUnassigned()) {
            this.sessionsList$
                .pipe(
                    take(1),
                    switchMap(sessions =>
                        this.#matDialog.open(
                            SeasonTicketSessionsListDialogComponent,
                            new ObMatDialogConfig({
                                sessions,
                                numberOfAssignedSessions: this.#sessionsListSrv.numberOfAssignedSessions,
                                numberOfUnassignedSessions: this.#sessionsListSrv.numberOfUnassignedSessions
                            }))
                            .beforeClosed()
                    )).subscribe();
        } else {
            this.#ephemeralSrv.showSaveSuccess();
        }
        this.#saveSrv.finishSavingProgress();
    }

    #assigningFinishedHandler(): void {
        const value = this.#sessionsListSrv.numberOfValidatedSessions.valid
            + this.#sessionsListSrv.numberOfReassignedSessions.assigned;
        const totalValue = this.#sessionsListSrv.numberOfValidatedSessions.validated
            + this.#sessionsListSrv.numberOfReassignedSessions.toAssign;

        this.#assignSrv.reassigningFinish$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                if (this.#sessionsListSrv.isSomeSessionToValidate()) {
                    this.#assignSessions();
                } else {
                    this.#showSuccessMsg('SEASON_TICKET.SESSIONS_REASSIGNMENT_UPDATE', { value, totalValue });
                    this.#assignSrv.finishAssigningProcess();
                }
            });

        this.#assignSrv.validatingFinish$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#showSuccessMsg('SEASON_TICKET.SESSIONS_VALIDATION_UPDATE', { value, totalValue });
                this.#assignSrv.finishAssigningProcess();
            });
    }

    #removingFinishHandler(): void {
        const value = this.#sessionsListSrv.numberOfSessionsToUnAssign +
            this.#sessionsListSrv.numberOfUnvalidatedSessions;

        this.#removeSrv.removingUnAssigmentFinish$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                if (this.#sessionsListSrv.isSomeSessionValidationToRemove()) {
                    this.#removeSessionsStatus();
                } else {
                    this.#showSuccessMsg('SEASON_TICKET.SESSIONS_UNASSIGNMENT_UPDATE', { value });
                    this.#removeSrv.finishRemovingStatusProcess();
                }
            });

        this.#removeSrv.removingValidationFinish$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                this.#showSuccessMsg('SEASON_TICKET.SESSIONS_UNVALIDATION_UPDATE', { value });
                this.#removeSrv.finishRemovingStatusProcess();
            });
    }

    #showSuccessMsg(msgKey: string, msgParams: { value: number; totalValue?: number }): void {
        this.#ephemeralSrv.showSuccess({ msgKey, msgParams });
    }

    #initForm(): void {
        this.#loadLoyaltyPoints();
        if (this.$showLoyaltyPointsSettings()) {
            this.displayedColumns.push('attendance_points');
            this.sessionsList$.pipe(
                takeUntilDestroyed(this.#destroyRef),
                map(sessionsList =>
                    sessionsList?.map(session => {
                        const controlName = `${session.session_id}`;
                        const existingControl = this.form.get(controlName);
                        const loyaltyPoints = this.$seasonTicketLoyaltyPoints()?.sessions?.
                            find(s => s.sessionId === session.session_id)?.attendance || 0;

                        if (!existingControl) {
                            this.form.setControl(controlName, this.#fb.control(loyaltyPoints, [Validators.required]),
                                { emitEvent: false });
                        } else if (!existingControl.dirty) {
                            existingControl.setValue(loyaltyPoints, { emitEvent: false });
                        }

                        return { ...session, loyalty_points: loyaltyPoints };
                    })
                )
            ).subscribe();
        }
    }

    #saveFormData(): void {
        if (this.#isFormStateToSave()) {
            this.sessionsList$.pipe(first(Boolean))
                .subscribe(sessionsList => {
                    const notAssignedSessions = sessionsList.filter(
                        session => session.status === this.seasonTicketSessionStatus.notAssigned);

                    const loyaltyPointsData = Object.keys(this.form.controls).map(sessionId => {
                        const isNotAssigned = notAssignedSessions.some(session => session.session_id === Number(sessionId));
                        const loyaltyPoints = isNotAssigned ? 0 : this.form.get(sessionId).value;

                        const currentSession = this.$seasonTicketLoyaltyPoints()?.sessions.find(s => s.sessionId === Number(sessionId));
                        const transfer = currentSession?.transfer || 0;

                        return {
                            sessionId: Number(sessionId),
                            transfer,
                            attendance: loyaltyPoints
                        };
                    });
                    this.#seasonTicketSrv.seasonTicketLoyaltyPoint.update(Number(this.#seasonTicket().id), { sessions: loyaltyPointsData })
                        .subscribe(() => {
                            this.form.markAsPristine();
                            this.form.markAsUntouched();
                            this.form.updateValueAndValidity();
                            this.#loadLoyaltyPoints();
                            if (!this.#someSessionStateToSave()) {
                                this.#ephemeralSrv.showSaveSuccess();
                            }
                        });
                });
        }
    }

    #loadLoyaltyPoints(): void {
        if (this.$showLoyaltyPointsSettings()) {
            this.#seasonTicketSrv.seasonTicketLoyaltyPoint.load(Number(this.#seasonTicket().id));
        }
    }
}
