import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { EntitiesService, EntityCategoryMappingField } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Category, EntityCategory } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSelect } from '@angular/material/select';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, tap, combineLatest, throwError } from 'rxjs';
import { filter, first, switchMap } from 'rxjs/operators';
import { EditEntityCategoriesDialogComponent } from './edit-entity-categories-dialog/edit-entity-categories-dialog.component';

@Component({
    selector: 'app-entity-my-categories',
    templateUrl: './entity-my-categories.component.html',
    styleUrls: ['./entity-my-categories.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('parentCategoryExpand', [
            state('collapsed', style({ height: '0px', minHeight: '0', opacity: '0%', borderWidth: '0' })),
            state('expanded', style({ height: '*', minHeight: '*', opacity: '100%', borderWidth: '*' })),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)'))
        ])
    ],
    imports: [
        ReactiveFormsModule, TranslatePipe, FormContainerComponent, MatIcon, MatTable, MatHeaderCell,
        MatCell, MatTooltip, MatFormField, MatOption, MatSelect, MatHeaderRow, MatRow, MatHeaderRowDef,
        AsyncPipe, MatProgressSpinner, MatButton, MatColumnDef, MatHeaderCellDef, MatCellDef, MatRowDef,
        EllipsifyDirective
    ]
})
export class EntityMyCategoriesComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #categoriesSrv = inject(CategoriesService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);
    readonly #matDialog = inject(MatDialog);
    readonly #viewContainerRef = inject(ViewContainerRef);
    readonly #onDestroy = inject(DestroyRef);

    #entityId: number;

    categories: Category[];
    entityCategories: EntityCategory[];
    expandedCategory: Category;

    readonly form = this.#fb.group({});
    readonly columns = ['baseCategory', 'myCategories'];
    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.isEntityCategoriesLoading$(),
        this.#entitiesSrv.entityCategoriesMapping.loading$(),
        this.#categoriesSrv.isCategoriesLoading$()
    ]);

    ngOnInit(): void {
        this.#categoriesSrv.loadCategories();

        this.#entitiesSrv.getEntity$().pipe(
            takeUntilDestroyed(this.#onDestroy),
            filter(entity => !!entity)
        ).subscribe(entity => {
            this.#entityId = entity.id;
            this.#entitiesSrv.entityCategoriesMapping.load(entity.id);
            this.#entitiesSrv.loadEntityCategories(entity.id);
        });

        this.#categoriesSrv.getCategories$().pipe(
            first(categories => !!categories),
            switchMap(categories => {
                this.categories = this.sortCategories(categories);
                categories.filter(category => !!category.parent_id).forEach(subcategory => {
                    this.form.addControl(`${subcategory.id}`, this.#fb.control(null));
                });
                return combineLatest([
                    this.#entitiesSrv.entityCategoriesMapping.get$(),
                    this.#entitiesSrv.getEntityCategories$()
                ]);
            }),
            filter(res => res.every(item => !!item)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([entityCategoriesMapping, entityCategories]) => {
            //Disable all selects if there aren't entityCategories
            if (!entityCategories.length) {
                this.form.disable();
            } else {
                this.form.enable();
            }
            entityCategoriesMapping.forEach(({ category_id: categoryId, base_category_id: baseCategoryId }) => {
                const customCategory = entityCategories.find(category => category.id === categoryId);
                this.form.patchValue({ [baseCategoryId]: customCategory });
            });
            this.entityCategories = this.sortCategories(entityCategories);
        });
    }

    ngOnDestroy(): void {
        this.#categoriesSrv.clearCategories();
    }

    save(): void {
        this.save$().subscribe(() => this.reload());
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const formData = this.form.value;
            const categoryPairs: EntityCategoryMappingField[] = [];
            Object.keys(formData).forEach(key => {
                if (formData[key]?.id) {
                    const pair = { base_category_id: Number(key), category_id: formData[key].id };
                    categoryPairs.push(pair);
                }
            });
            return this.#entitiesSrv.entityCategoriesMapping.update(this.#entityId, categoryPairs)
                .pipe(
                    tap(() => {
                        this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                        this.#entitiesSrv.loadEntity(this.#entityId);
                        this.form.markAsPristine();
                    })
                );
        } else {
            return throwError(() => 'Invalid form');
        }
    }

    reload(): void {
        this.#entitiesSrv.loadEntity(this.#entityId);
    }

    openEditCategoriesDialog(): void {
        this.#matDialog.open<EditEntityCategoriesDialogComponent>(
            EditEntityCategoriesDialogComponent, new ObMatDialogConfig(null, this.#viewContainerRef)
        ).beforeClosed().subscribe();
    }

    private sortCategories(categories: (Category | EntityCategory)[]): Category[] | EntityCategory[] {
        let finalCategories = [];
        categories.filter(category => !category.parent_id).sort((a, b) => a.id - b.id).forEach(parentCategory => {
            finalCategories = finalCategories.concat(
                [parentCategory, ...categories.filter(category => category.parent_id === parentCategory.id)]
            );
        });
        return finalCategories;
    }
}
