import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { GoBackComponent, NavTabsMenuComponent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { CommunicationTextContentComponent } from '@admin-clients/shared-common-ui-communication-texts';
import { GroupContainerComponent } from '../container/group-container.component';
import { VoucherGiftCardModule } from '../gift-card/voucher-gift-card.module';
import { ManualCodeGroupModule } from '../manual-code/manual-code-group.module';
import { GroupPrincipalInfoComponent } from '../principal-info/group-principal-info.component';
import { GroupRedeemComponent } from '../redeem/group-redeem.component';
import { VoucherGroupDetailsComponent } from './voucher-group-details.component';
import { VoucherGroupRoutingModule } from './voucher-group-routing.module';

@NgModule({
    declarations: [
        VoucherGroupDetailsComponent,
        GroupPrincipalInfoComponent,
        GroupRedeemComponent,
        GroupContainerComponent
    ],
    exports: [],
    imports: [
        MaterialModule,
        CommonModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        TranslatePipe,
        LastPathGuardListenerDirective,
        FormControlErrorsComponent,
        GoBackComponent,
        VoucherGroupRoutingModule,
        ManualCodeGroupModule,
        VoucherGiftCardModule,
        CommunicationTextContentComponent,
        SearchablePaginatedSelectionModule,
        NavTabsMenuComponent,
        FormContainerComponent
    ]
})
export class VoucherGroupModule { }
