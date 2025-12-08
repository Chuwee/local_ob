/// <reference types="google.maps" />
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    venueImageRestrictions, VenueType, VenuesService, PutVenueRequest, venueFieldsRestrictions, VenueDetails
} from '@admin-clients/cpanel/venues/data-access';
import {
    CountriesService, RegionsService, TimezonesService, EntitiesBaseService
} from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { GoogleCloudService } from '@admin-clients/shared/data-access/google-cloud';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, OnDestroy, OnInit, signal, viewChildren
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, Observable, of, throwError } from 'rxjs';
import { filter, first, shareReplay, switchMap, tap, startWith } from 'rxjs/operators';

@Component({
    selector: 'app-venue-general-data',
    templateUrl: './venue-general-data.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VenueGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #venuesService = inject(VenuesService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #timezonesService = inject(TimezonesService);
    readonly #countriesService = inject(CountriesService);
    readonly #regionsService = inject(RegionsService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #googleCloudService = inject(GoogleCloudService);

    readonly $matExpansionPanelQueryList = viewChildren(MatExpansionPanel);

    form: UntypedFormGroup;
    mainInfoGroup: UntypedFormGroup;
    locationGroup: UntypedFormGroup;
    companyGroup: UntypedFormGroup;
    contactGroup: UntypedFormGroup;
    webComGroup: UntypedFormGroup;

    #placesService: google.maps.places.PlacesService;
    readonly #googleMapsPlaceDetailsOptions: google.maps.places.PlaceDetailsRequest = {
        placeId: '',
        fields: ['formatted_address']
    };

    readonly #$entity = toSignal(this.#entitiesService.getEntity$().pipe(filter(Boolean)));
    readonly #$venue = toSignal(this.#venuesService.getVenue$().pipe(filter(Boolean)));

    readonly $isInProgress = toSignal(booleanOrMerge([
        this.#venuesService.isVenueLoading$(),
        this.#venuesService.isVenueSaving$(),
        this.#entitiesService.isEntityCalendarsLoading$(),
        this.#countriesService.allCountries.loading$(),
        this.#regionsService.isRegionsLoading$(),
        this.#timezonesService.timezones.loading$(),
        this.#googleCloudService.timezone.isInProgress$(),
        this.#googleCloudService.placesLibrary.isInProgress$(),
        this.#entitiesService.externalVenues.loading$()
    ]));

    readonly $externalProviderVenues = toSignal(
        this.#entitiesService.externalVenues.get$().pipe(filter(externalVenues => externalVenues?.length > 0))
    );

    readonly entityCalendars$ = this.#venuesService.getVenue$()
        .pipe(
            first(venue => !!venue),
            switchMap(venue => {
                this.#entitiesService.loadEntityCalendars(venue.entity.id);
                return this.#entitiesService.getEntityCalendars$();
            }),
            tap(entityCalendars => {
                if (entityCalendars?.length) {
                    this.mainInfoGroup.get('calendar').enable();
                }
            }),
            shareReplay(1)
        );

    readonly $country = signal<string>(null);
    readonly $province = signal<string>(null);
    readonly $timezone = signal<string>(null);
    readonly $googleAddress = signal<string>(null);

    readonly $entityName = computed(() => this.#$venue().entity.name);
    readonly $venueId = computed(() => this.#$venue().id);

    readonly imageRestrictions = venueImageRestrictions;
    readonly venueTypes = VenueType;

    constructor() {
        effect(() => {
            if (this.$externalProviderVenues()?.length > 0) {
                this.mainInfoGroup.get('external_id').enable();
                this.mainInfoGroup.get('external_id').setValidators([Validators.required]);
            } else {
                this.mainInfoGroup.get('external_id').disable();
                this.mainInfoGroup.get('external_id').clearValidators();
            }
        });

        effect(() => {
            if (this.#$entity()?.id) {
                this.#entitiesService.externalVenues.load(this.#$entity().id);
            }
        });
    }

    ngOnInit(): void {
        this.#initForm();
        this.#timezonesService.timezones.load();
        this.#countriesService.allCountries.load();
        this.#initGoogleMapsPlacesService();
        this.#entitiesService.loadEntity(this.#$venue().entity.id);

        this.#googleCloudService.placesLibrary.get$().pipe(
            first(Boolean),
            switchMap(() => this.#venuesService.getVenue$().pipe(filter(Boolean))),
            tap(venue => {
                this.#updateFormValues(venue);
                this.#regionsService.loadSystemRegions(venue.country?.code);
                if (venue.google_place_id) {
                    this.#loadGoogleAddress(venue.google_place_id);
                }
            }),
            switchMap(venue => combineLatest([
                this.#countriesService.allCountries.get$().pipe(first(Boolean)),
                this.#regionsService.getRegions$().pipe(first(Boolean)),
                of(venue)
            ])),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(([countries, regions, venue]) => {
            this.$country.set(countries.find(c => c.code === venue.country?.code)?.name);
            this.$province.set(regions.find(r => r.code === venue.country_subdivision?.code)?.name);
        });

        this.#timezonesService.timezones.get$().pipe(first(Boolean)).subscribe(timezones => {
            this.$timezone.set(timezones.find(t => t.olson_id === this.form.get('locationGroup').get('timezone').value)?.name);
        });

        this.#initFormHandlers();
    }

    ngOnDestroy(): void {
        this.#entitiesService.clearEntityCalendars();
        this.#countriesService.allCountries.clear();
        this.#regionsService.clearRegions();
        this.#googleCloudService.placesLibrary.clear();
        this.#entitiesService.externalVenues.clear();
        this.#entitiesService.clearEntity();
        this.#placesService = null;
    }

    cancel(): void {
        this.#reloadModels();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const mainInfoGroup = this.mainInfoGroup.value;
            const locationGroup = this.locationGroup.getRawValue();
            const companyGroup = this.companyGroup.value;
            const contactGroup = this.contactGroup.value;
            const webComGroup = this.webComGroup.value;
            const request: PutVenueRequest = {
                name: mainInfoGroup.name,
                type: mainInfoGroup.type,
                capacity: mainInfoGroup.capacity,
                calendar_id: mainInfoGroup.calendar,
                external_id: mainInfoGroup.external_id,
                public: mainInfoGroup.public,
                timezone: locationGroup.timezone,
                country_code: locationGroup.country,
                country_subdivision_code: locationGroup.countrySubdivision,
                city: locationGroup.city,
                address: locationGroup.address,
                postal_code: locationGroup.postalCode,
                coordinates: {
                    latitude: locationGroup.latitude,
                    longitude: locationGroup.longitude
                },
                manager: companyGroup.manager,
                owner: companyGroup.owner,
                website: companyGroup.website,
                image_logo: webComGroup.venueImage ? webComGroup.venueImage.data : null,
                contact: {
                    name: contactGroup.contactName,
                    surname: contactGroup.contactSurname,
                    job: contactGroup.contactJob,
                    email: contactGroup.contactEmail,
                    phone: contactGroup.contactPhone
                },
                google_place_id: locationGroup.googlePlaceId
            };
            return this.#venuesService.saveVenue(this.$venueId(), request)
                .pipe(tap(() => this.#ephemeralMessage.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this.$matExpansionPanelQueryList());
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => this.#reloadModels());
    }

    #initGoogleMapsPlacesService(): void {
        this.#googleCloudService.placesLibrary.get$()
            .pipe(first(Boolean))
            .subscribe(placesLib => {
                this.#placesService = new placesLib.PlacesService(document.createElement('div'));
            });
    }

    #initForm(): void {
        this.mainInfoGroup = this.#fb.group({
            name: [null, Validators.required],
            type: null,
            capacity: [null, Validators.min(venueFieldsRestrictions.venueCapacityMinLength)],
            calendar: { value: null, disabled: true },
            public: false,
            external_id: null
        });
        this.locationGroup = this.#fb.group({
            googlePlaceId: [null, Validators.required],
            latitude: [null, Validators.required],
            longitude: [null, Validators.required],
            timezone: [null, Validators.required],
            country: [null, Validators.required],
            countrySubdivision: [null, Validators.required],
            city: [null, Validators.required],
            address: [null, Validators.required],
            postalCode: [null],
            googlePlaceholder: [null, Validators.required]
        });
        this.companyGroup = this.#fb.group({
            manager: null,
            owner: null,
            website: null
        });
        this.contactGroup = this.#fb.group({
            contactName: null,
            contactSurname: null,
            contactJob: null,
            contactEmail: [null, Validators.email],
            contactPhone: [null, Validators.pattern(venueFieldsRestrictions.venuePhonePattern)]
        });
        this.webComGroup = this.#fb.group({
            venueImage: null
        });
        this.form = this.#fb.group({
            mainInfoGroup: this.mainInfoGroup,
            locationGroup: this.locationGroup,
            companyGroup: this.companyGroup,
            contactGroup: this.contactGroup,
            webComGroup: this.webComGroup
        });
    }

    #updateFormValues(venue: VenueDetails): void {
        this.form.patchValue({
            mainInfoGroup: {
                name: venue.name,
                type: venue.type,
                capacity: venue.capacity,
                calendar: venue.calendar?.id,
                public: venue.public,
                external_id: venue.external_id
            },
            locationGroup: {
                timezone: venue.timezone,
                country: venue.country?.code,
                countrySubdivision: venue.country_subdivision?.code,
                city: venue.city,
                address: venue.address,
                postalCode: venue.postal_code,
                latitude: venue.coordinates?.latitude,
                longitude: venue.coordinates?.longitude,
                googlePlaceId: venue.google_place_id
            },
            companyGroup: {
                manager: venue.manager,
                owner: venue.owner,
                website: venue.website
            },
            contactGroup: {
                contactName: venue.contact?.name,
                contactSurname: venue.contact?.surname,
                contactJob: venue.contact?.job,
                contactEmail: venue.contact?.email,
                contactPhone: venue.contact?.phone
            },
            webComGroup: {
                venueImage: venue.image_logo_url
            }
        });
        this.form.get('locationGroup').get('googlePlaceholder').setValue(null);
        this.form.markAsPristine();
    }

    #reloadModels(): void {
        this.#venuesService.loadVenue(this.$venueId());
        this.form.markAsPristine();
        this.form.get('locationGroup').get('googlePlaceholder').reset();
    }

    #initFormHandlers(): void {
        const googlePlaceIdCtrl = this.form.get('locationGroup').get('googlePlaceId');
        const googlePlaceholderCtrl = this.form.get('locationGroup').get('googlePlaceholder');

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

        combineLatest([
            this.form.get('locationGroup').get('timezone').valueChanges,
            this.#timezonesService.timezones.get$().pipe(filter(Boolean))
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([timezoneOlsonID, timezones]) => {
                this.$timezone.set(timezones.find(t => t.olson_id === timezoneOlsonID)?.name);
            });

        combineLatest([
            this.form.get('locationGroup').get('country').valueChanges,
            this.#countriesService.allCountries.get$().pipe(first(Boolean))
        ]).pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([country, countries]) => {
                this.$country.set(countries.find(c => c.code === country)?.name);
            });

        combineLatest([
            this.form.get('locationGroup').get('countrySubdivision').valueChanges,
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
