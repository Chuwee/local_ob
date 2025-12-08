import {
    EventPrice, EventsService, EventStatus, PutEventPrice
} from '@admin-clients/cpanel/promoters/events/data-access';
import { VmEventPrice } from '@admin-clients/cpanel-promoters-events-prices-data-access';
import { IsItalianComplianceEventPipe } from '@admin-clients/cpanel-promoters-events-utils';
import { PriceTypeTranslationsComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import {
    CurrencyInputComponent, DialogSize, MessageDialogService, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import {
    ActivityTicketType, VenueTemplatePriceType, VenueTemplatesService, VenueTemplateStatus
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CdkDrag, CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, Input, OnDestroy, OnInit, viewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    ReactiveFormsModule, UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTableModule } from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import { catchError, filter, finalize, first, map, shareReplay } from 'rxjs/operators';
import { EventPriceTypeRestrictionsComponent } from './restrictions/event-price-type-restrictions.component';

@Component({
    selector: 'app-event-price-types-matrix',
    templateUrl: './event-price-types-matrix.component.html',
    styleUrls: ['./event-price-types-matrix.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        AsyncPipe, ReactiveFormsModule, MatFormFieldModule, MatSelectModule, MatTooltip,
        TranslatePipe, TabsMenuComponent, TabDirective, MatTableModule, MatSlideToggle,
        MatIcon, CurrencyInputComponent, LocalCurrencyPipe, ErrorMessage$Pipe, EllipsifyDirective,
        ErrorIconDirective, PriceTypeTranslationsComponent, EventPriceTypeRestrictionsComponent,
        CdkDrag, DragDropModule
    ]
})
export class EventPriceTypesMatrixComponent implements OnInit, OnDestroy {
    readonly #eventsSrv = inject(EventsService);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #isItalianComplianceEventPipe = inject(IsItalianComplianceEventPipe);
    readonly #destroyRef = inject(DestroyRef);

    readonly #event$ = this.#eventsSrv.event.get$();
    readonly #eventPrices$ = this.#eventsSrv.eventPrices.get$().pipe(filter(Boolean));

    readonly #eventPricesMatrixSB = new BehaviorSubject<(string | VmEventPrice)[][]>(null);
    readonly #cancelBtnClickSbj = new BehaviorSubject<void>(null);

    private readonly _priceTypeTranslations = viewChild(PriceTypeTranslationsComponent);

    #eventPricesMatrix: (string | VmEventPrice)[][] = [];
    #eventPriceType: VenueTemplatePriceType[] = [];

    readonly languages$ = this.#event$
        .pipe(
            filter(Boolean),
            map(event => event.settings?.languages?.selected)
        );

    readonly venueTplCtrl = this.#fb.control(null);

    // seteamos en el dropdown el primer venueTpl de la lista
    readonly venueTemplates$ = this.#venueTemplatesSrv.getVenueTemplatesList$()
        .pipe(
            filter(Boolean),
            map(value => value.data)
        );

    readonly isEventEditable$ = this.#event$.pipe(
        map(event =>
            // TODO: Más adelante este flag será más genérico y será el backend el que haga estas comprobaciones
            this.#isItalianComplianceEventPipe.transform(event) ? event?.status === EventStatus.inProgramming : (
                event.status === EventStatus.inProgramming ||
                event.status === EventStatus.planned ||
                event.status === EventStatus.ready
            )
        ),
        shareReplay(1)
    );

    enableRestrictionTab = false;

    readonly eventPricesMatrix$ = this.#eventPricesMatrixSB.asObservable().pipe(filter(value => value !== null));

    readonly channelContentsGroup = this.#fb.group({});
    readonly pricesMatrix = this.#fb.group({});
    readonly draggableTable = this.#fb.group({ enabled: false });
    readonly pricesForm = this.#fb.group({
        pricesMatrix: this.pricesMatrix,
        draggableTable: this.draggableTable
    });

    readonly currency$ = this.#eventsSrv.event.get$()
        .pipe(map(event => event.currency_code));

    readonly compareWith = compareWithIdOrCode;

    defaultRatesMap: { [key: string]: boolean } = {};
    rateNames: string[] = [];

    @Input() tplId: number;
    @Input() set form(value: UntypedFormGroup) {
        if (!value || (value.contains('priceTypesRatesMatrix') && value.contains('channelContents'))) {
            return;
        }
        value.addControl('priceTypesRatesMatrix', this.pricesForm, { emitEvent: false });
        value.addControl('channelContents', this.channelContentsGroup, { emitEvent: false });
    }

    @Input() set formGroupPrices(value: UntypedFormGroup) {
        if (!value || (value.contains('priceTypesRatesMatrixGroup'))) {
            return;
        }
        value.addControl('priceTypesRatesMatrixGroup', this.pricesForm, { emitEvent: false });
    }

    @Input() saveBtnClicked$: Observable<void>;
    @Input() isActivity = false;
    @Input() groupMode = false;

    get pricesTableHead(): string[] {
        return [this.isActivity ? 'TICKET_TYPES' : 'PRICE_TYPE', ...this.rateNames];
    }

    ngOnInit(): void {
        this.#venueTemplatesSrv.clearVenueTemplateList();
        this.#venueTemplatesSrv.clearVenueTemplateData();
        this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
        this.#eventsSrv.eventPrices.clear();
        this.#eventsSrv.eventRates.clear();

        // con el event model obtenemos los VenueTemplate[]
        this.#event$
            .pipe(first())
            .subscribe(event => {
                this.#eventsSrv.eventRates.load(String(event.id));
                this.#venueTemplatesSrv.loadVenueTemplatesList({
                    limit: 999, offset: 0, sort: 'name:asc', eventId: event.id, status: [VenueTemplateStatus.active]
                });
            });

        this.#eventsSrv.eventRates.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(rates => {
                this.defaultRatesMap = {};
                rates.forEach(rate => this.defaultRatesMap[rate.name] = rate.default);
            });

        if (!this.groupMode) {
            this.venueTemplates$
                .pipe(first(Boolean))
                .subscribe(venueTpls => {
                    this.venueTplCtrl.setValue(venueTpls?.[0]);
                });

            // group mode uses the same data, but group mode appears next to non group mode instance, and with one load is enough,
            // tplId is updated here for non group mode, in group mode is setted by non group mode instance
            combineLatest([
                this.venueTplCtrl.valueChanges,
                this.#eventsSrv.eventRates.get$() //only for trigger use
            ])
                .pipe(
                    filter(values => values.every(value => !!value)),
                    map(([valueChanges]) => valueChanges),
                    takeUntilDestroyed(this.#destroyRef)
                )
                .subscribe(venueTpl => {
                    this.tplId = venueTpl.id;
                    this.#event$
                        .pipe(first())
                        .subscribe(event => this.#eventsSrv.eventPrices.load(String(event.id), venueTpl.id));
                    this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
                    this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(venueTpl.id);
                });
        }
        // mapeamos el modelo de precios y orden a una matriz de FormControl para la tabla
        combineLatest([
            this.#eventPrices$,
            this.#venueTemplatesSrv.getVenueTemplatePriceTypes$(),
            this.#cancelBtnClickSbj
        ]).pipe(
            filter(([eventPrices, eventPriceTypes]) => !!eventPrices && !!eventPriceTypes),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([eventPrices, eventPriceTypes]) => {
            this.enableRestrictionTab = eventPriceTypes.length > 1;
            const vmEventPrices = this.setVmEventPrices(eventPrices, eventPriceTypes);
            const ticketType = this.groupMode ? ActivityTicketType.group : ActivityTicketType.individual;
            this.#eventPriceType = vmEventPrices.map(eventPrice => {
                const eventData = eventPrice[1] as VmEventPrice;
                // the check must give always true, but avet is the special one breaking rules.
                if (eventData) {
                    if (!this.groupMode) {
                        this.draggableTable.get('enabled').setValue(!!eventData.priority);
                    }
                    return { id: eventData.priceTypeId, priority: eventData.priority, ticketType };
                } else {
                    return null;
                }
            });
            this.#eventPricesMatrixSB.next([...vmEventPrices]);
            this.draggableTable.markAsPristine();
        });

        this.saveBtnClicked$
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => {
                if (this.pricesForm.invalid) {
                    this.pricesForm.markAllAsTouched();
                    this.#ref.markForCheck();
                }
            });
    }

    ngOnDestroy(): void {
        this.#venueTemplatesSrv.clearVenueTemplateList();
        this.#venueTemplatesSrv.clearVenueTemplateData();
        this.#eventsSrv.eventPrices.clear();
        this.#eventsSrv.eventRates.clear();
    }

    cancel(): void {
        this.#cancelBtnClickSbj.next(null);
        this._priceTypeTranslations()?.reset();
    }

    savePrices(): Observable<unknown>[] {
        const modifiedPrices: PutEventPrice[] = [];
        const savePrices$: Observable<unknown>[] = [];
        this.updatePriceZonesOrder(savePrices$);
        this.updatePriceZonesValues(savePrices$, modifiedPrices);
        if (this._priceTypeTranslations() && this.channelContentsGroup.dirty) {
            savePrices$.push(this._priceTypeTranslations().save());
        }
        return savePrices$;
    }

    onListDrop(event: CdkDragDrop<string[]>): void {
        this.eventPricesMatrix$
            .pipe(first())
            .subscribe(eventPrices => {
                if (event) {
                    const previousIndex = eventPrices.findIndex(eventPrice => eventPrice === event.item.data);
                    moveItemInArray(eventPrices, previousIndex, event.currentIndex);
                }
                this.#eventPriceType = eventPrices.map((eventPrice, index) => {
                    const eventData = eventPrice[1] as VmEventPrice;
                    return { id: eventData.priceTypeId, priority: index + 1, ticketType: eventData.ticketType };
                });
                this.#eventPricesMatrixSB.next([...eventPrices]);
                this.draggableTable.markAsDirty();
            });
    }

    onDragChange(isEnabled: boolean): void {
        if (!isEnabled) {
            this.#messageDialogSrv.showWarn({
                size: DialogSize.SMALL,
                title: 'EVENTS.DRAG_MODAL_OPTION_TITLE',
                message: 'EVENTS.DRAG_MODAL_OPTION_INFO',
                actionLabel: 'FORMS.ACTIONS.YES',
                showCancelButton: true
            })
                .subscribe(isConfirmed => {
                    this.#eventPrices$
                        .pipe(first())
                        .subscribe(eventPrices => {
                            if (!isConfirmed) {
                                this.draggableTable.get('enabled').setValue(true);
                            } else {
                                const vmEventPrices = this.setVmEventPrices(eventPrices);
                                this.#eventPriceType = vmEventPrices.map((eventPrice, index) => {
                                    const eventData = eventPrice[1] as VmEventPrice;
                                    return { id: eventData.priceTypeId, priority: index + 1, ticketType: eventData.ticketType };
                                });
                                this.#eventPricesMatrixSB.next([...vmEventPrices]);
                            }
                        });
                });
        }
    }

    private getOrderedPrices(eventPrices: EventPrice[], eventPricesType: VenueTemplatePriceType[]): EventPrice[] {
        const eventPricesOrder = eventPrices.map(eventPrice => {
            const eventPriceType = eventPricesType.find(priceType => priceType.id === eventPrice.price_type.id);
            const priority = eventPricesType && eventPriceType ? eventPriceType.priority : 0;
            return { eventPrice, priority };
        });
        return eventPricesOrder
            .sort((a, b) => a.priority ? a.priority - b.priority : 1)
            .map(current => current.eventPrice);
    }

    private setVmEventPrices(eventPrices: EventPrice[], eventPriceTypes?: VenueTemplatePriceType[]): (string | VmEventPrice)[][] {
        const controlKeys = Object.keys(this.pricesMatrix.controls);
        controlKeys.forEach(controlKey => this.pricesMatrix.removeControl(controlKey));
        const desiredTicketType = this.groupMode ? ActivityTicketType.group : ActivityTicketType.individual;
        eventPrices = eventPrices.filter(eventPrice => eventPrice.ticket_type === desiredTicketType);
        const rates = new Map<string, number>();
        const orderedEventPrices = eventPriceTypes ?
            this.getOrderedPrices(eventPrices, eventPriceTypes) : eventPrices;
        this.#eventPricesMatrix = orderedEventPrices.reduce((accEventPricesMatrix, eventPrice) => {
            const eventPriceType = eventPriceTypes ?
                eventPriceTypes.find(evPriceType => evPriceType.id === eventPrice.price_type.id)
                : null;
            const priority = eventPriceTypes && eventPriceType ? eventPriceType.priority : 0;

            let priceTypeRowIndex = accEventPricesMatrix.findIndex(row => row[0] === eventPrice.price_type.description);
            if (priceTypeRowIndex < 0) {
                priceTypeRowIndex = accEventPricesMatrix.push([eventPrice.price_type.description]) - 1;
            }
            let colIndex: number;
            if (rates.has(eventPrice.rate.name)) {
                colIndex = rates.get(eventPrice.rate.name);
            } else {
                colIndex = rates.size + 1;
                this.pricesMatrix.addControl(eventPrice.rate.name, this.#fb.array([]));
                rates.set(eventPrice.rate.name, colIndex);
            }
            const ratePrices = this.pricesMatrix.get([eventPrice.rate.name]) as UntypedFormArray;

            while (!ratePrices.at(priceTypeRowIndex)) {
                const ctrl = this.#fb.control(0, [Validators.required, Validators.min(0)]);
                ratePrices.push(ctrl);
            }

            const priceFormControl = ratePrices.at(priceTypeRowIndex) as UntypedFormControl;
            priceFormControl.setValue(eventPrice.value, { emitEvent: false });

            accEventPricesMatrix[priceTypeRowIndex][colIndex] = {
                initValue: eventPrice.value,
                rateId: eventPrice.rate.id,
                priceTypeId: eventPrice.price_type.id,
                ctrl: priceFormControl,
                ticketType: desiredTicketType,
                priority
            };

            return accEventPricesMatrix;
        }, []);
        this.rateNames = Array.from(rates.keys());
        this.pricesMatrix.markAsPristine();

        return this.#eventPricesMatrix;
    }

    private updatePriceZonesValues(
        savePrices$: Observable<unknown>[],
        modifiedPrices: PutEventPrice[]
    ): void {
        this.#event$.pipe(first())
            .subscribe(event => {
                if (this.pricesMatrix.valid) {
                    if (this.pricesMatrix.dirty) {
                        modifiedPrices = this.#eventPricesMatrix.reduce<PutEventPrice[]>((result, row) => {
                            for (let i = 1; i < row.length; i++) {
                                const elem = row[i] as VmEventPrice;
                                if (elem.initValue !== elem.ctrl.value) {
                                    result.push({
                                        price_type_id: elem.priceTypeId,
                                        rate_id: elem.rateId,
                                        value: elem.ctrl.value,
                                        ticket_type: elem.ticketType
                                    });
                                }
                            }
                            return result;
                        }, []);
                    }
                } else {
                    this.pricesForm.markAllAsTouched();
                    this.#ref.markForCheck();
                }
                if (modifiedPrices.length) {
                    const VENUE_TEMPLATE_PRICE_NOT_FOUND_ERROR = 'VENUE_TEMPLATE_PRICE_NOT_FOUND';
                    savePrices$.push(this.#eventsSrv.eventPrices.update(String(event.id), String(this.tplId), modifiedPrices)
                        .pipe(
                            catchError(error => {
                                if (error.error.code === VENUE_TEMPLATE_PRICE_NOT_FOUND_ERROR) {
                                    return of(null);
                                } else {
                                    return throwError(error);
                                }
                            }),
                            finalize(() => {
                                this.#eventsSrv.eventPrices.load(String(event.id), String(this.tplId));
                                this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.tplId);
                            })));
                }
            });
    }

    private updatePriceZonesOrder(savePrices$: Observable<unknown>[]): void {
        const VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND_ERROR = 'VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND';
        if (this.draggableTable.dirty || this.draggableTable.get('enabled').value && this.#eventPriceType.length) {
            this.#eventPriceType.forEach((eventPriceType, index) => {
                if (!this.groupMode) {
                    if (!this.draggableTable.value.enabled) {
                        eventPriceType.priority = 0;
                    }
                } else {
                    eventPriceType.priority = null;
                }
                if (this.#eventPriceType.length - 1 === index) {
                    savePrices$.push(this.#venueTemplatesSrv.updateVenueTemplatePriceType(this.tplId, eventPriceType).pipe(
                        catchError(error => {
                            if (error.error.code === VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND_ERROR) {
                                return of(null);
                            } else {
                                return throwError(error);
                            }
                        }),
                        finalize(() => {
                            this.#event$
                                .pipe(first())
                                .subscribe(event => this.#eventsSrv.eventPrices.load(String(event.id), String(this.tplId)));
                            this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(this.tplId);
                        })));
                } else {
                    savePrices$.push(this.#venueTemplatesSrv.updateVenueTemplatePriceType(this.tplId, eventPriceType).pipe(
                        catchError(error => {
                            if (error.error.code === VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND_ERROR) {
                                return of(null);
                            } else {
                                return throwError(error);
                            }
                        })));
                }
            });
        }
    }

}
