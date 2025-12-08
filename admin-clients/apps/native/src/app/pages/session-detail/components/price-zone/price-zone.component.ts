import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { PriceAvailabilityModel } from '../../models/price-availability.model';

@Component({
    selector: 'detail-price-zone',
    templateUrl: './price-zone.component.html',
    styleUrls: ['./price-zone.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class PriceZoneComponent {
    @Input() readonly priceAvailability: PriceAvailabilityModel | null;

    get labels(): string[] {
        return Object.keys(this.priceAvailability);
    }

    getValue(key: string): number {
        if (key === 'total') {
            if (this.priceAvailability.total.type === 'FIXED') {
                return this.priceAvailability.total.value;
            }
            return 0;
        }
        return this.priceAvailability[key];
    }
}
