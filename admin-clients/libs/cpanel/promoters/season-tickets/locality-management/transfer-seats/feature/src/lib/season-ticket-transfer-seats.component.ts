import {
    FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg
} from '@OneboxTM/feature-form-control-errors';
import { entitiesProviders } from '@admin-clients/cpanel/organizations/entities/data-access';
import { SeasonTicketsService, SeasonTicketStatus }
    from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    delayType, DelayType, PutSeasonTicketTransferSeats, SeasonTicketTransferForm
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/data-access';
import {
    ExcludedActionType, ExcludedSessionsConfigComponent
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/excluded-sessions-config/feature';
import { GetSeasonTicketSessionsRequest, seasonTicketSessionsProviders, SeasonTicketSessionsService, SeasonTicketSessionStatus }
    from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    ContextNotificationComponent, DialogSize, EphemeralMessageService, MessageDialogService, SearchTableChangeEvent, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, maxDecimalLength, rangeValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList, ViewChildren
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/divider';
import {
    MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle
} from '@angular/material/expansion';
import { MatError, MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressBar } from '@angular/material/progress-bar';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { distinctUntilChanged, filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { MatIcon } from '@angular/material/icon';
import { MatOption } from '@angular/material/core';
import { MatSelect } from '@angular/material/select';

const PAGE_SIZE = 6;

@Component({
    selector: 'app-season-ticket-transfer-seats',
    templateUrl: './season-ticket-transfer-seats.component.html',
    styleUrls: ['./season-ticket-transfer-seats.component.scss'],
    imports: [
        FormContainerComponent, ContextNotificationComponent, TranslatePipe, AsyncPipe, MatAccordion, MatExpansionPanel,
        MatExpansionPanelHeader, MatExpansionPanelTitle, MatCheckbox, ReactiveFormsModule, MatRadioGroup, MatRadioButton,
        MatFormField, MatInput, MatError, FormControlErrorsComponent, MatDivider, SearchTableComponent, MatColumnDef,
        MatHeaderCell, MatCell, LocalDateTimePipe, MatHeaderCellDef, MatCellDef, MatProgressSpinner, MatProgressBar,
        MatSlideToggle, MatIcon, MatOption, MatSelect, ExcludedSessionsConfigComponent
    ],
    providers: [seasonTicketSessionsProviders, entitiesProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SeasonTicketTransferSeatsComponent implements OnInit {
    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly #onDestroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketSessionsSrv = inject(SeasonTicketSessionsService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);

    #prevMaxTicketTransfers: number;
    #filters: GetSeasonTicketSessionsRequest = {
        limit: PAGE_SIZE,
        sort: 'session_starting_date:asc',
        status: SeasonTicketSessionStatus.assigned,
        startDate: new Date(Date.now()).toISOString()
    };

    loyaltyForm = this.#fb.group({});

    readonly pageSize = PAGE_SIZE;
    readonly columns = ['session', 'date', 'event', 'points'];
    readonly dateTimeFormats = DateTimeFormats;
    readonly $seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$());

    readonly $excludedSessions = toSignal(this.#seasonTicketSrv.seasonTicketTransferSeat.get$().pipe(
        filter(Boolean), map(transferSeats => transferSeats.excluded_sessions)));

    readonly seasonTicketListData$ = this.#seasonTicketSessionsSrv.sessions.getData$();
    readonly seasonTicketListMetaData$ = this.#seasonTicketSessionsSrv.sessions.getMetadata$();
    readonly $seasonTicketLoyaltyPoints = toSignal(this.#seasonTicketSrv.seasonTicketLoyaltyPoint.get$());
    readonly isSeasonTicketEditable$ = this.#seasonTicketSrv.seasonTicketStatus.get$()
        .pipe(
            map(seasonTicketStatus => (
                !!seasonTicketStatus.status &&
                (
                    seasonTicketStatus.status === SeasonTicketStatus.setUp ||
                    seasonTicketStatus.status === SeasonTicketStatus.pendingPublication
                ))),
            shareReplay(1)
        );

    readonly $entityCustomerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$());

    readonly form: FormGroup<SeasonTicketTransferForm> = this.#fb.group({
        enable_transfer_delay: [false],
        transfer_delay_type: ['FROM' as DelayType, Validators.required],
        transfer_delay: this.#fb.group({
            from: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            to: [3, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
            range: this.#fb.group({
                min_delay_time: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
                max_delay_time: [3, [Validators.required, Validators.min(1), maxDecimalLength(0)]]
            }, { validators: [rangeValidator('max_delay_time', 'min_delay_time', false)] })
        }),
        enable_recovery_delay: [false],
        recovery_ticket_max_delay_time: [3, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
        enable_max_ticket_transfers: [false],
        enable_friends_family: [false],
        max_ticket_transfers: [0, Validators.required],
        loyalty_points: this.loyaltyForm,
        enable_bulk: [false],
        bulk_customer_types: [null as number[], Validators.required]
    });

    readonly allowTransferForm = this.#fb.group({
        allow_transfer: null as boolean
    });

    readonly isGenerationStatusInProgress$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$()
        .pipe(distinctUntilChanged());

    readonly isGenerationStatusReady$ = this.#seasonTicketSrv.seasonTicketStatus.isGenerationStatusReady$().pipe(distinctUntilChanged());
    readonly isLoadingOrSaving$ = booleanOrMerge([
        this.#seasonTicketSrv.seasonTicket.inProgress$(),
        this.#seasonTicketSrv.seasonTicketStatus.inProgress$(),
        this.#seasonTicketSessionsSrv.sessions.loading$(),
        this.#seasonTicketSrv.seasonTicketTransferSeat.loading$()
    ]);

    readonly $showLoyaltyPointsSettings = toSignal(this.#entitiesSrv.getEntity$().pipe(
        first(Boolean),
        map(entity => entity.settings?.allow_loyalty_points)
    ));

    readonly $showFriendsSettings = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => entity.settings?.allow_friends)
    ));

    ngOnInit(): void {
        this.#seasonTicketSrv.seasonTicketTransferSeat.load(this.$seasonTicket().id);
        this.#entitiesSrv.entityCustomerTypes.load(this.$seasonTicket().entity.id);
        this.allowTransferForm.controls.allow_transfer.reset(this.$seasonTicket()?.settings.operative.allow_transfer);
        this.#initializeForm();

        // Enable max_ticket_transfers value
        this.form.controls.enable_max_ticket_transfers.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.max_ticket_transfers.disable();
                } else {
                    this.form.controls.max_ticket_transfers.enable();
                }
            });

        // Enable recover delay
        this.form.controls.enable_recovery_delay.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.recovery_ticket_max_delay_time.disable();
                } else {
                    this.form.controls.recovery_ticket_max_delay_time.enable();
                }
            });

        // Enable bulk customer types
        this.form.controls.enable_bulk.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.bulk_customer_types.disable();
                } else {
                    this.form.controls.bulk_customer_types.enable();
                }
            });

        this.form.controls.enable_transfer_delay.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
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
            .pipe(takeUntilDestroyed(this.#onDestroyRef))
            .subscribe(type => {
                this.form.controls.transfer_delay.disable();
                this.form.controls.transfer_delay.controls[(type)?.toString().toLowerCase()]?.enable();
            });

        this.#setLoyaltyPointsTable();
    }

    save(): void {
        this.save$().subscribe(() => this.refresh());
    }

    save$(): Observable<unknown> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];

            const transferBody = this.form.value;
            const minDelayTime = transferBody.transfer_delay?.from ?? transferBody.transfer_delay?.range?.min_delay_time ?? null;
            const maxDelayTime = transferBody.transfer_delay?.to ?? transferBody.transfer_delay?.range?.max_delay_time ?? null;

            const updateBulk = this.#getBulkRequestBody();

            const updateTransferSeat = {
                enable_transfer_delay: transferBody.enable_transfer_delay,
                transfer_ticket_min_delay_time: minDelayTime,
                transfer_ticket_max_delay_time: maxDelayTime,
                enable_recovery_delay: transferBody.enable_recovery_delay,
                recovery_ticket_max_delay_time: transferBody.enable_recovery_delay ? transferBody.recovery_ticket_max_delay_time : null,
                enable_max_ticket_transfers: transferBody.enable_max_ticket_transfers,
                transfer_policy: transferBody.enable_friends_family ? 'FRIENDS_AND_FAMILY' : 'ALL',
                max_ticket_transfers: this.form.controls.max_ticket_transfers.value,
                ...updateBulk
            } as PutSeasonTicketTransferSeats;

            let canContinue$: Observable<boolean>;

            if (this.#prevMaxTicketTransfers !== updateTransferSeat.max_ticket_transfers) {
                canContinue$ = this.#msgDialogSrv.showWarn({
                    title: 'SEASON_TICKET.TRANSFER_SEAT.MAX_LIMIT_TRANSFERS_SAVE_WARNING_TITLE',
                    message: 'SEASON_TICKET.TRANSFER_SEAT.MAX_LIMIT_TRANSFERS_SAVE_WARNING_INFO',
                    actionLabel: 'FORMS.ACTIONS.UPDATE',
                    showCancelButton: true,
                    size: DialogSize.MEDIUM
                });
            } else {
                canContinue$ = of(true);
            }

            if (this.$showLoyaltyPointsSettings() && this.loyaltyForm.dirty) {
                const loyaltyPointsData = Object.keys(this.loyaltyForm.controls).map(sessionId => {
                    const loyaltyPoints = this.loyaltyForm.get(sessionId).value;
                    const currentSession = this.$seasonTicketLoyaltyPoints()?.sessions?.find(s => s.sessionId === Number(sessionId));
                    const attendance = currentSession?.attendance || 0;

                    return {
                        sessionId: Number(sessionId),
                        transfer: loyaltyPoints,
                        attendance
                    };
                });
                obs$.push(this.#seasonTicketSrv.seasonTicketLoyaltyPoint.update(Number(this.$seasonTicket()?.id),
                    { sessions: loyaltyPointsData }));
            }

            return canContinue$.pipe(
                switchMap(canContinue => {
                    if (!canContinue) return of(null);

                    obs$.push(this.#seasonTicketSrv.seasonTicketTransferSeat.update(this.$seasonTicket()?.id, updateTransferSeat));

                    return forkJoin(obs$).pipe(
                        tap(() => this.#ephemeralMessageService.showSuccess({
                            msgKey: 'SEASON_TICKET.UPDATE_SUCCESS',
                            msgParams: { seasonTicketName: this.$seasonTicket()?.name }
                        }))
                    );
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    saveExcludedSessions(event: {type: ExcludedActionType, excludedSessions: number[]}): void {
        const successMessage = 'SEASON_TICKET.EXCLUDED_SESSIONS.TRANSFER_SEAT_' + event.type + '_SAVED_SUCCESS';
        this.#seasonTicketSrv.seasonTicketTransferSeat.update(this.$seasonTicket().id, { excluded_sessions: event.excludedSessions })
            .subscribe(() => {
                this.#ephemeralMessageSrv.showSuccess({ msgKey: successMessage });
                this.refresh();
            });
    }

    refresh(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this.#seasonTicketSrv.seasonTicketTransferSeat.load(this.$seasonTicket()?.id);
        this.#seasonTicketSessionsSrv.sessions.load(this.$seasonTicket()?.id.toString(), this.#filters);
        this.#loadLoyaltyPoints();
    }

    loadSeasonTicketList(event: SearchTableChangeEvent = null): void {
        this.#filters = { ...this.#filters, offset: event?.offset, q: event?.q };
        this.#seasonTicketSessionsSrv.sessions.load(this.$seasonTicket()?.id.toString(), this.#filters);
    }

    handleStatusChange(isActive: boolean): void {
        this.#seasonTicketSrv.seasonTicket.save(
            this.$seasonTicket().id.toString(), { settings: { operative: { allow_transfer: isActive } } }
        ).subscribe(() => {
            const successMessage = isActive ? 'SEASON_TICKET.TRANSFER_SEAT.ENABLED_SUCCESS' : 'SEASON_TICKET.TRANSFER_SEAT.DISABLED_SUCCESS';
            this.#ephemeralMessageSrv.showSuccess({ msgKey: successMessage });
            this.#seasonTicketSrv.seasonTicket.load(this.$seasonTicket().id.toString());
        });
    }

    #initializeForm(): void {
        this.#seasonTicketSrv.seasonTicketTransferSeat.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroyRef)
        ).subscribe(transferSeat => {
            this.#prevMaxTicketTransfers = transferSeat?.max_ticket_transfers;
            const minDelayTime = transferSeat?.transfer_ticket_min_delay_time || null;
            const maxDelayTime = transferSeat?.transfer_ticket_max_delay_time || null;
            const config = this.#getTransferDelayConfig(minDelayTime, maxDelayTime);

            for (const [controlName, obj] of Object.entries(config)) {
                let control = this.form.controls[controlName];
                if (delayType.find(type => type.toLowerCase() === controlName)) {
                    control = this.form.controls.transfer_delay.controls[controlName];
                }
                control.patchValue(obj.value);
                obj.enabled ? control.enable() : control.disable();
            }

            this.form.patchValue({
                enable_transfer_delay: transferSeat?.enable_transfer_delay,
                enable_recovery_delay: transferSeat?.enable_recovery_delay,
                recovery_ticket_max_delay_time: transferSeat?.recovery_ticket_max_delay_time || null,
                enable_max_ticket_transfers: transferSeat?.enable_max_ticket_transfers || false,
                enable_friends_family: transferSeat?.transfer_policy === 'FRIENDS_AND_FAMILY',
                max_ticket_transfers: transferSeat?.max_ticket_transfers || 0,
                enable_bulk: transferSeat?.enable_bulk || false,
                bulk_customer_types: transferSeat?.bulk_customer_types?.map(customerType => customerType.id) || []
            });
            if (!transferSeat?.enable_max_ticket_transfers) {
                this.form.controls.max_ticket_transfers.disable();
            }

            if (!this.form.controls.enable_recovery_delay.value) {
                this.form.controls.recovery_ticket_max_delay_time.disable();
            }

            if (!this.form.controls.enable_bulk.value) {
                this.form.controls.bulk_customer_types.disable();
            }

            this.form.markAsUntouched();
            this.form.markAsPristine();
        });
    }

    #setLoyaltyPointsTable(): void {
        this.#loadLoyaltyPoints();

        if (this.$showLoyaltyPointsSettings()) {
            this.seasonTicketListData$.pipe(
                takeUntilDestroyed(this.#onDestroyRef),
                map(seasonTicketList =>
                    seasonTicketList?.map(session => {
                        const controlName = `${session.session_id}`;
                        const existingControl = this.loyaltyForm.get(controlName);
                        const loyaltyPoints = this.$seasonTicketLoyaltyPoints()?.sessions?.
                            find(s => s.sessionId === session.session_id)?.transfer || 0;

                        if (!existingControl) {
                            this.loyaltyForm.setControl(controlName, this.#fb.control(loyaltyPoints, Validators.required));
                        } else if (!existingControl.dirty) {
                            existingControl.setValue(loyaltyPoints);
                        }

                        return { ...session, loyalty_points: loyaltyPoints };
                    })
                )
            ).subscribe();
        }
    }

    #getBulkRequestBody(): PutSeasonTicketTransferSeats {
        if (this.form.controls.enable_bulk.dirty || this.form.controls.bulk_customer_types.dirty) {
            const bulkCustomerTypes = this.form.controls.bulk_customer_types.value;
            return {
                enable_bulk: this.form.controls.enable_bulk.value,
                ...!!bulkCustomerTypes.length && {
                    bulk_customer_types: bulkCustomerTypes
                }
            };
        }
        return null;
    }

    #loadLoyaltyPoints(): void {
        if (this.$showLoyaltyPointsSettings()) {
            this.#seasonTicketSrv.seasonTicketLoyaltyPoint.load(this.$seasonTicket()?.id);
        }
    }

    #getTransferDelayConfig(minDelayTime: number | null, maxDelayTime: number | null):
        Record<string, { value: any; enabled: boolean }> {
        const isRange = minDelayTime !== null && maxDelayTime !== null;
        const isFrom = minDelayTime !== null && maxDelayTime === null;
        const isTo = maxDelayTime !== null && minDelayTime === null;

        return {
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
}
