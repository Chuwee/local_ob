import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatError, MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, Observable, tap, throwError } from 'rxjs';
import { ClubCodeSelectorComponent } from '../club-code-selector/club-code-selector.component';
import { ExternalEntityService } from '../service/external.service';

@Component({
    selector: 'app-entity-external-sga',
    imports: [
        ClubCodeSelectorComponent,
        FormContainerComponent,
        MatDividerModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        TranslatePipe,
        MatError,
        MatInputModule,
        FormControlErrorsComponent
    ],
    templateUrl: './sga.component.html',
    styleUrls: ['./sga.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SGAComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #externalSrv = inject(ExternalEntityService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly form = this.#fb.group({
        url: [null as string, Validators.required],
        auth_url: [null as string, Validators.required],
        profile: [null as string, Validators.required],
        client_id: [null as string, Validators.required]
    });

    ngOnInit(): void {
        this.#externalSrv.configuration.get$().pipe(
            takeUntilDestroyed(this.#destroyRef),
            filter(Boolean),
            tap(configuration => this.form.patchValue(configuration.sga.connection))
        ).subscribe();
    }

    get entityIdPath(): number | undefined {
        const allRouteParams = Object.assign({}, ...this.#activatedRoute.snapshot.pathFromRoot.map(path => path.params));
        return parseInt(allRouteParams.entityId);
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#externalSrv.configuration.save(this.entityIdPath, { sga: { connection: this.form.value } }).pipe(
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
