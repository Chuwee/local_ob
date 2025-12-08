import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { entitiesProviders, EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Pack, PacksService, PutPack } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { CurrencyInputComponent, EphemeralMessageService, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, greaterThanValidator } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, QueryList, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, type Observable, Subject, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, tap } from 'rxjs/operators';
import { PackPriceTypesMatrixComponent } from '../prices-matrix/pack-price-types-matrix.component';

@Component({
    selector: 'app-pack-prices',
    templateUrl: './pack-prices.component.html',
    styleUrls: ['./pack-prices.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, RouterModule, MatProgressSpinnerModule, ReactiveFormsModule,
        FormContainerComponent, MatRadioModule, MatFormFieldModule, CurrencyInputComponent, MatExpansionModule,
        LocalCurrencyPipe, MatTableModule, HelpButtonComponent, PackPriceTypesMatrixComponent, MatButton, MatTooltipModule, MatIconModule,
        MatSelectModule, MatCheckboxModule
    ],
    providers: [entitiesProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPricesComponent implements OnInit, WritingComponent {
    readonly #packsSrv = inject(PacksService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #router = inject(Router);
    readonly #saveBtnClickedSbj = new Subject<void>();
    readonly #ref = inject(ChangeDetectorRef);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #entitiesSrv = inject(EntitiesService);

    @ViewChild('ratePrices')
    private readonly _pricesComponent: PackPriceTypesMatrixComponent;

    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly loading$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#packsSrv.priceTypes.loading$(),
        this.#packsSrv.packItems.loading$(),
        this.#venueTemplatesSrv.venueTpl.inProgress$(),
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#packsSrv.packPrices.inProgress$(),
        this.#packsSrv.packRates.inProgress$()
    ]);

    readonly currency$ = this.#authSrv.getLoggedUser$()
        .pipe(
            first(Boolean),
            map(user => user.currency)
        );

    readonly form = this.#fb.group({
        pricing: this.#fb.group({
            type: [null as 'COMBINED' | 'INCREMENTAL' | 'NEW_PRICE', Validators.required],
            price_increment: [{ value: null as number, disabled: true }, [Validators.required, greaterThanValidator(0)]],
            rates: this.#fb.group({})
        }),
        ticket_taxes: [null as number, Validators.required],
        unified_price: null as boolean
    });

    readonly priceRates = this.form.controls.pricing.controls.rates;
    readonly saveBtnClicked$ = this.#saveBtnClickedSbj.asObservable();
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap(pack => {
            if (pack.type === 'MANUAL') {
                this.#router.navigate(['/pack', pack.id, 'elements']);
            }
            this.updateFormValues(pack);
        })));

    readonly $priceType = toSignal(
        combineLatest([
            this.form.controls.pricing.controls.type.valueChanges,
            this.#packsSrv.pack.get$()
        ]).pipe(
            filter(([, pack]) => !!pack),
            map(([type, pack]) => {
                const value = this.form.controls.pricing.controls.price_increment;
                if (type === 'NEW_PRICE') {
                    this.#packsSrv.packPrices.load(pack.id);
                    this.#packsSrv.packRates.load(pack.id);
                } else {
                    this.form.controls.pricing.removeControl('rates');
                    this.form.controls.pricing.addControl('rates', this.#fb.group({}));
                }
                type === 'INCREMENTAL' ? value.enable() : value.disable();

                return type;
            })));

    readonly $entityTaxes = toSignal(this.#entitiesSrv.getEntityTaxes$().pipe(filter(Boolean)));

    ngOnInit(): void {
        this.load();
    }

    cancel(): void {
        this._pricesComponent?.cancel();
        this.load();
    }

    save$(): Observable<void | void[]> {
        this.#saveBtnClickedSbj.next();
        const updateObs: Observable<void>[] = [];
        if (this.form.valid || this._pricesComponent?.pricesForm.valid) {
            const packPrices = {
                pricing: this.form.controls.pricing.value,
                tax_id: this.form.controls.ticket_taxes.value,
                unified_price: this.form.controls.unified_price.value
            };
            if (this.form.dirty) {
                if (this._pricesComponent) {
                    updateObs.push(
                        this._pricesComponent.savePrices()
                            .pipe(switchMap(() =>
                                this.#packsSrv.pack.update(this.$pack().id, packPrices as PutPack)
                                    .pipe(catchError(error => {
                                        this.#ref.detectChanges();
                                        throw error;
                                    }))
                            ))
                    );
                } else {
                    updateObs.push(
                        this.#packsSrv.pack.update(this.$pack().id, packPrices as PutPack)
                            .pipe(catchError(error => {
                                this.#ref.detectChanges();
                                throw error;
                            }))
                    );
                }
            }
            return forkJoin(updateObs).pipe(tap(() => {
                this.#ephemeralMessageSrv.showSaveSuccess();
                this.load();
            }));
        } else {
            this.form.markAllAsTouched();
            //Refresh error state in currency-input child component
            this.form.setValue(this.form.getRawValue(), { emitEvent: false });
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
            return throwError(() => new Error('Invalid form'));
        }
    }

    save(): void {
        this.save$().subscribe();
    }

    refreshRates(): void {
        this.#packsSrv.packRates.refresh(this.$pack().id).subscribe(() => {
            this.#packsSrv.packPrices.load(this.$pack().id);
            this.#packsSrv.packRates.load(this.$pack().id);
            this.#ephemeralMessageSrv.showSuccess({ msgKey: 'PACK.PACK_PRICE_TYPES.REFRESH_RATES_SUCCESS' });
        });
    }

    private load(): void {
        this.#packsSrv.pack.load(this.$pack().id);
        this.#packsSrv.packItems.load(this.$pack().id);
        this.#entitiesSrv.loadEntityTaxes(this.$pack().entity.id);
        this.form.markAsPristine();
    }

    private updateFormValues(pack: Pack): void {
        this.form.reset({
            pricing: pack.pricing,
            ticket_taxes: pack.tax.id,
            unified_price: pack.unified_price
        });
        this.form.markAsPristine();
    }
}
