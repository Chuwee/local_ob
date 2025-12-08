import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EventsService, EventVenueTpl } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { VenueTemplatePriceTypesService } from '@admin-clients/cpanel/venues/venue-templates/data-access';
import { PriceTypeTranslationsComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import { ExternalInventoryProviders } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, TabsMenuComponent, TabDirective, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { compareWithIdOrCode } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, DestroyRef, effect, inject, OnDestroy, OnInit, viewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelectModule } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { first, map, tap } from 'rxjs/operators';
import { EventPriceTypesRatesGroupFiltersComponent } from './filters/event-price-types-rates-group-filters.component';
import { EventPriceTypesRatesGroupListComponent } from './list/event-price-types-rates-group-list.component';

@Component({
    selector: 'app-event-price-types-rates-group',
    templateUrl: './event-price-types-rates-group.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, TabsMenuComponent, TabDirective,
        EventPriceTypesRatesGroupFiltersComponent, EventPriceTypesRatesGroupListComponent,
        PriceTypeTranslationsComponent, SelectSearchComponent, EllipsifyDirective,
        MatFormFieldModule, MatSelectModule, MatTooltip, MatOption, ReactiveFormsModule,
        TranslatePipe, AsyncPipe, MatProgressSpinner
    ]
})
export class EventPriceTypesRatesGroupComponent implements OnInit, OnDestroy {
    readonly #eventsSrv = inject(EventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #venueTplPriceTypesSrv = inject(VenueTemplatePriceTypesService);
    readonly #venueTplSrv = inject(VenueTemplatesService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #ref = inject(ChangeDetectorRef);
    readonly #destroyRef = inject(DestroyRef);

    private readonly _priceTypeTranslations = viewChild(PriceTypeTranslationsComponent);

    readonly form = new FormGroup({});
    readonly venueCtrl = new FormControl<EventVenueTpl>(null);
    readonly compareWith = compareWithIdOrCode;
    readonly languages$ = this.#eventsSrv.event.get$().pipe(
        first(),
        map(event => event.settings?.languages?.selected)
    );

    readonly venueTpls$ = this.#eventsSrv.event.get$()
        .pipe(
            first(),
            map(event => {
                this.venueCtrl.setValue(event.venue_templates[0]);
                return event.venue_templates;
            })
        );

    readonly isInProgress$ = booleanOrMerge([
        this.#eventsSrv.ratesGroup.loading$(),
        this.#eventsSrv.eventPrices.inProgress$(),
        this.#eventSessionsSrv.sessionList.inProgress$(),
        this.#venueTplPriceTypesSrv.isVenueTemplatePriceTypeChannelContentLoading$(),
        this.#venueTplPriceTypesSrv.isPriceTypeChannelContentSaving$()
    ]);

    readonly #$event = toSignal(this.#eventsSrv.event.get$());
    readonly $isSga = computed(() => this.#$event()?.additional_config?.inventory_provider === ExternalInventoryProviders.sga);

    constructor() {
        effect(() => {
            if (this.$isSga()) {
                const template = this.#$event()?.venue_templates?.[0];
                if (template) {
                    this.venueCtrl.setValue(template);
                    this.#venueTplSrv.loadVenueTemplatePriceTypes(template.id);
                }
            }
        });
    }

    ngOnInit(): void {
        this.venueCtrl.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(venueTpl => {
                this.#venueTplSrv.clearVenueTemplatePriceTypes();
                if (venueTpl) {
                    this.#venueTplSrv.loadVenueTemplatePriceTypes(venueTpl.id);
                }
            });
    }

    ngOnDestroy(): void {
        this.#venueTplSrv.clearVenueTemplatePriceTypes();
    }

    cancel(): void {
        this._priceTypeTranslations()?.reset();
    }

    save$(): Observable<void> {
        if (this._priceTypeTranslations() && this.form.dirty && this.form.valid) {
            return this._priceTypeTranslations().save()
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    savePrices(): void {
        this.save$()
            .subscribe(() => this.#ref.markForCheck());
    }

}
