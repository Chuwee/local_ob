import { NavTabsMenuComponent, PaginatorComponent, EmptyStateComponent, GoBackComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BuyerBasicInfoComponent } from './basic-info/buyer-basic-info.component';
import { BuyerRoutingModule } from './buyer-routing.module';
import { BuyerCommercialInfoComponent } from './commercial-info/buyer-commercial-info.component';
import {
    BuyerSubscriptionListSelDialogComponent
} from './commercial-info/buyer-subscription-list-sel-dialog/buyer-subscription-list-sel-dialog.component';
import { BuyerDetailsComponent } from './details/buyer-details.component';

@NgModule({
    declarations: [
        BuyerDetailsComponent,
        BuyerBasicInfoComponent,
        BuyerCommercialInfoComponent,
        BuyerSubscriptionListSelDialogComponent
    ],
    imports: [
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        CommonModule,
        LocalCurrencyPipe,
        GoBackComponent,
        FlexLayoutModule,
        FormContainerComponent,
        BuyerRoutingModule,
        EmptyStateComponent,
        DateTimePipe,
        NavTabsMenuComponent,
        PaginatorComponent,
        EllipsifyDirective
    ]
})
export class BuyerModule {
}
