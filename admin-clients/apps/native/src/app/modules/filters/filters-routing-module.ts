import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authCanActivateGuard } from '../auth/services/auth.guard';
import { FiltersComponent } from './filters.component';

const routes: Routes = [
    {
        canActivate: [authCanActivateGuard],
        path: '',
        component: FiltersComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class FiltersRoutingModule {}
