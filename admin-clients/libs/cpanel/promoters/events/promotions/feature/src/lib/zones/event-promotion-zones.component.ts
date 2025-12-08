import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { PromotionPriceTypesScope, PromotionRatesScope, PromotionSessionsScope } from '@admin-clients/cpanel/promoters/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventPromotionPacks, EventPromotionsService, PutEventPromotionPriceTypes, PutEventPromotionRates, PutEventPromotionSessions } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { Id, IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnDestroy, OnInit, viewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, throwError } from 'rxjs';
import { catchError, filter, first, map, tap, withLatestFrom } from 'rxjs/operators';
import { EventPromotionPacksComponent } from '../packs/event-promotion-packs.component';
import { EventPromotionPriceTypesComponent } from '../price-types/event-promotion-price-types.component';
import { EventPromotionRatesComponent } from '../rates/event-promotion-rates.component';
import { EventPromotionSessionsComponent } from '../sessions/event-promotion-sessions.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, MaterialModule, TranslatePipe, EventPromotionSessionsComponent,
        EventPromotionPriceTypesComponent, EventPromotionRatesComponent, ArchivedEventMgrComponent,
        CommonModule, EventPromotionPacksComponent
    ],
    selector: 'app-event-promotion-zones',
    templateUrl: './event-promotion-zones.component.html',
    styleUrls: ['./event-promotion-zones.component.scss']
})
export class EventPromotionZonesComponent implements OnInit, OnDestroy {
    readonly #eventPromotionsService = inject(EventPromotionsService);
    readonly #eventsService = inject(EventsService);
    readonly #packsSrv = inject(PacksService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMsg = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #translateSrv = inject(TranslateService);

