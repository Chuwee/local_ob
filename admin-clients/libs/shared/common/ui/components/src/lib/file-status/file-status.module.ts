import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { FileStatusComponent } from './file-status.component';

@NgModule({
    declarations: [
        FileStatusComponent
    ],
    imports: [
        CommonModule,
        FlexLayoutModule,
        MatButtonModule,
        MatIconModule,
        MatProgressBarModule,
        EllipsifyDirective
    ],
    exports: [
        FileStatusComponent
    ]
})
export class FileStatusModule {
}
