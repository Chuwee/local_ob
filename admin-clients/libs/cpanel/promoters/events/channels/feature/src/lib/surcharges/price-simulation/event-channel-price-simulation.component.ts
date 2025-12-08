import { ChannelSurchargeSimulationType } from '@admin-clients/cpanel/channels/data-access';
import { EventChannelsService } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { of, Subject, switchMap } from 'rxjs';
import { filter, first, map, shareReplay, startWith } from 'rxjs/operators';
import { VmEventChannelPriceSimulationMapped } from '../../models/vm-event-channel-price-simulation.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        TranslatePipe,
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        LocalCurrencyPipe,
        EllipsifyDirective
    ],
    selector: 'app-event-channel-price-simulation',
    templateUrl: './event-channel-price-simulation.component.html',
    styleUrls: ['./event-channel-price-simulation.component.scss']
})
export class EventChannelPriceSimulationComponent implements OnDestroy {
    private readonly _eventChannelsService = inject(EventChannelsService);

    private readonly _onDestroy = new Subject<void>();
    private _expanded = false;

    readonly venueTplCtrl = inject(FormBuilder).nonNullable.control(null as number);
    readonly priceSimulations$ = this._eventChannelsService.getEventChannelPriceSimulation$()
        .pipe(
            switchMap(priceSimulations => {
                const priceSimMapped: VmEventChannelPriceSimulationMapped = {};

                if (priceSimulations) {
                    // VENUE TEMPLATES
                    priceSimulations.forEach((priceSimulation, i) => {
                        if (i === 0) {
                            this.venueTplCtrl.setValue(+priceSimulation.venue_template.id, { emitEvent: false });
                        }
                        priceSimMapped[priceSimulation.venue_template.id] = [];
                        // RATES
                        priceSimulation.rates.forEach((rate, j) => {
                            priceSimMapped[priceSimulation.venue_template.id][j] = {
                                priceTypes: [],
                                rateName: rate.name
                            };
                            // PRICE TYPES
                            rate.price_types.forEach(pt => {
                                // SIMULATIONS
                                pt.simulations.forEach(sim => {
                                    priceSimMapped[priceSimulation.venue_template.id][j].priceTypes.push({
                                        priceTypeName: pt.name,
                                        promotions: sim.promotions,
                                        total: sim.price.total,
                                        base: sim.price.base,
                                        channelSurcharges: sim.price.surcharges
                                            .filter(s => s.type === ChannelSurchargeSimulationType.channel)
                                            .reduce(s => s)?.value,
                                        promoterSurcharges: sim.price.surcharges
                                            .filter(s => s.type === ChannelSurchargeSimulationType.promoter)
                                            .reduce(s => s)?.value
                                    });
                                });
                            });
                        });
                    });
                }
                return of(priceSimMapped);
            }),
            switchMap(priceSimMapped => this.venueTplCtrl.valueChanges
                .pipe(
                    startWith(this.venueTplCtrl.value),
                    map(venueTplSelected => priceSimMapped[venueTplSelected])
                ))
        );

    readonly venueTpls$ = this._eventChannelsService.getEventChannelPriceSimulation$()
        .pipe(
            filter(Boolean),
            map(priceSimulations => priceSimulations.map(ps => ps.venue_template))
        );

    readonly isLoading$ = this._eventChannelsService.isEventChannelPriceSimulationLoading$();
    readonly displayedColumns = ['price-type', 'promotion', 'promotion-type', 'base', 'promoter-surcharges', 'channel-surcharges', 'total'];
    readonly currency$ = inject(EventsService).event.get$()
        .pipe(map(event => event.currency_code), shareReplay({ bufferSize: 1, refCount: true }));

    @Input() set expanded(expanded: boolean) {
        this._expanded = expanded;
        this.loadData();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._eventChannelsService.clearEventChannelPriceSimulation();
    }

    private loadData(): void {
        this._eventChannelsService.eventChannel.get$()
            .pipe(first())
            .subscribe(eventChannel => {
                if (this._expanded && eventChannel) {
                    this._eventChannelsService.loadEventChannelPriceSimulation(eventChannel.event.id, eventChannel.channel.id);
                } else {
                    this.venueTplCtrl.reset(null, { emitEvent: false });
                    this._eventChannelsService.clearEventChannelPriceSimulation();
                }
            });
    }
}
