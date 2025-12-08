import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { VenueType, VenuesService, PostVenueRequest, venueFieldsRestrictions } from '@admin-clients/cpanel/venues/data-access';
import {
    CountriesService, RegionsService, EntitiesBaseService, EntitiesBaseState, EntitiesFilterFields, TimezonesService
} from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectOption } from '@admin-clients/shared/common/ui/components';
import { GoogleCloudService } from '@admin-clients/shared/data-access/google-cloud';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { combineLatest } from 'rxjs';
import { filter, first, map, startWith, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-new-venue-dialog',
    templateUrl: './new-venue-dialog.component.html',
    styleUrls: ['./new-venue-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [EntitiesBaseService, EntitiesBaseState],
    standalone: false
})
export class NewVenueDialogComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #authSrv = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #countriesService = inject(CountriesService);
    readonly #regionsService = inject(RegionsService);
    readonly #venuesService = inject(VenuesService);
    readonly #timezonesService = inject(TimezonesService);
    readonly #googleCloudService = inject(GoogleCloudService);

    readonly #dialogRef = inject<MatDialogRef<NewVenueDialogComponent, number>>(MatDialogRef);

    #placesService: google.maps.places.PlacesService;
    readonly #googleMapsPlaceDetailsOptions: google.maps.places.PlaceDetailsRequest = {
        placeId: '',
        fields: ['formatted_address']
    };

    readonly entities$ = this.#entitiesService.entityList.getData$()
        .pipe(
            map(entities => {
                const entitiesFilterOption: SelectOption[] = entities?.map(entity => ({
                    id: entity.id,
                    name: entity.name
                }));
                return entitiesFilterOption;
            })
        );

    readonly $externalProviderVenues = toSignal(this.#entitiesService.getEntity$()
        .pipe(
            filter(Boolean),
            tap(entity => {
                this.#entitiesService.externalVenues.load(entity.id);
            }),
            switchMap(() => this.#entitiesService.externalVenues.get$()),
            filter(Boolean),
            tap(externalVenues => {
                if (externalVenues?.length > 0) {
                    this.form.get('external_id').enable();
                    this.form.get('external_id').setValidators([Validators.required]);
                } else {
                    this.form.get('external_id').disable();
                    this.form.get('external_id').clearValidators();
                }
            })
        ));

    readonly $timezones = toSignal(this.#timezonesService.timezones.get$().pipe(filter(Boolean)));
    readonly $countries = toSignal(this.#countriesService.allCountries.get$().pipe(filter(Boolean)));
    readonly $regions = toSignal(this.#regionsService.getRegions$().pipe(filter(Boolean)));

    readonly $timezone = signal<string>(null);
    readonly $country = signal<string>(null);
    readonly $province = signal<string>(null);
    readonly $googleAddress = signal<string>(null);

    readonly moreEntitiesAvailable$ = this.#entitiesService.entityList.getMetadata$()
        .pipe(map(metadata => metadata?.offset + metadata?.limit < metadata?.total));

    form: UntypedFormGroup;

    readonly canReadMultipleEntities$ = this.#authSrv.canReadMultipleEntities$();
    readonly $canReadMultipleEntities = toSignal(this.canReadMultipleEntities$);

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#venuesService.isVenueSaving$(),
        this.#countriesService.allCountries.loading$(),
        this.#regionsService.isRegionsLoading$(),
        this.#googleCloudService.placesLibrary.isInProgress$(),
        this.#googleCloudService.timezone.isInProgress$(),
        this.#timezonesService.timezones.loading$(),
        this.#entitiesService.externalVenues.loading$(),
        this.#entitiesService.isEntityLoading$()
    ]));

    readonly venueTypes = VenueType;
    readonly #PAGE_LIMIT = 100;

    constructor() {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        this.#initGoogleMapsPlacesService();
    }

    ngOnInit(): void {
        this.#initForm();
        this.#countriesService.allCountries.load();
        this.#timezonesService.timezones.load();
        this.#initFormHandlers();
    }

    ngOnDestroy(): void {
        this.#entitiesService.entityList.clear();
        this.#countriesService.allCountries.clear();
        this.#regionsService.clearRegions();
        this.#googleCloudService.placesLibrary.clear();
        this.#placesService = null;
    }

    createVenue(): void {
        if (this.form.valid) {
            const formValue = this.form.getRawValue();
            const data: PostVenueRequest = {
                name: formValue.name,
                type: formValue.type,
                capacity: formValue.capacity,
                timezone: formValue.timezone,
                country_code: formValue.country,
                country_subdivision_code: formValue.countrySubdivision,
                city: formValue.city,
                address: formValue.address,
                postal_code: formValue.postalCode,
                coordinates: {
                    latitude: formValue.latitude,
                    longitude: formValue.longitude
                },
                google_place_id: formValue.googlePlaceId,
                external_id: formValue.external_id
            };
            if (this.$canReadMultipleEntities()) {
                data.entity_id = formValue.entity.id;
            }
            this.#venuesService.createVenue(data)
                .subscribe(venueId => this.close(venueId));
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(venueId: number = null): void {
        this.#dialogRef.close(venueId);
    }

    loadEntities(q: string, next = false): void {
        this.#entitiesService.loadServerSearchEntityList({
            limit: this.#PAGE_LIMIT,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            q
        }, next);
    }

    #initGoogleMapsPlacesService(): void {
        this.#googleCloudService.placesLibrary.get$()
            .pipe(first(Boolean))
            .subscribe(placesLib => {
                this.#placesService = new placesLib.PlacesService(document.createElement('div'));
            });
    }

    #initForm(): void {
        this.form = this.#fb.group({
            entity: [{ value: null, disabled: true }, Validators.required],
            name: [null, Validators.required],
            type: [null, Validators.required],
            capacity: [null, [
                Validators.required,
                Validators.min(venueFieldsRestrictions.venueCapacityMinLength)
            ]],
            timezone: [null, Validators.required],
            address: [null, Validators.required],
            country: [null, Validators.required],
            countrySubdivision: [null, Validators.required],
            city: [null, Validators.required],
            googlePlaceId: [null, Validators.required],
            latitude: [null, Validators.required],
            longitude: [null, Validators.required],
            postalCode: [null, Validators.required],
            googlePlaceholder: [null, Validators.required],
            external_id: [null]
        });
    }

    #initFormHandlers(): void {
        this.canReadMultipleEntities$.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(canReadMultipleEntities => {
            if (canReadMultipleEntities) { this.form.get('entity').enable(); }
        });

        const googlePlaceIdCtrl = this.form.get('googlePlaceId');
        const googlePlaceholderCtrl = this.form.get('googlePlaceholder');
        googlePlaceIdCtrl.valueChanges
            .pipe(startWith(googlePlaceIdCtrl.value), takeUntilDestroyed(this.#destroyRef))
            .subscribe((placeId: string) => {
                if (!placeId) {
                    googlePlaceholderCtrl.setValidators([Validators.required]);
                } else {
                    googlePlaceholderCtrl.clearValidators();
                    if (googlePlaceholderCtrl.hasError('required')) {
                        googlePlaceholderCtrl.setErrors(null);
                    }
                }
                googlePlaceholderCtrl.updateValueAndValidity({ emitEvent: false });
                if (placeId) {
                    this.#loadGoogleAddress(placeId);
                }
            });

        this.form.get('timezone').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(timezoneOlsonID => {
                this.$timezone.set(this.$timezones().find(t => t.olson_id === timezoneOlsonID)?.name);
            });

        this.form.get('entity').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(entity => {
                this.#entitiesService.loadEntity(entity.id);
            });

        combineLatest([
            this.form.get('country').valueChanges,
            this.#countriesService.allCountries.get$().pipe(first(Boolean))
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([country, countries]) => {
                this.$country.set(countries.find(c => c.code === country)?.name);
            });

        combineLatest([
            this.form.get('countrySubdivision').valueChanges,
            this.#regionsService.getRegions$().pipe(filter(Boolean))
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([province, regions]) => {
                this.$province.set(regions.find(r => r.code === province)?.name);
            });
    }

    #loadGoogleAddress(placeId: string): void {
        this.#placesService.getDetails({ ...this.#googleMapsPlaceDetailsOptions, placeId }, (place, status) => {
            if (status === google.maps.places.PlacesServiceStatus.OK) {
                this.$googleAddress.set(place?.formatted_address || null);
            } else {
                this.$googleAddress.set(null);
            }
        });
    }
}
