import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { VenueTemplatePriceTypesService } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { ContextNotificationComponent, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, Input, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, Subject, throwError } from 'rxjs';
import { distinctUntilChanged, tap } from 'rxjs/operators';
import { SeasonTicketPriceTypesMatrixComponent } from './price-types-matrix/season-ticket-price-types-matrix.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent,
        FlexLayoutModule,
        ContextNotificationComponent,
        TranslatePipe,
        CommonModule,
        MaterialModule,
        SeasonTicketPriceTypesMatrixComponent
    ],
    selector: 'app-season-ticket-price-types-rates',
    templateUrl: './season-ticket-price-types-rates.component.html',
    styleUrls: ['./season-ticket-price-types-rates.component.scss']
})
export class SeasonTicketPriceTypesRatesComponent {
    private readonly _seasonTicketSrv = inject(SeasonTicketsService);
    private readonly _venueTplSrv = inject(VenueTemplatesService);
    private readonly _venueTplPriceTypesSrv = inject(VenueTemplatePriceTypesService);
    private readonly _fb = inject(UntypedFormBuilder);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _ref = inject(ChangeDetectorRef);

    private readonly _saveBtnClickedSbj = new Subject<void>();

    @ViewChild(SeasonTicketPriceTypesMatrixComponent)
    private readonly _pricesComponent: SeasonTicketPriceTypesMatrixComponent;

    readonly isGenerationStatusInProgress$ = this._seasonTicketSrv.seasonTicketStatus.isGenerationStatusInProgress$()
        .pipe(distinctUntilChanged());

    readonly saveBtnClicked$ = this._saveBtnClickedSbj.asObservable();
    readonly formPriceTypesRates = this._fb.group({});

    readonly isInProgress$ = booleanOrMerge([
        this._seasonTicketSrv.seasonTicket.inProgress$(),
        this._seasonTicketSrv.isSeasonTicketPricesInProgress$(),
        this._seasonTicketSrv.isSeasonTicketPricesSavingInProgress$(),
        this._venueTplSrv.isVenueTemplatePriceTypesLoading$(),
        this._venueTplSrv.isVenueTemplatePriceTypeSaving$(),
        this._venueTplPriceTypesSrv.isVenueTemplatePriceTypeChannelContentLoading$(),
        this._venueTplPriceTypesSrv.isPriceTypeChannelContentSaving$()
    ]);

    @Input() set form(value: UntypedFormGroup) {
        if (value.contains('priceTypesRates')) {
            return;
        }
        value.addControl('priceTypesRates', this.formPriceTypesRates, { emitEvent: false });
    }

    cancel(): void {
        this._pricesComponent?.cancel();
    }

    save$(): Observable<unknown[]> {
        this._saveBtnClickedSbj.next();
        if (this.formPriceTypesRates.valid && this.formPriceTypesRates.dirty) {
            const obs$ = [];
            if (this._pricesComponent) {
                obs$.push(...this._pricesComponent.savePrices());
            }
            return forkJoin(obs$)
                .pipe(tap(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    savePrices(): void {
        this.save$()
            .subscribe(() => this._ref.markForCheck());
    }
}
