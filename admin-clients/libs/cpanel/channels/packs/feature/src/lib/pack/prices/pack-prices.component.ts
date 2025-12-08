import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { Pack, PackPriceTypesScope, PacksService, PutPack, PutPackPriceTypes } from '@admin-clients/cpanel/channels/packs/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { CurrencyInputComponent, EphemeralMessageService, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge, greaterThanValidator } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit, QueryList, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatExpansionModule, MatExpansionPanel } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, forkJoin, type Observable, Subject, throwError } from 'rxjs';
import { catchError, filter, first, map, switchMap, tap } from 'rxjs/operators';
import { PackPriceTypesComponent } from './price-types/pack-price-types.component';
import { PackPriceTypesMatrixComponent } from './prices-matrix/pack-price-types-matrix.component';

@Component({
    selector: 'app-pack-prices',
    templateUrl: './pack-prices.component.html',
    styleUrls: ['./pack-prices.component.scss'],
    imports: [
        FlexLayoutModule, AsyncPipe, TranslatePipe, RouterModule, MatProgressSpinnerModule, ReactiveFormsModule,
        FormContainerComponent, MatRadioModule, MatFormFieldModule, CurrencyInputComponent, MatExpansionModule,
        LocalCurrencyPipe, MatTableModule, PackPriceTypesMatrixComponent, PackPriceTypesComponent, HelpButtonComponent,
        MatButton, MatTooltipModule, MatIconModule
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPricesComponent implements OnInit, WritingComponent {
    readonly #packsSrv = inject(PacksService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #channelsSrv = inject(ChannelsService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #router = inject(Router);
    readonly #saveBtnClickedSbj = new Subject<void>();
    readonly #ref = inject(ChangeDetectorRef);

    @ViewChild('ratePrices')
    private readonly _pricesComponent: PackPriceTypesMatrixComponent;

    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly loading$ = booleanOrMerge([
        this.#packsSrv.pack.loading$(),
        this.#packsSrv.priceTypes.loading$()
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
        priceTypes: this.#fb.group({
            selection_type: [null as PackPriceTypesScope, Validators.required],
            price_type_ids: [{ value: [], disabled: true }, Validators.required]
        })
    });

    readonly priceRates = this.form.controls.pricing.controls.rates;
    readonly saveBtnClicked$ = this.#saveBtnClickedSbj.asObservable();
    readonly $pack = toSignal(this.#packsSrv.pack.get$().pipe(
        filter(Boolean),
        tap(pack => {
            if (pack.type === 'MANUAL') {
                this.#router.navigate(['/channels', pack.channel_id, 'packs', pack.id, 'elements']);
            }
            this.updateFormValues(pack);
        })));

    readonly $channel = toSignal(this.#channelsSrv.getChannel$());
    readonly $priceType = toSignal(
        combineLatest([
            this.form.controls.pricing.controls.type.valueChanges,
            this.#packsSrv.pack.get$(),
            this.#channelsSrv.getChannel$()
        ]).pipe(
            filter(([, pack, channel]) => !!pack && !!channel),
            map(([type, pack, channel]) => {
                const value = this.form.controls.pricing.controls.price_increment;
                if (type === 'NEW_PRICE') {
                    this.#packsSrv.packPrices.load(channel.id, pack.id);
                    this.#packsSrv.packRates.load(channel.id, pack.id);
                } else {
                    this.form.controls.pricing.removeControl('rates');
                    this.form.controls.pricing.addControl('rates', this.#fb.group({}));
                }
                type === 'INCREMENTAL' ? value.enable() : value.disable();

                return type;
            })));

    readonly $venueTpl = toSignal(this.#packsSrv.packItems.get$().pipe(
        filter(Boolean),
        map(packItems => {
            const mainItem = packItems.find(item => item.main);
            this.mainItemId = mainItem.id;
            this.#packsSrv.priceTypes.load(this.$channel().id, this.$pack().id, this.mainItemId);
            return mainItem.type === 'SESSION' ? mainItem.session_data.venue_template : mainItem.event_data.venue_template;
        })
    ));

    mainItemId = null;

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
                pricing: this.form.controls.pricing.value
            };
            if (this.form.controls.pricing.dirty) {
                if (this._pricesComponent) {
                    updateObs.push(
                        this._pricesComponent.savePrices()
                            .pipe(switchMap(() =>
                                this.#packsSrv.pack.update(this.$channel().id, this.$pack().id, packPrices as PutPack)
                                    .pipe(catchError(error => {
                                        this.#ref.detectChanges();
                                        throw error;
                                    }))
                            ))
                    );
                } else {
                    updateObs.push(
                        this.#packsSrv.pack.update(this.$channel().id, this.$pack().id, packPrices as PutPack)
                            .pipe(catchError(error => {
                                this.#ref.detectChanges();
                                throw error;
                            }))
                    );
                }
            }

            const priceTypesForm = this.form.controls.priceTypes;
            if (priceTypesForm.dirty) {
                const req: PutPackPriceTypes = {
                    selection_type: priceTypesForm.value.selection_type,
                    price_type_ids: priceTypesForm.value.selection_type === PackPriceTypesScope.restricted ? priceTypesForm.value.price_type_ids : []
                };
                updateObs.push(
                    this.#packsSrv.priceTypes.update(this.$channel().id, this.$pack().id, this.mainItemId, req)
                        .pipe(
                            catchError(error => {
                                this.#ref.detectChanges();
                                throw error;
                            })
                        )
                );
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
        this.#packsSrv.packRates.refresh(this.$channel().id, this.$pack().id).subscribe(() => {
            this.#packsSrv.packPrices.load(this.$channel().id, this.$pack().id);
            this.#packsSrv.packRates.load(this.$channel().id, this.$pack().id);
            this.#packsSrv.priceTypes.load(this.$channel().id, this.$pack().id, this.mainItemId);
            this.#ephemeralMessageSrv.showSuccess({ msgKey: 'CHANNELS.PACKS.PACK_PRICE_TYPES.REFRESH_RATES_SUCCESS' });
        });
    }

    private load(): void {
        this.#packsSrv.pack.load(this.$channel().id, this.$pack().id);
        this.#packsSrv.packItems.load(this.$channel().id, this.$pack().id);
        this.form.markAsPristine();
    }

    private updateFormValues(pack: Pack): void {
        this.form.controls.pricing.reset({
            ...pack.pricing
        });
        this.form.markAsPristine();
    }
}
