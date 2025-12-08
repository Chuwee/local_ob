import { PromotionRatesScope } from '@admin-clients/cpanel/promoters/data-access';
import {
    SeasonTicketsApi, SeasonTicketsService, SeasonTicketsState
} from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketPromotionsService
} from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
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
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FlexLayoutModule,
        CommonModule
    ],
    selector: 'app-season-ticket-promotion-rates',
    templateUrl: './season-ticket-promotion-rates.component.html',
    styleUrls: ['./season-ticket-promotion-rates.component.scss'],
    providers: [
        SeasonTicketsApi,
        SeasonTicketsService,
        SeasonTicketsState
    ]
})
export class SeasonTicketPromotionRatesComponent implements OnInit {
    private readonly _stSrv = inject(SeasonTicketsService);
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);
    private readonly _destroyRef = inject(DestroyRef);

    readonly promotionRatesScope = PromotionRatesScope;
    readonly rates$ = this._stSrv.getSeasonTicketRates$();
    readonly loading$ = this._stSrv.isSeasonTicketRatesInProgress$();

    @Input() ratesForm: UntypedFormGroup;
    @Input() seasonTicketId: number;
    @Input() promotionId: number;

    ngOnInit(): void {
        this._stSrv.loadSeasonTicketRates(String(this.seasonTicketId));
        this.ratesForm.get('type').valueChanges
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe((ratesScope: PromotionRatesScope) =>
                ratesScope === PromotionRatesScope.restricted ?
                    this.ratesForm.get('ids').enable() : this.ratesForm.get('ids').disable()
            );

        combineLatest([
            this._stPromotionsSrv.promotionRates.get$().pipe(filter(Boolean)),
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

        this._stPromotionsSrv.promotionRates.get$()
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
