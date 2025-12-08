import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PromotionDetailPage } from './promotion-detail.page';

const routes: Routes = [
    {
        path: '',
        component: PromotionDetailPage
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class PromotionDetailRoutingModule { }
