import { ExternalProviderEvents, PromotersExternalProviderService } from '@admin-clients/cpanel/promoters/data-access';
import { Entity, ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { VenueTemplate, VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input, OnInit, inject, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, tap } from 'rxjs';

export interface SgaFieldsForm {
    template: FormControl<VenueTemplate>;
    inventory: FormControl<string>;
    event: FormControl<ExternalProviderEvents>;
}
@Component({
    selector: 'app-new-event-sga-fields',
    templateUrl: './new-event-sga-fields.component.html',
    styleUrls: ['./new-event-sga-fields.component.scss'],
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, AsyncPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewEventSgaFieldsComponent implements OnInit {
    readonly #venueTemplatesService = inject(VenueTemplatesService);
    readonly #fb = inject(FormBuilder);
    readonly #externalSrv = inject(PromotersExternalProviderService);
    readonly #destroyRef = inject(DestroyRef);

    @Input() form: FormGroup;
    @Input() entity: Entity;

    readonly inventoryProviderTemplates$ = this.#venueTemplatesService.getVenueTemplatesListData$();

    readonly externalProviderEvents$ = this.#externalSrv.providerEvents.get$()
        .pipe(filter(Boolean), tap(() => this.form.get('sga.event').enable()));

    ngOnInit(): void {
        this.form.setControl('sga', this.#fb.group({
            template: [null, Validators.required],
            event: [{ value: null, disabled: true }, Validators.required]
        }));

        this.#venueTemplatesService.clearVenueTemplateList();
        this.#venueTemplatesService.loadVenueTemplatesList({
            entityId: this.entity.id,
            inventory_provider: ExternalInventoryProviders.sga,
            status: [VenueTemplateStatus.active],
            limit: 999,
            offset: 0,
            sort: 'name:asc'
        });

        this.form.get('sga.template').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(template => {
                if (template) {
                    this.#externalSrv.providerEvents.clear();
                    this.#externalSrv.providerEvents.load({
                        entity_id: this.entity.id,
                        venue_template_id: template.id,
                        skip_used: true,
                        type: 'TICKETING'
                    });
                }
            });
    }

}
