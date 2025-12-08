import {
    EmptyStateTinyComponent, EphemeralMessageService, SearchInputComponent, openDialog
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { SalesApi, SalesService, SalesState } from '@admin-clients/shi-panel/feature-sales';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, Observable, combineLatest, filter, map, shareReplay, take } from 'rxjs';
import { SalesSettingsApi } from '../../api/sales-settings.api';
import { SalesConfigurationDelivery } from '../../models/sales-configuration.model';
import { SalesSettingsService } from '../../sales-settings.service';
import { SalesSettingsState } from '../../state/sales-settings.state';
import { NewValueDialogComponent } from '../add-value/add-value.component';

@Component({
    imports: [
        AsyncPipe, MaterialModule, TranslatePipe, ReactiveFormsModule, FormContainerComponent, FlexLayoutModule,
        SearchInputComponent, EmptyStateTinyComponent, EllipsifyDirective
    ],
    selector: 'app-export-settings',
    templateUrl: './export-settings.component.html',
    styleUrls: ['./export-settings.component.scss'],
    providers: [SalesSettingsService, SalesSettingsApi, SalesSettingsState, SalesService, SalesApi, SalesState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ExportSettingsComponent implements OnInit, OnDestroy {

    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #salesSettingsService = inject(SalesSettingsService);
    readonly #salesService = inject(SalesService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #dialog = inject(MatDialog);

    readonly #emailsPage = new BehaviorSubject<number>(0);
    readonly #emailsSearchText = new BehaviorSubject<string>('');
    readonly #formChanged = new BehaviorSubject<boolean>(false);

    readonly #form = this.#fb.group({
        enabled: [false as boolean],
        emails: [[] as string[]]
    });

    #initialFormValue = this.#form.getRawValue();

    readonly formChanged$ = this.#formChanged.asObservable();

    readonly isLoading$ = this.#salesSettingsService.salesConfiguration.isInProgress$();
    readonly pageLimit = 10;

    readonly emailsPage$ = this.#emailsPage.asObservable();

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveAllPermissions(loggedUser,
            [UserPermissions.configurationWrite, UserPermissions.salesWrite]))
    );

    readonly filteredEmails$ = combineLatest([
        this.#form.controls.emails.valueChanges,
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
            let pagedEmails = filteredEmails?.slice(page * this.pageLimit, (page + 1) * this.pageLimit);
            if (filteredEmails?.length > 0 && pagedEmails?.length === 0) {
                pagedEmails = filteredEmails?.slice((page - 1) * this.pageLimit, (page) * this.pageLimit);
                this.#emailsPage.next(this.#emailsPage.getValue() - 1);
            }
            return pagedEmails;
        }),
        shareReplay(1)
    );

    readonly form = this.#form;

    ngOnInit(): void {
        this.#salesSettingsService.salesConfiguration.load();

        this.hasWritePermissions$.pipe(take(1)).subscribe(writePermission => {
            if (!writePermission) {
                this.#form.controls.enabled.disable();
            }
        });

        this.#salesSettingsService.salesConfiguration.getSalesConfiguration$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(salesSettings => { this.updateForm(salesSettings?.sales.export?.delivery); });

        this.#form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(() => {
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
        this.#form.controls.emails.setValue(this.#form.value.emails.filter(e => e !== element));
    }

    addElement(): void {
        openDialog(this.#dialog, NewValueDialogComponent, { placeHolder: 'FORMS.LABELS.EMAIL', email: true })
            .beforeClosed()
            .subscribe(response => {
                if (response?.newValue && !this.#form.value.emails.includes(response.newValue)) {
                    this.#form.setValue({
                        enabled: this.#form.value.enabled,
                        emails: [...this.#form.value.emails, response.newValue]
                    });
                }
            });
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#formChanged.next(false);
            this.#salesSettingsService.salesConfiguration.load();
            this.#ephemeralSrv.showSaveSuccess();
        });
    }

    save$(): Observable<void> {
        return this.#salesSettingsService.salesConfiguration.updateSalesConfiguration({
            sales: {
                export: {
                    delivery: {
                        enabled: this.form.value.enabled,
                        emails: this.form.value.emails
                    }
                }
            }
        });
    }

    cancel(): void {
        this.#salesSettingsService.salesConfiguration.load();
    }

    sendExport(): void {
        this.#salesService.list.exportSalesDailyList().subscribe(() => {
            this.#ephemeralSrv.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
        });
    }

    private updateForm(salesSettings: SalesConfigurationDelivery): void {
        this.#initialFormValue = {
            enabled: salesSettings.enabled,
            emails: salesSettings.emails
        };

        this.#form.setValue(this.#initialFormValue);
    }
}