    private readonly $matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly $isMainItemInPack = toSignal(this.#packsSrv.packsList.getMetadata$().pipe(
        first(Boolean),
        map(metadata => metadata.total > 0),
        tap(hasPacks => { if (!hasPacks) { this.form.controls.packs.disable(); } })
    ));

    readonly $assignedRatesNames = toSignal(this.#eventPromotionsService.promotionRates.get$().pipe(
        filter(Boolean),
        map(promotionRates => promotionRates.type === PromotionRatesScope.all
            ? this.#translateSrv.instant('EVENTS.PROMOTIONS.RATES_OPTS.ALL')
            : promotionRates.rates.map(rate => rate.name).join(', ')
        )
    ));

    readonly $arePromotionRatesGroupsEnabled = toSignal(this.#eventPromotionsService.promotion.get$().pipe(
        filter(Boolean),
        map(promotion => !!promotion.applicable_conditions?.rates_relations_condition?.enabled)
    ));

    readonly errors = {
        savePromotionSessions: false,
        savePromotionPriceTypes: false,
        savePromotionRates: false,
        savePromotionPacks: false
    };

    readonly reqInProgress$ = booleanOrMerge([
        this.#eventPromotionsService.promotionSessions.loading$(),
        this.#eventPromotionsService.promotionPriceTypes.loading$(),
        this.#eventPromotionsService.promotionRates.loading$(),
        this.#eventPromotionsService.promotionPacks.loading$()
    ]);

    readonly form = this.#fb.group({
        sessions: this.#fb.group({
            type: [null as PromotionSessionsScope, Validators.required],
            selected: [{ value: null as IdName[], disabled: true }, Validators.required]
        }),
        priceTypes: this.#fb.group({
            type: [null as PromotionPriceTypesScope, Validators.required],
            ids: [{ value: null as number[], disabled: true }, Validators.required]
        }),
        rates: this.#fb.group({
            type: [null as PromotionRatesScope, Validators.required],
            ids: [{ value: null as number[], disabled: true }, Validators.required]
        }),
        packs: this.#fb.group({
            allow_entity_packs: null as boolean,
            packs: [{ value: [] as IdName[], disabled: true }, Validators.required]
        })
    });

    eventId: number;
    promotionId: number;
    presale: boolean;
    selfManagePromotion: boolean;

    ngOnInit(): void {
        this.#eventPromotionsService.promotion.get$()
            .pipe(
                filter(Boolean),
                withLatestFrom(this.#eventsService.event.get$()),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([promotion, event]) => {
                this.eventId = event.id;
                this.promotionId = promotion.id;
                this.presale = promotion.presale;
                this.selfManagePromotion = promotion.collective?.self_managed;
                if (promotion.applicable_conditions?.rates_relations_condition?.enabled) {
                    this.form.get('rates')?.disable();
                }
                //We need to check if this event has related packs
                //in order to show or hide pack assignation
                const packsReq = { limit: 10, offset: 0, eventId: this.eventId };
                this.#packsSrv.packsList.load(packsReq);
            });

        this.form.controls.packs.controls.allow_entity_packs.valueChanges.pipe(
            withLatestFrom(this.#eventPromotionsService.promotion.get$()),
            takeUntilDestroyed(this.#destroyRef))
            .subscribe(([allow, promotion]) => {
                if (promotion.applicable_conditions?.rates_relations_condition?.enabled) {
                    this.form.get('rates')?.disable();
                    return;
                }
                if (allow) {
                    this.form.controls.rates.controls.type.setValue(PromotionRatesScope.all);
                    this.form.controls.rates.markAsDirty();
                    this.form.controls.rates.disable();
                } else {
                    this.form.controls.rates.enable();
                    if (this.form.value.rates.type === PromotionRatesScope.all) {
                        this.form.controls.rates.controls.ids.disable();
                    }
                }
            });
    }

    ngOnDestroy(): void {
        this.#packsSrv.packsList.clear();
    }

    cancel(): void {
        this.#loadPromotionModels();
    }

    save$(): Observable<void | void[]> {
        if (this.form.valid) {
            const updateObs: Observable<void>[] = [];
            const sessionsForm = this.form.controls.sessions;
            if (sessionsForm.dirty) {
                this.errors.savePromotionSessions = false;
                const selectedSessionIds = sessionsForm.value.selected?.map((elem: Id) => elem.id);
                const req: PutEventPromotionSessions = {
                    type: sessionsForm.value.type,
                    sessions: selectedSessionIds
                };
                updateObs.push(
                    this.#eventPromotionsService.promotionSessions.update(this.eventId, this.promotionId, req)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionSessions = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            const priceTypesForm = this.form.controls.priceTypes;
            if (priceTypesForm.dirty) {
                this.errors.savePromotionPriceTypes = false;
                const req: PutEventPromotionPriceTypes = {
                    type: priceTypesForm.value.type,
                    price_types: priceTypesForm.value.ids || []
                };
                updateObs.push(
                    this.#eventPromotionsService.promotionPriceTypes.update(this.eventId, this.promotionId, req)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionPriceTypes = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            const ratesForm = this.form.controls.rates;
            if (ratesForm.dirty) {
                this.errors.savePromotionRates = false;
                const req: PutEventPromotionRates = {
                    type: ratesForm.value.type,
                    rates: ratesForm.value.type === PromotionRatesScope.restricted ? ratesForm.value.ids || [] : null
                };
                updateObs.push(
                    this.#eventPromotionsService.promotionRates.update(this.eventId, this.promotionId, req)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionRates = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            const packsForm = this.form.controls.packs;
            if (packsForm.dirty) {
                this.errors.savePromotionPacks = false;
                const selectedPackIds = packsForm.value.packs?.map((elem: Id) => elem.id);
                const req: EventPromotionPacks = {
                    allow_entity_packs: packsForm.value.allow_entity_packs,
                    packs: selectedPackIds
                };
                updateObs.push(
                    this.#eventPromotionsService.promotionPacks.update(this.eventId, this.promotionId, req)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionPacks = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            return forkJoin(updateObs).pipe(tap(() => {
                this.#ephemeralMsg.showSaveSuccess();
                this.#loadPromotionModels();
            }));

        } else {
            this.form.markAllAsTouched();
            // workaraund to refresh validations and show them
            this.form.controls.sessions.controls.type.setValue(this.form.controls.sessions.controls.type.value);
            this.form.controls.priceTypes.controls.type.setValue(this.form.controls.priceTypes.controls.type.value);
            this.form.controls.rates.controls.type.setValue(this.form.controls.rates.controls.type.value);
            this.form.controls.packs.controls.allow_entity_packs.setValue(this.form.controls.packs.controls.allow_entity_packs.value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.$matExpansionPanels());
            return throwError(() => 'invalid fields');
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    #loadPromotionModels(): void {
        this.#eventPromotionsService.promotionSessions.load(this.eventId, this.promotionId);
        this.#eventPromotionsService.promotionPriceTypes.load(this.eventId, this.promotionId);
        this.#eventPromotionsService.promotionRates.load(this.eventId, this.promotionId);
        this.#eventPromotionsService.promotionPacks.load(this.eventId, this.promotionId);
    }
}
