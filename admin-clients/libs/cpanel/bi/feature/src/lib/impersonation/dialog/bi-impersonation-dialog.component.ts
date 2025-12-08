import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles, MstrUrls } from '@admin-clients/cpanel/core/data-access';
import { EntityUsersService, EntityUserRoles } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { EntitiesFilterFields, EntitiesBaseService, Entity } from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, TranslatePipe, SelectSearchComponent, FormControlErrorsComponent,
        FlexLayoutModule, AsyncPipe, MatFormFieldModule, MatSelectModule, MatDialogModule, MatIcon,
        MatIconButton, MatButton, MatProgressSpinner
    ],
    providers: [
        EntityUsersService
    ],
    selector: 'app-bi-impersonation-dialog',
    templateUrl: './bi-impersonation-dialog.component.html'
})
export class BiImpersonationDialogComponent implements OnInit, OnDestroy {
    private readonly _dialogRef = inject<MatDialogRef<BiImpersonationDialogComponent, { urls?: MstrUrls; userId: number }>>(MatDialogRef);
    private readonly _entitiesSrv = inject(EntitiesBaseService);
    private readonly _entityUsersSrv = inject(EntityUsersService);
    private readonly _auth = inject(AuthenticationService);
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _fb = inject(FormBuilder);

    //TODO: when only instantiated from bi section, delete
    readonly impersonation = inject<{ userId: number; entityId: number }>(MAT_DIALOG_DATA);

    readonly form = this._fb.group({
        entityId: null as number,
        userId: [{ value: null as number, disabled: true }]
    });

    readonly entities$: Observable<Entity[]> = this._auth.getLoggedUser$().pipe(
        filter(user => !!user),
        switchMap(user => {
            const isOperator = AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]);
            if (this.impersonation) {
                this.form.patchValue({ userId: this.impersonation.userId });
            } else {
                this.form.patchValue({ userId: user.id });
            }
            if (isOperator) {
                this._entitiesSrv.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [EntitiesFilterFields.name]
                });
                return this._entitiesSrv.entityList.getData$().pipe(
                    first(entities => !!entities),
                    tap(entities => {
                        if (this.impersonation) {
                            this.form.patchValue({ entityId: this.impersonation.entityId });
                        } else {
                            this.form.patchValue({ entityId: entities.find(entity => entity.id === user.entity.id).id });
                        }
                    })
                );
            } else {
                this._entitiesSrv.loadEntity(user.entity.id);
                return this._entitiesSrv.getEntity$().pipe(
                    first(entity => !!entity),
                    tap(entity => this.form.patchValue({ entityId: entity.id })),
                    map(entity => [entity])
                );
            }
        }),
        takeUntilDestroyed(this._destroyRef),
        shareReplay(1)
    );

    readonly users$ = this._entityUsersSrv.getUsersListData$().pipe(filter(Boolean));

    readonly reqInProgress$ = booleanOrMerge([
        this._entitiesSrv.entityList.inProgress$(),
        this._entityUsersSrv.isEntityUserLoading$()
    ]);

    constructor() {
        this._dialogRef.addPanelClass(DialogSize.MEDIUM);
        this._dialogRef.disableClose = false;
        if (this.impersonation) { //TODO: when only instantiated from bi section, delete, it will be always handset
            this._dialogRef.addPanelClass('handset');
        }
    }

    ngOnInit(): void {
        this.form.controls.entityId.valueChanges
            .pipe(filter(Boolean), takeUntilDestroyed(this._destroyRef))
            .subscribe(entityId => {
                if (entityId) {
                    this._entityUsersSrv.loadEntityUsersList({
                        entityId,
                        offset: 0,
                        limit: 999,
                        sort: 'email:asc',
                        roles: [EntityUserRoles.BI_USR]
                    });
                    this.form.controls.userId.enable();
                } else {
                    this.form.controls.userId.disable();
                }
            });
    }

    ngOnDestroy(): void {
        this._entitiesSrv.clearEntity();
        this._entitiesSrv.entityList.clear();
    }

    close(response?: { urls?: MstrUrls; userId: number }): void {
        this._dialogRef.close(response);
    }

    selectUser(): void {
        const userId = this.form.value.userId;
        if (this.impersonation) {
            this.close({ userId });
        } else {
            this._auth.mstrUrls.getMstrUrls$(userId)
                .pipe(first(Boolean))
                .subscribe(urls => this.close({ urls, userId }));
        }
    }
}
