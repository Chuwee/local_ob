import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelSurcharge } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelRequestStatus, EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
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
import { EventChannelSurchargesChannelComponent } from './channel/event-channel-surcharges-channel.component';
import { EventChannelPriceSimulationComponent } from './price-simulation/event-channel-price-simulation.component';
import { EventChannelSurchargesPromoterComponent } from './promoter/event-channel-surcharges-promoter.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        MaterialModule,
        TranslatePipe,
        CommonModule,
        EventChannelSurchargesPromoterComponent,
        EventChannelSurchargesChannelComponent,
        ArchivedEventMgrComponent,
        EventChannelPriceSimulationComponent
    ],
    selector: 'app-event-channel-surcharges',
    templateUrl: './event-channel-surcharges.component.html',
    styleUrls: ['./event-channel-surcharges.component.scss']
})
export class EventChannelSurchargesComponent implements OnInit, OnDestroy, WritingComponent {
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

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#eventChannelsSrv.eventChannel.inProgress$(),
        this.#eventChannelsSrv.isEventChannelSurchargesLoading$(),
        this.#eventChannelsSrv.isEventChannelChannelSurchargesLoading$(),
        this.#eventChannelsSrv.isEventChannelSurchargesSaving$()
    ]));

    readonly form = this.#fb.nonNullable.group({});
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
                    switchMap(eventChannel => this.#eventChannelsSrv.saveChannelSurcharges(
                        eventChannel.event.id,
                        eventChannel.channel.id,
                        this.surchargesRequestCtrl.value
                    ).pipe(
                        tap(() => {
                            this.simulationExpanded = false;
                            this.#ephemeralMessageSrv.showSuccess({ msgKey: 'EVENTS.CHANNEL.PROMOTER_SURCHARGES.UPDATE_SUCCESS' });
                        })
                    ))
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
                this.#eventChannelsSrv.loadEventChannelSurcharges(eventChannel.event.id, eventChannel.channel.id);
            });
    }
}
