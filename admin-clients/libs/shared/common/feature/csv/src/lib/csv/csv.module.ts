import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { FileStatusModule, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { DownloadFileDirective, UploadFileDirective } from '@admin-clients/shared/utility/directives';
import { AsyncPipe, CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { CsvHeaderMappingComponent } from './csv-header-mapping/csv-header-mapping.component';
import { CsvFileSelectorComponent } from './csv-selector/csv-file-selector/csv-file-selector.component';
import { CsvSelectorComponent } from './csv-selector/csv-selector.component';

@NgModule({
    declarations: [
        CsvSelectorComponent,
        CsvFileSelectorComponent,
        CsvHeaderMappingComponent
    ],
    imports: [
        SelectSearchComponent,
        FormControlErrorsComponent,
        CommonModule,
        FlexLayoutModule,
        ReactiveFormsModule,
        FileStatusModule,
        UploadFileDirective,
        DownloadFileDirective,
        MatButtonModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatIconModule,
        MatSelectModule,
        MatTableModule,
        MatTooltipModule,
        AsyncPipe,
        TranslatePipe
    ],
    exports: [
        CsvSelectorComponent,
        CsvHeaderMappingComponent
    ]
})
export class CsvModule {
}
