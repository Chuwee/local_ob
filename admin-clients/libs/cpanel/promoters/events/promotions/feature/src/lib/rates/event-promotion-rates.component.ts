import { PromotionRatesScope } from '@admin-clients/cpanel/promoters/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import {
    EventPromotionsService
} from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormControlHandler } from '@admin-clients/shared/utility/utils';
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
        HelpButtonComponent,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        CommonModule
    ],
    selector: 'app-event-promotion-rates',
    templateUrl: './event-promotion-rates.component.html',
    styleUrls: ['./event-promotion-rates.component.scss']
})
export class EventPromotionRatesComponent implements OnInit {
    private readonly _eventsService = inject(EventsService);
    private readonly _eventPromotionsService = inject(EventPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    readonly promotionRatesScope = PromotionRatesScope;
    readonly rates$ = this._eventsService.eventRates.get$();
    readonly loading$ = this._eventsService.eventRates.inProgress$();
    @Input() ratesForm: UntypedFormGroup;
    @Input() eventId: number;
    @Input() promotionId: number;
    @Input() presale: boolean;

    ngOnInit(): void {
        this._eventsService.eventRates.load(String(this.eventId));

        this.ratesForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((ratesScope: PromotionRatesScope) =>
                ratesScope === PromotionRatesScope.restricted ?
                    this.ratesForm.get('ids').enable() : this.ratesForm.get('ids').disable()
            );

        combineLatest([
            this._eventPromotionsService.promotionRates.get$().pipe(filter(Boolean)),
            this.ratesForm.valueChanges // only used as a trigger
        ]).pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(([promRates]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.ratesForm.get('type'),
                    promRates.type
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.ratesForm.get('ids'),
                    promRates.rates?.map(({ id }) => id) || []
                );
            });

        this._eventPromotionsService.promotionRates.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(promRates => {
                this.ratesForm.patchValue({
                    type: promRates.type,
                    ids: promRates.rates?.map(({ id }) => id) || []
                });
                this.ratesForm.markAsPristine();
                this.ratesForm.markAsUntouched();
            });
    }
}
