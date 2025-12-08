import { ChannelSurchargeSimulationType } from '@admin-clients/cpanel/channels/data-access';
import { SalesRequestsService } from '@admin-clients/cpanel-channels-sales-requests-data-access';
import { EphemeralMessageService, ExportDialogComponent, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { ExportDialogData, ExportRequest, ExportFormat } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, DestroyRef, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { filter, first, map } from 'rxjs/operators';
import { exportDataPriceSimulation } from './price-simulation-export-data';

@Component({
    selector: 'app-sale-request-price-simulation',
    templateUrl: './sale-request-price-simulation.component.html',
    styleUrls: ['./sale-request-price-simulation.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class SaleRequestPriceSimulationComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #salesRequestsService = inject(SalesRequestsService);
    readonly #dialog = inject(MatDialog);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);

    #saleRequestId: number;
    #expanded = false;

    readonly currency$ = this.#salesRequestsService.getSaleRequest$()
        .pipe(
            first(),
            map(saleRequest => saleRequest.event.currency_code)
        );

    readonly priceSimulations$ = this.#salesRequestsService.salesRequestPriceSimulation.get$()
        .pipe(map(priceSimulations => {
            const priceSimMapped: Record<string, any> = {};
            if (priceSimulations) {
                // VENUE TEMPLATES
                priceSimulations.forEach((priceSimulation, i) => {
                    if (i === 0) {
                        this.venueTplSelected = +priceSimulation.venue_template.id;
                    }
                    priceSimMapped[priceSimulation.venue_template.id] = [];
                    // RATES
                    priceSimulation.rates.forEach((rate, j) => {
                        priceSimMapped[priceSimulation.venue_template.id][j] = {
                            price_types: [],
                            rateName: rate.name
                        };
                        // PRICE TYPES
                        rate.price_types.forEach(pt => {
                            // SIMULATIONS
                            pt.simulations.forEach(sim => {
                                priceSimMapped[priceSimulation.venue_template.id][j].price_types.push({
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
            return priceSimMapped;
        }));

    readonly venueTpls$ = this.#salesRequestsService.salesRequestPriceSimulation.get$()
        .pipe(
            filter(value => value !== null),
            map(priceSimulations => priceSimulations.map(ps => ps.venue_template))
        );

    readonly isLoading$ = this.#salesRequestsService.salesRequestPriceSimulation.inProgress$();

    venueTplSelected: number;

    displayedColumns = ['price-type', 'promotion', 'promotion-type', 'base', 'promoter-surcharges', 'channel-surcharges', 'total'];

    @Input() set expanded(expanded: boolean) {
        this.#expanded = expanded;
        this.loadData();
    }

    ngOnInit(): void {
        this.#salesRequestsService.isSaleRequestSurchargesLoading$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(saving => {
                if (saving) {
                    this.loadData();
                }
            });

        this.#salesRequestsService.getSaleRequest$()
            .pipe(
                filter(saleRequest => saleRequest !== null),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(saleRequest => this.#saleRequestId = saleRequest.id);

    }

    ngOnDestroy(): void {
        this.#salesRequestsService.salesRequestPriceSimulation.clear();
    }

    changeSelectedVenueTpl(venueId: number): void {
        this.venueTplSelected = venueId;
    }

    loadData(): void {
        if (this.#expanded) {
            this.#salesRequestsService.salesRequestPriceSimulation.load(this.#saleRequestId);
        } else {
            this.venueTplSelected = null;
            this.#salesRequestsService.salesRequestPriceSimulation.clear();
        }
    }

    exportPriceSimulation(): void {
        this.#dialog.open<ExportDialogComponent, Partial<ExportDialogData>, ExportRequest>(
            ExportDialogComponent, new ObMatDialogConfig({
                exportData: exportDataPriceSimulation,
                exportFormat: ExportFormat.csv
            })
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                this.#salesRequestsService.salesRequestPriceSimulation.export(this.#saleRequestId, exportList)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this.#ephemeralMessageSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });
    }
}
