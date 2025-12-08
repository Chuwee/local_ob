import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import {
  AggregatedDataComponent,
  CurrencyInputComponent,
  DateTimeModule,
  EmptyStateComponent,
  GoBackComponent,
  HelpButtonComponent, NavTabsMenuComponent,
  PaginatorComponent,
  PopoverComponent,
  PopoverDateRangePickerFilterComponent,
  PopoverFilterDirective,
  SearchInputComponent,
  SearchTableComponent,
  SelectSearchComponent,
  TableMoreActionsButtonDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe, LocalCurrencyPipe, LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { B2bConditionsFormComponent } from '../../generic-conditions-form/b2b-conditions-form.component';
import { B2bClientRoutingModule } from './b2b-client-routing.module';
import { B2bClientConditionsComponent } from './conditions/b2b-client-conditions.component';
import { B2bClientDetailsComponent } from './details/b2b-client-details.component';
import { B2bClientEconomicManagementComponent } from './economic-management/b2b-client-economic-management.component';
import {
  B2bClientBalanceOperationDialogComponent
} from './economic-management/balance-operation/b2b-client-balance-operation-dialog.component';
import {
  B2bClientEconomicManagementCurrencyFilterComponent
} from './economic-management/currency-filter/b2b-client-economic-management-currency-filter.component';
import { B2bClientEconomicManagementFilterComponent } from './economic-management/filter/b2b-client-economic-management-filter.component';
import { B2bClientGeneralDataComponent } from './general-data/b2b-client-general-data.component';
import { B2bClientUsersManagementComponent } from './users-management/b2b-client-users-management.component';
import {
  CreateUpdateB2bClientUserDialogComponent
} from './users-management/create-update-b2b-client-user-dialog/create-update-b2b-client-user-dialog.component';

@NgModule({
  declarations: [
    B2bClientDetailsComponent,
    B2bClientGeneralDataComponent,
    B2bClientConditionsComponent,
    B2bClientEconomicManagementComponent,
    B2bClientEconomicManagementFilterComponent,
    B2bClientUsersManagementComponent,
    CreateUpdateB2bClientUserDialogComponent,
    B2bClientBalanceOperationDialogComponent
  ],
  imports: [
    CommonModule,
    B2bClientRoutingModule,
    ReactiveFormsModule,
    TranslatePipe,
    MaterialModule,
    DateTimeModule,
    FlexLayoutModule,
    FormControlErrorsComponent,
    FormContainerComponent,
    SelectSearchComponent,
    SearchTableComponent,
    B2bConditionsFormComponent,
    HelpButtonComponent,
    AggregatedDataComponent,
    PaginatorComponent,
    SearchInputComponent,
    CurrencyInputComponent,
    GoBackComponent,
    DateTimePipe,
    AsyncPipe,
    LocalDateTimePipe,
    LocalCurrencyPipe,
    TableMoreActionsButtonDirective,
    PopoverComponent,
    PopoverFilterDirective,
    PopoverDateRangePickerFilterComponent,
    EmptyStateComponent,
    NavTabsMenuComponent,
    B2bClientEconomicManagementCurrencyFilterComponent,
    EllipsifyDirective
  ]
})
export class B2bClientModule { }
