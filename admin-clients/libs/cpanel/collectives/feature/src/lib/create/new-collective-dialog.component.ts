import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    CollectiveExternalValidator, CollectiveRestrictions, CollectiveType, CollectiveValidationMethod, CollectivesService, PostCollective
} from '@admin-clients/cpanel/collectives/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { EntitiesFilterFields, Entity, EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent, SelectServerSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit, inject } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { EMPTY, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-new-collective-dialog',
    templateUrl: './new-collective-dialog.component.html',
    styleUrls: ['./new-collective-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NgForOf, NgIf, AsyncPipe, ReactiveFormsModule, FlexLayoutModule, TranslatePipe,
        MaterialModule, SelectServerSearchComponent, FormControlErrorsComponent, SelectSearchComponent,
        EllipsifyDirective
    ]
})
export class NewCollectiveDialogComponent implements OnInit, OnDestroy {

    private readonly _onDestroy = new Subject<void>();
    private readonly _dialogRef = inject(MatDialogRef<NewCollectiveDialogComponent>);
    private readonly _fb = inject(FormBuilder);
    private readonly _elemRef = inject(ElementRef);
    private readonly _collectivesSrv = inject(CollectivesService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _entitiesService = inject(EntitiesBaseService);

    readonly form = this._fb.group({
        generic: null as boolean,
        entity: [{ value: null as Entity, disabled: true }, Validators.required],
        name: [null as string, [Validators.required, Validators.maxLength(CollectiveRestrictions.nameMaxLength)]],
        type: [null as CollectiveType, [Validators.required]],
        validationMethod: [{ value: null as CollectiveValidationMethod, disabled: true }, [Validators.required]],
        externalValidator: [{ value: null as CollectiveExternalValidator, disabled: true }, [Validators.required]]
    });

    readonly isSaving$ = this._collectivesSrv.isCollectiveSaving$();

    readonly entities$ = this._auth.canReadMultipleEntities$()
        .pipe(
            switchMap(canSelectEntity => {
                if (canSelectEntity) {
                    this._entitiesService.entityList.load({
                        limit: 999,
                        sort: 'name:asc',
                        fields: [EntitiesFilterFields.name],
                        type: 'EVENT_ENTITY'
                    });
                    this.form.controls.entity.enable();
                    return this._entitiesService.entityList.getData$().pipe(filter(Boolean));
                } else {
                    this.form.controls.entity.disable();
                    return of([] as Entity[]);
                }
            }),
            takeUntil(this._onDestroy),
            shareReplay(1)
        );

    readonly isOperator$ = this._auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]).pipe(first());

    readonly operatorEntity$ = this.isOperator$
        .pipe(
            first(),
            switchMap(isOperator => {
                if (isOperator) {
                    return this._auth.getLoggedUser$().pipe(first(Boolean), map(({ entity }) => entity.name));
                }
                return EMPTY;
            })
        );

    readonly collectiveExternalValidators$ = this._collectivesSrv.getCollectiveExternalValidators$().pipe(filter(Boolean));

    readonly moreEntitiesAvailable$ = this._entitiesService.entityList.getMetadata$().pipe(
        map(metadata => metadata?.offset + metadata?.limit < metadata?.total)
    );

    types = Object.values(CollectiveType);
    validationMethods = Object.values(CollectiveValidationMethod);

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
    }

    ngOnInit(): void {

        this.form.controls.entity.valueChanges.pipe(
            filter(entity => entity !== null),
            takeUntil(this._onDestroy)
        ).subscribe(entity => {
            if (entity) {
                this.types = [CollectiveType.internal];
            }
        });

        this.form.controls.generic.valueChanges.pipe(
            filter(checked => checked !== null),
            takeUntil(this._onDestroy)
        ).subscribe(checked => {
            if (checked) {
                this.types = Object.values(CollectiveType);
                this.form.controls.entity.reset();
                this.form.controls.entity.disable();
            } else {
                this.types = [CollectiveType.internal];
                this.form.controls.entity.enable();
            }
        });

        this.form.controls.type.valueChanges.pipe(
            filter(value => !!value),
            takeUntil(this._onDestroy)
        ).subscribe(value => {
            if (value === CollectiveType.internal) {
                this.validationMethods = [
                    CollectiveValidationMethod.promotionalCode,
                    CollectiveValidationMethod.user,
                    CollectiveValidationMethod.userPassword,
                    CollectiveValidationMethod.giftTicket
                ];
                this.form.controls.externalValidator.reset();
                this.form.controls.externalValidator.disable();
            } else if (value === CollectiveType.external) {
                this.validationMethods = [
                    CollectiveValidationMethod.promotionalCode,
                    CollectiveValidationMethod.user,
                    CollectiveValidationMethod.userPassword,
                    CollectiveValidationMethod.userCodePassword,
                    CollectiveValidationMethod.shoppingCart
                ];
                this.form.controls.externalValidator.enable();
            } else {
                this.validationMethods = [];
                this.form.controls.validationMethod.disable();
                this.form.controls.externalValidator.disable();
                return;
            }
            this.form.controls.validationMethod.enable();
        });

        this.isOperator$.pipe(first()).subscribe(
            isOperator => {
                if (isOperator) {
                    this._collectivesSrv.loadCollectiveExternalValidators();
                } else {
                    this.form.controls.validationMethod.enable();
                    this.types = [CollectiveType.internal];
                    this.form.patchValue({ type: CollectiveType.internal });
                    this.form.controls.entity.disable();
                }
            }
        );

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    createCollective(): void {
        this.form.markAllAsTouched();
        if (this.form.valid) {
            const data = this.form.value;
            const collective: PostCollective = {
                name: data.name,
                type: data.type,
                validation_method: data.validationMethod,
                external_validator: data.externalValidator?.execution_class
            };

            if (!data.generic && data.entity?.id) {
                collective.entity_id = data.entity.id;
            }

            this._collectivesSrv.createCollective(collective)
                .subscribe(collectiveId => this.close({ name: collective.name, id: collectiveId }));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elemRef.nativeElement);
        }
    }

    close(collective: { id: number; name: string } = null): void {
        this._dialogRef.close(collective);
    }

    loadEntities(q: string, next = false): void {
        this._entitiesService.loadServerSearchEntityList({
            limit: 100,
            sort: 'name:asc',
            fields: [EntitiesFilterFields.name],
            q
        }, next);
    }
}
