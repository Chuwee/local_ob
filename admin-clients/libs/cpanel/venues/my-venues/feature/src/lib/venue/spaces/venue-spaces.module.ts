import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { VenueSpacesContainerComponent } from './container/venue-spaces-container.component';
import { NewVenueSpaceDialogComponent } from './create/new-venue-space-dialog.component';
import { VenueSpaceDetailsComponent } from './details/venue-space-details.component';
import { VenueSpacesListComponent } from './list/venue-spaces-list.component';
import { VenueSpacesRoutingModule } from './venue-spaces-routing.module';

@NgModule({
    declarations: [
        VenueSpacesContainerComponent,
        VenueSpaceDetailsComponent,
        VenueSpacesListComponent,
        NewVenueSpaceDialogComponent
    ],
    imports: [
        VenueSpacesRoutingModule,
        EmptyStateComponent,
        FlexLayoutModule,
        MaterialModule,
        TranslatePipe,
        AsyncPipe,
        LastPathGuardListenerDirective,
        FormContainerComponent,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        CommonModule,
        EllipsifyDirective
    ]
})
export class VenueSpacesModule { }
