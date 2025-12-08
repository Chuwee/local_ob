import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { SeasonTicketPriceTypesRatesComponent } from './price-types-rates/season-ticket-price-types-rates.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        SeasonTicketPriceTypesRatesComponent, FlexLayoutModule
    ],
    selector: 'app-season-ticket-price-types',
    templateUrl: './season-ticket-price-types.component.html'
})
export class SeasonTicketPriceTypesComponent implements WritingComponent {
    @ViewChild(SeasonTicketPriceTypesRatesComponent)
    private readonly _seasonTicketPriceTypesRatesTempComponent: SeasonTicketPriceTypesRatesComponent;

    readonly form = new UntypedFormGroup({});

    save$(): Observable<unknown> {
        if (this._seasonTicketPriceTypesRatesTempComponent) {
            return this._seasonTicketPriceTypesRatesTempComponent.save$();
        }
        return of(null);
    }
}
