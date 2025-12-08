import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { ChangeDetectionStrategy, Component, effect, inject, input, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { type Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { VenueTemplateOriginOrderItem } from '../../models/venue-template-origin-order-item';
import { StandardVenueTemplateBaseService } from '../../services/standard-venue-template-base.service';
import { StandardVenueTemplateRelocationService } from '../../services/standard-venue-template-relocation.service';
import { RelocationMgrDestinationSeatsComponent } from './destination-seats/relocation-mgr-destination-seats.component';
import { RelocationMgrOriginSeatsComponent } from './origin-seats/relocation-mgr-origin-seats.component';

@Component({
    selector: 'app-venue-template-relocation-mgr',
    imports: [
        WizardBarComponent, RelocationMgrOriginSeatsComponent, RelocationMgrDestinationSeatsComponent
    ],
    templateUrl: './venue-tpl-relocation-mgr.component.html',
    styleUrl: './venue-tpl-relocation-mgr.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [StandardVenueTemplateRelocationService]
})
export class VenueTemplateRelocationMgrComponent {
    readonly #standardVenueTplRelocationSrv = inject(StandardVenueTemplateRelocationService);
    readonly #standardVenueTplBaseSrv = inject(StandardVenueTemplateBaseService);

    readonly $relocateSeatsInfo = input<VenueTemplateOriginOrderItem[]>([], { alias: 'relocateSeatsInfo' });

    readonly $wizardBar = viewChild(WizardBarComponent);

    readonly $activeStep = toSignal(this.#standardVenueTplBaseSrv.relocation.getStatus$().pipe(map(status => status?.activeStep || 0)));

    constructor() {
        this.#standardVenueTplBaseSrv.relocation.updateStatus({
            activeStep: 0,
            originSeatsSelected: false,
            canStartRelocation: false
        });
        effect(
            () => this.#standardVenueTplRelocationSrv.selectedOriginOrderItems.set(this.$relocateSeatsInfo() || [])
        );
        effect(() => this.$wizardBar()?.setActiveStep(this.$activeStep()));
    }

    getSelectedDestinationSeats(): Observable<{ [originId: number]: number }> {
        return this.#standardVenueTplRelocationSrv.selectedDestinationSeats.get$();
    }
}
