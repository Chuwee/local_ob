import { Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventType, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { KeyValuePipe, UpperCasePipe } from '@angular/common';
import { Component, ChangeDetectionStrategy, input, computed } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-event-venue-template-info',
    templateUrl: './event-venue-template-info.component.html',
    styleUrls: ['./event-venue-template-info.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatDivider, EllipsifyDirective,
        TranslatePipe, UpperCasePipe, LocalNumberPipe, KeyValuePipe
    ]
})
export class EventVenueTemplateInfoComponent {

    $info = input<{ priceTypes: number; external_data: Record<string, string> }>(null, { alias: 'info' });
    $template = input.required<VenueTemplate>({ alias: 'template' });
    $event = input.required<Event>({ alias: 'event' });
    readonly $isAvet = computed(() => this.$event()?.type === EventType.avet);
    readonly $isSGA = computed(() => this.$event()?.additional_config?.inventory_provider === ExternalInventoryProviders.sga);
}
