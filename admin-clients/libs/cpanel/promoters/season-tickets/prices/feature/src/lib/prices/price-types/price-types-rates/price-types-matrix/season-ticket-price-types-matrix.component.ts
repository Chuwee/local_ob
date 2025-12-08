import {
    PutSeasonTicketPrice, SeasonTicketPrice, SeasonTicketStatus, SeasonTicketsService, VmSeasonTicketPrice
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { PriceTypeTranslationsComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import {
    CurrencyInputComponent, DialogSize, MessageDialogService, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import {
    ActivityTicketType, VenueTemplate, VenueTemplatePriceType, VenueTemplatesService
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, Input, OnDestroy, OnInit, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators
} from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, TabsMenuComponent, FlexLayoutModule, MaterialModule, CommonModule, DragDropModule, TabDirective,
        TranslatePipe, EllipsifyDirective, CurrencyInputComponent, LocalCurrencyPipe, PriceTypeTranslationsComponent,
        ErrorMessage$Pipe, ErrorIconDirective
    ],
    selector: 'app-season-ticket-price-types-matrix',
    templateUrl: './season-ticket-price-types-matrix.component.html',
    styleUrls: ['./season-ticket-price-types-matrix.component.scss']
})
export class SeasonTicketPriceTypesMatrixComponent implements OnInit, OnDestroy {
    private readonly _seasonTicketsSrv = inject(SeasonTicketsService);
    private readonly _venueTemplatesSrv = inject(VenueTemplatesService);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ref = inject(ChangeDetectorRef);
    private readonly _destroyRef = inject(DestroyRef);

    private readonly _cancelBtnClickSbj = new BehaviorSubject<void>(null);
    private readonly _seasonTicketPricesMatrixSB = new BehaviorSubject<(string | VmSeasonTicketPrice)[][]>(null);
    private _seasonTicketPriceType: VenueTemplatePriceType[] = [];
    private _seasonTicketPricesMatrix: (string | VmSeasonTicketPrice)[][] = [];

    @ViewChild(PriceTypeTranslationsComponent) private readonly _priceTypeTranslations: PriceTypeTranslationsComponent;

    readonly languages$ = this._seasonTicketsSrv.seasonTicket.get$()
        .pipe(filter(Boolean), map(seasonTicket => seasonTicket.settings?.languages?.selected));

    readonly venueTemplate$ = this._seasonTicketsSrv.seasonTicket.get$()
        .pipe(filter(Boolean), map(seasonTicket => seasonTicket.venue_templates?.[0] as VenueTemplate));

    readonly isSeasonTicketEditable$ = this._seasonTicketsSrv.seasonTicketStatus.get$()
        .pipe(
            map(seasonTicketStatus => (
                !!seasonTicketStatus.status &&
                (
                    seasonTicketStatus.status === SeasonTicketStatus.setUp ||
                    seasonTicketStatus.status === SeasonTicketStatus.pendingPublication
                ))),
            shareReplay(1)
        );

    readonly seasonTicketPricesMatrix$ = this._seasonTicketPricesMatrixSB.asObservable()
        .pipe(filter(value => value !== null));

    readonly channelContentsGroup = this._fb.group({});
    readonly pricesMatrix = this._fb.group({});
    readonly draggableTable = this._fb.group({ enabled: false });
    readonly pricesForm = this._fb.group({
        pricesMatrix: this.pricesMatrix,
        draggableTable: this.draggableTable
    });

    readonly currency$ = this._seasonTicketsSrv.seasonTicket.get$()
        .pipe(map(seasonTicket => seasonTicket.currency_code));

