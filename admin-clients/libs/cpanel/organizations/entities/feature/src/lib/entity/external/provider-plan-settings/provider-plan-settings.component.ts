import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, tap, throwError } from 'rxjs';
import { ClubCodeSelectorComponent } from '../club-code-selector/club-code-selector.component';
import { ExternalEntityService } from '../service/external.service';

@Component({
    selector: 'app-provider-plan-settings',
    imports: [
        ClubCodeSelectorComponent,
        FormContainerComponent,
        MatDividerModule,
        ReactiveFormsModule,
        MatExpansionModule,
        TranslatePipe,
        MatCheckboxModule,
        FormControlErrorsComponent
    ],
    templateUrl: './provider-plan-settings.component.html',
    styleUrls: ['./provider-plan-settings.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProviderPlanSettingsComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #externalSrv = inject(ExternalEntityService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly form = this.#fb.group({
        sync_main_plan_title: [false],
        sync_main_plan_description: [false],
        sync_main_plan_images: [false]
    });

    ngOnInit(): void {
        this.#externalSrv.configuration.get$().pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(Boolean),
            tap(configuration => {
                if (configuration.provider_plan_settings) {
                    this.form.patchValue(configuration.provider_plan_settings);
                }
            })
        ).subscribe();
    }

    get entityIdPath(): number | undefined {
        const allRouteParams = Object.assign({}, ...this.#activatedRoute.snapshot.pathFromRoot.map(path => path.params));
        return parseInt(allRouteParams.entityId, 10);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#externalSrv.configuration.save(this.entityIdPath, { provider_plan_settings: this.form.value }).pipe(
                tap(() => {
                    this.form.markAsPristine();
                    this.#ephemeralSrv.showSaveSuccess();
                })
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.form.markAsPristine();
        this.#externalSrv.configuration.reload(this.entityIdPath);
    }
}
