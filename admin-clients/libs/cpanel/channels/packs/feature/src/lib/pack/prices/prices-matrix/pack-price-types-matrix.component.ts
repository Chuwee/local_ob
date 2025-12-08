import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { PackPrice, PackRate, PacksService, PutPackPrice, VmPackPrice } from '@admin-clients/cpanel/channels/packs/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { eventsProviders, PutEventPrice } from '@admin-clients/cpanel/promoters/events/data-access';
import { CurrencyInputComponent, DialogSize, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatePriceType, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { priceLimitsForWarning } from '@admin-clients/shared-utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import {
    FormGroup, ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, Validators
} from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import { catchError, filter, finalize, first, map, switchMap } from 'rxjs/operators';

@Component({
    selector: 'app-pack-price-types-matrix',
    templateUrl: './pack-price-types-matrix.component.html',
    styleUrls: ['./pack-price-types-matrix.component.scss'],
    imports: [
        ReactiveFormsModule, MatInputModule, MatSelectModule, MatTooltipModule, MatTableModule, TranslatePipe,
        MatIconModule, CurrencyInputComponent, AsyncPipe, LocalCurrencyPipe, MatProgressSpinnerModule,
        ErrorMessage$Pipe, ErrorIconDirective, EllipsifyDirective
    ],
    providers: [eventsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPriceTypesMatrixComponent implements OnInit, OnDestroy {
    readonly #packsSrv = inject(PacksService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #onDestroy = inject(DestroyRef);
    readonly #authSrv = inject(AuthenticationService);
    readonly #localCurrencyPipe = inject(LocalCurrencyPipe);

    readonly #cancelBtnClickSbj = new BehaviorSubject<void>(null);
    readonly #packPricesMatrixSB = new BehaviorSubject<(string | VmPackPrice)[][]>(null);
    #packPricesMatrix: (string | VmPackPrice)[][] = [];
    #packPriceType: VenueTemplatePriceType[] = [];

    readonly $channel = toSignal(this.#channelsSrv.getChannel$().pipe(filter(Boolean)));
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(filter(Boolean)));

    readonly $loading = toSignal(booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#packsSrv.packItems.loading$(),
        this.#venueTemplatesSrv.venueTpl.inProgress$(),
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#packsSrv.packPrices.inProgress$(),
        this.#packsSrv.packRates.inProgress$()
    ]));

    readonly $venueTemplate = toSignal(this.#venueTemplatesSrv.venueTpl.get$().pipe(first(Boolean)));

    readonly packPricesMatrix$ = this.#packPricesMatrixSB.asObservable()
        .pipe(filter(value => value !== null));

    readonly pricesMatrix = this.#fb.group({});
    readonly pricesForm = this.#fb.group({
        pricesMatrix: this.pricesMatrix
    });

    readonly $currencyCode = toSignal(this.#authSrv.getLoggedUser$().pipe(
        first(Boolean),
        map(user => user.currency)
    ));

    readonly rates$ = this.#packsSrv.packRates.get$().pipe(filter(Boolean));

    rateNames: string[] = [];

    @Input() saveBtnClicked$: Observable<void>;
    @Input() set form(value: FormGroup) {
        value.removeControl('priceTypesRatesMatrix');
        value.addControl('priceTypesRatesMatrix', this.pricesForm, { emitEvent: false });
    }

    get pricesTableHead(): string[] {
        return ['PRICE_TYPE', ...this.rateNames];
    }

    ngOnInit(): void {
        this.#venueTemplatesSrv.venueTpl.clear();
        this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
        this.#packsSrv.packPrices.clear();
        this.#packsSrv.packRates.clear();

        this.#packsSrv.packItems.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(items => {
            const mainItem = items.find(item => item.main);
            const template = (mainItem.type === 'EVENT') ? mainItem.event_data.venue_template : mainItem.session_data.venue_template;
            this.#venueTemplatesSrv.venueTpl.load(template.id);
            this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(template.id);
        });

        // mapeamos el modelo de precios y orden a una matriz de FormControl para la tabla
        combineLatest([
            this.#packsSrv.packPrices.get$(),
            this.#venueTemplatesSrv.getVenueTemplatePriceTypes$(),
            this.rates$,
            this.#cancelBtnClickSbj
        ]).pipe(
            filter(([eventPrices, priceTypes, rates]) => !!eventPrices && !!priceTypes && !!rates),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([packPrices, priceTypes, rates]) => {
            const vmPackPrices = this.setVmPackPrices(packPrices, priceTypes, rates);
            this.#packPriceType = vmPackPrices.map(packPrice => {
                const packData = packPrice[1] as VmPackPrice;
                // the check must give always true, but avet is the special one breaking rules.
                if (packData) {
                    return { id: packData.priceTypeId };
                } else {
                    return null;
                }
            });
            this.#packPricesMatrixSB.next([...vmPackPrices]);
        });

        this.saveBtnClicked$
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                if (this.pricesForm.invalid) {
                    this.pricesForm.markAllAsTouched();
                    this.#ref.markForCheck();
                }
            });
    }

    ngOnDestroy(): void {
        this.#venueTemplatesSrv.venueTpl.clear();
        this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
        this.#packsSrv.packPrices.clear();
        this.#packsSrv.packRates.clear();
    }

    cancel(): void {
        this.#cancelBtnClickSbj.next(null);
    }

    savePrices(): Observable<void> {
        const modifiedPrices: PutEventPrice[] = [];
        return this.updatePriceZonesValues(modifiedPrices);
    }

    private setVmPackPrices(
        packPrices: PackPrice[], priceTypes?: VenueTemplatePriceType[], packRates?: PackRate[]
    ): (string | VmPackPrice)[][] {
        const controlKeys = Object.keys(this.pricesMatrix.controls);
        controlKeys.forEach(controlKey => this.pricesMatrix.removeControl(controlKey));
        const rates = new Map<string, number>();

        if (!packPrices.length) {
            priceTypes.forEach(priceType => {
                const newPrice = {
                    price_type: {
                        id: priceType.id,
                        code: priceType.code,
                        description: priceType.name
                    },
                    rate: {
                        id: packRates.length ? packRates[0].id : null, //TODO: Modify when we have more rates
                        name: packRates.length ? packRates[0].name : this.$pack().name
                    },
                    value: 0
                };
                packPrices.push(newPrice);
            });
        }

        this.#packPricesMatrix = packPrices.reduce((accPackPricesMatrix, packPrice) => {
            let priceTypeRowIndex = accPackPricesMatrix.findIndex(row => row[0] === packPrice.price_type.description);
            if (priceTypeRowIndex < 0) {
                priceTypeRowIndex = accPackPricesMatrix.push([packPrice.price_type.description]) - 1;
            }
            let colIndex: number;
            if (rates.has(packPrice.rate.name)) {
                colIndex = rates.get(packPrice.rate.name);
            } else {
                colIndex = rates.size + 1;
                this.pricesMatrix.addControl(packPrice.rate.name, this.#fb.array([]));
                rates.set(packPrice.rate.name, colIndex);
            }
            const ratePrices = this.pricesMatrix.get([packPrice.rate.name]) as UntypedFormArray;

            while (!ratePrices.at(priceTypeRowIndex)) {
                const ctrl = this.#fb.control(0, [Validators.required, Validators.min(0)]);
                ratePrices.push(ctrl);
            }

            const priceFormControl = ratePrices.at(priceTypeRowIndex) as UntypedFormControl;
            priceFormControl.setValue(packPrice.value, { emitEvent: false });

            accPackPricesMatrix[priceTypeRowIndex][colIndex] = {
                initValue: packPrice.value,
                rateId: packPrice.rate.id,
                priceTypeId: packPrice.price_type.id,
                ctrl: priceFormControl
            };
            return accPackPricesMatrix;
        }, []);
        this.rateNames = Array.from(rates.keys());
        this.pricesMatrix.markAsPristine();

        return this.#packPricesMatrix;
    }

    private updatePriceZonesValues(modifiedPrices: PutPackPrice[]): Observable<void> {
        if (this.pricesMatrix.valid) {
            if (this.pricesMatrix.dirty) {
                modifiedPrices = this.#packPricesMatrix.reduce<PutPackPrice[]>((result, row) => {
                    for (let i = 1; i < row.length; i++) {
                        const elem = row[i] as VmPackPrice;
                        if (elem.initValue !== elem.ctrl.value) {
                            result.push({
                                price_type_id: elem.priceTypeId,
                                rate_id: elem.rateId,
                                value: elem.ctrl.value
                            });
                        }
                    }
                    return result;
                }, []);
            }
        } else {
            this.pricesForm.markAllAsTouched();
            this.#ref.markForCheck();
            return throwError(() => new Error('Invalid form'));
        }

        if (modifiedPrices.length) {
            const minimumPriceForThisCurrency = priceLimitsForWarning.find(price => price.currency === this.$currencyCode())?.limit;
            const shouldShowWarning = modifiedPrices.some(price => price.value < minimumPriceForThisCurrency);
            const priceWithCurrencyCode = this.#localCurrencyPipe.transform(minimumPriceForThisCurrency, this.$currencyCode());

            if (shouldShowWarning) {
                return this.#messageDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'EVENTS.SOME_RATE_BELOW_LIMIT_WARNING_TITLE',
                    message: 'EVENTS.SOME_RATE_BELOW_LIMIT_WARNING_DESCRIPTION',
                    messageParams: { price: priceWithCurrencyCode },
                    actionLabel: 'FORMS.ACTIONS.CONTINUE',
                    showCancelButton: true
                })
                    .pipe(filter(Boolean), switchMap(() => this.#createRate(modifiedPrices)));
            } else {
                return this.#createRate(modifiedPrices);
            }
        }
        return of(null);
    }

    #createRate(modifiedPrices: PutPackPrice[]): Observable<void> {
        //Create rate if it doesn't exist and add id to prices
        if (modifiedPrices[0].rate_id === null) {
            const rate = {
                name: this.$pack().name,
                default: true,
                restrictive_access: false
            };
            return this.#packsSrv.packRates.create(this.$channel().id, this.$pack().id, rate)
                .pipe(filter(Boolean), takeUntilDestroyed(this.#onDestroy), switchMap(rate => {
                    modifiedPrices.forEach(price => price.rate_id = rate.id);
                    return this.#save(modifiedPrices);
                }));
        } else {
            return this.#save(modifiedPrices);
        }
    }

    #save(modifiedPrices: PutPackPrice[]): Observable<void> {
        return this.#packsSrv.packPrices.update(this.$channel().id, this.$pack().id, modifiedPrices)
            .pipe(
                catchError(error => throwError(error)),
                finalize(() => {
                    this.#packsSrv.packPrices.load(this.$channel().id, this.$pack().id);
                    this.#packsSrv.packRates.load(this.$channel().id, this.$pack().id);
                    this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.$venueTemplate().id);
                }));
    }
}
