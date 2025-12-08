import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    PutSessionPackRefundCondition, PutSessionRefundConditions
} from '@admin-clients/cpanel/promoters/events/session-packs/data-access';
import { EventSessionsService, Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { SeatStatus } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import {
    VenueTemplateBlockingReason, VenueTemplateQuota, VenueTemplatesService
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, shareReplay, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-session-pack-refunds',
    templateUrl: './session-pack-refunds.component.html',
    styleUrls: ['./session-pack-refunds.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SessionPackRefundsComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _event: Event;
    private _session: Session;

    loading$: Observable<boolean>;
    form: UntypedFormGroup;
    formPartialRefundConditions: UntypedFormGroup;
    seatStatuses = [SeatStatus.free, SeatStatus.promotorLocked, SeatStatus.kill];
    quotas$: Observable<VenueTemplateQuota[]>;
    blockingReasons$: Observable<VenueTemplateBlockingReason[]>;
    readonly seatStatus = SeatStatus;

    constructor(
        private _sessionsService: EventSessionsService,
        private _eventsService: EventsService,
        private _venueTemplateSrv: VenueTemplatesService,
        private _msgDialogSrv: MessageDialogService,
        private _fb: UntypedFormBuilder
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({
            refunded_seat_quota: this._fb.group({
                enabled: null,
                id: null
            }),
            refunded_seat_status: null,
            refunded_seat_block_reason_id: null,
            print_refund_price_in_ticket: null,
            session_pack_automatically_calculate_conditions: null,
            session_pack_refund_conditions: null
        });

        this.loading$ = booleanOrMerge([
            this._sessionsService.isSessionRefundConditionsLoading$(),
            this._eventsService.eventPrices.inProgress$()
        ]);

        combineLatest([
            this._eventsService.event.get$(),
            this._sessionsService.session.get$()
        ]).pipe(
            filter(([event, session]) => !!event && !!session),
            takeUntil(this._onDestroy)
        ).subscribe(([event, session]) => {
            this._event = event;
            this._session = session;
            this._sessionsService.loadSessionRefundConditions(event.id, session.id);
            this._eventsService.eventPrices.load(event.id.toString(), session.venue_template.id.toString());
        });

        this._venueTemplateSrv.clearVenueTemplateData();

        this.form.get('refunded_seat_status').valueChanges.pipe(
            takeUntil(this._onDestroy)
        ).subscribe(value => {
            if (value === SeatStatus.promotorLocked) {
                this.form.get('refunded_seat_block_reason_id').enable({ emitEvent: false });
            } else {
                this.form.get('refunded_seat_block_reason_id').disable({ emitEvent: false });
            }
        });

        this.form.get('refunded_seat_quota').valueChanges.pipe(
            takeUntil(this._onDestroy)
        ).subscribe(value => {
            if (value.enabled) {
                this.form.get('refunded_seat_quota.id').enable({ emitEvent: false });
            } else {
                this.form.get('refunded_seat_quota.id').disable({ emitEvent: false });
            }
        });

        this.quotas$ = this._venueTemplateSrv.getVenueTemplateQuotas$()
            .pipe(
                tap(value => value === null && this._venueTemplateSrv.loadVenueTemplateQuotas(this._session.venue_template.id)),
                first(value => !!value),
                shareReplay(1)
            );

        this.blockingReasons$ = this._venueTemplateSrv.getVenueTemplateBlockingReasons$()
            .pipe(
                tap(value => value === null && this._venueTemplateSrv.loadVenueTemplateBlockingReasons(this._session.venue_template.id)),
                first(value => !!value),
                shareReplay(1)
            );

        this._sessionsService.getSessionRefundConditions$()
            .pipe(
                filter(value => !!value),
                takeUntil(this._onDestroy)
            )
            .subscribe(refundconditions => {
                this.form.reset({
                    refunded_seat_quota: refundconditions.refunded_seat_quota,
                    refunded_seat_status: refundconditions.refunded_seat_status,
                    refunded_seat_block_reason_id: refundconditions.refunded_seat_block_reason_id,
                    print_refund_price_in_ticket: refundconditions.print_refund_price_in_ticket,
                    session_pack_automatically_calculate_conditions: refundconditions.session_pack_automatically_calculate_conditions
                });
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    refundModeChangeHandler(isAutoMode: boolean): void {
        this.form.get('session_pack_automatically_calculate_conditions').markAsPristine();
        this._msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: isAutoMode ?
                'EVENTS.SESSION_PACK.ENABLE_AUTO_CONDITIONS' :
                'EVENTS.SESSION_PACK.DISABLE_AUTO_CONDITIONS',
            message: isAutoMode ?
                'EVENTS.SESSION_PACK.ENABLE_AUTO_CONDITIONS_WARNING' :
                'EVENTS.SESSION_PACK.DISABLE_AUTO_CONDITIONS_WARNING',
            actionLabel: 'FORMS.ACTIONS.AGREED',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._sessionsService.updateSessionRefundConditions(this._event.id, this._session.id, {
                        session_pack_automatically_calculate_conditions: isAutoMode
                    }).subscribe(() => {
                        this._sessionsService.loadSessionRefundConditions(this._event.id, this._session.id);
                    });
                } else {
                    this.form.get('session_pack_automatically_calculate_conditions').setValue(!isAutoMode, { emitEvent: false });
                }
            });
    }

    cancel(): void {
        this.form.markAsPristine();
        this.form.markAsUntouched();
        this._sessionsService.loadSessionRefundConditions(this._event.id, this._session.id);
    }

    save(): void {
        if (this.form.valid) {
            const refundconditions = this.form.value;
            const packconditions: PutSessionPackRefundCondition[] = [];
            const tabledata: { (priceRateId: string): { (sessionid: string): number }[] }
                = this.form.get('session_pack_refund_conditions').value;
            Object.keys(tabledata).forEach(priceTypeRateId => {
                Object.keys(tabledata[priceTypeRateId]).forEach(sessionId => {
                    const foundCondition = packconditions.find(elem => `${elem.session_id}` === sessionId);
                    const currentCondition = foundCondition || {
                        session_id: parseInt(sessionId),
                        price_percentage_values: []
                    };
                    !foundCondition && packconditions.push(currentCondition);
                    currentCondition.price_percentage_values.push({
                        price_type_id: parseInt(priceTypeRateId.split('-')[0]),
                        rate_id: parseInt(priceTypeRateId.split('-')[1]),
                        value: tabledata[priceTypeRateId][sessionId]
                    });
                });
            });
            const updateRefundConditions: PutSessionRefundConditions = {
                refunded_seat_status: refundconditions.refunded_seat_status || null,
                refunded_seat_block_reason_id: refundconditions.refunded_seat_block_reason_id || null,
                refunded_seat_quota: refundconditions.refunded_seat_quota || null,
                print_refund_price_in_ticket: refundconditions.print_refund_price_in_ticket,
                session_pack_automatically_calculate_conditions: refundconditions.session_pack_automatically_calculate_conditions,
                session_pack_refund_conditions: refundconditions.session_pack_automatically_calculate_conditions ? null : packconditions
            };
            this._sessionsService
                .updateSessionRefundConditions(this._event.id, this._session.id, updateRefundConditions)
                .subscribe(() => this.cancel());
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
        }
    }
}
