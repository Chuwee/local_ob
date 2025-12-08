import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { GoBackComponent, NavTabsMenuComponent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe, NgIf } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { routes } from './collective.routes';
import { CollectiveDetailsComponent } from './details/collective-details.component';
import { CollectiveEntityComponent } from './general-data/collective-entity/collective-entity.component';
import { ValidatorAuthDialogComponent }
    from './general-data/collective-entity/validator-auth-edit/collective-entity-validator-auth-edit-dialog.component';
import { CollectiveGeneralDataComponent } from './general-data/collective-general-data.component';

@NgModule({
    declarations: [
        CollectiveDetailsComponent,
        CollectiveGeneralDataComponent,
        CollectiveEntityComponent,
        ValidatorAuthDialogComponent
    ],
    imports: [
        RouterModule.forChild(routes),
        NgIf,
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        FlexLayoutModule,
        GoBackComponent,
        AsyncPipe,
        FormContainerComponent,
        FormControlErrorsComponent,
        SearchablePaginatedSelectionModule,
        NavTabsMenuComponent
    ]
})
export class CollectiveModule {
}
