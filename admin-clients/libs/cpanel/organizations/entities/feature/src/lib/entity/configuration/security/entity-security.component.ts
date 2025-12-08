
import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    EntitiesService,
    EntitySecurityPasswordConfig,
    EntitySecurityPasswordExpirationType,
    EntitySecuritySettings
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { UpperCasePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, throwError } from 'rxjs';

@Component({
    selector: 'app-entity-security',
    templateUrl: './entity-security.component.html',
    styleUrls: ['./entity-security.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, UpperCasePipe, MatFormFieldModule,
        MatInputModule, MatSelectModule, MatProgressSpinnerModule, MatDividerModule, MatCheckboxModule
    ]
})

export class EntitySecurityComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralMessage = inject(EphemeralMessageService);
    readonly #destroyRef = inject(DestroyRef);

    readonly passwordOptions = Array.from({ length: 5 }, (_, i) => i + 1);
    readonly passwordRetries = Array.from({ length: 10 }, (_, i) => i + 1);

    readonly $reqInProgress = toSignal<boolean>(this.#entitiesSrv.entitySecurity.inProgress$());

    #entityId: number;

    form = this.#fb.group({
        max_retries: [null as number, [Validators.min(1), Validators.max(10)]],
        expiration: this.#fb.group({
            enabled: false,
            type: 'DAYS' as EntitySecurityPasswordExpirationType,
            amount: [null as number, Validators.min(1)]
        }),
        storage: this.#fb.group({
            enabled: false,
            amount: [null as number, [Validators.min(1), Validators.max(5)]]
        })
    });

    readonly expirationTypes: EntitySecurityPasswordExpirationType[] = ['DAYS', 'MONTHS'];

    constructor() {
        this.#entitiesSrv.getEntity$().pipe(
            filter(Boolean),
            map(entity => entity.id),
            takeUntilDestroyed()
        ).subscribe(entityId => this.#entityId = entityId);

        this.#entitiesSrv.entitySecurity.getEntitySecurity$().pipe(
            filter(Boolean),
            map((securitySettings: EntitySecuritySettings) => securitySettings?.password_config),
            takeUntilDestroyed()
        ).subscribe(passwordConfig => this.form.patchValue(passwordConfig));
    }

    ngOnInit(): void {
        this.#entitiesSrv.entitySecurity.load(this.#entityId);
        this.#handleExpirationEnabledChange();
        this.#handleStorageEnabledChange();
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entitySecurity.clear();
    }

    cancel(): void {
        this.#entitiesSrv.entitySecurity.load(this.#entityId);
    }

    save(): void {
        this.save$().subscribe(() => {
            this.#ephemeralMessage.showSaveSuccess();
            this.#entitiesSrv.entitySecurity.load(this.#entityId);
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#entitiesSrv.entitySecurity.update(
                this.#entityId,
                this.form.getRawValue() as EntitySecurityPasswordConfig
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid form');
        }
    }

    #handleExpirationEnabledChange(): void {
        this.form.get('expiration.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((enabled: boolean) => {
                if (enabled) {
                    this.form.get('expiration.amount').enable();
                    this.form.get('expiration.type').enable();
                } else {
                    this.form.get('expiration.amount').disable();
                    this.form.get('expiration.type').disable();
                }
            });
    }

    #handleStorageEnabledChange(): void {
        this.form.get('storage.enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((enabled: boolean) => {
                if (enabled) {
                    this.form.get('storage.amount').enable();
                } else {
                    this.form.get('storage.amount').disable();
                }
            });
    }
}
