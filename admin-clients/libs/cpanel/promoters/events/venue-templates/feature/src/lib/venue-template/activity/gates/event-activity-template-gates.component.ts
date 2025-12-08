import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import {
    ActivityVenueTemplatePriceTypesGatesComponent, ActivityVenueTemplateGatesComponent
} from '@admin-clients/cpanel-common-venue-templates-feature';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ACTIVITY_PRICE_TYPES_GATES_SERVICE, ActVenueTplService } from '@admin-clients/shared/venues/data-access/activity-venue-tpls';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, computed, inject, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-activity-template-gates',
    templateUrl: './event-activity-template-gates.component.html',
    styleUrls: ['./event-activity-template-gates.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [{ provide: ACTIVITY_PRICE_TYPES_GATES_SERVICE, useExisting: ActVenueTplService }],
    imports: [
        FormContainerComponent, FlexLayoutModule, ReactiveFormsModule, MatAccordion, MatExpansionPanel, AsyncPipe,
        MatExpansionPanelHeader, MatExpansionPanelTitle, TranslatePipe, ReactiveFormsModule, MatProgressSpinner,
        ArchivedEventMgrComponent, ActivityVenueTemplatePriceTypesGatesComponent, ActivityVenueTemplateGatesComponent
    ]
})
export class EventActivityTemplateGatesComponent implements AfterViewInit, WritingComponent {
    readonly #fb = inject(FormBuilder);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #eventsService = inject(EventsService);

    @ViewChild(ActivityVenueTemplatePriceTypesGatesComponent) private _priceGatesComponent: ActivityVenueTemplatePriceTypesGatesComponent;
    @ViewChild(ActivityVenueTemplateGatesComponent) private _gatesComponent: ActivityVenueTemplateGatesComponent;

    readonly form = this.#fb.group({});
    readonly $event = toSignal(this.#eventsService.event.get$());
    readonly $venueTemplate = toSignal(this.#venueTemplatesSrv.venueTpl.get$());
    readonly $isSga = computed(() => this.$event()?.additional_config.inventory_provider === 'SGA');
    loading$: Observable<boolean>;

    ngAfterViewInit(): void {
        this.loading$ = booleanOrMerge([
            this._priceGatesComponent.loading$,
            this._gatesComponent.loading$
        ]);
    }

    gatesChanged(): void {
        this._priceGatesComponent.reset();
    }

    save(): void {
        this._priceGatesComponent.save().subscribe(() => this._priceGatesComponent.reset());
    }

    cancel(): void {
        this._priceGatesComponent.reset();
    }
}
