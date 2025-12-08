import {
    EphemeralMessageService, openDialog
} from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { SupplierName, UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, filter, map, Observable, tap } from 'rxjs';
import { IngestorSettingsApi } from '../../api/ingestor-settings.api';
import { IngestorSettingsService } from '../../ingestor-settings.service';
import { IngestorConfiguration, PutIngestorConfigurationRequest } from '../../models/ingestor-configuration.model';
import { IngestorSettingsState } from '../../state/ingestor-settings.state';
import { NewValueDialogComponent } from '../add-value/add-value.component';
import { IngestorListSettingContainerComponent } from './list-container/ingestor-list-setting-container.component';

@Component({
    imports: [
        AsyncPipe, TranslatePipe, ReactiveFormsModule, FormContainerComponent, MatAccordion, MatExpansionPanel,
        MatExpansionPanelHeader, MatExpansionPanelTitle, FlexLayoutModule, IngestorListSettingContainerComponent
    ],
    selector: 'app-ingestor-list-settings',
    templateUrl: './ingestor-list-settings.component.html',
    styleUrls: ['./ingestor-list-settings.component.scss'],
    providers: [IngestorSettingsService, IngestorSettingsApi, IngestorSettingsState],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class IngestorListSettingsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ingestorSettingsService = inject(IngestorSettingsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #dialog = inject(MatDialog);

    readonly #form = this.#fb.group({
        general_admission: [[] as string[]],
        excluded_sections: [[] as string[]]
    });

    readonly #selectedSupplierName = new BehaviorSubject<string>('');
    readonly #formChanged = new BehaviorSubject<boolean>(false);

    readonly selectedSupplierName$ = this.#selectedSupplierName.asObservable();
    readonly formChanged$ = this.#formChanged.asObservable();

    readonly pageLimit = 10;

    readonly hasWritePermissions$ = this.#authService.getLoggedUser$().pipe(
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.ingestorWrite]))
    );

    readonly form = this.#form;

    #initialFormValue = this.#form.getRawValue();

    ngOnInit(): void {
        this.#activatedRoute.queryParams.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(params => {
            this.#form.reset();
            const supplierId = params['supplier'] || SupplierName.tevo;
            this.#selectedSupplierName.next(supplierId);
            this.#ingestorSettingsService.ingestorConfiguration.load(supplierId);
        });

        this.#ingestorSettingsService.ingestorConfiguration.getIngestorConfiguration$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(ingestorSettings => { this.updateForm(ingestorSettings); });

        this.#form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe((form: PutIngestorConfigurationRequest) => {
            this.#formChanged.next(JSON.stringify(form) !== JSON.stringify(this.#initialFormValue));
            if (this.#formChanged.getValue()) {
                this.#form.markAsDirty();
            } else {
                this.#form.markAsPristine();
            }
        });
    }

    addElement(controlName: string, placeHolder: string, arrayValidator?: string[]): void {
        openDialog(this.#dialog, NewValueDialogComponent, { placeHolder, arrayValidator })
            .beforeClosed()
            .subscribe(response => {
                if (response?.newValue && !this.#form.value[controlName].includes(response.newValue)) {
                    const newFormValue = this.#form.value as IngestorConfiguration;
                    newFormValue[controlName] = [...newFormValue[controlName], response.newValue];
                    this.#form.setValue(newFormValue);
                }
            });
    }

    deleteElement(element: string, controlName: string): void {
        this.#form.controls[controlName].setValue(this.#form.value[controlName].filter(c => c !== element));
    }

    cancel(): void {
        this.#ingestorSettingsService.ingestorConfiguration.load(this.#selectedSupplierName.getValue());
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ingestorSettingsService.ingestorConfiguration.load(this.#selectedSupplierName.getValue());
        });
    }

    save$(): Observable<void> {
        return this.#ingestorSettingsService.ingestorConfiguration
            .updateIngestorConfiguration(this.#selectedSupplierName.getValue(), this.#form.value)
            .pipe(
                tap(() => {
                    this.#ephemeralSrv.showSaveSuccess();
                })
            );
    }

    private updateForm(ingestorConfiguration: IngestorConfiguration): void {
        this.#initialFormValue = {
            general_admission: ingestorConfiguration.general_admission ?? [],
            excluded_sections: ingestorConfiguration.excluded_sections ?? []
        };
        this.#form.setValue(this.#initialFormValue);
    }
}
