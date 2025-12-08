import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelRequestStatus, EventChannelsService, ProviderPlanSettings } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { TaxesMode } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    Component,
    inject,
    OnDestroy,
    OnInit,
    QueryList,
    ViewChildren
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, switchMap, throwError } from 'rxjs';
import { filter, first, map, takeUntil, tap } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        MaterialModule,
        TranslatePipe,
        CommonModule,
        ArchivedEventMgrComponent,
    ],
    selector: 'app-event-channel-destination',
    templateUrl: './event-channel-destination.component.html',
    styleUrls: ['./event-channel-destination.component.scss']
})
export class EventChannelDestinationComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #eventChannelsSrv = inject(EventChannelsService);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #onDestroy = new Subject<void>();
    readonly #eventsSrv = inject(EventsService);

    @ViewChildren(MatExpansionPanel) private readonly _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;
    readonly $currency = toSignal(inject(EventsService).event.get$()
        .pipe(map(event => event.currency_code)));

    readonly $requestAccepted = toSignal(this.#eventChannelsSrv.eventChannel.get$()
        .pipe(map(eventChannel => eventChannel?.status.request === EventChannelRequestStatus.accepted)));

    readonly $channel = toSignal(this.#eventChannelsSrv.eventChannel.get$()
        .pipe(filter(Boolean), map(event => event.channel)));

    readonly $event = toSignal(this.#eventsSrv.event.get$());
    
    readonly $channelsList = toSignal(this.#eventChannelsSrv.eventChannelsList.getData$()
        .pipe(map(channels => channels || [])));

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#eventChannelsSrv.eventChannel.inProgress$(),
        this.#eventChannelsSrv.isEventChannelSurchargesLoading$(),
        this.#eventChannelsSrv.isEventChannelChannelSurchargesLoading$(),
        this.#eventChannelsSrv.isEventChannelSurchargesSaving$()
    ]));

    readonly providerPlanSettingsForm = this.#fb.nonNullable.group({
        // General Sync Settings
        sync_sessions_as_hidden: [false],
        sync_prices: [false],
        sync_surcharges: [false],
        round_prices_up: [false],
        sync_session_labels: [false],
        sync_session_pics: [false],
        sync_session_key_dates: [false],
        sync_session_type_ordering: [false],
        sync_hidden_status: [false],
        sync_session_type_details: [false],
        sync_billing_terms: [false],
        sync_allowed_num_tickets: [false],
        sync_instructions: [false],

        // Cancellation
        enforce_cancellation: [false],
        price_modifier: [0],
        session_price_comes_with_taxes: [false],

        // Real-Time & Availability
        enable_real_time: [false],
        sync_available_tickets: [false],
        sync_sold_out_status: [false],

        // Session Types
        sync_session_type: [false],
        use_real_time_api: [false],
        channels_to_autopublish_session_types: [[] as number[]]
    });

    readonly form = this.#fb.nonNullable.group({
        providerPlanSettings: this.providerPlanSettingsForm
    });
    readonly surchargesRequestCtrl = this.#fb.nonNullable.control([] as ChannelSurcharge[]);
    readonly taxesMode = TaxesMode;
    simulationExpanded = false;
    // TODO: Remove isTestTaxes when  taxes end
    readonly isTestTaxes = false;

    ngOnInit(): void {
        this.#eventChannelsSrv.eventChannel.get$()
            .pipe(filter(Boolean), takeUntil(this.#onDestroy))
            .subscribe(eventChannel => {
                this.simulationExpanded = false;
                this.#eventChannelsSrv.loadEventChannelSurcharges(eventChannel.event.id, eventChannel.channel.id);
                if (eventChannel.status.request === EventChannelRequestStatus.accepted) {
                    this.#eventChannelsSrv.loadEventChannelChannelSurcharges(eventChannel.event.id, eventChannel.channel.id);
                }
                
                // Load provider plan settings
                if (eventChannel.provider_plan_settings) {
                    this.providerPlanSettingsForm.patchValue(eventChannel.provider_plan_settings, { emitEvent: false });
                } else {
                    this.providerPlanSettingsForm.reset({}, { emitEvent: false });
                }
                this.providerPlanSettingsForm.markAsPristine();
            });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#eventChannelsSrv.clearEventChannelSurcharges();
        this.#eventChannelsSrv.clearEventChannelChannelSurcharges();
    }

    cancel(): void {
        this.reloadModels();
    }

    save(): void {
        this.save$().subscribe(() => this.reloadModels());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            this.surchargesRequestCtrl.setValue([]);
            return this.#eventChannelsSrv.eventChannel.get$()
                .pipe(
                    first(),
                    switchMap(eventChannel => {
                        // Only include provider_plan_settings if the form is dirty
                        const providerPlanSettings: ProviderPlanSettings | undefined = 
                            this.providerPlanSettingsForm.dirty 
                                ? this.providerPlanSettingsForm.getRawValue() 
                                : eventChannel.provider_plan_settings;
                        
                        return this.#eventChannelsSrv.updateEventChannel(
                            eventChannel.event.id,
                            eventChannel.channel.id,
                            {
                                settings: eventChannel.settings,
                                use_all_quotas: eventChannel.use_all_quotas,
                                quotas: eventChannel.quotas.filter(q => q.selected).map(q => q.id),
                                provider_plan_settings: providerPlanSettings
                            }
                        ).pipe(
                            switchMap(() => this.#eventChannelsSrv.saveChannelSurcharges(
                                eventChannel.event.id,
                                eventChannel.channel.id,
                                this.surchargesRequestCtrl.value
                            )),
                            tap(() => {
                                this.simulationExpanded = false;
                                this.#ephemeralMessageSrv.showSuccess({ msgKey: 'EVENTS.CHANNEL.PROMOTER_SURCHARGES.UPDATE_SUCCESS' });
                            })
                        );
                    })
                );
        } else {
            this.form.markAllAsTouched();
            this.form.setValue(this.form.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    private reloadModels(): void {
        this.#eventChannelsSrv.eventChannel.get$()
            .pipe(first())
            .subscribe(eventChannel => {
                this.form.markAsPristine();
                this.form.markAsUntouched();
                this.surchargesRequestCtrl.reset([], { emitEvent: false });
                
                // Reload provider plan settings
                if (eventChannel.provider_plan_settings) {
                    this.providerPlanSettingsForm.patchValue(eventChannel.provider_plan_settings, { emitEvent: false });
                } else {
                    this.providerPlanSettingsForm.reset({}, { emitEvent: false });
                }
                this.providerPlanSettingsForm.markAsPristine();
                
                this.#eventChannelsSrv.loadEventChannelSurcharges(eventChannel.event.id, eventChannel.channel.id);
            });
    }
}
