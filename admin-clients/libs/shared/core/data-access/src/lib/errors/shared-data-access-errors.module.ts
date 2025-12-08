import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { ErrorsService } from './errors.service';

@NgModule({
    imports: [
        CommonModule,
        TranslatePipe
    ],
    providers: [
        ErrorsService
    ]
})
export class SharedDataAccessErrorsModule { }
