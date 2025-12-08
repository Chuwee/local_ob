import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { GoBackComponent, NavTabsMenuComponent } from '@admin-clients/shared/common/ui/components';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';

@Component({
    selector: 'app-venue-template-details',
    templateUrl: './venue-template-details.component.html',
    styleUrls: ['./venue-template-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [GoBackComponent, NavTabsMenuComponent, FlexLayoutModule, RouterOutlet, NgIf, AsyncPipe]
})
export class VenueTemplateDetailsComponent {
    readonly venueTemplate$ = inject(VenueTemplatesService).venueTpl.get$();
}
