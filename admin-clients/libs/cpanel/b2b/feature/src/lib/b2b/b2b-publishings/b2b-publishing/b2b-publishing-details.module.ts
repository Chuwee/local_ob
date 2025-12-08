import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { TableDetailRowModule } from '@admin-clients/cpanel/common/utils';
import {
    ContextNotificationComponent, CopyTextComponent, CurrencyInputComponent, GoBackComponent, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    AbsoluteAmountPipe, AmountPrefixPipe,
    DateTimePipe,
    LocalCurrencyPipe,
    LocalDateTimePipe,
    ObfuscateStringPipe
} from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { SatPopoverModule } from '@ncstate/sat-popover';
import { TranslatePipe } from '@ngx-translate/core';
import { B2bPublishingDetailsRoutingModule } from './b2b-publishing-details-routing.module';
import { B2bPublishingDetailsComponent } from './details/b2b-publishing-details.component';
import { B2bPublishingGeneralDataComponent } from './general-data/b2b-publishing-general-data.component';
import { B2bPublishingDetailsHistoricComponent } from './general-data/historic/b2b-publishing-details-historic.component';

@NgModule({
    declarations: [
        B2bPublishingDetailsComponent,
        B2bPublishingGeneralDataComponent,
        B2bPublishingDetailsHistoricComponent
    ],
    imports: [
        CommonModule,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        B2bPublishingDetailsRoutingModule,
        FlexLayoutModule,
        FormContainerComponent,
        FormControlErrorsComponent,
        CopyTextComponent,
        GoBackComponent,
        CurrencyInputComponent,
        ContextNotificationComponent,
        SatPopoverModule,
        LocalCurrencyPipe,
        ObfuscateStringPipe,
        LocalDateTimePipe,
        DateTimePipe,
        AsyncPipe,
        EllipsifyDirective,
        NavTabsMenuComponent,
        AbsoluteAmountPipe,
        AmountPrefixPipe,
        TableDetailRowModule
    ]
})
export class OrderDetailsModule { }
