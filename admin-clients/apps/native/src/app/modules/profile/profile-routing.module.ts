import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authCanActivateGuard } from '../auth/services/auth.guard';
import { ProfileConfigComponent } from './components/profile-config/profile-config.component';
import { ProfileComponent } from './profile.component';

const routes: Routes = [
    {
        path: '',
        component: ProfileComponent,
        children: [
            {
                path: '',
                canActivate: [authCanActivateGuard],
                component: ProfileConfigComponent
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class ProfileRoutingModule { }
