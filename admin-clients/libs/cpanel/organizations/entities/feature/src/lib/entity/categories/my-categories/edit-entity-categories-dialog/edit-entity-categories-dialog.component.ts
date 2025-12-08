import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    CreateOrUpdateCategoriesDialogActions, CreateOrUpdateCategoriesDialogData, CreateOrUpdateCategoriesDialogReturnData, EntitiesService
} from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntityCategory, EntityCategoryFlatNode, EntityCategoryTreeModel } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { FlatTreeControl } from '@angular/cdk/tree';
import { AsyncPipe } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeFlatDataSource, MatTreeFlattener, MatTreeModule } from '@angular/material/tree';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import {
    CreateUpdateEntityCategoriesDialogComponent
} from '../create-update-entity-categories-dialog/create-update-entity-categories-dialog.component';

@Component({
    selector: 'app-edit-entity-categories-dialog',
    templateUrl: './edit-entity-categories-dialog.component.html',
    styleUrls: ['./edit-entity-categories-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        TranslatePipe, MatDialogModule, MatTreeModule, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule,
        EllipsifyDirective, AsyncPipe, MatTooltipModule, MatProgressSpinnerModule
    ]
})
export class EditEntityCategoriesDialogComponent implements OnInit, AfterViewInit {
    readonly #dialogRef = inject(MatDialogRef<EditEntityCategoriesDialogComponent>);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #auth = inject(AuthenticationService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #ephemeralService = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #onDestroy = inject(DestroyRef);

    readonly #entityId = signal<number>(null);
    readonly #parentCategoryToExpandId = signal<number>(null);
    readonly #newCategoryId = signal<number>(null);
    readonly #isOperatorUser = signal<boolean>(false);

    treeControl: FlatTreeControl<EntityCategoryFlatNode>;
    treeFlattener: MatTreeFlattener<EntityCategoryTreeModel, EntityCategoryFlatNode>;
    dataSource: MatTreeFlatDataSource<EntityCategoryTreeModel, EntityCategoryFlatNode>;

    entityCategories$: Observable<EntityCategory[]>;
    isInProgress$: Observable<boolean>;

