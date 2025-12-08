import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EventPipesModule } from '@admin-clients/cpanel-promoters-events-utils';
import { PriceTypeRestrictionsComponent } from '@admin-clients/cpanel-promoters-venue-templates-feature';
import { PriceTypeTranslationsComponent } from '@admin-clients/cpanel-venues-venue-templates-feature';
import {
    CurrencyInputComponent,
    DateTimeModule, EmptyStateComponent,
    HelpButtonComponent,
    RangeTableComponent, RichTextAreaComponent, SelectSearchComponent, TabDirective, TabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective, ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { EventPricesRoutingModule } from './event-prices-routing.module';
import { EventPricesComponent } from './event-prices.component';
import { EventPriceTypesComponent } from './price-types/event-price-types.component';
import { EventPriceTypesRatesGroupComponent } from './price-types/price-type-rates-group/event-price-types-rates-group.component';
import {
    EventPriceTypesRatesGroupFiltersComponent
} from './price-types/price-type-rates-group/filters/event-price-types-rates-group-filters.component';
import {
    EventPriceTypesRatesGroupListComponent
} from './price-types/price-type-rates-group/list/event-price-types-rates-group-list.component';
import { EventPriceTypesRatesComponent } from './price-types/price-types-rates/event-price-types-rates.component';
import { EventPriceTypesMatrixComponent } from './price-types/price-types-rates/price-types-matrix/event-price-types-matrix.component';
import {
    EventPriceTypeRestrictionsComponent
} from './price-types/price-types-rates/price-types-matrix/restrictions/event-price-type-restrictions.component';
import { EventTiersComponent } from './price-types/tiers/event-tiers.component';
import {
    TiersCommunicationDialogComponent
} from './price-types/tiers/tiers-communication-table/tiers-communication-dialog/tiers-communication-dialog.component';
import { TiersCommunicationTableComponent } from './price-types/tiers/tiers-communication-table/tiers-communication-table.component';
import { EventRatesListComponent } from './rates/list/event-rates-list.component';
import { TranslateRatesDialogComponent } from './rates/list/translate-rates-dialog/translate-rates-dialog.component';
import { SaveRatesGroupDialogComponent } from './rates/rates-group-list/save/save-rates-group-dialog.component';
import { EventSurchargesComponent } from './surcharges/event-surcharges.component';
import { EventTaxesComponent } from './taxes/event-taxes.component';

@NgModule({
    declarations: [
        EventPricesComponent,
        EventSurchargesComponent,
        EventPriceTypesComponent,
        EventTaxesComponent,
        EventTiersComponent,
        TiersCommunicationTableComponent,
        TiersCommunicationDialogComponent,
        TranslateRatesDialogComponent,
        SaveRatesGroupDialogComponent
    ],
    imports: [
        CommonModule,
        RangeTableComponent,
        FlexLayoutModule,
        LastPathGuardListenerDirective,
        SelectSearchComponent,
        RichTextAreaComponent,
        FormControlErrorsComponent,
        ReactiveFormsModule,
        TranslatePipe,
        HelpButtonComponent,
        FormContainerComponent,
        MaterialModule,
        CurrencyInputComponent,
        EventPricesRoutingModule,
        EventPipesModule,
        PriceTypeTranslationsComponent,
        DragDropModule,
        PriceTypeRestrictionsComponent,
        DateTimeModule,
        LocalCurrencyPipe,
        TabsMenuComponent,
        TabDirective,
        EmptyStateComponent,
        RangeTableComponent,
        EventRatesListComponent,
        EventPriceTypesRatesComponent,
        EventPriceTypesRatesGroupComponent,
        EventPriceTypesRatesGroupFiltersComponent,
        EventPriceTypesRatesGroupListComponent,
        EventPriceTypeRestrictionsComponent,
        EventPriceTypesMatrixComponent,
        ErrorIconDirective,
        ErrorMessage$Pipe,
        EllipsifyDirective
    ]
})
export class EventPricesModule { }
