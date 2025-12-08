import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, PostSession, SessionDateCustomModifier, SessionDatesFormValidation,
    SessionLoyaltyPointsGainType, SessionsListCountersService, SessionStatus
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { VenueAccessControlSystems } from '@admin-clients/shared/data-access/models';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    AfterViewInit, ChangeDetectionStrategy, Component, ElementRef, EventEmitter, inject, Input, OnDestroy, OnInit, Output
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { AbstractControl, FormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import moment from 'moment-timezone';
import { combineLatest, Subject } from 'rxjs';
import { distinctUntilChanged, filter, first, map, startWith, takeUntil } from 'rxjs/operators';
import { SessionCreationType } from '../models/session-creation-type.enum';
import { NewSessionDialogComponent } from '../new-session-dialog.component';
import { SessionUtils } from '../utils/session.utils';

@Component({
    selector: 'app-create-single-session',
    templateUrl: './create-single-session.component.html',
    styleUrls: ['./create-single-session.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SessionDatesFormValidation],
    standalone: false
})
export class CreateSingleSessionComponent implements OnInit, AfterViewInit, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #refreshChanges = new Subject<void>();
    readonly #createdSessions: number[] = [];
    readonly #eventsService = inject(EventsService);
    readonly #sessionsService = inject(EventSessionsService);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #dialogRef = inject(MatDialogRef<NewSessionDialogComponent>);
    readonly #countersSrv = inject(SessionsListCountersService);
    readonly #sessionDatesFormValidation = inject(SessionDatesFormValidation);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    @Input() eventId: number;
    @Input() lastSessionId: number;
    @Output() closeDialog = new EventEmitter<number[]>();

    readonly form = this.#fb.group({
        base: new UntypedFormGroup({}),
        status: SessionStatus.ready,
        releaseEnable: true,
        releaseDate: null as string,
        bookingEnable: true,
        bookingsStartDate: null as string,
        bookingsEndDate: null as string,
        bookingEndDateModifier: SessionDateCustomModifier.start,
        saleEnable: true,
        salesStartDate: null as string,
        salesEndDate: null as string,
        saleEndDateModifier: SessionDateCustomModifier.start,
        secondaryMarket: this.#fb.group({
            salesEnable: true as boolean,
            salesStartDate: null as string,
            salesEndDate: null as string,
            saleEndDateModifier: SessionDateCustomModifier.start
        }),
        loyaltyPointGainConfig: this.#fb.group({
            amount: [0, Validators.min(0)],
            type: SessionLoyaltyPointsGainType.sessionPurchased
        })
    });

    readonly enableSecondaryMarketCtrl = this.#fb.control(false);

    readonly isReqInProgress$ = this.#sessionsService.isSessionSaving$();
    readonly refreshChanges$ = this.#refreshChanges.asObservable();
    readonly sessionCreationType = SessionCreationType;
    readonly sessionDateCustomModifiers = SessionDateCustomModifier;
    readonly sessionLoyaltyPointsGainType = SessionLoyaltyPointsGainType;
    readonly $showLoyaltyPointsSettings = toSignal(
        this.#entitiesSrv.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    bookingsEnabled: boolean;
    isActivity = false;
    isSGA = false;

    $showSecondaryMarketSettings = toSignal(
        this.#entitiesSrv.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_secondary_market))
    );

    $showSecondaryMarketDates = toSignal(
        this.enableSecondaryMarketCtrl.valueChanges.pipe(startWith(false))
    );

    ngOnInit(): void {
        this.enableSecondaryMarketCtrl.valueChanges.pipe(startWith(false), takeUntil(this.#onDestroy)).subscribe(enabled => {
            if (enabled) {
                const { salesStartDate, salesEndDate, saleEndDateModifier } = this.form.value;
                this.form.get('secondaryMarket').enable({ emitEvent: false });
                this.form.get('secondaryMarket').patchValue({
                    salesStartDate, salesEndDate, saleEndDateModifier, salesEnable: true
                }, { emitEvent: false });
            } else {
                this.form.get('secondaryMarket').disable({ emitEvent: false });
            }
        });
    }

    ngAfterViewInit(): void {
        combineLatest([
            this.#entitiesSrv.getEntity$().pipe(first(entity => entity !== null)),
            this.#eventsService.event.get$()
                .pipe(first(event => event !== null))
        ]).subscribe(([entity, event]) => {
            this.isSGA = event.additional_config.inventory_provider === ExternalInventoryProviders.sga;
            this.isActivity = event.type === EventType.activity || event.type === EventType.themePark;
            this.bookingsEnabled = event.settings.bookings?.enable;
            this.form.get('bookingEnable').setValue(event.settings.bookings?.enable);
            this.initFormDataHandlers();
            const hasFortressVenue = event.venue_templates?.some(tpl =>
                tpl.venue.access_control_systems?.some(system => system?.name === VenueAccessControlSystems.fortressBRISTOL)) || false;

            this.#sessionDatesFormValidation.addSessionDateValidations(
                {
                    status: this.form.get('status'),
                    startDate: this.form.get('base.startDate'),
                    endDate: this.form.get('base.endDate'),
                    releaseEnable: this.form.get('releaseEnable'),
                    releaseDate: this.form.get('releaseDate'),
                    saleEnable: this.form.get('saleEnable'),
                    saleStartDate: this.form.get('salesStartDate'),
                    saleEndDate: this.form.get('salesEndDate'),
                    bookingEnable: this.form.get('bookingEnable'),
                    bookingStartDate: this.form.get('bookingsStartDate'),
                    bookingEndDate: this.form.get('bookingsEndDate'),
                    secondaryMarketSaleStartDate: this.form.get('secondaryMarket.salesStartDate'),
                    secondaryMarketSaleEndDate: this.form.get('secondaryMarket.salesEndDate')
                },
                event,
                this.#onDestroy,
                entity,
                hasFortressVenue
            );
            this.initDates();
        });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    createSession(createNew = false): void {
        if (this.form.valid) {
            const session = {} as PostSession;
            const formValues = this.form.value;
            const baseDataFormValues = this.form.value.base;
            session.name = baseDataFormValues.name;
            session.venue_template_id = baseDataFormValues.venueTemplate.id;
            session.tax_ticket_id = baseDataFormValues.ticketTax.id;
            session.tax_charges_id = baseDataFormValues.surchargeTax.id;
            const venueTZ = baseDataFormValues.venueTemplate.venue.timezone;
            session.enable_smart_booking = baseDataFormValues.enableSmartBooking;
            let secondaryMarketSaleStart: string = null;
            let secondaryMarketSaleEnd: string = null;
            if (this.$showSecondaryMarketSettings() && this.enableSecondaryMarketCtrl.value) {
                secondaryMarketSaleStart = SessionUtils.formatDate(formValues.secondaryMarket.salesStartDate, venueTZ);
                secondaryMarketSaleEnd = SessionUtils.formatDate(formValues.secondaryMarket.salesEndDate, venueTZ);
            }
            if (this.$showLoyaltyPointsSettings()) {
                session.loyalty_points_config = {
                    point_gain: {
                        amount: formValues.loyaltyPointGainConfig.amount,
                        type: formValues.loyaltyPointGainConfig.type
                    }
                };
            }

            if (this.isSGA) {
                session.additional_config = { external_session_id: baseDataFormValues.externalProviderSession.id };
            }

            if (baseDataFormValues.reference) {
                session.reference = baseDataFormValues.reference;
            }

            session.dates = {
                start: SessionUtils.formatDate(baseDataFormValues.startDate, venueTZ),
                end: baseDataFormValues.endDate ? SessionUtils.formatDate(baseDataFormValues.endDate, venueTZ) : null,
                channels: SessionUtils.formatDate(formValues.releaseDate, venueTZ),
                sales_start: SessionUtils.formatDate(formValues.salesStartDate, venueTZ),
                sales_end: SessionUtils.formatDate(formValues.salesEndDate, venueTZ),
                bookings_start: SessionUtils.formatDate(formValues.bookingsStartDate, venueTZ),
                bookings_end: SessionUtils.formatDate(formValues.bookingsEndDate, venueTZ),
                secondary_market_sale_start: secondaryMarketSaleStart,
                secondary_market_sale_end: secondaryMarketSaleEnd
            };

            if (baseDataFormValues.enableOrphanSeats) {
                session.settings = { enable_orphan_seats: !!baseDataFormValues.enableOrphanSeats };
            }
            if (baseDataFormValues.avetMatch) {
                session.additional_config = { avet_match_id: baseDataFormValues.avetMatch.id };
            } else {
                session.rates = baseDataFormValues.sessionRates;
            }
            if (this.isActivity) {
                const baseForm = this.form.get('base') as UntypedFormGroup;
                if (baseForm.contains('activitySaleType')) {
                    session.activity_sale_type = baseDataFormValues.activitySaleType;
                } else {
                    session.activity_sale_type = ActivitySaleType.individual;
                }
            }

            this.#countersSrv.resetGroupsStatusCounters();
            this.#sessionsService.createSession(this.eventId, session)
                .subscribe(id => {
                    this.#createdSessions.push(id);
                    if (createNew) {
                        this.#dialogRef.updateSize('0', '0');
                        setTimeout(() => this.#dialogRef.updateSize(), 300);
                        this.#ephemeralMessageService.showSuccess({
                            msgKey: 'EVENTS.ADD_SESSION_SUCCESS',
                            msgParams: { sessionName: session.name }
                        });
                        this.prepareNewSessionDates();
                    } else {
                        this.close();
                    }
                });
        } else {
            this.form.markAllAsTouched();
            this.#refreshChanges.next();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(): void {
        this.closeDialog.emit(this.#createdSessions);
    }

    private modifyDate(srcDate: moment.Moment, outputDateCtrl: AbstractControl, dateModifier: SessionDateCustomModifier): void {
        switch (dateModifier) {
            case SessionDateCustomModifier.startMinusHalfHour:
                outputDateCtrl.setValue(srcDate.subtract(30, 'm').format(), { emitEvent: false });
                break;
            case SessionDateCustomModifier.start:
                outputDateCtrl.setValue(srcDate.format(), { emitEvent: false });
                break;
            case SessionDateCustomModifier.startPlusHalfHour:
                outputDateCtrl.setValue(srcDate.add(30, 'm').format(), { emitEvent: false });
                break;
            case SessionDateCustomModifier.startMinusOneDay:
                outputDateCtrl.setValue(srcDate.subtract(1, 'd').format(), { emitEvent: false });
                break;
        }
    }

    private prepareNewSessionDates(): void {
        this.form.get('base.startDate')
            .setValue(moment(this.form.get('base.startDate').value).add(1, 'd').format());
        this.form.get('salesEndDate').setValue(
            moment(this.form.get('base.startDate').value).add(1, 'd').format()
        );
        if (this.bookingsEnabled) {
            this.form.get('bookingsEndDate')
                .setValue(moment(this.form.get('base.startDate').value).add(1, 'd').format());
        }
        this.form.updateValueAndValidity();
    }

    private initDates(): void {
        if (this.lastSessionId) {
            this.#sessionsService.session.clear();
            this.#sessionsService.session.load(this.eventId, this.lastSessionId);
            this.#sessionsService.session.get$().pipe(first(s => s !== null)).subscribe(session => {
                const datesUpdateValues: { [key: string]: unknown } = {
                    releaseDate: moment(session.settings.release.date).format(),
                    salesStartDate: moment(session.settings.sale.start_date).format(),
                    salesEndDate: moment(session.start_date).add(1, 'd').format(),
                    base: {
                        activitySaleType: session.settings.activity_sale_type
                    }
                };
                if (this.bookingsEnabled) {
                    datesUpdateValues['bookingsStartDate'] = moment(session.settings.booking.start_date).format();
                    datesUpdateValues['bookingsEndDate'] = moment(session.start_date).add(1, 'd').format();
                }
                this.#entitiesSrv.getEntity$().pipe(first(Boolean)).subscribe(entity => {
                    if (entity.settings.allow_secondary_market) {
                        datesUpdateValues['secondaryMarketSaleStartDate'] = moment(session.settings.sale.start_date).format();
                        datesUpdateValues['secondaryMarketSaleEndDate'] = moment(session.start_date).add(1, 'd').format();
                    }
                    this.form.patchValue(datesUpdateValues, { emitEvent: false });
                });
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
        this.form.get('secondaryMarket.salesStartDate').setValue(defaultDate.format());
        this.form.get('secondaryMarket.salesEndDate').setValue(defaultDate.format());
    }

    private initFormDataHandlers(): void {
        let isBookingEndDateChangedByStartDate = false;
        let isSaleEndDateChangedByStartDate = false;
        let isSecondaryMarketSaleEndDateChangedByStartDate = false;
        let isBookingEndDateChangedByModifier = false;
        let isSaleEndDateChangedByModifier = false;
        let isSecondaryMarketSaleEndDateChangedByModifier = false;
        this.form.get('base.startDate').valueChanges
            .pipe(
                distinctUntilChanged((prev, curr) => moment(prev).format() === moment(curr).format()),
                filter(date => !!date),
                takeUntil(this.#onDestroy)
            )
            .subscribe(startDate => {
                if (this.bookingsEnabled) {
                    isBookingEndDateChangedByStartDate = true;
                    const bookingsModifier = this.form.get('bookingEndDateModifier').value;
                    this.modifyDate(moment(startDate), this.form.get('bookingsEndDate'), bookingsModifier);
                }
                isSaleEndDateChangedByStartDate = true;
                const salesModifier = this.form.get('saleEndDateModifier').value;
                this.modifyDate(moment(startDate), this.form.get('salesEndDate'), salesModifier);
                isSecondaryMarketSaleEndDateChangedByStartDate = true;
                const secondaryMarketSalesModifier = this.form.get('secondaryMarket.saleEndDateModifier').value;
                this.modifyDate(moment(startDate), this.form.get('secondaryMarket.salesEndDate'), secondaryMarketSalesModifier);
            });
        if (this.bookingsEnabled) {
            this.form.get('bookingEndDateModifier').valueChanges
                .pipe(
                    distinctUntilChanged(),
                    takeUntil(this.#onDestroy)
                )
                .subscribe(modifier => {
                    isBookingEndDateChangedByModifier = true;
                    this.modifyDate(
                        moment(this.form.get('base.startDate').value),
                        this.form.get('bookingsEndDate'),
                        modifier
                    );
                });
            this.form.get('bookingsEndDate').valueChanges
                .pipe(
                    filter(date => !!date),
                    distinctUntilChanged((prev, curr) => moment(prev).format() === moment(curr).format()),
                    takeUntil(this.#onDestroy)
                )
                .subscribe(_ => {
                    if (isBookingEndDateChangedByStartDate || isBookingEndDateChangedByModifier) {
                        isBookingEndDateChangedByStartDate = false;
                        isBookingEndDateChangedByModifier = false;
                    } else {
                        this.form.get('bookingEndDateModifier').setValue(SessionDateCustomModifier.custom, { emitEvent: false });
                    }
                });
        }
        this.form.get('saleEndDateModifier').valueChanges
            .pipe(
                distinctUntilChanged(),
                takeUntil(this.#onDestroy)
            )
            .subscribe(modifier => {
                isSaleEndDateChangedByModifier = true;
                this.modifyDate(
                    moment(this.form.get('base.startDate').value),
                    this.form.get('salesEndDate'),
                    modifier
                );
            });
        this.form.get('salesEndDate').valueChanges
            .pipe(
                filter(date => !!date),
                distinctUntilChanged((prev, curr) => moment(prev).format() === moment(curr).format()),
                takeUntil(this.#onDestroy)
            )
            .subscribe(() => {
                if (isSaleEndDateChangedByStartDate || isSaleEndDateChangedByModifier) {
                    isSaleEndDateChangedByStartDate = false;
                    isSaleEndDateChangedByModifier = false;
                } else {
                    this.form.get('saleEndDateModifier').setValue(SessionDateCustomModifier.custom, { emitEvent: false });
                }
            });

        this.form.get('secondaryMarket.saleEndDateModifier').valueChanges
            .pipe(
                distinctUntilChanged(),
                takeUntil(this.#onDestroy)
            )
            .subscribe(modifier => {
                isSecondaryMarketSaleEndDateChangedByModifier = true;
                this.modifyDate(
                    moment(this.form.get('base.startDate').value),
                    this.form.get('secondaryMarket.salesEndDate'),
                    modifier
                );
            });
        this.form.get('secondaryMarket.salesEndDate').valueChanges
            .pipe(
                filter(date => !!date),
                distinctUntilChanged((prev, curr) => moment(prev).format() === moment(curr).format()),
                takeUntil(this.#onDestroy)
            )
            .subscribe(() => {
                if (isSecondaryMarketSaleEndDateChangedByStartDate || isSecondaryMarketSaleEndDateChangedByModifier) {
                    isSecondaryMarketSaleEndDateChangedByStartDate = false;
                    isSecondaryMarketSaleEndDateChangedByModifier = false;
                } else {
                    this.form.get('secondaryMarket.saleEndDateModifier').setValue(SessionDateCustomModifier.custom, { emitEvent: false });
                }
            });

        // avet initialization
        this.form.get('base.avetMatch').valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(match => {
                this.form.get('releaseDate').setValue(match.start_sales_date);
                this.form.get('salesStartDate').setValue(match.start_sales_date);
                this.form.get('salesEndDate').setValue(match.end_sales_date);
            });
    }
}
