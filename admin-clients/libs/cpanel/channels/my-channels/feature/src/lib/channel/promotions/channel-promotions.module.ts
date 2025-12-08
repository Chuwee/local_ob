import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { ChannelsPipesModule } from '@admin-clients/cpanel/channels/data-access';
import { EventSessionSelectorComponent } from '@admin-clients/cpanel/common/feature/event-session-selector';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import {
    CurrencyInputComponent, DateTimeModule, EmptyStateComponent, HelpButtonComponent, NavTabsMenuComponent, PercentageInputComponent,
    SearchablePaginatedSelectionModule, SelectorListComponent, SelectServerSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import {
    DateTimePipe, ErrorMessage$Pipe, LocalCurrencyPartialTranslationPipe, LocalCurrencyPipe, LocalNumberPipe
} from '@admin-clients/shared/utility/pipes';
import { CommunicationTextContentComponent } from '@admin-clients/shared-common-ui-communication-texts';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { ChannelPromotionsRoutingModule } from './channel-promotions-routing.module';
import { ChannelPromotionsContainerComponent } from './container/channel-promotions-container.component';
import { NewPromotionDialogComponent } from './create/new-promotion-dialog.component';
import { ChannelPromotionsListComponent } from './list/channel-promotions-list.component';
import { ChannelPromotionConditionsComponent } from './promotion/conditions/channel-promotion-conditions.component';
import { ChannelPromotionDetailsComponent } from './promotion/details/channel-promotion-details.component';
import { ChannelPromotionEventsComponent } from './promotion/events/promotion-events.component';
import { ChannelPromotionGeneralDataComponent } from './promotion/general-data/channel-promotion-general-data.component';
import { ChannelPromotionPacksComponent } from './promotion/packs/channel-promotion-packs.component';
import { ChannelPromotionPriceTypesComponent } from './promotion/price-types/channel-promotion-price-types.component';
import { ChannelPromotionSessionComponent } from './promotion/sessions/channel-promotion-sessions.component';
import { ChannelPromotionZonesComponent } from './promotion/zones/channel-promotion-zones.component';

@NgModule({
    declarations: [
        ChannelPromotionsListComponent,
        ChannelPromotionsContainerComponent,
        ChannelPromotionGeneralDataComponent,
        ChannelPromotionConditionsComponent,
        ChannelPromotionZonesComponent,
        ChannelPromotionEventsComponent,
        ChannelPromotionSessionComponent,
        ChannelPromotionPriceTypesComponent,
        ChannelPromotionPacksComponent,
        NewPromotionDialogComponent
    ],
    imports: [
        ChannelPromotionsRoutingModule,
        CommunicationTextContentComponent,
        SearchablePaginatedSelectionModule,
        DateTimeModule,
        NavTabsMenuComponent,
        LocalNumberPipe,
        LocalCurrencyPipe,
        DateTimePipe,
        EmptyStateComponent,
        SelectorListComponent,
        LocalCurrencyPartialTranslationPipe,
        ChannelPromotionDetailsComponent,
        ChannelsPipesModule,
        EventSessionSelectorComponent,
        TranslatePipe,
        CommonModule,
        FlexLayoutModule,
        MaterialModule,
        ReactiveFormsModule,
        PercentageInputComponent,
        CurrencyInputComponent,
        FormContainerComponent,
        FormControlErrorsComponent,
        HelpButtonComponent,
        SelectServerSearchComponent,
        LastPathGuardListenerDirective,
        ErrorMessage$Pipe,
        ErrorIconDirective,
        RouterLink,
        EllipsifyDirective
    ]
})
export class ChannelPromotionsModule { }
