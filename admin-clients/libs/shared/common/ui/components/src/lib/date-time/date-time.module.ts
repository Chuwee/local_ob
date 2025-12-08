import { NgModule } from '@angular/core';
import { DateTimePickerComponent } from './date-time-picker/date-time-picker.component';
import { TimePickerComponent } from './time-picker/time-picker.component';

@NgModule({
    imports: [
        TimePickerComponent,
        DateTimePickerComponent
    ],
    exports: [
        DateTimePickerComponent,
        TimePickerComponent
    ]
})
export class DateTimeModule { }
