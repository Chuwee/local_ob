import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventSessionsService, PostSession, SessionsListCountersService
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { EntitiesBaseService, EventType } from '@admin-clients/shared/common/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { ActivitySaleType } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import {
    ChangeDetectionStrategy, Component, ElementRef, EventEmitter, inject, Input, OnInit, Output, QueryList, ViewChild, ViewChildren
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment-timezone';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, first, map, take } from 'rxjs/operators';
import { SessionUtils } from '../utils/session.utils';

@Component({
    selector: 'app-create-multi-session',
    templateUrl: './create-multi-session.component.html',
    styleUrls: ['./create-multi-session.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CreateMultiSessionComponent implements OnInit {

    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _elemRef = inject(ElementRef);
    private readonly _eventsService = inject(EventsService);
    private readonly _sessionsService = inject(EventSessionsService);
    private readonly _messageDialogService = inject(MessageDialogService);
    private readonly _translate = inject(TranslateService);
    private readonly _countersSrv = inject(SessionsListCountersService);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild(WizardBarComponent, { static: true })
    private _wizardBar: WizardBarComponent;

    private _refreshChanges = new Subject<void>();

    readonly STEPS = ['EVENTS.SESSION.BASE_SESSION', 'EVENTS.SESSION.REPETITIONS', 'EVENTS.SESSION.CALENDAR'];

    @Input() eventId: number;
    @Input() lastSessionId: number;
    @Output() closeDialog = new EventEmitter<number[]>();

    readonly newSessionForm = this._fb.group({});

    readonly refreshChanges$ = this._refreshChanges.asObservable();

    readonly isCreatingSessions$ = this._sessionsService.isCreatingSessions$();

    readonly sessionCount$ = this.newSessionForm.valueChanges
        .pipe(
            debounceTime(0),
            map(value => Object.values(value?.calendar || {}).filter(sel => !!sel).length),
            distinctUntilChanged()
        );

    readonly $showLoyaltyPointsSettings = toSignal(
        this.#entitiesSrv.getEntity$().pipe(first(Boolean), map(entity => entity.settings?.allow_loyalty_points))
    );

    currentStep = 0;
    groupsAllowed = false;
    isActivity = false;

    ngOnInit(): void {
        this._eventsService.event.get$().pipe(take(1))
            .subscribe(event => {
                this.isActivity = event.type === EventType.activity || event.type === EventType.themePark;
                this.groupsAllowed = this.isActivity && event.settings.groups.allowed;
            });
    }

    close(sessionIds: number[] = null): void {
        this.closeDialog.emit(sessionIds);
    }

    goToStep(step: number): void {
        this.currentStep = step;
        this._wizardBar.setActiveStep(step);
    }

    nextStep(): void {
        if (this.currentStep < this.STEPS.length - 1) {
            if ((this.currentStep === 0 && this.newSessionForm.get('baseSession')?.valid)
                || (this.currentStep === 1 && this.newSessionForm.get('repetitions')?.valid && this.isStepTwoValid())
                || (this.currentStep === 2 && this.newSessionForm.get('calendar')?.valid)) {
                this.goToStep(this.currentStep + 1);
            } else {
                this.newSessionForm.markAllAsTouched();
                this._refreshChanges.next();
                scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement, this._matExpansionPanelQueryList);
            }
        } else {
            if (this.newSessionForm.valid) {
                this.saveSessions();
            }
        }
    }

    private isStepTwoValid(): boolean {
        const weekDaysWithCheck = this.getWeekDaysWithSomeCheck();
        if (!weekDaysWithCheck.length) {
            this._messageDialogService.showAlert({
                size: DialogSize.SMALL,
                title: this._translate.instant('TITLES.ERROR_DIALOG'),
                message: this._translate.instant('EVENTS.SESSION.ERROR_NO_CHECK_SELECTED')
            });
        }

        return weekDaysWithCheck.length > 0;
    }

    private getWeekDaysWithSomeCheck(): string[] {
        const weekDays = new Set<string>();
        const hourRows = this.newSessionForm.get('repetitions.hourRows') as UntypedFormArray;
        hourRows.controls.forEach((row: any) => {
            Object.keys(row.controls).forEach((ctrlName: string) => {
                if (ctrlName !== 'hour') {
                    if (row.controls[ctrlName].value) {
                        weekDays.add(ctrlName);
                    }
                }
            });
        });

        return Array.from(weekDays);
    }

    private saveSessions(): void {
        const calendarValues = this.newSessionForm.get('calendar').value as { [key: string]: boolean };
        const sessionsDates: string[] = [];
        for (const startDateString in calendarValues) {
            if (calendarValues[startDateString]) { // marked check
                sessionsDates.push(startDateString);
            }
        }

        if (sessionsDates.length === 0) {
            this._messageDialogService.showAlert({
                size: DialogSize.SMALL,
                title: this._translate.instant('TITLES.ERROR_DIALOG'),
                message: this._translate.instant('EVENTS.SESSION.ERROR_NO_DATE_SELECTED')
            });
        } else if (sessionsDates.length > 1000) {
            this._messageDialogService.showAlert({
                size: DialogSize.SMALL,
                title: this._translate.instant('TITLES.ERROR_DIALOG'),
                message: this._translate.instant('EVENTS.SESSION.ERROR_TOO_MANY_DATES_SELECTED')
            });
        } else {
            const sessions: PostSession[] = [];
            const baseDataFormValues = this.newSessionForm.value.baseSession.base;
            const venueTZ = baseDataFormValues.venueTemplate.venue.timezone;
            const name = baseDataFormValues.name;
            const venueTplId = baseDataFormValues.venueTemplate.id;
            const reference = baseDataFormValues.reference;
            const taxTicketId = baseDataFormValues.ticketTax.id;
            const taxChargesId = baseDataFormValues.surchargeTax.id;

            const rates: { id: number; default: boolean }[] = Array.from(baseDataFormValues.sessionRates.values());
            const activitySaleType = this.groupsAllowed && baseDataFormValues.activitySaleType
                || this.isActivity && ActivitySaleType.individual
                || undefined;
            const enableSmartBooking = baseDataFormValues.enableSmartBooking;
            const durationHours = baseDataFormValues.durationHours;
            const durationMins = baseDataFormValues.durationMins;
            const enableOrphanSeats = !!baseDataFormValues.enableOrphanSeats;
            const baseSessionForm = this.newSessionForm.get('baseSession') as UntypedFormGroup;
            sessionsDates.forEach(startDateString => {
                const session: PostSession = {
                    name,
                    venue_template_id: venueTplId,
                    tax_ticket_id: taxTicketId,
                    tax_charges_id: taxChargesId,
                    enable_smart_booking: enableSmartBooking,
                    dates: {
                        start: SessionUtils.formatDate(startDateString, venueTZ),
                        channels: SessionUtils.getFixedDateTime(baseSessionForm, startDateString, 'release', venueTZ),
                        sales_start: SessionUtils.getFixedDateTime(baseSessionForm, startDateString, 'sales.start', venueTZ),
                        sales_end: SessionUtils.getFixedDateTime(baseSessionForm, startDateString, 'sales.end', venueTZ),
                        bookings_start: SessionUtils.getFixedDateTime(baseSessionForm, startDateString, 'bookings.start', venueTZ),
                        bookings_end: SessionUtils.getFixedDateTime(baseSessionForm, startDateString, 'bookings.end', venueTZ),
                        secondary_market_sale_start: baseSessionForm.get('secondary_market_sale_enabled').value ? SessionUtils
                            .getFixedDateTime(baseSessionForm, startDateString, 'secondary_market_sale.start', venueTZ) : null,
                        secondary_market_sale_end: baseSessionForm.get('secondary_market_sale_enabled').value ? SessionUtils
                            .getFixedDateTime(baseSessionForm, startDateString, 'secondary_market_sale.end', venueTZ) : null
                    },
                    rates,
                    activity_sale_type: activitySaleType
                };
                if (enableOrphanSeats) {
                    session.settings = {
                        enable_orphan_seats: enableOrphanSeats
                    };
                }
                if (this.$showLoyaltyPointsSettings()) {
                    session.loyalty_points_config = {
                        point_gain: {
                            amount: baseSessionForm.value.loyaltyPointGainConfig.amount,
                            type: baseSessionForm.value.loyaltyPointGainConfig.type
                        }
                    };
                }
                if (durationHours || durationMins) {
                    session.dates.end = SessionUtils.formatDate(moment(startDateString).add(durationHours, 'h')
                        .add(durationMins, 'm').format(), venueTZ);
                }
                if (reference) {
                    session.reference = reference;
                }
                sessions.push(session);
            });
            this._countersSrv.resetGroupsStatusCounters();
            this._sessionsService.createSessions(this.eventId, sessions)
                .subscribe(ids => this.close(ids));
        }
    }
}