    readonly dialogActions = CreateOrUpdateCategoriesDialogActions;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.LARGE);
        this.#dialogRef.disableClose = false;

        this.treeControl = new FlatTreeControl<EntityCategoryFlatNode>(
            node => node.level,
            node => node.expandable
        );

        //Note: Flat Tree renders on template each mat-tree-node without hierarchy
        this.treeFlattener = new MatTreeFlattener(
            this.transformer,
            node => node.level,
            node => node.expandable,
            node => node.subcategories
        );

        this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener, []);

        this.#entitiesSrv.getEntity$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(entity => {
            this.#entityId.set(entity.id);
        });
        this.#auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR])
            .pipe(filter(Boolean))
            .subscribe(isOperator => {
                this.#isOperatorUser.set(isOperator);
            });

        this.initComponentModels();
    }

    ngAfterViewInit(): void {
        //Expand all dataNodes when dialog opens
        this.treeControl.expandAll();
    }

    close(): void {
        this.#dialogRef.close();
    }

    hasChild = (_: number, node: EntityCategoryFlatNode): boolean => node.expandable;

    openDeleteCategoryDialog(categoryNode: EntityCategoryFlatNode): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_OWN_CATEGORY',
            message: 'ENTITY.OWN_CATEGORIES.DELETE_OWN_CATEGORY_WARNING',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#isOperatorUser()
                    ? this.#entitiesSrv.entityCategory.delete(categoryNode.id, this.#entityId())
                    : this.#entitiesSrv.entityCategory.delete(categoryNode.id))
            )
            .subscribe(() => {
                this.#ephemeralService.showSuccess({ msgKey: 'ENTITY.OWN_CATEGORIES.DELETE_OWN_CATEGORY_SUCCESS' });
                this.#entitiesSrv.loadEntityCategories(this.#entityId());
            });
    }

    openCreateOrUpdateCategoryDialog(
        action: CreateOrUpdateCategoriesDialogActions, categoryNode?: EntityCategoryFlatNode
    ): void {
        this.#matDialog.open<CreateUpdateEntityCategoriesDialogComponent, CreateOrUpdateCategoriesDialogData,
            CreateOrUpdateCategoriesDialogReturnData>(
                CreateUpdateEntityCategoriesDialogComponent, new ObMatDialogConfig({
                    action,
                    categoryId: categoryNode?.id,
                    entityId: this.#isOperatorUser() ? this.#entityId() : null
                })
            )
            .beforeClosed()
            .subscribe(dialogReturnData => {
                if (dialogReturnData) {
                    if (dialogReturnData.newCategoryId) {
                        this.#ephemeralService.showSuccess({ msgKey: 'ENTITY.OWN_CATEGORIES.CREATE_OWN_CATEGORY_SUCCESS' });
                        this.#newCategoryId.set(dialogReturnData.newCategoryId);
                        if (dialogReturnData.subcatgoryCreated) {
                            this.#parentCategoryToExpandId.set(categoryNode.id);
                        }
                    } else {
                        this.#ephemeralService.showSuccess({ msgKey: 'ENTITY.OWN_CATEGORIES.UPDATE_OWN_CATEGORY_SUCCESS' });
                    }
                    this.#entitiesSrv.loadEntityCategories(this.#entityId());
                }
            });
    }

    //Required by the Material Flat Tree for building the flat nodes
    private transformer = (node: EntityCategoryTreeModel, level: number): EntityCategoryFlatNode => ({
        expandable: node?.subcategories?.length > 0,
        id: node.id,
        code: node.code,
        description: node.description,
        base_category_id: node.base_category_id,
        level
    });

    private initComponentModels(): void {
        this.entityCategories$ = this.#entitiesSrv.getEntityCategories$().pipe(filter(Boolean));

        this.#entitiesSrv.getEntityCategories$()
            .pipe(
                filter(Boolean),
                map(entityCategories => {
                    const categoriesTreeModel = [...entityCategories] as EntityCategoryTreeModel[];
                    return this.buildCategoriesTreeModel(categoriesTreeModel);
                }),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(entityCategories => {
                //Save expanded nodes before refreshing
                const expandedNodesBeforeRefresh = this.treeControl.dataNodes.filter(node =>
                    node.expandable && this.treeControl.isExpanded(node)
                );
                //Refresh data and view
                this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener, entityCategories);
                //Expand the nodes that were expanded before refreshing
                expandedNodesBeforeRefresh?.forEach(expandedNode => this.treeControl.expand(
                    this.findDataNodeByCategoryId(expandedNode.id)
                ));
                //Expand the appropriate node when create a subcategory
                if (this.#parentCategoryToExpandId()) {
                    this.treeControl.expand(this.findDataNodeByCategoryId(this.#parentCategoryToExpandId()));
                    this.#parentCategoryToExpandId.set(null);
                }
                //Scroll to new category created
                if (this.#newCategoryId()) {
                    this.scrollToNewCategory(this.#newCategoryId());
                    this.#newCategoryId.set(null);
                }
            });

        this.isInProgress$ = this.#entitiesSrv.isEntityCategoriesLoading$();
    }

    //Order all parent categories by id and for each parent category that has subcategories, adds an array of subcategories.
    private buildCategoriesTreeModel(categories: EntityCategoryTreeModel[]): EntityCategoryTreeModel[] {
        const categoriesTreeModel: EntityCategoryTreeModel[] = [];
        categories.filter(category => !category.parent_id).sort((a, b) => a.id - b.id).forEach(parentCategory => {
            const parentCategoryTreeModel: EntityCategoryTreeModel = { ...parentCategory };
            const parentSubcategories = categories.filter(category => category.parent_id === parentCategory.id);
            if (parentSubcategories?.length) {
                parentCategoryTreeModel.subcategories = [...parentSubcategories];
            }
            categoriesTreeModel.push(parentCategoryTreeModel);
        });
        return categoriesTreeModel;
    }

    private scrollToNewCategory(categoryId: number): void {
        setTimeout(() => {
            const element = document.getElementById((categoryId).toString());
            if (element) {
                element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }, 300);
    }

    private findDataNodeByCategoryId(id: number): EntityCategoryFlatNode {
        return this.treeControl.dataNodes.find(node => node.id === id);
    }

}
