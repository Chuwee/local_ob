import {
    EmptyStateTinyComponent, EphemeralMessageService, SearchInputComponent, openDialog
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { SupplierName, UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, combineLatest, filter, map, shareReplay, take, tap } from 'rxjs';
import { MatcherSettingsApi } from '../../api/matcher-settings.api';
import { MatcherSettingsService } from '../../matcher-settings.service';
import { MatcherConfiguration, PutMatcherConfigurationRequest } from '../../models/matcher-configuration.model';
import { MatcherSettingsState } from '../../state/matcher-settings.state';
import { NewValueDialogComponent } from '../add-value/add-value.component';

@Component({
    imports: [
        AsyncPipe, MaterialModule, TranslatePipe, ReactiveFormsModule, FormContainerComponent,
        FlexLayoutModule, SearchInputComponent, EmptyStateTinyComponent, EllipsifyDirective
    ],
    selector: 'app-export-settings',
    templateUrl: './export-settings.component.html',
    styleUrls: ['./export-settings.component.scss'],
    providers: [MatcherSettingsService, MatcherSettingsApi, MatcherSettingsState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportSettingsComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #matcherSettingsService = inject(MatcherSettingsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #dialog = inject(MatDialog);

    readonly #emailsPage = new BehaviorSubject<number>(0);
    readonly #emailsSearchText = new BehaviorSubject<string>('');
    readonly #selectedSupplierName = new BehaviorSubject<string>('');
    readonly #formChanged = new BehaviorSubject<boolean>(false);

    readonly #form = this.#fb.group({
        enabled: [false as boolean],
        recipients: [[] as string[]]
    });

    #initialFormValue = this.#form.getRawValue();

    readonly formChanged$ = this.#formChanged.asObservable();

    readonly isLoading$ = this.#matcherSettingsService.matcherConfiguration.isLoading$();
    readonly pageLimit = 10;

    readonly emailsPage$ = this.#emailsPage.asObservable();

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.matchingWrite]))
    );

    readonly filteredEmails$ = combineLatest([
        this.#form.controls.recipients.valueChanges,
        this.#emailsSearchText
    ]).pipe(
        filter(Boolean),
        map(([recipients, searchText]) => recipients?.filter(e => e.toLocaleUpperCase().includes(searchText.toLocaleUpperCase()))),
        shareReplay(1)
    );

    readonly pagedEmails$ = combineLatest([
        this.filteredEmails$,
        this.#emailsPage
    ]).pipe(
        filter(Boolean),
        map(([filteredEmails, page]) => {
            let pagedCountries = filteredEmails?.slice(page * this.pageLimit, (page + 1) * this.pageLimit);
            if (filteredEmails?.length > 0 && pagedCountries?.length === 0) {
                pagedCountries = filteredEmails?.slice((page - 1) * this.pageLimit, (page) * this.pageLimit);
                this.#emailsPage.next(this.#emailsPage.getValue() - 1);
            }
            return pagedCountries;
        }),
        shareReplay(1)
    );

    readonly form = this.#form;

    ngOnInit(): void {
        this.#activatedRoute.queryParams.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(params => {
            this.#form.reset();
            const supplierId = params['supplier'] || SupplierName.tevo;
            this.#selectedSupplierName.next(supplierId);
            this.#matcherSettingsService.matcherConfiguration.load(supplierId);
        });

        this.hasWritePermissions$.pipe(take(1)).subscribe(writePermission => {
            if (!writePermission) {
                this.#form.controls.enabled.disable();
            }
        });

        this.#matcherSettingsService.matcherConfiguration.getMatcherConfiguration$()
            .pipe(takeUntilDestroyed(this.#destroyRef), filter(Boolean))
            .subscribe(matcherSettings => { this.updateForm(matcherSettings); });

        this.#form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe((form: PutMatcherConfigurationRequest) => {
            this.#formChanged.next(JSON.stringify(this.#form.getRawValue()) !== JSON.stringify(this.#initialFormValue));
            if (this.#formChanged.getValue()) {
                this.#form.markAsDirty();
            } else {
                this.#form.markAsPristine();
            }
        });
    }

    ngOnDestroy(): void {
        this.#emailsSearchText.complete();
        this.#selectedSupplierName.complete();
        this.#formChanged.complete();
        this.#emailsPage.complete();
    }

    changePage(pageEvent: PageEvent): void {
        this.#emailsPage.next(pageEvent.pageIndex);
    }

    changeSearchText(text: string): void {
        this.#emailsSearchText.next(text);
    }

    deleteElement(element: string): void {
        this.#form.controls.recipients.setValue(this.#form.value.recipients.filter(e => e !== element));
    }

    addElement(): void {
        openDialog(this.#dialog, NewValueDialogComponent, { placeHolder: 'FORMS.LABELS.EMAIL', email: true })
            .beforeClosed()
            .subscribe(response => {
                if (response?.newValue && !this.#form.value.recipients.includes(response.newValue)) {
                    this.#form.setValue({
                        enabled: this.#form.value.enabled,
                        recipients: [...this.#form.value.recipients, response.newValue]
                    });
                }
            });
    }

    cancel(): void {
        this.#matcherSettingsService.matcherConfiguration.load(this.#selectedSupplierName.getValue());
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#formChanged.next(false);
            this.#matcherSettingsService.matcherConfiguration.load(this.#selectedSupplierName.getValue());
        });
    }

    save$(): Observable<void> {
        return this.#matcherSettingsService.matcherConfiguration.updateMatcherConfiguration(
            this.#selectedSupplierName.getValue(),
            {
                delivery: {
                    enabled: this.form.value.enabled,
                    recipients: this.form.value.recipients
                }
            }
        ).pipe(
            tap(() => {
                this.#ephemeralSrv.showSaveSuccess();
            }));
    }

    private updateForm(matcherSettings: MatcherConfiguration): void {
        this.#initialFormValue = {
            enabled: matcherSettings.delivery.enabled ?? false,
            recipients: matcherSettings.delivery.recipients ?? []
        };

        this.#form.setValue(this.#initialFormValue);
    }
}
