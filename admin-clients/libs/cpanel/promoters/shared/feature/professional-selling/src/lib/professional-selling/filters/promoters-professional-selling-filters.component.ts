import { B2bService, B2bApi, B2bState } from '@admin-clients/cpanel/b2b/data-access';
import { SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { ObFormFieldLabelDirective } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-professional-selling-filters',
    templateUrl: './promoters-professional-selling-filters.component.html',
    styleUrls: ['./promoters-professional-selling-filters.component.scss'],
    providers: [B2bService, B2bApi, B2bState],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, SelectSearchComponent, TranslatePipe, AsyncPipe,
        MatFormFieldModule, MatSelectModule, ObFormFieldLabelDirective
    ]
})
export class ProfessionalSellingFiltersComponent {

    @Input() quotas$: Observable<IdName[]>;
    @Input() clients$: Observable<IdName[]>;
    @Input() form: FormGroup;

    reset(name: string): void {
        this.form.get(name).reset();
    }
}
