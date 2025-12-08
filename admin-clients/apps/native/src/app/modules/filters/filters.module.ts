import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';
import { BackButtonComponent } from '../../core/components/back-button/back-button.component';
import { CheckboxListTypeaheadComponent } from '../../core/components/checkbox-list-typeahead/checkbox-list-typeahead.component';
import { ModalComponent } from '../../core/components/modal/modal.component';
import { PickerComponent } from '../../core/components/picker/picker.component';
import { TimePickerComponent } from '../../core/components/time-picker/time-picker.component';
import { FiltersRoutingModule } from './filters-routing-module';
import { FiltersComponent } from './filters.component';

@NgModule({
    declarations: [FiltersComponent],
    imports: [
        CommonModule,
        IonicModule,
        FiltersRoutingModule,
        BackButtonComponent,
        TranslatePipe,
        ModalComponent,
        PickerComponent,
        CheckboxListTypeaheadComponent,
        TimePickerComponent
    ]
})
export class FiltersModule {
}
