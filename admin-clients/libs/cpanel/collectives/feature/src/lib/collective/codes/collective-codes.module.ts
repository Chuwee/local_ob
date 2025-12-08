import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { CsvModule } from '@admin-clients/shared/common/feature/csv';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe, NgIf } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { IsUserPassCollectivePipe } from '../../pipes/collectives-user-pass.pipe';
import { IsUserCollectivePipe } from '../../pipes/collectives-user.pipe';
import { routes } from './collective-codes.routes';
import { NewCollectiveCodeDialogComponent } from './create/new-collective-code-dialog.component';
import { EditCollectiveCodeDialogComponent } from './edit/edit-collective-code-dialog.component';
import {
    ImportCollectiveCodesHeaderMatchComponent
} from './import/import-dialog/header-match/import-collective-codes-header-match.component';
import { ImportCollectiveCodesDialogComponent } from './import/import-dialog/import-collective-codes-dialog.component';
import { ImportCollectiveCodesSelectionComponent } from './import/import-dialog/selection/import-collective-codes-selection.component';
import { CollectiveCodesComponent } from './list/collective-codes.component';

@NgModule({
    declarations: [
        CollectiveCodesComponent,
        NewCollectiveCodeDialogComponent,
        EditCollectiveCodeDialogComponent,
        ImportCollectiveCodesDialogComponent,
        ImportCollectiveCodesHeaderMatchComponent,
        ImportCollectiveCodesSelectionComponent
    ],
    imports: [
        RouterModule.forChild(routes),
        NgIf,
        MaterialModule,
        ReactiveFormsModule,
        TranslatePipe,
        FlexLayoutModule,
        WizardBarComponent,
        AsyncPipe,
        SearchablePaginatedSelectionModule,
        FormControlErrorsComponent,
        CsvModule,
        IsUserCollectivePipe,
        IsUserPassCollectivePipe,
        DateTimePipe
    ]
})
export class CollectiveCodesModule {
}
