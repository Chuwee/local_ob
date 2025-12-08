import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { EventFieldsRestriction, EventSessionPackConf, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { SessionPackBlockingActions } from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import {
    EventSessionsService, EventSessionsState, GetSessionsRequest, PostSession, Session, SessionDateCustomModifier,
    SessionDatesFormValidation, SessionLoyaltyPointsGainType, SessionRate, SessionStatus, SessionType
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionUtils } from '@admin-clients/cpanel-promoters-events-sessions-feature';
import { EntitiesBaseService, Tax } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { atLeastOneRequiredInArray, booleanOrMerge, differenceWith, unionWith } from '@admin-clients/shared/utility/utils';
import { VenueTemplate, VenueTemplateBlockingReason, VenueTemplatesService, VenueTemplatesState, VenueTemplateStatus }
    from '@admin-clients/shared/venues/data-access/venue-tpls';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, EventEmitter, inject, OnDestroy, OnInit, ViewChild
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, UntypedFormControl, UntypedFormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import moment from 'moment-timezone';
import { Observable, Subject } from 'rxjs';
import { filter, first, map, scan, shareReplay, startWith, switchMap, takeUntil, tap } from 'rxjs/operators';

const PAGE_SIZE = 5;

@Component({
    selector: 'app-create-session-pack-dialog',
    templateUrl: './create-session-pack-dialog.component.html',
    styleUrls: ['./create-session-pack-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        EventSessionsService, EventSessionsState,
        VenueTemplatesService, VenueTemplatesState,
        SessionDatesFormValidation
    ],
    standalone: false
})
export class CreateSessionPackDialogComponent implements OnInit, OnDestroy, AfterViewInit {
    @ViewChild('nameField') private _input: MatInput;
    readonly #onDestroy = new Subject<void>();
    readonly #eventsService = inject(EventsService);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #dialogRef = inject(MatDialogRef<CreateSessionPackDialogComponent>);
    readonly #sessionDatesFormValidation = inject(SessionDatesFormValidation);
    readonly #data = inject<{ eventId: number; lastSessionId: number }>(MAT_DIALOG_DATA);

    readonly #eventId = this.#data.eventId;
    readonly #lastSessionId = this.#data.lastSessionId;
    private get _selectedSessionsCtrl(): UntypedFormControl {
        return this.form.get('sessions') as UntypedFormControl;
    }

    private get _sessionRatesValue(): Map<number, SessionRate> {
        return this.form.get('sessionRates')?.value;
    }

    readonly pageSize = PAGE_SIZE;
    readonly sessionDateCustomModifiers = Object.values(SessionDateCustomModifier);
    readonly dateTimeFormats = DateTimeFormats;
    readonly sessionPackBlockingActions = SessionPackBlockingActions;
    readonly showSelectedOnlyClick = new EventEmitter<void>();
    readonly sessionLoyaltyPointsGainType = SessionLoyaltyPointsGainType;

    readonly $showLoyaltyPointsSettings = toSignal(
        this.#entitiesService.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    #selVenueTplId: number;
    #filters: GetSessionsRequest = {
        limit: PAGE_SIZE,
        sort: 'start_date:asc',
        type: SessionType.session,
        status: [SessionStatus.scheduled, SessionStatus.ready]
    };

    form: UntypedFormGroup;
    loyaltyPointGainConfig = this.#fb.group({
        amount: [0, Validators.min(0)],
        type: SessionLoyaltyPointsGainType.sessionPurchased
    });

    eventRates$: Observable<SessionRate[]>;
    taxes$: Observable<Tax[]>;
    venueTemplates$: Observable<VenueTemplate[]>;

    sessions$: Observable<Session[]>;
    sessionsMetadata$: Observable<Metadata>;
    selectedSessionsOnly$: Observable<boolean>;
    sessionsListLoading$: Observable<boolean>;
    totalSessions$: Observable<number>;
    isAllSessionsNotScheduled$: Observable<boolean>;

    blockingReasons$: Observable<VenueTemplateBlockingReason[]>;
    sessionPackType: EventSessionPackConf;
    isBookingsEnabled: boolean;
    isPartialRefundEnabled: boolean;
    requestInProgress$: Observable<boolean>;

    get selectedSessionsLength(): number {
        return this._selectedSessionsCtrl?.value?.length || 0;
    }

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
        this.#dialogRef.disableClose = false;
    }

    ngOnInit(): void {
        this.createForm();
        this.loadData();
        this.model();
        this.initFormChangesHandlers();
    }

    ngAfterViewInit(): void {
        // focus first input improves UX
        setTimeout(() => this._input.focus(), 500);
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    isNotScheduledSession: (s: Session) => boolean = (s: Session) => s.status !== SessionStatus.scheduled;

    // rate management
    isDefaultRate(rateId: number): boolean {
        return this._sessionRatesValue.get(rateId) && this._sessionRatesValue.get(rateId).default;
    }

    setDefaultRate(rateId: number): void {
        if (!this._sessionRatesValue.get(rateId)?.default) {
            this._sessionRatesValue.set(rateId, { id: rateId, default: true } as SessionRate);
            this._sessionRatesValue.forEach(r => {
                if (r.id !== rateId) {
                    r.default = false;
                }
            });
        }
        this.touchAndValidateRates();
    }

    isVisibleRate(rateId: number): boolean {
        return !!this._sessionRatesValue.get(rateId) || false;
    }

    setVisibleRate(rateId: number): void {
        if (this._sessionRatesValue.get(rateId)) {
            if (!this._sessionRatesValue.get(rateId).default) {
                this._sessionRatesValue.delete(rateId);
            }
        } else {
            this._sessionRatesValue.set(rateId,
                {
                    id: rateId,
                    default: Array.from(this._sessionRatesValue.values()).find(sessionRate => sessionRate.default) === undefined
                } as SessionRate);
        }
        this.touchAndValidateRates();
    }

    // action buttons actions
    createSessionPack(): void {
        const session = this.getPostSession();
        if (session) {
            this.#sessionsService.createSession(this.#eventId, session)
                .subscribe(id => {
                    this.#ephemeralMessageService.showSuccess({
                        msgKey: 'EVENTS.ADD_SESSION_PACK_SUCCESS',
                        msgParams: { sessionName: session.name }
                    });
                    this.close(id);
                });
        }
    }

    close(sessionId: number = null): void {
        this.#dialogRef.close(sessionId);
    }

    loadSessionsList(filters: Partial<GetSessionsRequest>): void {
        if (this.#selVenueTplId != null) {
            this.#sessionsService.sessionList.load(this.#eventId, {
                ...this.#filters,
                ...filters,
                venueTplId: this.#selVenueTplId
            });
        }
    }

    /**
     * selects all filtered sessions
     */
    selectAllSessions(change?: MatCheckboxChange): void {
        this.#sessionsService.loadAllSessions(this.#eventId, {
            ...this.#filters,
            limit: undefined,
            venueTplId: this.#selVenueTplId
        });
        this.#sessionsService.getAllSessionsData$()
            .pipe(
                first(sessions => !!sessions),
                map(sessions => sessions.filter(s => !this.isNotScheduledSession(s)))
            )
            .subscribe(sessions => {
                if (change?.checked) {
                    this._selectedSessionsCtrl.patchValue(unionWith(this._selectedSessionsCtrl.value, sessions));
                } else {
                    this._selectedSessionsCtrl.patchValue(differenceWith(this._selectedSessionsCtrl.value, sessions));
                }
                this.form.markAsTouched();
                this.form.markAsDirty();
            });
    }

    private loadData(): void {
        this.#eventsService.event.load(this.#eventId.toString());
        this.#venueTemplatesService.loadVenueTemplatesList({
            limit: 999, offset: 0, sort: 'name:asc',
            eventId: this.#eventId,
            status: [VenueTemplateStatus.active]
        });
        this.#eventsService.eventRates.load(this.#eventId.toString());
        this.#eventsService.event.get$()
            .pipe(first(Boolean))
            .subscribe(event => {
                this.sessionPackType = event.settings.session_pack;
                this.isPartialRefundEnabled =
                    event.settings?.session_pack === EventSessionPackConf.unrestricted &&
                    !event.settings?.use_tiered_pricing;
                this.isPartialRefundEnabled && this.form.get('allow_partial_refund').enable();
                this.#entitiesService.loadEntityTaxes(event.entity.id);
            });
    }

    private model(): void {
        this.venueTemplates$ = this.#venueTemplatesService.getVenueTemplatesListData$()
            .pipe(
                first(value => !!value),
                tap(vtl => vtl?.length && this.form.get('venueTemplate').setValue(vtl[0])),
                shareReplay(1)
            );
        this.eventRates$ = this.#eventsService.eventRates.get$()
            .pipe(
                first(value => value !== null),
                map(rates => rates.map(rate => ({ id: rate.id, name: rate.name } as SessionRate))),
                shareReplay(1)
            );
        this.taxes$ = this.#entitiesService.getEntityTaxes$()
            .pipe(first(taxes => taxes !== null), shareReplay(1));

        this.selectedSessionsOnly$ = this.showSelectedOnlyClick.pipe(
            scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
            startWith(false),
            takeUntil(this.#onDestroy),
            shareReplay(1)
        );
        const selectedSessions$ = this._selectedSessionsCtrl.valueChanges
            .pipe(
                takeUntil(this.#onDestroy),
                shareReplay(1)
            );
        selectedSessions$.subscribe();
        const allSessions$ = this.#sessionsService.sessionList.get$().pipe(
            filter(Boolean),
            map(sl => sl.data)
        );
        this.sessions$ = this.selectedSessionsOnly$.pipe(
            switchMap(isActive => isActive ? selectedSessions$ : allSessions$),
            shareReplay(1)
        );
        this.sessionsMetadata$ = this.selectedSessionsOnly$.pipe(
            switchMap(isActive => isActive ?
                selectedSessions$.pipe(map(list => new Metadata({ total: list?.length, limit: 99999, offset: 0 }))) :
                this.#sessionsService.sessionList.get$().pipe(map(sl => sl?.metadata))
            ),
            takeUntil(this.#onDestroy),
            shareReplay(1)
        );
        this.sessionsListLoading$ = booleanOrMerge([
            this.#sessionsService.sessionList.inProgress$(),
            this.#sessionsService.isAllSessionsLoading$()
        ]);
        this.totalSessions$ = this.#sessionsService.sessionList.get$()
            .pipe(map(sl => sl?.metadata?.total || 0));

        this.isAllSessionsNotScheduled$ = this.#sessionsService.sessionList.get$()
            .pipe(
                filter(Boolean),
                map(sessionsList => sessionsList.data.every(session => session.status !== SessionStatus.scheduled))
            );

        this.blockingReasons$ = this.#venueTemplatesService.getVenueTemplateBlockingReasons$();
        this.requestInProgress$ = this.#sessionsService.isSessionSaving$();
    }

    private createForm(): void {
        // FormGroup creation
        this.form = this.#fb.group({
            allow_partial_refund: { value: undefined, disabled: true },
            name: [null, [
                Validators.required,
                Validators.maxLength(EventFieldsRestriction.eventNameLength)
            ]],
            color: [null, Validators.required],
            sessionRates: [new Map<number, SessionRate>(), CreateSessionPackDialogComponent.requiredCollection],
            venueTemplate: [null, Validators.required],
            sessions: [[], [Validators.required, atLeastOneRequiredInArray()]],
            blockingActions: [[], []],
            ticketTax: [null, Validators.required],
            surchargeTax: [null, Validators.required],
            status: SessionStatus.ready,
            releaseEnable: true,
            startDate: null, // TODO: remove when api doesn't require start date
            releaseDate: null,
            bookingEnable: true,
            bookingsStartDate: null,
            bookingsEndDate: null,
            bookingEndDateModifier: SessionDateCustomModifier.start,
            saleEnable: true,
            salesStartDate: null,
            salesEndDate: null,
            saleEndDateModifier: SessionDateCustomModifier.start
        });
        this.setDatesValidators();
    }

    private initFormChangesHandlers(): void {
        this.form.get('venueTemplate').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(selectedVenueTemplate => {
                this.#selVenueTplId = selectedVenueTemplate.id;
                this.loadSessionsList({ offset: 0 });
                this.#venueTemplatesService.loadVenueTemplateBlockingReasons(selectedVenueTemplate.id);
            });
        this._selectedSessionsCtrl.valueChanges
            .pipe(
                takeUntil(this.#onDestroy)
            )
            .subscribe((selectedSessions: Session[]) => {
                let resultStartDate: moment.Moment = null;
                selectedSessions.forEach(session => {
                    const sessionStartDate = moment(session.start_date);
                    if (!resultStartDate || resultStartDate.isAfter(sessionStartDate)) {
                        resultStartDate = sessionStartDate;
                    }
                });
                this.form.get('startDate').setValue(resultStartDate.format());
            });
        this.initDateModifiers();
    }

    private static requiredCollection(control: AbstractControl): ValidationErrors | null {
        if (!control.value?.size) {
            return { required: true };
        } else {
            return null;
        }
    }

    private setDatesValidators(): void {
        this.#eventsService.event.get$()
            .pipe(first(Boolean))
            .subscribe(event => {
                this.isBookingsEnabled = event.settings?.bookings?.enable;
                this.form.get('bookingEnable').setValue(this.isBookingsEnabled);
                this.#sessionDatesFormValidation.addSessionDateValidations(
                    {
                        status: this.form.get('status'),
                        releaseEnable: this.form.get('releaseEnable'),
                        releaseDate: this.form.get('releaseDate'),
                        saleEnable: this.form.get('saleEnable'),
                        saleStartDate: this.form.get('salesStartDate'),
                        saleEndDate: this.form.get('salesEndDate'),
                        bookingEnable: this.form.get('bookingEnable'),
                        bookingStartDate: this.form.get('bookingsStartDate'),
                        bookingEndDate: this.form.get('bookingsEndDate')
                    },
                    event,
                    this.#onDestroy
                );
                this.initDates();
            });
    }

    private initDates(): void {
        if (this.#lastSessionId) {
            this.#sessionsService.session.clear();
            this.#sessionsService.session.load(this.#eventId, this.#lastSessionId);
            this.#sessionsService.session.get$().pipe(first(Boolean)).subscribe(session => {
                const datesUpdateValues: { [key: string]: unknown } = {
                    releaseDate: moment(session.settings.release.date).format(),
                    salesStartDate: moment(session.settings.sale.start_date).format(),
                    salesEndDate: moment(session.start_date).add(1, 'd').format()
                };
                if (this.isBookingsEnabled) {
                    datesUpdateValues['bookingsStartDate'] = moment(session.settings.booking.start_date).format();
                    datesUpdateValues['bookingsEndDate'] = moment(session.start_date).add(1, 'd').format();
                }
                this.form.patchValue(datesUpdateValues, { emitEvent: false });
            });
        } else {
            this.setDefaultDates();
        }
    }

    private setDefaultDates(): void {
        const defaultDate = moment().set('h', 10).set('m', 0).set('s', 0).set('ms', 0);
        this.form.get('releaseDate').setValue(defaultDate.format());
        this.form.get('bookingsStartDate').setValue(defaultDate.format());
        this.form.get('bookingsEndDate').setValue(defaultDate.format());
        this.form.get('salesStartDate').setValue(defaultDate.format());
        this.form.get('salesEndDate').setValue(defaultDate.format());
    }

    private initDateModifiers(): void {
        this._selectedSessionsCtrl.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe((sessions: Session[]) => {
                if (sessions?.length) {
                    const saleEndModifier = this.form.get('saleEndDateModifier').value as SessionDateCustomModifier;
                    if (saleEndModifier) {
                        this.modifyDate(
                            this.getLastSessionStartDate(sessions),
                            this.form.get('salesEndDate'),
                            saleEndModifier
                        );
                    }
                    if (this.isBookingsEnabled) {
                        const bookingEndModifier = this.form.get('bookingEndDateModifier').value;
                        if (bookingEndModifier) {
                            this.modifyDate(
                                this.getLastSessionStartDate(sessions),
                                this.form.get('bookingsEndDate'),
                                bookingEndModifier
                            );
                        }
                    }
                }
            });
        this.form.get('saleEndDateModifier').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe((modifier: SessionDateCustomModifier) => {
                if (this._selectedSessionsCtrl.value
                    && Array.from(this._selectedSessionsCtrl.value)?.length) {
                    this.modifyDate(
                        this.getLastSessionStartDate(Array.from(this._selectedSessionsCtrl.value)),
                        this.form.get('salesEndDate'),
                        modifier);
                }
            });
        this.form.get('bookingEndDateModifier').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe((modifier: SessionDateCustomModifier) => {
                if (this.isBookingsEnabled
                    && this._selectedSessionsCtrl.value
                    && Array.from(this._selectedSessionsCtrl.value)?.length) {
                    this.modifyDate(
                        this.getLastSessionStartDate(Array.from(this._selectedSessionsCtrl.value)),
                        this.form.get('bookingsEndDate'),
                        modifier);
                }
            });
    }

    private modifyDate(baseDate: moment.Moment, date: AbstractControl, dateModifier: SessionDateCustomModifier): void {
        if (baseDate) {
            switch (dateModifier) {
                case SessionDateCustomModifier.startMinusHalfHour:
                    date.setValue(baseDate.subtract(30, 'm').format(), { emitEvent: false });
                    break;
                case SessionDateCustomModifier.start:
                    date.setValue(baseDate.format(), { emitEvent: false });
                    break;
                case SessionDateCustomModifier.startPlusHalfHour:
                    date.setValue(baseDate.add(30, 'm').format(), { emitEvent: false });
                    break;
                case SessionDateCustomModifier.startMinusOneDay:
                    date.setValue(baseDate.subtract(1, 'd').format(), { emitEvent: false });
                    break;
            }
        }
    }

    private getLastSessionStartDate(sessions: Session[]): moment.Moment {
        let sessionDate: moment.Moment = null;
        sessions.forEach(session => {
            const currentSessionDate = moment(session.start_date);
            if (!sessionDate || sessionDate.isBefore(currentSessionDate)) {
                sessionDate = currentSessionDate;
            }
        });
        return sessionDate;
    }

    private getPostSession(): PostSession {
        let session: PostSession = null;
        if (this.form.valid) {
            session = {
                name: this.form.value.name,
                venue_template_id: this.form.value.venueTemplate.id,
                tax_ticket_id: this.form.value.ticketTax.id,
                tax_charges_id: this.form.value.surchargeTax.id
            };
            const venueTZ = this.form.value.venueTemplate.venue.timezone;
            session.dates = {
                start: SessionUtils.formatDate(this.form.value.startDate, venueTZ),
                channels: SessionUtils.formatDate(this.form.value.releaseDate, venueTZ),
                sales_start: SessionUtils.formatDate(this.form.value.salesStartDate, venueTZ),
                sales_end: SessionUtils.formatDate(this.form.value.salesEndDate, venueTZ),
                bookings_start: SessionUtils.formatDate(this.form.value.bookingsStartDate, venueTZ),
                bookings_end: SessionUtils.formatDate(this.form.value.bookingsEndDate, venueTZ)
            };
            session.pack_config = {
                session_ids: this.form.value.sessions
                    .filter((s: Session) => !this.isNotScheduledSession(s))
                    .map((s: Session) => s.id),
                color: (this.form.value.color as string).substring(1),
                blocking_actions: this.form.value.blockingActions,
                allow_partial_refund: this.form.value.allow_partial_refund || undefined
            };
            session.rates = Array.from(this.form.value.sessionRates.values());
            if (this.loyaltyPointGainConfig.valid && this.$showLoyaltyPointsSettings()) {
                session.loyalty_points_config = {
                    point_gain: {
                        amount: this.loyaltyPointGainConfig.value.amount,
                        type: this.loyaltyPointGainConfig.value.type
                    }
                };
            }
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
        return session;
    }

    private touchAndValidateRates(): void {
        this.form.get('sessionRates').markAsTouched();
        this.form.get('sessionRates').updateValueAndValidity();
    }
}
