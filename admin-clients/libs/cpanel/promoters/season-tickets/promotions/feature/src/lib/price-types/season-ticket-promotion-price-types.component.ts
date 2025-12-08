import { PromotionPriceTypesScope } from '@admin-clients/cpanel/promoters/data-access';
import { SeasonTicket } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
import {
    VenueTemplatesApi, VenueTemplatesService, VenueTemplatesState
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
        MaterialModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule
    ],
    selector: 'app-season-ticket-promotion-price-types',
    templateUrl: './season-ticket-promotion-price-types.component.html',
    styleUrls: ['./season-ticket-promotion-price-types.component.scss'],
    providers: [
        VenueTemplatesApi,
        VenueTemplatesService,
        VenueTemplatesState
    ]
})
export class SeasonTicketPromotionPriceTypesComponent implements OnInit {
    private readonly _venueTemplatesSrv = inject(VenueTemplatesService);
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    readonly promotionPriceTypesScope = PromotionPriceTypesScope;
    readonly priceTypesGroups$ = this._venueTemplatesSrv.getGroupedVenueTemplatePriceTypes$();
    readonly loading$ = this._venueTemplatesSrv.isVenueTemplatePriceTypesLoading$();

    @Input() priceTypesForm: UntypedFormGroup;
    @Input() seasonTicket: SeasonTicket;
    @Input() promotionId: number;

    ngOnInit(): void {
        this._venueTemplatesSrv.venueTpl.load(this.seasonTicket.venue_templates[0].id);

        this._venueTemplatesSrv.venueTpl.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(venueTemplate => {
                this._venueTemplatesSrv.loadMultipleVenueTemplatePriceTypes([venueTemplate]);
            });

        this.priceTypesForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((priceTypesScope: PromotionPriceTypesScope) =>
                priceTypesScope === PromotionPriceTypesScope.restricted ?
                    this.priceTypesForm.get('ids').enable() : this.priceTypesForm.get('ids').disable()
            );

        combineLatest([
            this._stPromotionsSrv.promotionPriceTypes.get$().pipe(filter(Boolean)),
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

        this._stPromotionsSrv.promotionPriceTypes.get$()
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
