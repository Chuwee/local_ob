import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { EventsService, PutEvent } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, GetSessionsRequest, SessionsFilterFields, SessionType

} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionsSelectorFilterComponent } from '@admin-clients/cpanel/shared/feature/sessions-selector-filter';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, SearchablePaginatedSelectionModule

} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { atLeastOneRequiredInArray, differenceWith, maxDecimalLength, rangeValidator, unionWith }
    from '@admin-clients/shared/utility/utils';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, EventEmitter, inject, signal, viewChildren, OnDestroy } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatRadioModule } from '@angular/material/radio';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import {
    filter, first, forkJoin,
    map, Observable, of, scan, shareReplay, startWith, switchMap, take, tap, throwError,
    withLatestFrom
} from 'rxjs';

export const delayType = ['FROM', 'TO', 'RANGE'] as const;
export type DelayType = typeof delayType[number];

const PAGE_SIZE = 5;

@Component({
    selector: 'app-event-transfer-seats',
    templateUrl: './event-transfer-seats.component.html',
    styleUrls: ['./event-transfer-seats.component.scss'],
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatExpansionModule, MatRadioModule,
        MatFormFieldModule, MatInputModule, TranslatePipe, MatTableModule,
        MatDivider, FormControlErrorsComponent, MatCheckbox, MatSlideToggle,
        MatRadioModule, SearchablePaginatedSelectionModule, AsyncPipe, DateTimePipe,
        SessionsSelectorFilterComponent, UpperCasePipe, MatIcon, MatTooltip
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventTransferSeatsComponent implements OnDestroy {

    readonly #eventsSrv = inject(EventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    private readonly _matExpansionPanelQueryList = viewChildren(MatExpansionPanel);

    #isSelectAllChecked: boolean;
    #prevMaxTicketTransfers: number;
    #sessionsFilter: GetSessionsRequest = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'start_date:asc',
        status: [],
        type: SessionType.session,
        fields: [
            SessionsFilterFields.name, SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
            SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType
        ]
    };

    readonly showSelectedOnlyClick = new EventEmitter<void>();
    readonly pageSize = PAGE_SIZE;
    readonly columns = ['session', 'date', 'points'];
    readonly dateTimeFormats = DateTimeFormats;

    readonly $event = toSignal(this.#eventsSrv.event.get$().pipe(first(Boolean)));
    readonly $showFriendsSettings = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings?.allow_friends)
    ));

    readonly $showLoyaltyPointsSettings = toSignal(this.#entitiesSrv.getEntity$().pipe(
        first(Boolean),
        map(entity => entity.settings?.allow_loyalty_points)
    ));

    readonly loyaltyForm = this.#fb.group({});
    readonly form = this.#fb.group({
        enabled: [false],
        enable_transfer_delay: [false],
        transfer_delay_type: ['FROM' as DelayType, Validators.required],
        transfer_delay: this.#fb.group({
            from: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            to: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            range: this.#fb.group({
                min_delay_time: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
                max_delay_time: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]]
            }, { validators: [rangeValidator('max_delay_time', 'min_delay_time', false)] })
        }),
        enable_recover_delay: [false],
        recovery_ticket_max_delay_time: [1, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
        enable_max_ticket_transfers: [false],
        enable_friends_family: [false],
        enable_multiple_transfers: [false],
        max_ticket_transfers: [0, Validators.required],
        sessions: this.#fb.group({
            select_all: [true],
            selection: this.#fb.control([], [atLeastOneRequiredInArray()])
        }),
        loyalty_points: this.loyaltyForm
    });

    readonly sessionsSelectedOnly$ = this.showSelectedOnlyClick.pipe(
        scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
        startWith(false),
        takeUntilDestroyed(),
        shareReplay(1)
    );

    readonly areSessionsLoading$ = this.#eventSessionsSrv.sessionList.inProgress$();
    readonly selectedSessions$ = this.form.controls.sessions.controls.selection.valueChanges.pipe(
        filter(Boolean),
        map(selected => selected?.sort((a, b) => a.id - b.id)),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly sessionsMetadata$ = this.sessionsSelectedOnly$.pipe(
        switchMap(isActive => isActive ?
            this.selectedSessions$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
            this.#eventSessionsSrv.sessionList.get$()
                .pipe(map(sl => sl?.metadata))
        ),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly allSessions$ = this.#eventSessionsSrv.sessionList.get$()
        .pipe(
            filter(Boolean),
            map((sessionsList => this.#mapSessionsToForm(sessionsList))),
            shareReplay(1)
        );

    readonly $sessionsMetadata = toSignal(this.sessionsMetadata$);
    readonly $selectedSessions = toSignal(this.selectedSessions$);
    readonly $allSessions = toSignal(this.allSessions$);

    readonly sessionsList$ = this.sessionsSelectedOnly$.pipe(
        switchMap(isActive => isActive ? this.selectedSessions$ : this.allSessions$),
        map(sessions => sessions.reduce((sessionsGroups, session) => {
            if (session.type === SessionType.session) {
                sessionsGroups.sessions.push(session);
            } else if (session.type) {
                sessionsGroups.sessionPacks.push(session);
            } else {
                sessionsGroups.unknown.push(session);
            }
            return sessionsGroups;
        }, {
            sessionPacks: [],
            sessions: [],
            unknown: []
        })),
        shareReplay(1)
    );

    readonly $loyaltyPointsMetadata = signal({ total: 0, offset: 0, limit: PAGE_SIZE });
    readonly $loyaltyPointsSessionsMetadata = computed(() => this.#isSelectAllChecked ? this.$sessionsMetadata() : this.$loyaltyPointsMetadata());

    readonly $loyaltyPointsSessions = computed(() => {
        const sessions = this.#isSelectAllChecked ? this.$allSessions() : (this.$selectedSessions() || []);
        const metadata = this.#isSelectAllChecked ? this.$sessionsMetadata() : this.$loyaltyPointsSessionsMetadata();
        return this.#isSelectAllChecked ? sessions : sessions?.slice(metadata?.offset, metadata?.offset + metadata?.limit) || [];
    });

    readonly loyaltyPointsSessionsMetadata$ = toObservable(this.$loyaltyPointsSessionsMetadata);
    readonly loyaltyPointsSessions$ = toObservable(this.$loyaltyPointsSessions);

    constructor() {
        this.#handleFormChanges();
        this.#initializeForm();
        this.#setLoyaltyPointsTable();
        this.#handleAllSessionsSelection();
    }

    ngOnDestroy(): void {
        this.#eventSessionsSrv.clearAllSessions();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const updateTransferSeat: PutEvent = this.#getSaveRequestBody();
            let canContinue$: Observable<boolean>;

            if (this.#prevMaxTicketTransfers !==
                updateTransferSeat.settings.transfer_settings.max_ticket_transfers &&
                this.form.controls.max_ticket_transfers.dirty) {
                canContinue$ = this.#msgDialogSrv.showWarn({
                    title: 'EVENTS.TRANSFER_SEATS.MAX_LIMIT_TRANSFERS_SAVE_WARNING_TITLE',
                    message: 'EVENTS.TRANSFER_SEATS.MAX_LIMIT_TRANSFERS_SAVE_WARNING_INFO',
                    actionLabel: 'FORMS.ACTIONS.UPDATE',
                    showCancelButton: true,
                    size: DialogSize.MEDIUM
                });
            } else {
                canContinue$ = of(true);
            }

            return canContinue$.pipe(
                switchMap(canContinue => {
                    if (!canContinue) return of(null);

                    obs$.push(this.#eventsSrv.event.update(this.$event().id, updateTransferSeat));

                    return forkJoin(obs$).pipe(
                        tap(() => {
                            this.#ephemeralMessageService.showSaveSuccess();
                            this.form.markAsPristine();
                            this.form.markAsUntouched();
                            this.#eventsSrv.event.load(this.$event().id.toString());
                            this.loadEventSessions();
                            this.#loadLoyaltyPoints();
                        })
                    );
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }

    }

    cancel(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#eventsSrv.event.load(this.$event().id.toString());
        this.loadEventSessions();
        this.#loadLoyaltyPoints();
    }

    loadEventSessions(): void {
        this.#eventSessionsSrv.sessionList.load(this.$event().id, this.#sessionsFilter);
        this.#eventSessionsSrv.sessionList.get$().pipe(
            withLatestFrom(this.sessionsSelectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    }

    loyaltyPointsTableChanged(event: any): void {
        this.$loyaltyPointsMetadata.set({
            total: this.$selectedSessions()?.length || 0,
            offset: event?.offset || 0,
            limit: event?.limit || PAGE_SIZE
        });
    }

    selectAll(change?: MatCheckboxChange): void {
        this.#isSelectAllChecked = change?.checked;
        this.#eventSessionsSrv.loadAllSessions(this.$event().id, {
            ...this.#sessionsFilter,
            limit: undefined,
            fields: [
                SessionsFilterFields.name, SessionsFilterFields.startDate, SessionsFilterFields.settingsSmartbookingStatus,
                SessionsFilterFields.settingsSmartbookingRelatedSession, SessionsFilterFields.venueTplType
            ]
        });
        this.form.controls.sessions.controls.selection.markAsTouched();
        this.form.controls.sessions.controls.selection.markAsDirty();
    }

    filterChangeHandler(filters): void {
        this.#sessionsFilter = {
            ...this.#sessionsFilter,
            ...filters
        };
        this.loadEventSessions();
    }

    #initializeForm(): void {
        this.#eventsSrv.event.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed())
            .subscribe(event => {
                this.$event().id = event.id;
                this.#prevMaxTicketTransfers = event.settings.transfer_settings?.max_ticket_transfers;

                const minDelayTime = event.settings.transfer_settings?.transfer_ticket_min_delay_time || null;
                const maxDelayTime = event.settings.transfer_settings?.transfer_ticket_max_delay_time || null;
                const config = this.#getTransferDelayConfig(minDelayTime, maxDelayTime);
                const allowedTransferSessions = event.settings.transfer_settings?.allowed_transfer_sessions;

                for (const [controlName, obj] of Object.entries(config)) {
                    let control = this.form.controls[controlName];
                    if (delayType.find(type => type.toLowerCase() === controlName)) {
                        control = this.form.controls.transfer_delay.controls[controlName];
                    }
                    control.patchValue(obj.value);
                    obj.enabled ? control.enable() : control.disable();
                }

                this.form.patchValue({
                    enabled: event.settings.transfer_settings?.enabled || false,
                    enable_recover_delay: event.settings.transfer_settings?.recovery_ticket_max_delay_time > 0,
                    recovery_ticket_max_delay_time: event.settings.transfer_settings?.recovery_ticket_max_delay_time || null,
                    enable_max_ticket_transfers: event.settings.transfer_settings?.enable_max_ticket_transfers || false,
                    enable_multiple_transfers: event.settings.transfer_settings?.enable_multiple_transfers || false,
                    enable_friends_family: event.settings.transfer_settings?.transfer_policy === 'FRIENDS_AND_FAMILY',
                    max_ticket_transfers: event.settings.transfer_settings?.max_ticket_transfers || 0,
                    sessions: {
                        select_all: !(event.settings.transfer_settings?.restrict_transfer_by_sessions),
                        selection: !!allowedTransferSessions
                            ? allowedTransferSessions.map(sessionId => ({ id: sessionId, name: '', start: '' }))
                            : []
                    }
                });
                if (!event.settings.transfer_settings?.enable_max_ticket_transfers) {
                    this.form.controls.max_ticket_transfers.disable();
                }

                if (!this.form.controls.enable_recover_delay.value) {
                    this.form.controls.recovery_ticket_max_delay_time.disable();
                }

                this.form.markAsUntouched();
                this.form.markAsPristine();
            });
    }

    #setLoyaltyPointsTable(): void {
        this.#loadLoyaltyPoints();
        if (this.$showLoyaltyPointsSettings()) {
            this.loyaltyPointsSessions$.pipe(
                takeUntilDestroyed(this.#destroyRef),
                tap(sessions => this.#createLoyaltyPointsControls(sessions))
            ).subscribe();
        }
    }

    #loadLoyaltyPoints(): void {
        // Uncomment when back ready
        // if (this.$showLoyaltyPointsSettings()) {
        //     this.#eventSessionsSrv.loyaltyPoints.load(Number(this.$event().id));
        // }
    }

    #mapSessionsToForm(sessions): (IdName & { start: string })[] {
        return sessions?.data?.map(session => ({
            id: session.id,
            name: session.name,
            start: session.start_date
        }));
    }

    #createLoyaltyPointsControls(sessions: (IdName & { start: string })[]): void {
        sessions?.map(session => {
            const controlName = `${session.id}`;
            const existingControl = this.loyaltyForm.get(controlName);
            // Uncomment when back ready
            // const loyaltyPoints = this.$seasonTicketLoyaltyPoints()?.sessions?.
            //     find(s => s.sessionId === session.session_id)?.transfer || 0;
            const loyaltyPoints = 0;

            if (!existingControl) {
                this.loyaltyForm.setControl(controlName, this.#fb.control(loyaltyPoints, Validators.required));
            } else if (!existingControl.dirty) {
                existingControl.setValue(loyaltyPoints);
            }

            return { ...session, loyalty_points: loyaltyPoints };
        });
    }

    #getTransferDelayConfig(minDelayTime: number | null, maxDelayTime: number | null):
        Record<string, { value: any; enabled: boolean }> {
        const isRange = minDelayTime !== null && maxDelayTime !== null;
        const isFrom = minDelayTime !== null && maxDelayTime === null;
        const isTo = maxDelayTime !== null && minDelayTime === null;

        return {
            enable_transfer_delay: {
                value: isRange || isFrom || isTo,
                enabled: true
            },
            transfer_delay_type: {
                value: isRange ? 'RANGE' : isFrom ? 'FROM' : isTo ? 'TO' : null,
                enabled: isRange || isFrom || isTo
            },
            range: {
                value: isRange ? { min_delay_time: minDelayTime, max_delay_time: maxDelayTime }
                    : { min_delay_time: null, max_delay_time: null },
                enabled: isRange
            },
            from: {
                value: isFrom ? minDelayTime : null,
                enabled: isFrom
            },
            to: {
                value: isTo ? maxDelayTime : null,
                enabled: isTo
            }
        };
    }

    #getSaveRequestBody(): PutEvent {
        const transferBody = this.form.value;

        const minDelayTime = transferBody.transfer_delay?.from ?? transferBody.transfer_delay?.range?.min_delay_time ?? null;
        const maxDelayTime = transferBody.transfer_delay?.to ?? transferBody.transfer_delay?.range?.max_delay_time ?? null;

        return {
            settings: {
                transfer_settings: {
                    enabled: transferBody.enabled,
                    transfer_ticket_min_delay_time: minDelayTime,
                    transfer_ticket_max_delay_time: maxDelayTime,
                    recovery_ticket_max_delay_time: transferBody.enable_recover_delay ? transferBody.recovery_ticket_max_delay_time
                        : null,
                    enable_max_ticket_transfers: transferBody.enable_max_ticket_transfers,
                    enable_multiple_transfers: transferBody.enable_multiple_transfers,
                    transfer_policy: transferBody.enable_friends_family ? 'FRIENDS_AND_FAMILY' : 'ALL',
                    restrict_transfer_by_sessions: transferBody.sessions.select_all ? false : true,
                    max_ticket_transfers: this.form.controls.max_ticket_transfers.value,
                    ...(!transferBody.sessions.select_all &&
                        { allowed_transfer_sessions: transferBody.sessions?.selection?.map(s => s.id) })
                }
            }

        };
    }

    #handleFormChanges(): void {
        this.form.controls.enable_max_ticket_transfers.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => {
                if (!value) {
                    this.form.controls.max_ticket_transfers.disable();
                } else {
                    this.form.controls.max_ticket_transfers.enable();
                }
            });

        this.form.controls.enable_recover_delay.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => {
                if (!value) {
                    this.form.controls.recovery_ticket_max_delay_time.disable();
                } else {
                    this.form.controls.recovery_ticket_max_delay_time.enable();
                }
            });

        this.form.controls.enable_transfer_delay.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(value => {
                if (!value) {
                    this.form.controls.transfer_delay_type.disable();
                    this.form.controls.transfer_delay.disable();
                } else {
                    this.form.controls.transfer_delay_type.enable();
                    this.form.controls.transfer_delay.controls
                    [(this.form.controls.transfer_delay_type.value)?.toString().toLowerCase()]?.enable();
                }
            });

        this.form.controls.transfer_delay_type.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(type => {
                this.form.controls.transfer_delay.disable();
                this.form.controls.transfer_delay.controls[(type)?.toString().toLowerCase()]?.enable();
            });

        this.form.controls.sessions.controls.select_all.valueChanges
            .pipe(takeUntilDestroyed())
            .subscribe(selectAll => {
                if (selectAll) this.form.controls.sessions.controls.selection.disable();
                else this.form.controls.sessions.controls.selection.enable();
            });
    }

    #handleAllSessionsSelection(): void {
        this.#eventSessionsSrv.getAllSessionsData$()
            .pipe(
                filter(sessions => !!sessions),
                map(sessions => sessions?.map(session => ({
                    id: session.id,
                    name: session.name,
                    start: session.start_date
                }))),
                takeUntilDestroyed()
            )
            .subscribe(sessions => {
                const sessionsSelectionCtrl = this.form.controls.sessions.controls.selection;
                if (this.#isSelectAllChecked) {
                    sessionsSelectionCtrl.patchValue(unionWith(sessionsSelectionCtrl.value, sessions));
                } else {
                    sessionsSelectionCtrl.patchValue(differenceWith(sessionsSelectionCtrl.value, sessions));
                }
            });
    }

}