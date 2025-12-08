import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { CountriesService, RegionsService, PutEntity } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, SearchablePaginatedSelectionModule, SelectSearchComponent, HelpButtonComponent
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, QueryList } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatLabel, MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-entity-contact',
    templateUrl: './entity-contact.component.html',
    styleUrls: ['./entity-contact.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, AsyncPipe, ReactiveFormsModule, TranslatePipe, FormControlErrorsComponent,
        SearchablePaginatedSelectionModule, MatInput, MatLabel, MatFormField, MatProgressSpinner, MatSelect, MatOption,
        SelectSearchComponent, HelpButtonComponent, FlexLayoutModule, MatSuffix
    ]
})
export class EntityContactComponent implements OnInit, AfterViewInit, WritingComponent {

    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #countriesSrv = inject(CountriesService);
    readonly #regionsSrv = inject(RegionsService);

    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly form = this.#fb.group({
        address: ['', Validators.required],
        email: ['', Validators.email],
        country: ['', Validators.required],
        region: '',
        city: '',
        phone: ['', Validators.pattern(/^[0-9+ ]*$/)],
        postalCode: ''
    });

    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.isEntityLoading$(),
        this.#entitiesSrv.isEntitySaving$()
    ]);

    readonly countries$ = this.#countriesSrv.getCountries$()
        .pipe(
            filter(countries => !!countries),
            map(countries => countries.map(country => ({ name: country.name, code: country.code })))
        );

    readonly regions$ = this.#regionsSrv.getRegions$()
        .pipe(
            filter(regions => !!regions),
            map(regions => regions.map(region => ({ name: region.name, code: region.code })))
        );

    readonly entity$ = this.#entitiesSrv.getEntity$()
        .pipe(filter(entity => !!entity));

    readonly $entityId = toSignal(this.entity$.pipe(map(entity => entity.id)));

    ngOnInit(): void {
        this.#countriesSrv.loadCountries();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    save(): void {
        this.save$().subscribe(() => this.form.markAsPristine());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const { address, email, country, region, city, phone, postalCode } = this.form.value;
            const updatedEntity: PutEntity = {
                contact: {
                    address, email, city, phone,
                    postal_code: postalCode,
                    country: { code: country },
                    country_subdivision: { code: region || '' }
                }
            };
            return this.#entitiesSrv.updateEntity(this.$entityId(), updatedEntity)
                .pipe(tap(() => {
                    this.#entitiesSrv.loadEntity(this.$entityId());
                    this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                }));
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.#entitiesSrv.loadEntity(this.$entityId());
    }

    private refreshFormDataHandler(): void {
        this.form.get('country').valueChanges
            .pipe(
                takeUntilDestroyed(this.#destroyRef),
                filter(countryCode => !!countryCode)
            )
            .subscribe(countryCode => {
                this.#regionsSrv.loadSystemRegions(countryCode);
            });

        this.entity$
            .pipe(
                filter(entity => !!entity),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(({ contact }) => {
                this.form.patchValue({
                    address: contact.address,
                    email: contact.email,
                    city: contact.city,
                    phone: contact.phone,
                    postalCode: contact.postal_code,
                    country: contact.country?.code,
                    region: contact.country_subdivision?.code
                });
                this.form.markAsPristine();
            });

    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }
}
