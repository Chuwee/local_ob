import { HeaderSummaryComponent, WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    ChipsFilterDirective, PopoverFilterDirective, PopoverComponent, SearchablePaginatedSelectionModule,
    ChipsComponent, EmptyStateComponent, PaginatorComponent, ContextNotificationComponent,
    SearchInputComponent, SelectSearchComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalCurrencyPipe, LocalDateTimePipe, LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { SeasonTicketRenewalsContainerComponent } from './container/season-ticket-renewals-container.component';
import {
    SeasonTicketRenewalExternalCandidateComponent
} from './create/candidate/external-candidate/season-ticket-renewal-external-candidate.component';
import {
    SeasonTicketRenewalInternalCandidateComponent
} from './create/candidate/internal-candidate/season-ticket-renewal-internal-candidate.component';
import { SeasonTicketRenewalCandidateComponent } from './create/candidate/season-ticket-renewal-candidate.component';
import { NewSeasonTicketRenewalDialogComponent } from './create/new-season-ticket-renewal-dialog.component';
import { SeasonTicketRenewalExternalRatesComponent } from './create/rates/external-rates/season-ticket-renewal-external-rates.component';
import { SeasonTicketRenewalInternalRatesComponent } from './create/rates/internal-rates/season-ticket-renewal-internal-rates.component';
import { SeasonTicketRenewalRatesMappingComponent } from './create/rates/rates-mapping/season-ticket-renewal-rates-mapping.component';
import { SeasonTicketRenewalRatesComponent } from './create/rates/season-ticket-renewal-rates.component';
import { GroupSelectSearchComponent } from './edit/group-select-search/group-select-search.component';
import { SeasonTicketRenewalsListFilterComponent } from './list/filter/season-ticket-renewals-list-filter.component';
import { SeasonTicketRenewalsListComponent } from './list/season-ticket-renewals-list.component';
import { SeasonTicketRenewalsListSummaryComponent } from './list/summary/season-ticket-renewals-list-summary.component';
import { SeasonTicketsRenewalsRoutingModule } from './season-ticket-renewals-routing.module';

@NgModule({
    declarations: [
        SeasonTicketRenewalsContainerComponent,
        NewSeasonTicketRenewalDialogComponent,
        SeasonTicketRenewalCandidateComponent,
        SeasonTicketRenewalRatesComponent,
        SeasonTicketRenewalInternalCandidateComponent,
        SeasonTicketRenewalExternalCandidateComponent,
        SeasonTicketRenewalInternalRatesComponent,
        SeasonTicketRenewalExternalRatesComponent,
        SeasonTicketRenewalRatesMappingComponent
    ],
    imports: [
        SeasonTicketsRenewalsRoutingModule,
        SearchablePaginatedSelectionModule,
        LocalNumberPipe,
        LocalDateTimePipe,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        ChipsComponent,
        EmptyStateComponent,
        WizardBarComponent,
        HeaderSummaryComponent,
        PaginatorComponent,
        GroupSelectSearchComponent,
        MaterialModule,
        ContextNotificationComponent,
        CommonModule,
        FlexLayoutModule,
        TranslatePipe,
        SearchInputComponent,
        LocalCurrencyPipe,
        ReactiveFormsModule,
        SelectSearchComponent,
        EllipsifyDirective,
        SeasonTicketRenewalsListComponent,
        SeasonTicketRenewalsListFilterComponent,
        SeasonTicketRenewalsListSummaryComponent
    ]
})
export class SeasonTicketsRenewalsModule {
}
