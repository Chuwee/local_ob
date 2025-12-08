import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventType } from '@admin-clients/shared/common/data-access';
import { getDeepPath$ } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

@Component({
    selector: 'app-event-prices',
    templateUrl: './event-prices.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class EventPricesComponent implements OnInit {
    readonly #route = inject(ActivatedRoute);
    readonly #router = inject(Router);

    // TODO: Remove isTestTaxes when  taxes end
    readonly isTestTaxes = false;

    readonly event$ = inject(EventsService).event.get$();
    readonly deepPath$ = getDeepPath$(this.#router, this.#route);
    readonly eventType = EventType;

    async ngOnInit(): Promise<void> {
        const event = await firstValueFrom(this.event$);
        const deepPath = await firstValueFrom(this.deepPath$);
        if (deepPath === 'rates' && event.settings.use_tiered_pricing) {
            this.#router.navigate(['price-types'], { relativeTo: this.#route });
        }
    }
}
