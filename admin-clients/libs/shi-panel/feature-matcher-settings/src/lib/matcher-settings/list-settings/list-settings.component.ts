import { CountriesService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { SupplierName, UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, filter, map, shareReplay, tap } from 'rxjs';
import { MatcherSettingsApi } from '../../api/matcher-settings.api';
import { MatcherSettingsService } from '../../matcher-settings.service';
import { MatcherConfiguration, PutMatcherConfigurationRequest } from '../../models/matcher-configuration.model';
import { MatcherSettingsState } from '../../state/matcher-settings.state';
import { NewValueDialogComponent } from '../add-value/add-value.component';
import { ListSettingContainerComponent } from './list-container/list-setting-container.component';

@Component({
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, FormContainerComponent, FlexLayoutModule, ListSettingContainerComponent,
        MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle
    ],
    selector: 'app-list-settings',
    templateUrl: './list-settings.component.html',
    styleUrls: ['./list-settings.component.scss'],
    providers: [MatcherSettingsService, MatcherSettingsApi, MatcherSettingsState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ListSettingsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #matcherSettingsService = inject(MatcherSettingsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #dialog = inject(MatDialog);
    readonly #countriesService = inject(CountriesService);

    readonly #form = this.#fb.group({
        countries: [[] as string[]],
        excluded_states: [[] as string[]],
        excluded_taxonomies: [[] as string[]],
        keywords: [[] as string[]]
    });

    #initialFormValue = this.#form.getRawValue();

    readonly #selectedSupplierName = new BehaviorSubject<string>('');
    readonly #formChanged = new BehaviorSubject<boolean>(false);

    readonly formChanged$ = this.#formChanged.asObservable();

    readonly pageLimit = 10;

    readonly countryCodes$ = this.#countriesService.getCountries$().pipe(
        filter(Boolean),
        map(countries => countries.map(country => country.code)),
        shareReplay(1)
    );

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.matchingWrite]))
    );

    readonly form = this.#form;

    ngOnInit(): void {
        this.#activatedRoute.queryParams.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(params => {
            this.#form.reset();
            const supplierId = params['supplier'] || SupplierName.tevo;
            this.#selectedSupplierName.next(supplierId);
            this.#matcherSettingsService.matcherConfiguration.load(supplierId);
        });

        this.#matcherSettingsService.matcherConfiguration.getMatcherConfiguration$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(matcherSettings => { this.updateForm(matcherSettings); });

        this.#form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe((form: PutMatcherConfigurationRequest) => {
            this.#formChanged.next(JSON.stringify(form) !== JSON.stringify(this.#initialFormValue));
            if (this.#formChanged.getValue()) {
                this.#form.markAsDirty();
            } else {
                this.#form.markAsPristine();
            }
        });

        this.#countriesService.loadCountries();
    }

    addElement(controlName: string, placeHolder: string, arrayValidator?: string[]): void {
        openDialog(this.#dialog, NewValueDialogComponent, { placeHolder, arrayValidator })
            .beforeClosed()
            .subscribe(response => {
                if (response?.newValue && !this.#form.value[controlName].includes(response.newValue)) {
                    const newFormValue = this.#form.value as MatcherConfiguration;
                    newFormValue[controlName] = [...newFormValue[controlName], response.newValue];
                    this.#form.setValue(newFormValue);
                }
            });
    }

    deleteElement(element: string, controlName: string): void {
        this.#form.controls[controlName].setValue(this.#form.value[controlName].filter(c => c !== element));
    }

    cancel(): void {
        this.#matcherSettingsService.matcherConfiguration.load(this.#selectedSupplierName.getValue());
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#matcherSettingsService.matcherConfiguration.load(this.#selectedSupplierName.getValue());
        });
    }

    save$(): Observable<void> {
        return this.#matcherSettingsService.matcherConfiguration.updateMatcherConfiguration(
            this.#selectedSupplierName.getValue(),
            this.#form.value
        ).pipe(
            tap(() => {
                this.#ephemeralSrv.showSaveSuccess();
            })
        );
    }

    private updateForm(matcherConfiguration: MatcherConfiguration): void {
        this.#initialFormValue = {
            countries: matcherConfiguration.countries ?? [],
            excluded_states: matcherConfiguration.excluded_states ?? [],
            excluded_taxonomies: matcherConfiguration.excluded_taxonomies ?? [],
            keywords: matcherConfiguration.keywords ?? []
        };
        this.#form.setValue(this.#initialFormValue);
    }
}
