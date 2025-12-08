import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { EntityFilterButtonComponent } from './entity-filter-button/entity-filter-button.component';
import { EntitySelectionDialogComponent } from './entity-selection-dialog/entity-selection-dialog.component';

@NgModule({
    declarations: [
        EntitySelectionDialogComponent,
        EntityFilterButtonComponent
    ],
    exports: [
        EntitySelectionDialogComponent,
        EntityFilterButtonComponent
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        AsyncPipe,
        ReactiveFormsModule,
        TranslatePipe,
        FormControlErrorsComponent,
        MaterialModule,
        SelectSearchComponent,
        EllipsifyDirective
    ]
})
export class EntityFilterModule { }
