import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { PromotionRatesScope } from '@admin-clients/cpanel/promoters/data-access';
import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    PutSeasonTicketPromotionPriceTypes, PutSeasonTicketPromotionRates,
    SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component, DestroyRef,
    inject,
    OnInit,
    viewChildren
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, filter, map, withLatestFrom } from 'rxjs/operators';
import { SeasonTicketPromotionPriceTypesComponent } from '../price-types/season-ticket-promotion-price-types.component';
import { SeasonTicketPromotionRatesComponent } from '../rates/season-ticket-promotion-rates.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        FormContainerComponent,
        ReactiveFormsModule,
        SeasonTicketPromotionPriceTypesComponent,
        TranslatePipe,
        SeasonTicketPromotionRatesComponent,
        CommonModule,
        FlexLayoutModule
    ],
    selector: 'app-season-ticket-promotion-zones',
    templateUrl: './season-ticket-promotion-zones.component.html',
    styleUrls: ['./season-ticket-promotion-zones.component.scss']
})
export class SeasonTicketPromotionZonesComponent implements OnInit, WritingComponent {
    readonly #stPromotionsSrv = inject(SeasonTicketPromotionsService);
    readonly #stSrv = inject(SeasonTicketsService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);
    readonly #translateSrv = inject(TranslateService);

    private readonly $matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly $assignedRatesNames = toSignal(this.#stPromotionsSrv.promotionRates.get$().pipe(
        filter(Boolean),
        map(promotionRates => promotionRates.type === PromotionRatesScope.all
            ? this.#translateSrv.instant('SEASON_TICKET.PROMOTIONS.RATES_OPTS.ALL')
            : promotionRates.rates.map(rate => rate.name).join(', ')
        )
    ));

    readonly $arePromotionRatesGroupsEnabled = toSignal(this.#stPromotionsSrv.promotion.get$().pipe(
        filter(Boolean),
        map(promotion => !!promotion.applicable_conditions?.rates_relations_condition?.enabled)
    ));

    errors = {
        savePromotionPriceTypes: false,
        savePromotionRates: false
    };

    readonly reqInProgress$ = booleanOrMerge([
        this.#stPromotionsSrv.promotionPriceTypes.loading$(),
        this.#stPromotionsSrv.promotionRates.loading$()
    ]);

    readonly form = this.#fb.group({
        priceTypes: this.#fb.group({
            type: [null, Validators.required],
            ids: [{ value: [], disabled: true }, Validators.required]
        }),
        rates: this.#fb.group({
            type: [null, Validators.required],
            ids: [{ value: [], disabled: true }, Validators.required]
        })
    });

    seasonTicket: SeasonTicket;
    seasonTicketId: number;
    promotionId: number;

    ngOnInit(): void {
        this.#stPromotionsSrv.promotion.get$()
            .pipe(
                filter(promotion => !!promotion),
                withLatestFrom(this.#stSrv.seasonTicket.get$()),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([promotion, seasonTicket]) => {
                this.seasonTicket = seasonTicket;
                this.seasonTicketId = seasonTicket.id;
                this.promotionId = promotion.id;
                if (promotion.applicable_conditions?.rates_relations_condition?.enabled) {
                    this.form.get('rates')?.disable();
                }
            });
    }

    cancel(): void {
        this.#loadPromotionModels();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<boolean> {
        if (this.form.valid) {
            const updateObs = [];
            const priceTypesForm = this.form.get('priceTypes');
            if (priceTypesForm.dirty) {
                this.errors.savePromotionPriceTypes = false;
                const req: PutSeasonTicketPromotionPriceTypes = {
                    type: priceTypesForm.value.type,
                    price_types: priceTypesForm.value.ids || []
                };
                updateObs.push(
                    this.#stPromotionsSrv.promotionPriceTypes.update(this.seasonTicketId, this.promotionId, req)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionPriceTypes = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            const ratesForm = this.form.get('rates');
            if (ratesForm.dirty) {
                this.errors.savePromotionRates = false;
                const req: PutSeasonTicketPromotionRates = {
                    type: ratesForm.value.type,
                    rates: ratesForm.value.ids || []
                };
                updateObs.push(
                    this.#stPromotionsSrv.promotionRates.update(this.seasonTicketId, this.promotionId, req)
                        .pipe(
                            catchError(error => {
                                this.errors.savePromotionRates = true;
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
            }
            return forkJoin(updateObs)
                .pipe(
                    map(() => {
                        this.#ephemeralMessageSrv.showSaveSuccess();
                        this.#loadPromotionModels();
                        return true;
                    }),
                    catchError(() => of(false))
                );
        } else {
            this.form.markAllAsTouched();
            // workaraund to refresh validations and show them
            this.form.get('priceTypes.type').setValue(this.form.get('priceTypes.type').value);
            this.form.get('rates.type').setValue(this.form.get('rates.type').value);
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.$matExpansionPanels());
            return of(false);
        }
    }

    #loadPromotionModels(): void {
        this.#stPromotionsSrv.promotionPriceTypes.load(this.seasonTicketId, this.promotionId);
        this.#stPromotionsSrv.promotionRates.load(this.seasonTicketId, this.promotionId);
    }
}
