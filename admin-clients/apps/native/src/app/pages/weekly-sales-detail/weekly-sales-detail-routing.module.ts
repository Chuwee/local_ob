import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WeeklySalesDetailPage } from './weekly-sales-detail.page';

const routes: Routes = [
    {
        path: '',
        component: WeeklySalesDetailPage
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class WeeklySalesDetailRoutingModule { }
