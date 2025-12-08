import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authCanActivateGuard } from '../auth/services/auth.guard';
import { GlobalSearchComponent } from './global-search.component';

const routes: Routes = [
    {
        path: '',
        canActivate: [authCanActivateGuard],
        component: GlobalSearchComponent,
        children: []
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class GobalSearchRoutingModule { }
