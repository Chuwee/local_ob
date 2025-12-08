import { VenuesService, VenuesState } from '@admin-clients/cpanel/venues/data-access';
import { EntitiesBaseService, EntitiesBaseState, EntitiesFilterFields } from '@admin-clients/shared/common/data-access';
import {
    EmptyStateComponent, ListFiltersService, SearchInputComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ReactiveFormsModule } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { merge } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { NewVenueTemplateDialogMode } from '../models/new-venue-template-dialog-mode.enum';
import { VenueSelectionForm } from '../models/new-venue-template-form.model';

@Component({
    selector: 'app-venue-selection',
    templateUrl: './venue-selection.component.html',
    styleUrls: ['./venue-selection.component.scss'],
    imports: [
        TranslatePipe, ReactiveFormsModule, AsyncPipe, MatFormField, MatLabel, MatOption,
        MatSelect, MatError, MatTooltip, MatPaginator, MatSelectionList, MatListOption,
        MatProgressSpinner, SearchInputComponent, SelectSearchComponent, EmptyStateComponent,
        LocalNumberPipe, EllipsifyDirective
    ],
    providers: [VenuesService, VenuesState, EntitiesBaseService, EntitiesBaseState, ListFiltersService],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueSelectionComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #PAGE_SIZE = 20;

    readonly #translateSrv = inject(TranslateService);
    readonly #venuesSrv = inject(VenuesService);
    readonly #entitiesSrv = inject(EntitiesBaseService);

    readonly form = input<VenueSelectionForm>();
    readonly prefixedEntityId = input<IdName>();
    readonly mode = input<NewVenueTemplateDialogMode>();

    readonly loading$ = booleanOrMerge([
        this.#entitiesSrv.entityList.inProgress$(),
        this.#venuesSrv.isVenueCountriesListLoading$(),
        this.#venuesSrv.isVenueCitiesListLoading$(),
        this.#venuesSrv.venuesList.isLoading$()
    ]);

    readonly entities$ = this.#entitiesSrv.entityList.getData$();
    readonly venues$ = this.#venuesSrv.venuesList.getData$();
    readonly venueListMetadata$ = this.#venuesSrv.venuesList.getMetadata$();

    readonly countries$ = this.#venuesSrv.getVenueCountriesListData$().pipe(
        filter(Boolean),
        map(countries => countries
            .map(country => ({ code: country.code, name: this.#translateSrv.instant('COUNTRIES.' + country.code) }))
            .sort((a, b) => a.name.localeCompare(b.name))
        ));

    readonly cities$ = this.#venuesSrv.getVenueCitiesListData$();

    ngOnInit(): void {
        // init data load
        if (this.prefixedEntityId() == null) {
            this.loadEntityList();
        }
        this.loadVenueList();
        this.loadVenueCountries();
        this.loadVenueCities();
        // filter side effects loads
        this.form().controls.country.valueChanges.pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => {
                if (this.form().controls.city.value) {
                    this.form().controls.city.setValue(null, { emitEvent: false });
                }
                this.loadVenueList();
                this.loadVenueCities();
            });
        merge(
            this.form().controls.entity.valueChanges,
            this.form().controls.city.valueChanges,
            this.form().controls.keyword.valueChanges
        )
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(() => this.loadVenueList());
    }

    changeKeyword(keyword: string): void {
        this.form().controls.keyword.setValue(keyword);
    }

    loadVenueList(pageEvent: PageEvent = null): void {
        if (this.mode() === NewVenueTemplateDialogMode.venueTemplate
            && !this.form().controls.entity.value && !this.prefixedEntityId()?.id) {
            // venue mode needs entity id to load venues
            return;
        }
        this.form().controls.selectedVenue.setValue(null);
        const offset = pageEvent ? pageEvent.pageIndex * pageEvent.pageSize : 0;
        this.#venuesSrv.venuesList.load({
            limit: this.#PAGE_SIZE,
            offset,
            includeOwnTemplateVenues: this.mode() !== NewVenueTemplateDialogMode.venueTemplate,
            includeThirdPartyVenues: this.mode() === NewVenueTemplateDialogMode.promoterTemplate,
            entityId: this.form().controls.entity.value?.id || this.prefixedEntityId()?.id,
            countryCode: this.form().controls.country.value?.code || undefined,
            city: this.form().controls.city.value?.name || undefined,
            q: this.form().controls.keyword.value || ''
        });
    }

    private loadEntityList(): void {
        this.#entitiesSrv.entityList.load({
            limit: 999,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            type: 'EVENT_ENTITY'
        });
    }

    private loadVenueCountries(): void {
        this.#venuesSrv.loadVenueCountriesList({
            limit: 999,
            entityId: this.prefixedEntityId()?.id,
            includeThirdPartyVenues: true,
            includeOwnTemplateVenues: true
        });
    }

    private loadVenueCities(): void {
        this.#venuesSrv.loadVenueCitiesList({
            limit: 999,
            countryCode: this.form().controls.country.value?.code,
            entityId: this.prefixedEntityId()?.id,
            includeThirdPartyVenues: true,
            includeOwnTemplateVenues: true
        });
    }
}
