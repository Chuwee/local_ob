import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { B2bService, B2bApi, B2bState } from '@admin-clients/cpanel/b2b/data-access';
import { EntityFilterModule } from '@admin-clients/cpanel/organizations/entities/feature';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import {
    ObDialogService, ContextNotificationComponent, PaginatorComponent,
    SearchInputComponent, SelectSearchComponent, HelpButtonComponent, EmptyStateComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { B2bClientsRoutingModule } from './b2b-clients-routing.module';
import { B2bClientBasicDataComponent } from './create/basic-data/b2b-client-basic-data.component';
import { B2bClientContactDataComponent } from './create/contact-data/b2b-client-contact-data.component';
import { NewB2bClientDialogComponent } from './create/new-b2b-client-dialog.component';
import { B2bClientUserCreationComponent } from './create/user-creation/b2b-client-user-creation.component';
import { B2bClientsListComponent } from './list/b2b-clients-list.component';

@NgModule({
    declarations: [
        B2bClientsListComponent,
        NewB2bClientDialogComponent,
        B2bClientBasicDataComponent,
        B2bClientContactDataComponent,
        B2bClientUserCreationComponent
    ],
    imports: [
        CommonModule,
        B2bClientsRoutingModule,
        EntityFilterModule,
        TranslatePipe,
        ReactiveFormsModule,
        MaterialModule,
        FlexLayoutModule,
        FormControlErrorsComponent,
        ContextNotificationComponent,
        PaginatorComponent,
        SearchInputComponent,
        WizardBarComponent,
        SelectSearchComponent,
        AsyncPipe,
        LocalDateTimePipe,
        HelpButtonComponent,
        EmptyStateComponent
    ],
    providers: [
        B2bService,
        B2bApi,
        B2bState,
        ObDialogService
    ]
})
export class B2bClientsModule { }
