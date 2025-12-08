import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { grantPermissionsProviders } from '@admin-clients/shi-panel/data-access-grant-access';
import { SupplierName, UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxChange, MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, map, tap } from 'rxjs/operators';
import { IngestorSettingsService } from '../../ingestor-settings.service';
import {
    IngestorConfiguration, IngestorScheduleLoadType, ingestorScheduleLoadType, ingestorSources, IngestorSourcesType, IngestorStatus,
    sourcesDependingDefault
} from '../../models/ingestor-configuration.model';

@Component({
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe,
        MatProgressSpinnerModule, MatFormFieldModule, MatCheckboxModule, MatSlideToggleModule
    ],
    selector: 'app-ingestor-status',
    templateUrl: './ingestor-status.component.html',
    styleUrls: ['./ingestor-status.component.scss'],
    providers: [grantPermissionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class IngestorStatusComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #fb = inject(FormBuilder);
    readonly #ingestorSettingsService = inject(IngestorSettingsService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authService = inject(AuthenticationService);
    readonly #activatedRoute = inject(ActivatedRoute);

    readonly #form = this.#fb.group({
        enabled: [false as boolean],
        ingestor_sources: [[] as IngestorSourcesType[]],
        schedule_load_type: [[] as string[]]
    });

    readonly $selectedSupplierName = signal<string>('');
    readonly $formChanged = signal<boolean>(false);

    readonly $isInProgress = toSignal(this.#ingestorSettingsService.ingestorConfiguration.isInProgress$());
    readonly $hasWritePermissions = toSignal(this.#authService.getLoggedUser$().pipe(
        first(Boolean),
        map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.ingestorWrite])),
        tap(permission => { if (!permission) { this.#form.controls.enabled.disable(); } })
    ));

    readonly ingestorSourcesList = ingestorSources;
    readonly ingestorSourcesStatus = IngestorStatus;
    readonly ingestorSourcesDependingDefault = sourcesDependingDefault;

    readonly form = this.#form;

    #initialFormValue = this.#form.getRawValue();

    ngOnInit(): void {
        this.#activatedRoute.queryParams.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(params => {
            this.#form.reset();
            const supplierId = params['supplier'] || SupplierName.tevo;
            this.$selectedSupplierName.set(supplierId);
            this.#ingestorSettingsService.ingestorConfiguration.load(supplierId);
        });

        this.#ingestorSettingsService.ingestorConfiguration.getIngestorConfiguration$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(ingestorSettings => { this.updateForm(ingestorSettings); });

        this.#form.valueChanges.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe(form => {
            const sortedSourcesForm = {
                ...form,
                ingestor_sources: [...form.ingestor_sources].sort(),
                schedule_load_type: [...form.schedule_load_type].sort()
            };
            this.$formChanged.set(JSON.stringify(sortedSourcesForm) !== JSON.stringify(this.#initialFormValue));
            if (this.$hasWritePermissions() && this.$formChanged()) {
                this.#form.markAsDirty();
            } else {
                this.#form.markAsPristine();
            }
        });
    }

    cancel(): void {
        this.#ingestorSettingsService.ingestorConfiguration.load(this.$selectedSupplierName());
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ingestorSettingsService.ingestorConfiguration.load(this.$selectedSupplierName());
        });
    }

    save$(): Observable<void> {
        return this.#ingestorSettingsService.ingestorConfiguration.updateIngestorConfiguration(
            this.$selectedSupplierName(),
            this.#form.value
        ).pipe(
            tap(() => {
                this.#ephemeralSrv.showSaveSuccess();
            }));
    }

    onCheckboxChange(event: MatCheckboxChange, source: IngestorSourcesType): void {
        const ingestorSources = this.#form.value.ingestor_sources;
        const scheduledLoadType = this.#form.value.schedule_load_type;

        if (ingestorScheduleLoadType.includes(source as IngestorScheduleLoadType)) {
            this.#form.patchValue({
                schedule_load_type: event.checked ? [...scheduledLoadType, source] : scheduledLoadType.filter((s: string) => s !== source)
            });
        } else {
            this.#form.patchValue({
                ingestor_sources: event.checked ? [...ingestorSources, source] : ingestorSources.filter((s: string) => s !== source)
            });
        }
    }

    private updateForm(ingestorConfiguration: IngestorConfiguration): void {
        this.#initialFormValue = {
            enabled: ingestorConfiguration.enabled,
            ingestor_sources: ingestorConfiguration.ingestor_sources.sort(),
            schedule_load_type: ingestorConfiguration.schedule_load_type.sort()
        };
        this.#form.setValue(this.#initialFormValue);
    }
}
