import { CountriesService } from '@admin-clients/shared/common/data-access';
import {
    EphemeralMessageService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable } from 'rxjs';
import { SalesSettingsApi } from '../../api/sales-settings.api';
import { PatchSalesConfiguration, SalesConfiguration } from '../../models/sales-configuration.model';
import { SalesSettingsService } from '../../sales-settings.service';
import { SalesSettingsState } from '../../state/sales-settings.state';
import { NewValueDialogComponent } from '../add-value/add-value.component';
import { ErrorSettingContainerComponent } from './list-container/error-setting-container.component';

@Component({
    imports: [TranslatePipe, ReactiveFormsModule, FormContainerComponent, ErrorSettingContainerComponent, MatExpansionModule],
    selector: 'app-error-settings',
    templateUrl: './error-settings.component.html',
    styleUrls: ['./error-settings.component.scss'],
    providers: [SalesSettingsService, SalesSettingsApi, SalesSettingsState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ErrorSettingsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #salesSettingsService = inject(SalesSettingsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #dialog = inject(MatDialog);
    readonly #countriesService = inject(CountriesService);

    readonly #form = this.#fb.group({
        retry_confirm_errors: [[] as string[]],
        retry_fulfill_errors: [[] as string[]]
    });

    #initialFormValue = this.#form.getRawValue();

    readonly $formChanged = signal<boolean>(false);
    readonly pageLimit = 10;

    readonly $hasWritePermissions = toSignal(this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveAllPermissions(loggedUser,
            [UserPermissions.configurationWrite, UserPermissions.salesWrite]))
    ));

    readonly form = this.#form;

    ngOnInit(): void {
        this.#form.reset();
        this.#salesSettingsService.salesConfiguration.load();

        this.#salesSettingsService.salesConfiguration.getSalesConfiguration$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(salesSettings => { this.updateForm(salesSettings.sales); });

        this.#form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe((form: PatchSalesConfiguration) => {
            this.$formChanged.set(JSON.stringify(form) !== JSON.stringify(this.#initialFormValue));
            if (this.$formChanged()) {
                this.#form.markAsDirty();
            } else {
                this.#form.markAsPristine();
            }
        });

        this.#countriesService.loadCountries();
    }

    addElement(controlName: string, placeHolder: string): void {
        openDialog(this.#dialog, NewValueDialogComponent, { placeHolder })
            .beforeClosed()
            .subscribe(response => {
                if (response?.newValue && !this.#form.value[controlName].includes(response.newValue)) {
                    const newFormValue = this.#form.value as SalesConfiguration;
                    newFormValue[controlName] = [...newFormValue[controlName], response.newValue];
                    this.#form.setValue(newFormValue);
                }
            });
    }

    deleteElement(controlName: string, element: string): void {
        this.#form.controls[controlName].setValue(this.#form.value[controlName].filter((c: string) => c !== element));
    }

    cancel(): void {
        this.#salesSettingsService.salesConfiguration.load();
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#salesSettingsService.salesConfiguration.load();
            this.#ephemeralSrv.showSaveSuccess();
        });
    }

    save$(): Observable<void> {
        return this.#salesSettingsService.salesConfiguration.updateSalesConfiguration({
            sales: this.#form.value
        });
    }

    private updateForm(salesConfiguration: SalesConfiguration): void {
        this.#initialFormValue = {
            retry_confirm_errors: salesConfiguration.retry_confirm_errors,
            retry_fulfill_errors: salesConfiguration.retry_fulfill_errors
        };
        this.#form.setValue(this.#initialFormValue);
    }
}
