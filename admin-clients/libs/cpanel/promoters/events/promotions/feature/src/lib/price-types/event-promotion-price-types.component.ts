import { PromotionPriceTypesScope } from '@admin-clients/cpanel/promoters/data-access';
import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplatesService, VenueTemplateStatus
    , venueTemplatesProviders
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        HelpButtonComponent,
        MaterialModule,
        CommonModule
    ],
    selector: 'app-event-promotion-price-types',
    templateUrl: './event-promotion-price-types.component.html',
    styleUrls: ['./event-promotion-price-types.component.scss'],
    providers: [
        venueTemplatesProviders
    ]
})
export class EventPromotionPriceTypesComponent implements OnInit {
    private readonly _venueTemplatesSrv = inject(VenueTemplatesService);
    private readonly _eventPromotionsService = inject(EventPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    readonly promotionPriceTypesScope = PromotionPriceTypesScope;
    readonly priceTypesGroups$ = this._venueTemplatesSrv.getGroupedVenueTemplatePriceTypes$();
    readonly loading$ = this._venueTemplatesSrv.isVenueTemplatePriceTypesLoading$();
    @Input() priceTypesForm: UntypedFormGroup;
    @Input() eventId: number;
    @Input() promotionId: number;
    @Input() presale: boolean;

    ngOnInit(): void {
        this._venueTemplatesSrv.loadVenueTemplatesList({
            limit: 999,
            offset: 0,
            sort: 'name:asc',
            eventId: this.eventId,
            status: [VenueTemplateStatus.active]
        });

        this._venueTemplatesSrv.getVenueTemplatesListData$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(venueTpls => this._venueTemplatesSrv.loadMultipleVenueTemplatePriceTypes(venueTpls));

        this.priceTypesForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((priceTypesScope: PromotionPriceTypesScope) =>
                priceTypesScope === PromotionPriceTypesScope.restricted ?
                    this.priceTypesForm.get('ids').enable() : this.priceTypesForm.get('ids').disable()
            );

        combineLatest([
            this._eventPromotionsService.promotionPriceTypes.get$().pipe(filter(Boolean)),
            this.priceTypesForm.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promPriceTypes]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.priceTypesForm.get('type'),
                    promPriceTypes.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.priceTypesForm.get('ids'),
                    promPriceTypes.price_types?.map(({ id }) => id) || []
                );
            });

        this._eventPromotionsService.promotionPriceTypes.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(promPriceTypes => {
                this.priceTypesForm.patchValue({
                    type: promPriceTypes.type,
                    ids: promPriceTypes.price_types?.map(({ id }) => id) || []
                });
                this.priceTypesForm.markAsPristine();
                this.priceTypesForm.markAsUntouched();
            });
    }
}
