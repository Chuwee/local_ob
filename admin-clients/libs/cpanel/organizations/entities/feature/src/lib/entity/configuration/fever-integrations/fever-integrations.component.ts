import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { PutEntity } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, FormControlHandler } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, viewChildren } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

const FORM_FIELDS = {
    allowFeverZone: 'allowFeverZone',
    pbQuestions: 'pbQuestions'
} as const;

type FeverForm = Record<keyof typeof FORM_FIELDS, boolean>;

@Component({
    selector: 'app-fever-integrations',
    templateUrl: './fever-integrations.component.html',
    styleUrls: ['./fever-integrations.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, TranslatePipe, SearchablePaginatedSelectionModule,
        MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle, MatProgressSpinner, MatCheckbox
    ]
})
export class FeverIntegrationsComponent implements WritingComponent {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #auth = inject(AuthenticationService);

    private readonly _$matExpansionPanels = viewChildren(MatExpansionPanel);

    readonly formControlNames = FORM_FIELDS;

    readonly $entityId = toSignal(this.#entitiesSrv.getEntity$().pipe(map(entity => entity.id)));

    readonly $isOperatorManager = toSignal(this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]), { initialValue: false });

    readonly $isAllowFeverZoneShown = toSignal(
        this.#auth.getLoggedUser$().pipe(map(user => !!user?.operator?.allow_fever_zone)),
        { initialValue: false }
    );

    readonly $reqInProgress = toSignal(booleanOrMerge([
        this.#entitiesSrv.isEntitySaving$(),
        this.#entitiesSrv.isEntityLoading$()
    ]));

    readonly form = this.#fb.group<FeverForm>({
        allowFeverZone: false,
        pbQuestions: false
    });

    constructor() {
        this.#entityChangeHandler();
    }

    save(): void {
        this.save$().subscribe(() =>
            this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' })
        );
    }

    cancel(): void {
        this.#entitiesSrv.loadEntity(this.$entityId());
    }

    save$(): Observable<void> {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._$matExpansionPanels());
            return throwError(() => 'Invalid form');
        }

        const entityId = this.$entityId();
        const entity: PutEntity = this.#getFormPutEntity();

        return this.#entitiesSrv.updateEntity(entityId, entity).pipe(
            finalize(() => this.#entitiesSrv.loadEntity(entityId))
        );
    }

    #entityChangeHandler(): void {
        this.#entitiesSrv.getEntity$()
            .pipe(takeUntilDestroyed())
            .subscribe(({ settings }) => {
                this.form.patchValue({
                    allowFeverZone: !!settings.allow_fever_zone,
                    pbQuestions: !!settings.post_booking_questions?.enabled
                });
                this.form.markAsPristine();
            });
    }

    #getFormPutEntity(): PutEntity {
        const settings: NonNullable<PutEntity['settings']> = {};

        const dirtyValues: Partial<FeverForm> = FormControlHandler.getDirtyValues(this.form);

        if (FORM_FIELDS.allowFeverZone in dirtyValues) {
            settings.allow_fever_zone = dirtyValues[FORM_FIELDS.allowFeverZone];
        }

        if (FORM_FIELDS.pbQuestions in dirtyValues) {
            settings.post_booking_questions = {
                enabled: dirtyValues[FORM_FIELDS.pbQuestions]
            };
        }

        return { settings };
    }
}
