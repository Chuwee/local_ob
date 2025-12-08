import { FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { Sector } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule, UntypedFormGroup } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { FormControlNames } from '../form-names.enum';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        FormControlErrorsComponent,
        SelectSearchComponent
    ],
    selector: 'app-zone',
    templateUrl: './zone.component.html',
    styleUrls: ['./zone.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ZoneComponent {

    @Input() showWizardBar: boolean;
    @Input() sectors$: Observable<Sector[]>;
    @Input() showCapacityField: boolean;
    @Input() showSectorSelector: boolean;
    @Input() showZoneName: boolean;
    @Input() showCapacityIncreaseInfo;
    @Input() formControlNames: typeof FormControlNames;
    @Input() nnzFormGroup: UntypedFormGroup;

    constructor() { }
}
