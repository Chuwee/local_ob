import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Entity } from '@admin-clients/shared/common/data-access';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, firstValueFrom, Observable, Subject, throwError, tap } from 'rxjs';
import { ClubCodeSelectorComponent } from '../club-code-selector/club-code-selector.component';
import {
    ExternalEntityConfiguration, HttpProtocol, WSConnectionVersion
} from '../models/configuration.model';
import { ExternalEntityService } from '../service/external.service';

type EntityMembersConfig = ExternalEntityConfiguration['members'];

@Component({
    selector: 'app-entity-external-members',
    imports: [
        CommonModule, FormsModule, ReactiveFormsModule, FlexLayoutModule,
        FormContainerComponent, TranslatePipe, FormControlErrorsComponent, ClubCodeSelectorComponent,
        MaterialModule
    ],
    templateUrl: './members.component.html',
    styleUrls: ['./members.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EntityExternalMembersComponent implements OnInit, OnDestroy {
    readonly #onDestroy = new Subject<void>();
    readonly #fb = inject(FormBuilder);
    readonly #externalService = inject(ExternalEntityService);
    readonly #entitiesService = inject(EntitiesService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #router = inject(Router);

    readonly form = this.#fb.group({
        connection: this.#fb.group<EntityMembersConfig['connection']>({
            ip: null,
            port: null,
            protocol: null,
            username: null,
            password: null,
            ws_connection_version: null
        }),
        enabled: this.#fb.control<boolean>(null),
        avet_connection_type: this.#fb.control<ExternalEntityConfiguration['avet_connection_type']>(null)
    });

    readonly loading$: Observable<boolean> = booleanOrMerge([
        this.#externalService.clubCodes.loading$(),
        this.#externalService.clubCodes.linking$(),
        this.#externalService.configuration.loading$()
    ]);

    readonly httpProtocols = Object.values(HttpProtocol);
    readonly wsVersions = Object.values(WSConnectionVersion);

    entity: Entity;

    ngOnInit(): void {
        this.init();
    }

    ngOnDestroy(): void {
        this.#externalService.configuration.clear();
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    async init(): Promise<void> {
        this.entity = await firstValueFrom(this.#entitiesService.getEntity$());

        this.form.disable();

        this.#externalService.configuration.load(this.entity.id);

        this.#externalService.configuration.get$().pipe(
            filter(v => !!v),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(({ members, club_code: code, avet_connection_type: avetConnectionType }) => {
            code ? this.form.enable() : this.form.disable();
            this.form.reset({
                connection: members.connection,
                enabled: members.enabled,
                avet_connection_type: avetConnectionType
            });
            if (avetConnectionType === 'SOCKET') {
                this.#router.navigate(['/entities', this.entity.id, 'external', 'ticketing']);
            }
            const enableCtrl = this.form.get('enabled');
            this.enableConnection(enableCtrl.value);
        });

        this.form.get('enabled').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => this.enableConnection(enabled));
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            return this.#externalService.configuration.save(this.entity.id,
                {
                    members: {
                        connection: this.form.controls.connection.value,
                        enabled: this.form.controls.enabled.value
                    },
                    avet_connection_type: this.form.controls.avet_connection_type.value
                }).pipe(
                    tap(() => this.cancel())
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    async cancel(): Promise<void> {
        this.form.markAsPristine();
        this.#externalService.configuration.reload(this.entity.id);
    }

    private enableConnection(enabled: boolean): void {
        const control = this.form.get('connection');
        enabled && this.form.enabled ? control.enable() : control.disable();
    }

}
