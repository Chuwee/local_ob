import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { Category, PutEntity } from '@admin-clients/shared/common/data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe, KeyValuePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

@Component({
    selector: 'app-entity-base-categories',
    templateUrl: './entity-base-categories.component.html',
    styleUrls: ['./entity-base-categories.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatAccordion, MatCheckbox, MatExpansionPanel,
        AsyncPipe, MatProgressSpinnerModule, TranslatePipe, MatExpansionPanelHeader, MatExpansionPanelTitle,
        KeyValuePipe
    ]
})
export class EntityBaseCategoriesComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #categoriesSrv = inject(CategoriesService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);

    #entityId: number;
    #categoriesList: Category[];

    readonly form = this.#fb.group({
        useOwnCategories: false,
        categories: []
    });

    readonly reqInProgress$ = booleanOrMerge([
        this.#entitiesSrv.isEntitySaving$(),
        this.#entitiesSrv.isEntityLoading$(),
        this.#categoriesSrv.isCategoriesLoading$()
    ]);

    readonly mapCategories$ = this.#categoriesSrv.getCategories$().pipe(
        filter(Boolean),
        map(categories => {
            this.#categoriesList = categories;
            const mapCategories = {};
            categories.forEach(category => {
                if (category.parent_id) {
                    const parentCategory = categories.find(generalCategory => generalCategory.id === category.parent_id);
                    if (!mapCategories[parentCategory.code]) {
                        mapCategories[parentCategory.code] = {};
                    }
                    mapCategories[parentCategory.code][category.code] = category.id;
                }
            });
            return mapCategories;
        })
    );

    ngOnInit(): void {
        this.#categoriesSrv.loadCategories();

        this.#entitiesSrv.getEntity$().pipe(
            filter(entity => !!entity),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(({ id, settings }) => {
            this.#entityId = id;
            this.form.patchValue({
                useOwnCategories: settings.categories.allow_custom_categories,
                categories: settings.categories.selected?.map((category => category.id))
            });
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
            const value = this.form.value;
            const updatedEntity: PutEntity = {
                settings: {
                    categories: {
                        allow_custom_categories: value.useOwnCategories,
                        selected: value.categories.map(id => ({ id }))
                    }
                }
            };
            return this.#entitiesSrv.updateEntity(this.#entityId, updatedEntity)
                .pipe(tap(() => {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'ENTITY.UPDATE_SUCCESS' });
                    this.#entitiesSrv.loadEntity(this.#entityId);
                    this.form.markAsPristine();
                }));
        } else {
            return throwError(() => 'Invalid form');
        }
    }

    reload(): void {
        this.#entitiesSrv.loadEntity(this.#entityId);
    }

    doChange(isChecked: boolean, selected: number): void {
        const parentId = this.#categoriesList.find(category => category.id === selected)?.parent_id;
        let categories = this.form.value.categories || [];

        if (isChecked) {
            categories.push(selected);
            if (!categories.includes(parentId)) {
                categories.push(parentId);
            }
        } else {
            categories = categories.filter(categorySelected => categorySelected !== selected);
            const lastParentCategory = this.#categoriesList
                .filter(category => category.parent_id === parentId)
                .every(category => !categories.includes(category.id));
            if (lastParentCategory) {
                categories = categories.filter(categorySelected => categorySelected !== parentId);
            }
        }
        this.form.patchValue({ categories });
        this.form.markAsDirty();
    }
}
