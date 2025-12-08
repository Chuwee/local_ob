import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    SeasonTicketChangeSeatPrice,
    SeasonTicketsService,
    SeasonTicketStatus
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { CurrencyInputComponent } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe, PrefixPipe } from '@admin-clients/shared/utility/pipes';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe, UpperCasePipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, computed, EventEmitter, inject, OnDestroy, OnInit, Output
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
    FormArray, FormBuilder, FormControl, FormGroup, FormGroupDirective, ReactiveFormsModule, UntypedFormGroup, Validators
} from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Subject } from 'rxjs';
import { debounceTime, filter, first, map, shareReplay, takeUntil } from 'rxjs/operators';
import { PriceTypesFilterComponent } from '../filter/price-types-filter.component';
import { PriceTypesFilter } from '../models/price-types-filter.model';
import { VmSeasonTicketChangeSeatsPrice } from '../models/vm-season-ticket-change-seats-price.model';

@Component({
    selector: 'app-change-seat-price-zone-matrix',
    templateUrl: './change-seat-price-zone-matrix.component.html',
    styleUrls: ['./change-seat-price-zone-matrix.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, ReactiveFormsModule, PrefixPipe, PriceTypesFilterComponent, ErrorIconDirective,
        ErrorMessage$Pipe, CurrencyInputComponent, LocalCurrencyPipe, MatFormField, MatLabel, MatSelect, MatOption,
        AsyncPipe, MatTable, MatColumnDef, MatHeaderCell, MatHeaderCellDef, UpperCasePipe, MatCell, MatCellDef,
        EllipsifyDirective, MatTooltip, MatIcon, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef
    ]
})
export class ChangeSeatPriceZoneMatrixComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #parentForm = inject(FormGroupDirective);
    readonly #authSrv = inject(AuthenticationService);
    readonly #seasonTicketsSrv = inject(SeasonTicketsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #onDestroy = new Subject<void>();

    readonly #seasonTicketPricesMatrixSB = new BehaviorSubject<(string | VmSeasonTicketChangeSeatsPrice)[][]>(null);
    readonly $userCurrency = toSignal(this.#authSrv.getLoggedUser$().pipe(map(user => user.currency)));
    readonly $seasonTicketCurrency = toSignal(this.#seasonTicketsSrv.seasonTicket.get$().pipe(filter(Boolean),
        map(seasonTicket => seasonTicket.currency_code)));

    // TODO Multicurrency: remove this.$userCurrency() when all operators are multicurrency
    readonly $currency = computed(() => this.$seasonTicketCurrency() ?? this.$userCurrency());

    #modifiedSeasonTicketPricesData: SeasonTicketChangeSeatPrice[] = [];
    #finalSeasonTicketPricesData: SeasonTicketChangeSeatPrice[] = [];
    #seasonTicketPriceTypes: VenueTemplatePriceType[] = [];
    #filters: Partial<PriceTypesFilter> = {};

    @Output() readonly finalPricesData = new EventEmitter<SeasonTicketChangeSeatPrice[]>();

    readonly isSeasonTicketEditable$ = this.#seasonTicketsSrv.seasonTicketStatus.get$().pipe(
        map(seasonTicketStatus => (
            !!seasonTicketStatus.status &&
            (
                seasonTicketStatus.status === SeasonTicketStatus.setUp ||
                seasonTicketStatus.status === SeasonTicketStatus.pendingPublication
            ))),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly seasonTicketRates$ = this.#seasonTicketsSrv.getSeasonTicketRates$().pipe(filter(Boolean));
    readonly seasonTicketPriceTypes$ = this.#venueTemplatesSrv.getVenueTemplatePriceTypes$().pipe(filter(Boolean));
    readonly seasonTicketPricesMatrix$ = this.#seasonTicketPricesMatrixSB.asObservable();
    readonly rateTypeControl = this.#fb.control(null);
    readonly customHeaderRow = [
        { key: 'PRICE_TYPES.ORIGIN', colspanSize: 1, styles: { color: '#47abcc' } },
        { key: 'PRICE_TYPES.TARGET', colspanSize: '100%', styles: { color: '#c154c1' } }
    ];

    get pricesTableHead(): string[] {
        return [' ', ...this.rowZones.map(zone => zone.name)];
    }

    form: UntypedFormGroup;
    rowZones = [];
    userCurrency: string;
    priceTypeNames: { [key: number]: string } = {};

    ngOnInit(): void {
        this.form = this.#parentForm.control;
        const changeSeatsPricesDataForm = this.form.controls['change_seats_prices_data_form'] as FormGroup;

        this.#authSrv.getLoggedUser$()
            .pipe(first())
            .subscribe(user => this.userCurrency = user.currency);

        // Load rates, prices and price-types (zones)
        this.#seasonTicketsSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                this.#seasonTicketsSrv.loadSeasonTicketRates(String(seasonTicket.id));
                this.#seasonTicketsSrv.seasonTicketChangeSeatPrices.load(seasonTicket.id);
                this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(seasonTicket.venue_templates?.[0].id);
            });

        // Set variables values and matrix data
        combineLatest([
            this.#seasonTicketsSrv.getSeasonTicketRates$(),
            this.#seasonTicketsSrv.seasonTicketChangeSeatPrices.get$(),
            this.#venueTemplatesSrv.getVenueTemplatePriceTypes$()
        ])
            .pipe(
                filter(data => data.every(Boolean)),
                takeUntil(this.#onDestroy)
            )
            .subscribe(([seasonTicketRates, seasonTicketPrices, seasonTicketPriceTypes]) => {
                this.rateTypeControl.setValue(seasonTicketRates[0].id, { emitEvent: false });
                this.#finalSeasonTicketPricesData = seasonTicketPrices;
                this.#seasonTicketPriceTypes = seasonTicketPriceTypes;
                this.initializePriceTypeNames();

                this.updatePricesMatrix();
                changeSeatsPricesDataForm.markAsPristine();
            });

        this.rateTypeControl.valueChanges
            .pipe(takeUntil(this.#onDestroy))
            .subscribe(() => this.updatePricesMatrix());

        changeSeatsPricesDataForm.valueChanges
            .pipe(
                debounceTime(500),
                takeUntil(this.#onDestroy)
            )
            .subscribe((actualPrices: [string, number[]]) => {
                this.updateFinalPricesData(actualPrices);
                this.finalPricesData.emit(this.#finalSeasonTicketPricesData);
            });
    }

    initializePriceTypeNames(): void {
        this.#seasonTicketPriceTypes.forEach(priceType => {
            this.priceTypeNames[priceType.id] = priceType.name;
        });
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
        this.#seasonTicketsSrv.clearSeasonTicketRates();
        this.#seasonTicketsSrv.seasonTicketChangeSeatPrices.clear();
        this.#venueTemplatesSrv.clearVenueTemplateList();
        this.#venueTemplatesSrv.clearVenueTemplateData();
    }

    filterChangeHandler(filters: Partial<PriceTypesFilter>): void {
        this.#filters = {
            ...this.#filters,
            ...filters
        };

        this.updatePricesMatrix();
    }

    private updatePricesMatrix(): void {
        // Set modified prices
        const seasonTicketPricesOrder = this.#finalSeasonTicketPricesData.filter(priceCell => {
            let isFilterValid = true;
            if (Object.keys(this.#filters).length !== 0) {
                const oriFilter = this.#filters.price_types_origin?.length > 0 ?
                    this.#filters.price_types_origin.some(priceType => priceType.id === priceCell.source_price_type_id) : true;
                const tarFilter = this.#filters.price_types_target?.length > 0 ?
                    this.#filters.price_types_target.some(priceType => priceType.id === priceCell.target_price_type_id) : true;
                isFilterValid = !!oriFilter && !!tarFilter;
            }
            return isFilterValid && priceCell;
        });

        this.#modifiedSeasonTicketPricesData = seasonTicketPricesOrder
            .filter(changeSeatPrice => changeSeatPrice.rate_id === this.rateTypeControl.value);

        // Set matrix data
        const zones = new Map();
        const changeSeatsPricesDataForm = this.form.controls['change_seats_prices_data_form'] as FormGroup;
        const seasonTicketPricesMatrix = this.#modifiedSeasonTicketPricesData
            .reduce((accSeasonTicketPricesMatrix, seasonTicketPrice) => {
                // Set price-type (zone) name on row's first position
                let priceTypeRowIndex = accSeasonTicketPricesMatrix.findIndex(row => row[0] === seasonTicketPrice.source_price_type_id);
                if (priceTypeRowIndex < 0) {
                    priceTypeRowIndex = accSeasonTicketPricesMatrix.push([seasonTicketPrice.source_price_type_id]) - 1;
                }

                // Set/Get col index
                let zoneColIndex: number;
                if (zones.has(seasonTicketPrice.target_price_type_id)) {
                    zoneColIndex = zones.get(seasonTicketPrice.target_price_type_id);
                } else {
                    zoneColIndex = zones.size + 1;
                    changeSeatsPricesDataForm.addControl(String(seasonTicketPrice.target_price_type_id), this.#fb.array([]),
                        { emitEvent: false });
                    zones.set(seasonTicketPrice.target_price_type_id, zoneColIndex);
                }

                // Set cell values
                const zonePrices = changeSeatsPricesDataForm.get([seasonTicketPrice.target_price_type_id]) as FormArray;
                let priceFormControl: FormControl;
                if (zonePrices.at(priceTypeRowIndex)) {
                    priceFormControl = zonePrices.at(priceTypeRowIndex) as FormControl;
                    priceFormControl.setValue(seasonTicketPrice.value, { emitEvent: false });
                } else {
                    priceFormControl = this.#fb.control(seasonTicketPrice.value, [Validators.required]);
                    zonePrices.insert(priceTypeRowIndex, priceFormControl, { emitEvent: false });
                }

                accSeasonTicketPricesMatrix[priceTypeRowIndex][zoneColIndex] = {
                    initValue: seasonTicketPrice.value,
                    relationId: seasonTicketPrice.relation_id,
                    sourcePriceTypeId: seasonTicketPrice.source_price_type_id,
                    targetPriceTypeId: seasonTicketPrice.target_price_type_id,
                    ctrl: priceFormControl
                };
                return accSeasonTicketPricesMatrix;
            }, []);

        this.rowZones = Array.from(zones, ([zoneId]) => ({
            id: zoneId,
            name: this.#seasonTicketPriceTypes.find(priceType => priceType.id === zoneId).name
        }));

        this.#seasonTicketPricesMatrixSB.next([...seasonTicketPricesMatrix]);
    }

    private updateFinalPricesData(actualPrices: [string, number[]]): void {
        const rateTypeId = this.rateTypeControl.value;
        const pricesFiltered = this.#modifiedSeasonTicketPricesData.filter(changeSeatPrice => changeSeatPrice.rate_id === rateTypeId);
        let targetPrices: SeasonTicketChangeSeatPrice[] = [];
        Object.entries(actualPrices).forEach(column => {
            const targetId = Number(column[0]);
            const prices = column[1] as number[];
            targetPrices = pricesFiltered.filter(changeSeatPrice => changeSeatPrice.target_price_type_id === targetId);

            prices.forEach((price, index) => {
                if (targetPrices[index]) {
                    targetPrices[index].value = price;
                }
            });
        });
    }
}
