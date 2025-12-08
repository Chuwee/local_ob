import { ExternalProviderEvents, PromotersExternalProviderService } from '@admin-clients/cpanel/promoters/data-access';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplate, VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, inject, DestroyRef, input } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter } from 'rxjs';

export interface SgaStFieldsForm {
    template: FormControl<VenueTemplate>;
    event: FormControl<ExternalProviderEvents>;
}
@Component({
    selector: 'app-new-st-sga-fields',
    templateUrl: './new-st-sga-fields.component.html',
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewSeasonTicketSgaFieldsComponent implements OnInit {
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #externalSrv = inject(PromotersExternalProviderService);
    readonly #destroyRef = inject(DestroyRef);

    readonly $form = input.required<FormGroup<SgaStFieldsForm>>({ alias: 'form' });
    readonly $entityId = input.required<number>({ alias: 'entityId' });

    readonly inventoryProviderTemplates$ = this.#venueTemplatesService.getVenueTemplatesListData$();

    readonly externalProviderEvents$ = toSignal(this.#externalSrv.providerEvents.get$().pipe(filter(Boolean)));

    ngOnInit(): void {
        this.#venueTemplatesService.clearVenueTemplateList();
        this.#venueTemplatesService.loadVenueTemplatesList({
            limit: 999,
            offset: 0,
            sort: 'name:asc',
            status: [VenueTemplateStatus.active],
            entityId: this.$entityId(),
            inventory_provider: ExternalInventoryProviders.sga
        });

        this.$form().controls.template.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(template => {
                if (template) {
                    this.#externalSrv.providerEvents.clear();
                    this.#externalSrv.providerEvents.load({
                        entity_id: this.$entityId(),
                        venue_template_id: template.id,
                        skip_used: true,
                        type: 'MEMBERS'
                    });
                }
            });

        this.externalProviderEvents$()?.length > 0 && this.$form().controls.event.enable();
    }

}
