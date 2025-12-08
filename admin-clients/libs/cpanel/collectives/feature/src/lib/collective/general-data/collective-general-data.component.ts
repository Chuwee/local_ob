import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    CollectiveType, CollectiveValidationMethod, CollectiveEntity,
    PutCollectiveExternalValidatorProperties, Collective, PutCollective, CollectivesService
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, QueryList, ViewChildren, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, forkJoin, Observable, Subject, throwError } from 'rxjs';
import { filter, map, shareReplay, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-collective-general-data',
    templateUrl: './collective-general-data.component.html',
    styleUrls: ['./collective-general-data.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CollectiveGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy = new Subject<void>();
    private _collectiveId: number;
    private _userIsNotSetYet = false;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanels: QueryList<MatExpansionPanel>;

    private readonly _collectiveSrv = inject(CollectivesService);
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _auth = inject(AuthenticationService);

    readonly collectiveType = CollectiveType;
    readonly collectiveValidationMethod = CollectiveValidationMethod;
    readonly reqInProgress$ = booleanOrMerge([
        this._collectiveSrv.isCollectiveLoading$(),
        this._collectiveSrv.isCollectiveSaving$(),
        this._collectiveSrv.isCollectiveExternalValidatorPropertiesSaving$()
    ]);

    readonly collective$ = this._collectiveSrv.getCollective$()
        .pipe(
            filter(Boolean),
            tap(collective => {
                this._collectiveId = collective.id;
                this.updateForm(collective);
            }),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

    readonly showCollectiveEntity$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS])
        .pipe(
            filter(Boolean),
            switchMap(() => this._collectiveSrv.getCollective$()),
            filter(Boolean),
            map(collective => !!collective.generic)
        );

    readonly isNotOperatorUser$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
        .pipe(map(isOperator => !isOperator));

    readonly form = this._fb.group({
        generalData: this._fb.group({
            name: [{ value: null as string, disabled: true }, [Validators.required]],
            description: [{ value: null as string, disabled: true }],
            showUsages: true
        }),
        externalValidator: this._fb.group({
            user: null as string,
            password: [null as string, Validators.required]
        }),
        entities: [{ value: null as CollectiveEntity[], disabled: true }, [Validators.required]]
    });

    userEntityId: number;

    ngOnInit(): void {
        combineLatest([
            this._auth.getLoggedUser$(),
            this._collectiveSrv.getCollective$()
        ]).pipe(
            filter(([user, collective]) => !!user && !!collective),
            takeUntil(this._onDestroy)
        ).subscribe(([user, collective]) => {
            this.userEntityId = user.entity.id;
            const generalDataForm = this.form.controls.generalData;
            if (collective.entity && user.entity.id === collective.entity.id) {
                generalDataForm.controls.name.enable();
                generalDataForm.controls.description.enable();
            }
        });

        // Password disabled until user has value
        const externalValidatorGroup = this.form.controls.externalValidator;
        externalValidatorGroup.controls.user.valueChanges
            .pipe(
                filter(_ => !!this._userIsNotSetYet),
                takeUntil(this._onDestroy)
            )
            .subscribe(value => {
                if (value) {
                    externalValidatorGroup.controls.password.enable();
                } else {
                    externalValidatorGroup.controls.password.disable();
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    cancel(): void {
        this._collectiveSrv.loadCollective(this._collectiveId);
    }

    save(): void {
        this.save$().subscribe(() => this._collectiveSrv.loadCollective(this._collectiveId));
    }

    save$(): Observable<void[]> {
        if (this.form.valid) {
            const obs$: Observable<void>[] = [];
            const value = this.form.value;
            if (this.form.controls.generalData.dirty) {
                const data: PutCollective = {
                    name: value.generalData.name,
                    description: value.generalData.description,
                    show_usages: value.generalData.showUsages
                };
                obs$.push(this._collectiveSrv.saveCollective(this._collectiveId, data));
            }

            if (this.form.controls.externalValidator?.dirty) {
                const data: PutCollectiveExternalValidatorProperties = {
                    external_validator_properties: {
                        user: value.externalValidator.user ?? null,
                        password: value.externalValidator.password ?? null
                    }
                };
                obs$.push(this._collectiveSrv.saveCollectiveExternalValidators(this._collectiveId, data));
            }

            if (this.form.controls.entities?.dirty) {
                let data = value.entities.map(collectiveEntity => collectiveEntity.id);
                data = data.filter(collectiveEntityId => collectiveEntityId !== this.userEntityId);
                obs$.push(this._collectiveSrv.saveCollectiveEntities(this._collectiveId, data));
            }
            return forkJoin(obs$).pipe(tap(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid form');
        }
    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }

    private updateForm(collective: Collective): void {
        const generalDataForm = this.form.controls.generalData;
        generalDataForm.reset();
        generalDataForm.patchValue({
            name: collective.name,
            description: collective.description,
            showUsages: collective.show_usages
        });

        const externalValidatorForm = this.form.controls.externalValidator;
        if (collective.external_validator?.external_validator_properties) {
            externalValidatorForm.patchValue(collective.external_validator.external_validator_properties);
            externalValidatorForm.controls.user.setValidators([Validators.required]);
        } else if (collective.external_validator) {
            externalValidatorForm.controls.user.enable();
            externalValidatorForm.controls.password.disable();
            this._userIsNotSetYet = true;
        } else {
            externalValidatorForm.disable();
        }

        generalDataForm.markAsPristine();
        externalValidatorForm.markAsPristine();
    }
}
