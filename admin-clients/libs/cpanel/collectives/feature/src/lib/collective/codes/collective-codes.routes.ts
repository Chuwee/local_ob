import { Routes } from '@angular/router';
import { CollectiveCodesComponent } from './list/collective-codes.component';

export const routes: Routes = [{
    path: '',
    children: [
        {
            path: '',
            component: CollectiveCodesComponent,
            pathMatch: 'full',
            data: {
                breadcrumb: 'COLLECTIVE.CODE_LIST'
            }
        }
    ]
}];