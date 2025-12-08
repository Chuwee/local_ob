import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { voucherOrderDetailsResolver } from './details/voucher-order-details-resolver';
import { VoucherOrderDetailsComponent } from './details/voucher-order-details.component';
import { VoucherOrderGeneralDataComponent } from './general-data/voucher-order-general-data.component';

const routes: Routes = [
    {
        path: '',
        component: VoucherOrderDetailsComponent,
        resolve: {
            order: voucherOrderDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: VoucherOrderGeneralDataComponent
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class VoucherOrderDetailsRoutingModule { }
