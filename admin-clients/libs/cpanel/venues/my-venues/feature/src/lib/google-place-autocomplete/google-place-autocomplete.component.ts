/// <reference types="google.maps" />
import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CountriesService, RegionsService } from '@admin-clients/shared/common/data-access';
import { HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { GoogleCloudService } from '@admin-clients/shared/data-access/google-cloud';
import { ChangeDetectionStrategy, Component, ElementRef, inject, OnInit, viewChild, OnDestroy } from '@angular/core';
import { FormGroupDirective, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatError, MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslatePipe } from '@ngx-translate/core';
import { skip, map, take, first, shareReplay } from 'rxjs/operators';

@Component({
    selector: 'app-google-place-autocomplete',
    templateUrl: './google-place-autocomplete.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatFormField, MatInput, FormControlErrorsComponent, MatLabel, TranslatePipe, ReactiveFormsModule, MatError, HelpButtonComponent,
        MatSuffix
    ]
})
export class GooglePlaceAutocompleteComponent implements OnInit, OnDestroy {
    readonly #regionsService = inject(RegionsService);
    readonly #parentForm = inject(FormGroupDirective, { host: true });
    readonly #googleCloudService = inject(GoogleCloudService);
    readonly #countriesService = inject(CountriesService);

    readonly $placesInputRef = viewChild<ElementRef<HTMLInputElement>>('placesInput');

    readonly countriesWithMandatoryPC$ = this.#countriesService.allCountries.get$().pipe(
        first(Boolean),
        shareReplay(1),
        map(countries => countries.filter(country => country.tax_calculation === 'COUNTRY_ZIPCODE'))
    );

    form: UntypedFormGroup;

    #placeChangedListener: google.maps.MapsEventListener;
    #autocomplete: google.maps.places.Autocomplete;

    readonly #googleMapsAutocompleteOptions: google.maps.places.AutocompleteOptions = {
        fields: ['place_id', 'formatted_address', 'address_components', 'geometry']
    };

    ngOnInit(): void {
        this.form = this.#parentForm.form as UntypedFormGroup;
        this.#initPlacesAutocomplete();
        this.#googleCloudService.placesLibrary.load();
        this.#countriesService.allCountries.load();
    }

    ngOnDestroy(): void {
        this.#placeChangedListener?.remove();
        this.#autocomplete = undefined;
        this.#googleCloudService.timezone.clear();
        this.#googleCloudService.placesLibrary.clear();
        this.#countriesService.allCountries.clear();
    }

    #initPlacesAutocomplete(): void {
        this.#googleCloudService.placesLibrary.get$()
            .pipe(first(Boolean))
            .subscribe(placesLib => {
                this.#autocomplete = new placesLib.Autocomplete(
                    this.$placesInputRef()?.nativeElement,
                    this.#googleMapsAutocompleteOptions
                );
                this.#placeChangedListener = this.#autocomplete.addListener('place_changed', () => {
                    this.#handleAddressChange(this.#autocomplete.getPlace());
                });
            });
    }

    #handleAddressChange(place: google.maps.places.PlaceResult): void {
        if (!place.address_components) return;

        const country = this.#getMapsComponent(place, 'country')?.short_name;
        const postalCode = this.#getMapsComponent(place, 'postal_code')?.long_name;

        const level1 = this.#getMapsComponent(place, 'administrative_area_level_1')?.long_name;
        const level2 = this.#getMapsComponent(place, 'administrative_area_level_2')?.long_name;
        const locality = this.#getMapsComponent(place, 'locality')?.long_name;
        const postalTown = this.#getMapsComponent(place, 'postal_town')?.long_name;
        const streetNumber = this.#getMapsComponent(place, 'street_number')?.long_name;
        const route = this.#getMapsComponent(place, 'route')?.long_name;
        const sublocality = this.#getMapsComponent(place, 'sublocality_level_1')?.long_name;

        const latitude = place.geometry?.location?.lat().toFixed(4);
        const longitude = place.geometry?.location?.lng().toFixed(4);

        const address = place.formatted_address;

        // This is different depending on the country administrative area levels returned by Places API from Google
        const province = level2 || level1 || locality;
        const town = locality || level2 || postalTown;

        const result = {
            address,
            country,
            province,
            town,
            latitude,
            longitude
        };

        const customAddress = [
            route ? `${route}${streetNumber ? (' ' + streetNumber) : ''}` : null,
            sublocality
        ].filter(Boolean).join(', ');

        if (result.latitude && result.longitude) { this.#googleCloudService.timezone.load(result.latitude, result.longitude); }

        this.countriesWithMandatoryPC$.subscribe(countries => {
            const mandatoryCP = countries.find(c => c.code === result.country);
            const isValidLocation = Object.values(result).every(r => !!r) && (!mandatoryCP || !!postalCode);

            if (!mandatoryCP) {
                this.form.get('postalCode').setValidators(null);
            } else {
                this.form.get('postalCode').setValidators([Validators.required]);
            }

            if (!isValidLocation) {
                this.form.get('googlePlaceId').patchValue(null);
                this.form.get('timezone').patchValue(null);
                this.form.get('country').patchValue(null);
                this.form.get('countrySubdivision').patchValue(null);
                this.form.get('city').patchValue(null);
                this.form.get('address').patchValue(null);
                this.form.get('postalCode').patchValue(null);
                this.form.get('latitude').patchValue(null);
                this.form.get('longitude').patchValue(null);
                this.form.get('googlePlaceholder').setErrors({ invalidLocation: true });
            } else {
                this.form.get('googlePlaceholder').setErrors(null);
                this.#googleCloudService.timezone.get$().pipe(skip(1), take(1))
                    .subscribe(timezone => {
                        this.form.get('googlePlaceId').patchValue(place.place_id);
                        this.form.get('timezone').patchValue(timezone.timeZoneId);
                        this.form.get('city').patchValue(result.town);
                        this.form.get('latitude').patchValue(result.latitude);
                        this.form.get('longitude').patchValue(result.longitude);
                        this.form.get('postalCode').patchValue(postalCode);
                        this.form.get('address').patchValue(customAddress);
                        this.form.get('country').patchValue(result.country);

                        this.#regionsService.loadSystemRegions(result.country);
                        this.#regionsService.getRegions$().pipe(
                            skip(1),
                            take(1),
                            map(regions => regions.find(
                                region => region.name === result.province) || regions.find(r => r.code.includes('NOT_DEF'))
                            )
                        )
                            .subscribe(region => {
                                this.form.get('countrySubdivision').patchValue(region.code);
                            });
                    });
            }
        });
    }

    #getMapsComponent(place: google.maps.places.PlaceResult, type: string): { short_name: string; long_name: string } {
        return place.address_components.find(c => c.types.includes(type)) || null;
    }
}
