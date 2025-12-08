import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { venuesProviders } from '@admin-clients/cpanel/venues/data-access';
import {
    PopoverFilterDirective,
    PopoverComponent,
    ChipsFilterDirective,
    ChipsComponent,
    PaginatorComponent,
    SearchInputComponent,
    ContextNotificationComponent,
    SelectServerSearchComponent,
    SelectSearchComponent,
    HelpButtonComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { NewVenueDialogComponent } from './create/new-venue-dialog.component';
import { GooglePlaceAutocompleteComponent } from './google-place-autocomplete/google-place-autocomplete.component';
import { VenuesListFilterComponent } from './list/filter/venues-list-filter.component';
import { VenuesListComponent } from './list/venues-list.component';
import { VenuesRoutingModule } from './venues-routing.module';

@NgModule({
    declarations: [
        VenuesListComponent,
        VenuesListFilterComponent,
        NewVenueDialogComponent
    ],
    imports: [
        VenuesRoutingModule,
        PopoverComponent,
        PopoverFilterDirective,
        ChipsFilterDirective,
        ChipsComponent,
        PaginatorComponent,
        MaterialModule,
        TranslatePipe,
        SearchInputComponent,
        AsyncPipe,
        LocalNumberPipe,
        ContextNotificationComponent,
        FlexLayoutModule,
        CommonModule,
        SelectServerSearchComponent,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        SelectSearchComponent,
        EllipsifyDirective,
        GooglePlaceAutocompleteComponent,
        HelpButtonComponent
    ],
    providers: [venuesProviders]
})
export class VenuesModule { }
