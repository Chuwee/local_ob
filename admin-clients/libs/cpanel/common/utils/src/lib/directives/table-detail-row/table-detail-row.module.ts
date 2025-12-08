import { NgModule } from '@angular/core';
import { TableDetailRowDirective } from './table-detail-row.directive';

@NgModule({
    declarations: [TableDetailRowDirective],
    exports: [TableDetailRowDirective]
})
export class TableDetailRowModule { }
