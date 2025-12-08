import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
    CreateOrUpdateCategoriesDialogActions, CreateOrUpdateCategoriesDialogReturnData,
    CreateOrUpdateCategoriesDialogData, EntitiesService
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntityCategory } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, DialogSize } from '@admin-clients/shared/common/ui/components';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-create-update-entity-categories-dialog',
    templateUrl: './create-update-entity-categories-dialog.component.html',
    styleUrls: ['./create-update-entity-categories-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatDialogModule, MatTreeModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule,
        AsyncPipe, MatTooltipModule, MatProgressSpinnerModule, ReactiveFormsModule, FormControlErrorsComponent, MatLabel
    ]
})
export class CreateUpdateEntityCategoriesDialogComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralService = inject(EphemeralMessageService);
    readonly #dialogRef = inject(MatDialogRef<CreateUpdateEntityCategoriesDialogComponent, CreateOrUpdateCategoriesDialogReturnData>);

    #lastIdIfCreateAndNew = signal<number | null>(null); //For cases when create and new, but then cancel

    readonly form = this.#fb.group({
        description: [null as string, Validators.required],
        code: [null as string, Validators.required]
    });

    readonly isInProgress$ = this.#entitiesSrv.entityCategory.inProgress$();
    readonly dialogActions = CreateOrUpdateCategoriesDialogActions;
    readonly data = inject(MAT_DIALOG_DATA) as CreateOrUpdateCategoriesDialogData;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;

        if (this.data.action === this.dialogActions.update) {
            this.#entitiesSrv.entityCategory.load(this.data.categoryId, this.data.entityId);
            this.#entitiesSrv.entityCategory.get$()
                .pipe(filter(entityCategory => !!entityCategory))
                .subscribe(entityCategory => {
                    this.form.patchValue({
                        description: entityCategory.description,
                        code: entityCategory.code
                    });
                });
        }
    }

    ngOnDestroy(): void {
        if (this.data.action === this.dialogActions.update) {
            this.#entitiesSrv.entityCategory.clear();
        }
    }

    createOrUpdateCategory(createAndNew = false): void {
        /*
            dialogActions.add --> Create parent category
            dialogActions.add && categoryId --> Create subcategory
            dialogActions.update && categoryId --> Edit parent or subcategory
        */
        if (this.form.valid) {
            const request: Partial<EntityCategory> = this.form.value;
            const dialogReturnData: CreateOrUpdateCategoriesDialogReturnData = { subcatgoryCreated: false };
            let obs$: Observable<number | void>;

            if (this.data.action === this.dialogActions.add) {
                if (this.data.categoryId) {
                    request.parent_id = this.data.categoryId;
                }
                obs$ = this.#entitiesSrv.entityCategory.create(request, this.data.entityId);
            } else if (this.data.action === this.dialogActions.update) {
                obs$ = this.#entitiesSrv.entityCategory.update(this.data.categoryId, request, this.data.entityId);
            }
            obs$.subscribe(categoryId => {
                if (categoryId && this.data.action === this.dialogActions.add) {
                    if (createAndNew) {
                        this.#ephemeralService.showSuccess({ msgKey: 'ENTITY.OWN_CATEGORIES.CREATE_OWN_CATEGORY_SUCCESS' });
                        this.form.reset();
                        dialogReturnData.newCategoryId = categoryId;
                        this.#lastIdIfCreateAndNew.set(categoryId);
                    } else {
                        if (request.parent_id) {
                            dialogReturnData.subcatgoryCreated = true;
                        }
                        dialogReturnData.newCategoryId = categoryId;
                        this.#lastIdIfCreateAndNew.set(null);
                        this.close(dialogReturnData);
                    }
                } else if (this.data.action === this.dialogActions.update) {
                    this.#lastIdIfCreateAndNew.set(null);
                    this.close(dialogReturnData);
                }
            });
        } else {
            this.form.markAllAsTouched();
        }
    }

    close(dialogReturnData: CreateOrUpdateCategoriesDialogReturnData = null): void {
        //For cases when create and new, but then cancel
        if (this.#lastIdIfCreateAndNew()) {
            dialogReturnData = {
                newCategoryId: this.#lastIdIfCreateAndNew()
            };
        }
        this.#dialogRef.close(dialogReturnData);
    }

}
