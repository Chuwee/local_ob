import { ChannelSurchargeSimulationType } from '@admin-clients/cpanel/channels/data-access';
import { SeasonTicketChannelsService } from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { first, map, shareReplay } from 'rxjs/operators';
import { VmSeasonTicketChannelPriceSimulationValue } from '../../models/vm-season-ticket-channel-price-simulation.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        TranslatePipe,
        FlexLayoutModule,
        MaterialModule,
        LocalCurrencyPipe
    ],
    selector: 'app-season-ticket-channel-price-simulation',
    templateUrl: './season-ticket-channel-price-simulation.component.html',
    styleUrls: ['./season-ticket-channel-price-simulation.component.scss']
})
export class SeasonTicketChannelPriceSimulationComponent implements OnDestroy {
    private readonly _seasonTicketChannelsService = inject(SeasonTicketChannelsService);

    private readonly _onDestroy = new Subject<void>();
    private _expanded = false;

    readonly priceSimulations$ = this._seasonTicketChannelsService.getSeasonTicketChannelPriceSimulation$()
        .pipe(map(priceSimulations => {
            const priceSimulationValues: VmSeasonTicketChannelPriceSimulationValue[] = [];
            const priceSimulation = priceSimulations?.[0];
            if (priceSimulation) {
                // RATES
                priceSimulation.rates.forEach((rate, j) => {
                    priceSimulationValues[j] = {
                        priceTypes: [],
                        rateName: rate.name
                    };
                    // PRICE TYPES
                    rate.price_types.forEach(pt => {
                        // SIMULATIONS
                        pt.simulations.forEach(sim => {
                            priceSimulationValues[j].priceTypes.push({
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
            }
            return priceSimulationValues;
        }));

    readonly isLoading$ = this._seasonTicketChannelsService.isSeasonTicketChannelPriceSimulationLoading$();
    readonly displayedColumns = ['price-type', 'promotion', 'promotion-type', 'base', 'promoter-surcharges', 'channel-surcharges', 'total'];
    readonly currency$ = inject(SeasonTicketsService).seasonTicket.get$()
        .pipe(map(st => st.currency_code), shareReplay({ bufferSize: 1, refCount: true }));

    @Input() set expanded(expanded: boolean) {
        this._expanded = expanded;
        this.loadData();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._seasonTicketChannelsService.clearSeasonTicketChannelPriceSimulation();
    }

    private loadData(): void {
        this._seasonTicketChannelsService.getSeasonTicketChannel$()
            .pipe(first())
            .subscribe(stChannel => {
                if (this._expanded && stChannel) {
                    this._seasonTicketChannelsService.loadSeasonTicketChannelPriceSimulation(
                        stChannel.season_ticket.id,
                        stChannel.channel.id
                    );
                } else {
                    this._seasonTicketChannelsService.clearSeasonTicketChannelPriceSimulation();
                }
            });
    }
}
