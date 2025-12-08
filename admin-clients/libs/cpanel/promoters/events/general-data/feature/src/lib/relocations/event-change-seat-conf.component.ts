import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    EventChannelRequestStatus, eventChannelsProviders, EventChannelsService
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import {
    EventsService, ChangeSeatType, ChangeSeatAllowedSessions, ChangeSeatTicketsType, ChangeSeatRefundType
} from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ObTimeUnit } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, LowerCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, effect, inject, viewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { firstValueFrom, Observable, throwError } from 'rxjs';
import { filter, map } from 'rxjs/operators';

@Component({
    imports: [
        AsyncPipe,
        MaterialModule,
        TranslatePipe,
        FormContainerComponent,
        ReactiveFormsModule,
        FormsModule,
        FormControlErrorsComponent,
        EllipsifyDirective,
        LowerCasePipe
    ],
    providers: [eventChannelsProviders],
    selector: 'app-event-change-seat-conf',
    templateUrl: './event-change-seat-conf.component.html',
    styleUrls: ['./event-change-seat-conf.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EventChangeSeatConfComponent implements WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #router = inject(Router);
    readonly #eventsSrv = inject(EventsService);
    readonly #eventChannelsSrv = inject(EventChannelsService);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralMsgSrv = inject(EphemeralMessageService);

    $matExpansionPanelQueryList = viewChildren(MatExpansionPanel);
    readonly #$event = toSignal(this.#eventsSrv.event.get$().pipe(filter(Boolean)));
    readonly #$entityId = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean), map(entity => entity.id)));

    readonly changeSeatType = ChangeSeatType;
    readonly changeSeatAllowedSessions = ChangeSeatAllowedSessions;
    readonly changeSeatTicketsType = ChangeSeatTicketsType;
    readonly changeSeatRefundType = ChangeSeatRefundType;

    readonly loading$ = this.#eventsSrv.event.inProgress$();

    readonly $channels = toSignal(this.#eventChannelsSrv.eventChannelsList.getData$().pipe(
        filter(Boolean),
        map(ec => ec.map(eventChannel => eventChannel.channel))
    ));

    readonly enabledControl = this.#fb.nonNullable.control<boolean>(false, Validators.required);

    readonly limitationSectionForm = this.#fb.group({
        type: this.#fb.nonNullable.control<ChangeSeatType>(ChangeSeatType.partial, Validators.required),
        expiry: this.#fb.group({
            enabled: this.#fb.nonNullable.control<boolean>(false, Validators.required),
            expiryTime: this.#fb.group({
                timeOffsetLimitAmount: this.#fb.nonNullable.control<number>(
                    { value: 1, disabled: true },
                    [Validators.required, Validators.min(1)]
                ),
                timeOffsetLimitUnit: this.#fb.nonNullable.control<ObTimeUnit.hours | ObTimeUnit.days>(
                    { value: ObTimeUnit.days, disabled: true },
                    Validators.required
                )
            })
        })
    });

    readonly ticketsSectionForm = this.#fb.group({
        allowedSessions: this.#fb.nonNullable.control<ChangeSeatAllowedSessions>(ChangeSeatAllowedSessions.any, Validators.required),
        sameDateOnly: this.#fb.nonNullable.control<boolean>({ value: false, disabled: true }, Validators.required),
        price: this.#fb.group({
            type: this.#fb.nonNullable.control<ChangeSeatTicketsType>(ChangeSeatTicketsType.greaterOrEqual, Validators.required),
            refund: this.#fb.group({
                type: this.#fb.control<ChangeSeatRefundType>(
                    { value: null, disabled: true },
                    Validators.required
                ),
                voucherExpiry: this.#fb.group({
                    enabled: this.#fb.nonNullable.control<boolean>({ value: false, disabled: true }, Validators.required),
                    expiryTime: this.#fb.group({
                        timeOffsetLimitAmount: this.#fb.nonNullable.control<number>(
                            { value: 15, disabled: true },
                            [Validators.required, Validators.min(1)]
                        ),
                        timeOffsetLimitUnit: this.#fb.nonNullable.control<
                            ObTimeUnit.hours | ObTimeUnit.days | ObTimeUnit.months | ObTimeUnit.weeks
                        >(
                            { value: ObTimeUnit.days, disabled: true },
                            Validators.required
                        )
                    })
                })
            })
        }),
        quantityTickets: this.#fb.nonNullable.control<ChangeSeatTicketsType>(ChangeSeatTicketsType.greaterOrEqual, Validators.required)
    });

    readonly channelsSectionForm = this.#fb.group({
        id: this.#fb.control<number | null>(null, Validators.required),
        redirectObPortal: this.#fb.nonNullable.control<boolean>(false, Validators.required)
    });

    readonly form = this.#fb.group({
        limitationSection: this.limitationSectionForm,
        ticketsSection: this.ticketsSectionForm,
        channelsSection: this.channelsSectionForm
    });

    readonly limitationTimeUnits = [ObTimeUnit.hours, ObTimeUnit.days];
    readonly voucherTimeUnits = [
        ObTimeUnit.hours,
        ObTimeUnit.days,
        ObTimeUnit.weeks,
        ObTimeUnit.months
    ];

    readonly limitsExpiryGroup = this.limitationSectionForm.controls.expiry;
    readonly limitsExpiryTimeGroup = this.limitsExpiryGroup.controls.expiryTime;
    readonly ticketsPriceGroup = this.ticketsSectionForm.controls.price;
    readonly ticketsRefundGroup = this.ticketsPriceGroup.controls.refund;
    readonly ticketsRefundVoucherExpiryGroup = this.ticketsRefundGroup.controls.voucherExpiry;
    readonly ticketsRefundVoucherExpiryTimeGroup = this.ticketsRefundVoucherExpiryGroup.controls.expiryTime;

    constructor() {
        this.#initChangesHandlers();
        this.#initEffects();
    }

    save$(): Observable<void> {
        if (this.form.invalid) {
            return this.#cancelSaveAndShowErrors();
        }
        const val = this.form.value;
        const enable = this.enabledControl.value;

        return this.#eventsSrv.event.update(this.#$event().id, {
            settings: {
                change_seat_settings: {
                    enable,
                    change_type: val.limitationSection.type,
                    event_change_seat_expiry: val.limitationSection.expiry.enabled ? {
                        time_offset_limit_amount: val.limitationSection.expiry.expiryTime.timeOffsetLimitAmount,
                        time_offset_limit_unit: val.limitationSection.expiry.expiryTime.timeOffsetLimitUnit
                    } : undefined,
                    ticket_selection: {
                        allowed_sessions: val.ticketsSection.allowedSessions,
                        same_date_only: val.ticketsSection.allowedSessions === ChangeSeatAllowedSessions.different
                            ? val.ticketsSection.sameDateOnly : undefined,
                        price: {
                            type: val.ticketsSection.price.type,
                            refund: val.ticketsSection.price.type === ChangeSeatTicketsType.any ? {
                                type: val.ticketsSection.price.refund.type,
                                voucher_expiry: val.ticketsSection.price.refund.type === ChangeSeatRefundType.voucher ? {
                                    enabled: val.ticketsSection.price.refund.voucherExpiry.enabled,
                                    expiry_time: val.ticketsSection.price.refund.voucherExpiry.enabled ? {
                                        time_offset_limit_amount:
                                            val.ticketsSection.price.refund.voucherExpiry.expiryTime.timeOffsetLimitAmount,
                                        time_offset_limit_unit:
                                            val.ticketsSection.price.refund.voucherExpiry.expiryTime.timeOffsetLimitUnit
                                    } : undefined
                                } : undefined
                            } : undefined
                        },
                        tickets: val.ticketsSection.quantityTickets
                    },
                    reallocation_channel: {
                        id: val.channelsSection.id,
                        apply_to_all_channel_types: val.channelsSection.redirectObPortal
                    }
                }
            }
        });
    }

    save(): void {
        this.save$().subscribe({
            next: () => {
                this.#ephemeralMsgSrv.showSaveSuccess();
                this.refresh();
            },
            error: () => this.#revertToggle()
        });
    }

    refresh(): void {
        this.form.markAsPristine();
        this.#eventsSrv.event.load(this.#$event().id.toString());
    }

    navigateToCharges(): void {
        this.#router.navigate(
            ['events', this.#$event().id, 'prices', 'surcharges'],
            { queryParams: { from: 'change-seats' } }
        );
    }

    getTimeUnitKey(timeUnit: ObTimeUnit, amount: number): string {
        switch (timeUnit) {
            case ObTimeUnit.hours:
                return amount === 1 ? 'FORMS.LABELS.HOUR' : 'FORMS.LABELS.HOURS';
            case ObTimeUnit.days:
                return amount === 1 ? 'FORMS.LABELS.DAY' : 'FORMS.LABELS.DAYS';
            case ObTimeUnit.weeks:
                return amount === 1 ? 'FORMS.LABELS.WEEK' : 'FORMS.LABELS.WEEKS';
            case ObTimeUnit.months:
                return amount === 1 ? 'FORMS.LABELS.MONTH' : 'FORMS.LABELS.MONTHS';
            default:
                return '';
        }
    }

    #initChangesHandlers(): void {
        FormControlHandler.getValueChanges(this.enabledControl)
            .subscribe(enabled => this.#changeEnabled(enabled));
        FormControlHandler.getValueChanges(this.form.controls.limitationSection.controls.expiry.controls.enabled)
            .subscribe(enabled => this.#seatExpiryEnabledChange(enabled));
        FormControlHandler.getValueChanges(this.form.controls.ticketsSection.controls.allowedSessions)
            .subscribe(value => this.#allowedSessionsTypeChange(value));
        FormControlHandler.getValueChanges(this.form.controls.ticketsSection.controls.price.controls.type)
            .subscribe(value => this.#ticketSelectionTypeChange(value));
        FormControlHandler.getValueChanges(this.ticketsRefundGroup.controls.type)
            .subscribe(value => this.#ticketRefundTypeChange(value));
        FormControlHandler.getValueChanges(this.ticketsRefundVoucherExpiryGroup.controls.enabled)
            .subscribe(value => this.#ticketRefundVoucherEnabledChange(value));
    }

    #initEffects(): void {
        // Reset the form with the available data when the event is loaded or reloaded
        effect(() => this.#resetForm());

        effect(() => {
            const entityId = this.#$entityId();
            const eventId = this.#$event()?.id;
            if (!entityId || !eventId) return;

            this.#eventChannelsSrv.eventChannelsList.load(this.#$event().id, {
                request_status: EventChannelRequestStatus.accepted,
                type: ChannelType.web,
                limit: 100,
                entity_id: this.#$entityId()
            });
        });
    }

    async #changeEnabled(value: boolean): Promise<void> {
        if (!value && !(await this.#confirmDisablingChangeSeat())) {
            this.#revertToggle();
            return;
        }

        if (this.form.dirty) {
            const proceed = await this.#confirmStatusChange();
            proceed ? this.save() : this.#revertToggle();
        } else {
            this.save();
        }
    }

    #revertToggle(): void {
        const control = this.enabledControl;
        if (control.pristine) return;
        control.setValue(!control.value, { emitEvent: false });
        control.markAsPristine();
    }

    #seatExpiryEnabledChange(value: boolean): void {
        const changeFunc = this.#getChangeEnabledFunction(value);
        changeFunc(this.limitsExpiryTimeGroup.controls.timeOffsetLimitAmount);
        changeFunc(this.limitsExpiryTimeGroup.controls.timeOffsetLimitUnit);
    }

    #allowedSessionsTypeChange(value: ChangeSeatAllowedSessions): void {
        const changeFunc = this.#getChangeEnabledFunction(value === ChangeSeatAllowedSessions.different);
        changeFunc(this.ticketsSectionForm.controls.sameDateOnly);
    }

    #ticketSelectionTypeChange(value: ChangeSeatTicketsType): void {
        const changeFunc = this.#getChangeEnabledFunction(value === ChangeSeatTicketsType.any);
        changeFunc(this.ticketsRefundGroup.controls.type);
    }

    #ticketRefundTypeChange(value: ChangeSeatRefundType): void {
        const changeFunc = this.#getChangeEnabledFunction(value === ChangeSeatRefundType.voucher);
        changeFunc(this.ticketsRefundGroup.controls.voucherExpiry.controls.enabled);
    }

    #ticketRefundVoucherEnabledChange(value: boolean): void {
        const changeFunc = this.#getChangeEnabledFunction(value);
        changeFunc(this.ticketsRefundVoucherExpiryTimeGroup.controls.timeOffsetLimitAmount);
        changeFunc(this.ticketsRefundVoucherExpiryTimeGroup.controls.timeOffsetLimitUnit);
    }

    #getChangeEnabledFunction(value: boolean): (control: FormControl) => void {
        return value ?
            (control: FormControl): void => {
                control.enable();
                this.form.updateValueAndValidity();
            }
            : (control: FormControl): void => {
                control.disable();
                this.form.updateValueAndValidity();
            };
    }

    #confirmDisablingChangeSeat(): Promise<boolean> {
        return firstValueFrom(this.#msgDialogSrv.showWarn({
            title: 'EVENTS.RELOCATIONS.FORMS.LABELS.DISABLE_WARNING',
            message: 'EVENTS.RELOCATIONS.FORMS.INFOS.DISABLE_WARNING',
            actionLabel: 'FORMS.ACTIONS.DISABLE',
            showCancelButton: true,
            size: DialogSize.MEDIUM
        }));
    }

    #confirmStatusChange(): Promise<boolean> {
        return firstValueFrom(this.#msgDialogSrv.showWarn({
            title: 'EVENTS.PROMOTIONS.STATUS_CHANGE_WARNING.TITLE',
            message: 'EVENTS.PROMOTIONS.STATUS_CHANGE_WARNING.DESCRIPTION',
            actionLabel: 'FORMS.ACTIONS.UPDATE',
            showCancelButton: true,
            size: DialogSize.MEDIUM
        }));
    }

    #cancelSaveAndShowErrors(): Observable<never> {
        this.#revertToggle();
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this.$matExpansionPanelQueryList());
        return throwError(() => 'invalid form');
    }

    #resetForm(): void {
        const set = this.#$event()?.settings.change_seat_settings;
        if (!set) {
            this.enabledControl.reset(false, { emitEvent: false });
            this.form.reset();
            return;
        }
        this.enabledControl.reset(set.enable ?? false, { emitEvent: false });

        this.form.reset(
            {
                limitationSection: {
                    type: set.change_type,
                    expiry: {
                        enabled: set.event_change_seat_expiry?.time_offset_limit_amount > 0,
                        expiryTime: {
                            timeOffsetLimitAmount: set.event_change_seat_expiry?.time_offset_limit_amount,
                            timeOffsetLimitUnit: set.event_change_seat_expiry?.time_offset_limit_unit
                        }
                    }
                },
                ticketsSection: {
                    allowedSessions: set.ticket_selection?.allowed_sessions,
                    sameDateOnly: set.ticket_selection?.same_date_only,
                    price: {
                        type: set.ticket_selection?.price?.type,
                        refund: {
                            type: set.ticket_selection?.price?.refund?.type,
                            voucherExpiry: {
                                enabled: set.ticket_selection?.price?.refund?.voucher_expiry?.enabled,
                                expiryTime: {
                                    timeOffsetLimitAmount:
                                        set.ticket_selection?.price?.refund?.voucher_expiry?.expiry_time?.time_offset_limit_amount,
                                    timeOffsetLimitUnit:
                                        set.ticket_selection?.price?.refund?.voucher_expiry?.expiry_time?.time_offset_limit_unit
                                }
                            }
                        }
                    },
                    quantityTickets: set.ticket_selection?.tickets
                },
                channelsSection: {
                    id: set.reallocation_channel?.id,
                    redirectObPortal: set.reallocation_channel?.apply_to_all_channel_types
                }
            }
        );
    }

}
