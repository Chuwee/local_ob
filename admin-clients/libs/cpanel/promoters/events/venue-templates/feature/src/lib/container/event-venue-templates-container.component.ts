import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, inject, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { EventVenueTemplatesListComponent } from '../list/event-venue-templates-list.component';

@Component({
    selector: 'app-event-venue-templates-container',
    templateUrl: './event-venue-templates-container.component.html',
    styleUrls: ['./event-venue-templates-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        RouterOutlet, MaterialModule, EmptyStateComponent,
        FlexLayoutModule, EventVenueTemplatesListComponent, TranslatePipe
    ]
})
export class EventVenueTemplatesContainerComponent {
    readonly #eventVenueTplSrv = inject(VenueTemplatesService);

    readonly $listWidth = toSignal(inject(BreakpointObserver)
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(map(result => result.matches ? '240px' : '280px')));

    readonly $empty = toSignal(this.#eventVenueTplSrv.getVenueTemplatesList$().pipe(
        map(templates => templates?.metadata?.total === 0)
    ));

    readonly $loading = toSignal(this.#eventVenueTplSrv.isVenueTemplatesListLoading$());
    readonly $listComponent = viewChild(EventVenueTemplatesListComponent);

    newTemplate(): void {
        this.$listComponent().openNewVenueTemplateDialog();
    }
}
