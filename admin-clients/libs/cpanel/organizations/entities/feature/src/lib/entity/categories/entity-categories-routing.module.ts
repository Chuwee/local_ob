import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntityBaseCategoriesComponent } from './base-categories/entity-base-categories.component';
import { EntityCategoriesComponent } from './entity-categories.component';
import { EntityMyCategoriesComponent } from './my-categories/entity-my-categories.component';

const routes: Routes = [{
    path: '',
    component: EntityCategoriesComponent,
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'base-categories'
        },
        {
            path: 'base-categories',
            component: EntityBaseCategoriesComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.BASE_CATEGORIES.TITLE'
            }
        },
        {
            path: 'my-categories',
            component: EntityMyCategoriesComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.MY_CATEGORIES'
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EntityCategoriesRoutingModule { }
