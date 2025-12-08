import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { GoBackComponent, ImageUploaderComponent, NavTabsMenuComponent, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { GooglePlaceAutocompleteComponent } from '../google-place-autocomplete/google-place-autocomplete.component';
import { VenueAccessControlComponent } from './access-control/venue-access-control.component';
import { VenueDetailsComponent } from './details/venue-details.component';
import { VenueGeneralDataComponent } from './general-data/venue-general-data.component';
import { VenueRoutingModule } from './venue-routing.module';

@NgModule({
    declarations: [
        VenueDetailsComponent,
        VenueGeneralDataComponent,
        VenueAccessControlComponent
    ],
    imports: [
        VenueRoutingModule,
        NavTabsMenuComponent,
        MaterialModule,
        TranslatePipe,
        FormControlErrorsComponent,
        ImageUploaderComponent,
        SelectSearchComponent,
        FormContainerComponent,
        AsyncPipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        CommonModule,
        GoBackComponent,
        EllipsifyDirective,
        GooglePlaceAutocompleteComponent
    ]
})
export class VenueModule { }