    rateNames: string[] = [];
    defaultRatesMap: { [key: string]: boolean } = {};

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('priceTypesRatesMatrix') && value.contains('channelContents')) {
            return;
        }
        value.addControl('priceTypesRatesMatrix', this.pricesForm, { emitEvent: false });
        value.addControl('channelContents', this.channelContentsGroup, { emitEvent: false });
    }

    @Input() saveBtnClicked$: Observable<void>;

    get pricesTableHead(): string[] {
        return ['PRICE_TYPE', ...this.rateNames];
    }

    ngOnInit(): void {
        this._venueTemplatesSrv.clearVenueTemplateList();
        this._venueTemplatesSrv.clearVenueTemplateData();
        this._venueTemplatesSrv.clearVenueTemplatePriceTypes();
        this._seasonTicketsSrv.clearSeasonTicketRates();
        this._seasonTicketsSrv.clearSeasonTicketPrices();

        //Load rates
        this._seasonTicketsSrv.seasonTicketStatus.isGenerationStatusReady$()
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(isGenerationStatusReady => {
                if (!isGenerationStatusReady) return;

                this._seasonTicketsSrv.seasonTicket.get$()
                    .pipe(first())
                    .subscribe(st => {
                        this._seasonTicketsSrv.loadSeasonTicketRates(String(st.id));
                        this._seasonTicketsSrv.loadSeasonTicketPrices(st.id);
                        this._venueTemplatesSrv.loadVenueTemplatePriceTypes(st.venue_templates?.[0].id);
                    });
            });

        // Season Ticket Price zones from Season Ticket Rates
        this._seasonTicketsSrv.getSeasonTicketRates$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(rates => {
                this.defaultRatesMap = {};
                rates.forEach(rate => this.defaultRatesMap[rate.name] = rate.default);
            });

        // Set and update matrix
        combineLatest([
            this._seasonTicketsSrv.getSeasonTicketPrices$(),
            this._venueTemplatesSrv.getVenueTemplatePriceTypes$(),
            this._cancelBtnClickSbj
        ]).pipe(
            filter(([seasonTicketPrices, seasonTicketPriceTypes]) => !!seasonTicketPrices && !!seasonTicketPriceTypes),
            takeUntilDestroyed(this._destroyRef)
        ).subscribe(([seasonTicketPrices, seasonTicketPriceTypes]) => {
            const vmSeasonTicketPrices = this.setVmSeasonTicketPrices(seasonTicketPrices, seasonTicketPriceTypes);
            this._seasonTicketPriceType = this.getSeasonTicketPriceType(vmSeasonTicketPrices);
            this.enableDraggableTable(vmSeasonTicketPrices);
            this._seasonTicketPricesMatrixSB.next([...vmSeasonTicketPrices]);
            this.draggableTable.markAsPristine();
        });

        // Save button action stream, from parent
        this.saveBtnClicked$
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(() => {
                if (this.pricesForm.invalid) {
                    this.pricesForm.markAllAsTouched();
                    this._ref.markForCheck();
                }
            });
    }

    ngOnDestroy(): void {
        this._seasonTicketsSrv.clearSeasonTicketRates();
        this._seasonTicketsSrv.clearSeasonTicketPrices();
        this._venueTemplatesSrv.clearVenueTemplateList();
        this._venueTemplatesSrv.clearVenueTemplateData();
    }

    cancel(): void {
        this._cancelBtnClickSbj.next(null);
        this._priceTypeTranslations?.reset();
    }

    savePrices(): Observable<unknown>[] {
        const modifiedPrices: PutSeasonTicketPrice[] = [];
        const savePrices$: Observable<unknown>[] = [];
        this.updatePriceZonesOrder(savePrices$);
        this.updatePriceZonesValues(savePrices$, modifiedPrices);
        if (this._priceTypeTranslations && this.channelContentsGroup.dirty) {
            savePrices$.push(this._priceTypeTranslations.save());
        }
        return savePrices$;
    }

    onListDrop(event: CdkDragDrop<string[]>): void {
        this.seasonTicketPricesMatrix$
            .pipe(first())
            .subscribe(seasonTicketPrices => {
                if (event) {
                    const previousIndex = seasonTicketPrices.findIndex(seasonTicketPrice => seasonTicketPrice === event.item.data);
                    moveItemInArray(seasonTicketPrices, previousIndex, event.currentIndex);
                }
                this._seasonTicketPriceType = seasonTicketPrices.map((seasonTicketPrice, index) => {
                    const seasonTicketData = seasonTicketPrice[1] as VmSeasonTicketPrice;
                    return { id: seasonTicketData.priceTypeId, priority: index + 1, ticketType: ActivityTicketType.individual };
                });
                this._seasonTicketPricesMatrixSB.next([...seasonTicketPrices]);
                this.draggableTable.markAsDirty();
            });
    }

    onDragChange(isEnabled: boolean): void {
        if (!isEnabled) {
            this._messageDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'EVENTS.DRAG_MODAL_OPTION_TITLE',
                message: 'EVENTS.DRAG_MODAL_OPTION_INFO',
                actionLabel: 'FORMS.ACTIONS.YES',
                showCancelButton: true
            })
                .subscribe(isConfirmed => {
                    this._seasonTicketsSrv.getSeasonTicketPrices$()
                        .pipe(first())
                        .subscribe(seasonTicketPrices => {
                            if (!isConfirmed) {
                                this.draggableTable.get('enabled').setValue(true);
                            } else {
                                const vmSeasonTicketPrices = this.setVmSeasonTicketPrices(seasonTicketPrices);
                                this._seasonTicketPriceType = vmSeasonTicketPrices.map((seasonTicketPrice, index) => {
                                    const seasonTicketData = seasonTicketPrice[1] as VmSeasonTicketPrice;
                                    return {
                                        id: seasonTicketData.priceTypeId, priority: index + 1,
                                        ticketType: ActivityTicketType.individual
                                    };
                                });
                                this._seasonTicketPricesMatrixSB.next([...vmSeasonTicketPrices]);
                            }
                        });
                });
        }
    }

    // mapeamos el modelo de precios a una matriz de FormControl para la tabla
    private setVmSeasonTicketPrices(
        seasonTicketPrices: SeasonTicketPrice[],
        seasonTicketPriceTypes?: VenueTemplatePriceType[]
    ): (string | VmSeasonTicketPrice)[][] {
        const controlKeys = Object.keys(this.pricesMatrix.controls);
        controlKeys.forEach(controlKey => this.pricesMatrix.removeControl(controlKey));
        const rates = new Map();
        const orderedSeasonTicketPrices = seasonTicketPriceTypes ?
            this.getOrderedPrices(seasonTicketPrices, seasonTicketPriceTypes) :
            seasonTicketPrices;
        this._seasonTicketPricesMatrix = orderedSeasonTicketPrices.reduce((accSeasonTicketPricesMatrix, seasonTicketPrice) => {
            // Set priority
            const seasonTicketPriceType = seasonTicketPriceTypes ?
                seasonTicketPriceTypes.find(seasonTicketPriceType => seasonTicketPriceType.id === seasonTicketPrice.price_type.id)
                : null;
            const priority = seasonTicketPriceTypes && seasonTicketPriceType ? seasonTicketPriceType.priority : 0;
            // Set price Matrix
            let priceTypeRowIndex = accSeasonTicketPricesMatrix.findIndex(row => row[0] === seasonTicketPrice.price_type.description);
            if (priceTypeRowIndex < 0) {
                priceTypeRowIndex = accSeasonTicketPricesMatrix.push([seasonTicketPrice.price_type.description]) - 1;
            }
            let rateColIndex: number;
            if (rates.has(seasonTicketPrice.rate.name)) {
                rateColIndex = rates.get(seasonTicketPrice.rate.name);
            } else {
                rateColIndex = rates.size + 1;
                this.pricesMatrix.addControl(seasonTicketPrice.rate.name, this._fb.array([]));
                rates.set(seasonTicketPrice.rate.name, rateColIndex);
            }
            const ratePrices = this.pricesMatrix.get([seasonTicketPrice.rate.name]) as UntypedFormArray;
            let priceFormControl: UntypedFormControl;
            if (ratePrices.at(priceTypeRowIndex)) {
                priceFormControl = ratePrices.at(priceTypeRowIndex) as UntypedFormControl;
                priceFormControl.setValue(seasonTicketPrice.value, { emitEvent: false });
            } else {
                priceFormControl = this._fb.control(seasonTicketPrice.value, [Validators.required, Validators.min(0)]);
                ratePrices.insert(priceTypeRowIndex, priceFormControl);
            }
            // Set Result
            accSeasonTicketPricesMatrix[priceTypeRowIndex][rateColIndex] = {
                initValue: seasonTicketPrice.value,
                rateId: seasonTicketPrice.rate.id,
                priceTypeId: seasonTicketPrice.price_type.id,
                ctrl: priceFormControl,
                priority
            };

            return accSeasonTicketPricesMatrix;
        }, []);
        this.rateNames = Array.from(rates.keys());
        this.pricesMatrix.markAsPristine();

        return this._seasonTicketPricesMatrix;
    }

    private getOrderedPrices(
        seasonTicketPrices: SeasonTicketPrice[],
        seasonTicketPricesType: VenueTemplatePriceType[]
    ): SeasonTicketPrice[] {
        const seasonTicketPricesOrder = seasonTicketPrices.map(seasonTicketPrice => {
            const seasonTicketPriceType = seasonTicketPricesType.find(priceType => priceType.id === seasonTicketPrice.price_type.id);
            const priority = seasonTicketPricesType && seasonTicketPriceType ? seasonTicketPriceType.priority : 0;
            return { seasonTicketPrice, priority };
        });
        return seasonTicketPricesOrder
            .sort((a, b) =>
                a.priority ? a.priority - b.priority : null)
            .map(current => current.seasonTicketPrice);
    }

    private enableDraggableTable(vmSeasonTicketPrice: (string | VmSeasonTicketPrice)[][]): void {
        const isEnabled = vmSeasonTicketPrice.some(seasonTicketPrice => {
            const seasonTicketData = seasonTicketPrice[1] as VmSeasonTicketPrice;
            return seasonTicketData?.priority !== 0;
        });
        this.draggableTable.get('enabled').setValue(isEnabled);
    }

    private getSeasonTicketPriceType(
        vmSeasonTicketPrice: (string | VmSeasonTicketPrice)[][]
    ): VenueTemplatePriceType[] {
        return vmSeasonTicketPrice.map(seasonTicketPrice => {
            const seasonTicketData = seasonTicketPrice[1] as VmSeasonTicketPrice;
            return { id: seasonTicketData?.priceTypeId, priority: seasonTicketData?.priority, ticketType: ActivityTicketType.individual };
        });
    }

    private updatePriceZonesOrder(savePrices$: Observable<unknown>[]): void {
        this._seasonTicketsSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                // Draggable Table
                if (this.draggableTable.dirty || this.draggableTable.get('enabled').value && this._seasonTicketPriceType.length) {
                    this._seasonTicketPriceType
                        .forEach((seasonTicketPriceType, index) => {
                            if (!this.draggableTable.value.enabled) {
                                seasonTicketPriceType.priority = 0;
                            }
                            if (this._seasonTicketPriceType.length - 1 === index) {
                                savePrices$.push(this._venueTemplatesSrv.updateVenueTemplatePriceType(
                                    seasonTicket.venue_templates[0].id,
                                    seasonTicketPriceType
                                ).pipe(
                                    tap(() =>
                                        this._venueTemplatesSrv.loadVenueTemplatePriceTypes(
                                            seasonTicket.venue_templates[0].id
                                        )
                                    )));
                            } else {
                                savePrices$.push(this._venueTemplatesSrv.updateVenueTemplatePriceType(
                                    seasonTicket.venue_templates[0].id,
                                    seasonTicketPriceType
                                ));
                            }
                        });
                }
            });
    }

    private updatePriceZonesValues(
        savePrices$: Observable<unknown>[],
        modifiedPrices: PutSeasonTicketPrice[]
    ): void {
        this._seasonTicketsSrv.seasonTicket.get$()
            .pipe(first())
            .subscribe(seasonTicket => {
                // Modify Prices
                if (this.pricesMatrix.valid) {
                    if (this.pricesMatrix.dirty) {
                        modifiedPrices = this._seasonTicketPricesMatrix.reduce<PutSeasonTicketPrice[]>(
                            (result, row) => {
                                for (let i = 1; i < row.length; i++) {
                                    const elem = row[i] as VmSeasonTicketPrice;
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
                    this._ref.markForCheck();
                }
                if (modifiedPrices.length) {
                    savePrices$.push(this._seasonTicketsSrv.saveSeasonTicketPrices(seasonTicket.id, modifiedPrices)
                        .pipe(tap(() => this._seasonTicketsSrv.loadSeasonTicketPrices(seasonTicket.id))));
                }
            });
    }

}
