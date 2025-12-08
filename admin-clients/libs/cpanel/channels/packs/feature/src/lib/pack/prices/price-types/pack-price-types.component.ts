import { PackPriceTypesScope, PacksService } from '@admin-clients/cpanel/channels/packs/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplatesService, venueTemplatesProviders
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-pack-price-types',
    templateUrl: './pack-price-types.component.html',
    styleUrls: ['./pack-price-types.component.scss'],
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, MatRadioModule, MatTooltipModule, MatListModule
    ],
    providers: [
        venueTemplatesProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PackPriceTypesComponent implements OnInit {
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #packSrv = inject(PacksService);
    readonly #destroyRef = inject(DestroyRef);

    readonly packPriceTypesScope = PackPriceTypesScope;
    readonly priceTypes$ = this.#venueTemplatesSrv.getVenueTemplatePriceTypes$();
    readonly loading$ = booleanOrMerge([
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#packSrv.priceTypes.loading$()
    ]);

    readonly $venueTpl = input<IdName, IdName>(null, {
        alias: 'venueTpl',
        transform: (venueTpl: IdName) => {
            this.#venueTemplatesSrv.clearVenueTemplatePriceTypes();
            if (venueTpl) {
                this.#venueTemplatesSrv.loadVenueTemplatePriceTypes(venueTpl.id);
            }
            return venueTpl;
        }
    });

    @Input() priceTypesForm: FormGroup<{
        selection_type: FormControl<PackPriceTypesScope>;
        price_type_ids: FormControl<number[]>;
    }>;

    @Input() promotionId: number;
    @Input() presale: boolean;

    ngOnInit(): void {
        this.priceTypesForm.controls.selection_type.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((priceTypesScope: PackPriceTypesScope) =>
                priceTypesScope === PackPriceTypesScope.restricted ?
                    this.priceTypesForm.controls.price_type_ids.enable() : this.priceTypesForm.controls.price_type_ids.disable()
            );

        combineLatest([
            this.#packSrv.priceTypes.get$().pipe(filter(Boolean)),
            this.priceTypesForm.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(([packPriceTypes]) => {
            FormControlHandler.checkAndRefreshDirtyState(
                this.priceTypesForm.controls.selection_type,
                packPriceTypes.selection_type
            );
            FormControlHandler.checkAndRefreshDirtyState(
                this.priceTypesForm.controls.price_type_ids,
                packPriceTypes.price_types?.map(({ id }) => id)
            );
        });

        combineLatest([
            this.#packSrv.priceTypes.get$(),
            this.priceTypes$
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([packPriceTypes, priceTypes]) => {
            this.priceTypesForm.patchValue({
                selection_type: packPriceTypes.selection_type,
                price_type_ids: packPriceTypes.price_types?.map(({ id }) => id) || priceTypes?.map(({ id }) => id)
            });
            this.priceTypesForm.markAsPristine();
            this.priceTypesForm.markAsUntouched();
        });
    }
}
