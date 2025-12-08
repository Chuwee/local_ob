import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { CategoriesService } from '@admin-clients/cpanel/organizations/data-access';
import { TourStatus, ToursService } from '@admin-clients/cpanel/promoters/tours/data-access';
import { Category, EntitiesBaseService, EntityCategory } from '@admin-clients/shared/common/data-access';
import { HelpButtonComponent, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, first, map, shareReplay, tap } from 'rxjs/operators';
import { CategorySelectionItem, PutCategorySelectionRequest } from './category-selection-item.model';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, MaterialModule, EllipsifyDirective, FlexLayoutModule, ReactiveFormsModule, SelectSearchComponent,
        TranslatePipe, HelpButtonComponent
    ],
    selector: 'app-categories-selection',
    templateUrl: './categories-selection.component.html'
})
export class CategoriesSelectionComponent implements OnInit, OnDestroy {
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #categoriesSrv = inject(CategoriesService);
    readonly #toursSrv = inject(ToursService);
    readonly #channelsService = inject(ChannelsService);

    #categories: Category[];
    #entityCategories: EntityCategory[];

    readonly #$channel = toSignal(this.#channelsService.getChannel$().pipe(first(Boolean)));

    readonly toursBS$ = this.#toursSrv.getToursListData$()
        .pipe(
            filter(Boolean),
            tap(tours => {
                if (tours.length) {
                    this.categoriesDataForm.controls.tour.enable({ emitEvent: false });
                } else {
                    this.categoriesDataForm.controls.tour.disable({ emitEvent: false });
                }
            }));

    readonly categories$ = this.#categoriesSrv.getCategories$().pipe(first(Boolean), shareReplay(1));
    readonly entityCategories$ = this.#entitiesSrv.getEntityCategories$().pipe(
        first(Boolean),
        map(categories =>
            categories
                .reduce<EntityCategory[]>((result, currentCategory, _, categoriesArray) => {
                    if (currentCategory.parent_id) {
                        const parentCategory = categoriesArray.find(
                            parentCategory => parentCategory.id === currentCategory.parent_id
                        );
                        if (parentCategory) {
                            currentCategory.description =
                                parentCategory.description + ' - ' + currentCategory.description;
                        }
                    }
                    if (
                        currentCategory.parent_id ||
                        categoriesArray.every(category => category.parent_id !== currentCategory.id)
                    ) {
                        result.push(currentCategory);
                    }
                    return result;
                }, [])
                .sort((a, b) => (a.description > b.description ? 1 : b.description > a.description ? -1 : 0))
        ),
        shareReplay(1)
    );

    readonly $categoryIsRequired = input<boolean, boolean>(true, {
        alias: 'categoryIsRequired',
        transform: (isRequired: boolean): boolean => {
            const categoryControl = this.categoriesDataForm.get('categoryId') as FormControl;
            if (categoryControl) {
                isRequired
                    ? categoryControl.setValidators([Validators.required])
                    : categoryControl.clearValidators();
                categoryControl.updateValueAndValidity({ emitEvent: false });
            }
            return isRequired;
        }
    });

    readonly $putItemCtrl = input<FormControl<PutCategorySelectionRequest>>(null!, { alias: 'putItemCtrl' });
    readonly $form = input<FormGroup>(null!, { alias: 'form' });
    readonly $item = input<CategorySelectionItem, CategorySelectionItem>(null!, {
        alias: 'item',
        transform: (item: CategorySelectionItem) => {
            if (item) {
                this.categoriesDataForm.reset(
                    {
                        categoryId: item.settings?.categories?.base?.id,
                        customCategoryId: item.settings?.categories?.custom?.id,
                        tour: item.settings?.tour?.id || null
                    },
                    { emitEvent: false }
                );
                if (item.entity?.id) {
                    this.#entitiesSrv.loadEntityCategories(item.entity.id);
                } else {
                    this.#entitiesSrv.loadEntityCategories(this.#$channel()?.entity.id);
                }
                if (item.settings?.tour) {
                    this.#toursSrv.loadToursList({
                        entityId: item.entity.id,
                        status: [TourStatus.active]
                    });
                }
            }
            return item;
        }
    });

    readonly categoriesDataForm = inject(FormBuilder).nonNullable.group({
        categoryId: [null as number, []],
        customCategoryId: null as number,
        tour: { value: null as number, disabled: true }
    });

    ngOnInit(): void {
        this.$form().addControl('categories', this.categoriesDataForm, { emitEvent: false });
        this.#categoriesSrv.loadCategories();
        this.#entitiesSrv
            .getEntityCategories$()
            .pipe(first(Boolean))
            .subscribe(entityCategories => (this.#entityCategories = entityCategories));
        this.#categoriesSrv
            .getCategories$()
            .pipe(first(Boolean))
            .subscribe(categories => (this.#categories = categories));

        this.$putItemCtrl()
            .valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(putItem => {
                if (this.$form().invalid) return;
                const { categoryId, customCategoryId, tour } = this.categoriesDataForm.controls;
                if (categoryId.dirty || customCategoryId.dirty || tour.dirty) {
                    putItem.settings = putItem.settings ?? {};
                    if (categoryId.dirty) {
                        const base = this.#categories.find(category => category.id === categoryId.value);
                        putItem.settings.categories = putItem.settings.categories ?? {};
                        putItem.settings.categories.base = base;
                    }
                    if (customCategoryId.dirty) {
                        const custom = this.#entityCategories.find(category => category.id === customCategoryId.value);
                        putItem.settings.categories = putItem.settings.categories ?? {};
                        putItem.settings.categories.custom = custom;
                    }
                    if (tour.dirty) {
                        putItem.settings.tour = putItem.settings.tour ?? { enable: false };
                        if (tour.value) {
                            putItem.settings.tour = { enable: true, id: tour.value };
                        }
                    }
                    this.$putItemCtrl().setValue(putItem, { emitEvent: false });
                }
            });
    }

    ngOnDestroy(): void {
        this.#categoriesSrv.clearCategories();
        this.#entitiesSrv.clearEntityCategories();
        this.#toursSrv.clearToursList();
    }
}
