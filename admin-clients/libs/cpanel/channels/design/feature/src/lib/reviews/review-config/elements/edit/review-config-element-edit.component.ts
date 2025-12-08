import { ReviewConfigElement, ReviewCriteria } from '@admin-clients/cpanel/channels/data-access';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';
import { ReviewConfigCriteriaControlComponent } from '../../criteria-control/review-config-criteria-control.component';
import { ElementTemplate, ReviewConfigElementsTemplateComponent } from '../template/review-config-elements-template.component';

@Component({
    templateUrl: './review-config-element-edit.component.html',
    imports: [
        TranslatePipe, MatIcon, MatDialogModule, MatIcon, MatButtonModule,
        ReviewConfigCriteriaControlComponent, ReactiveFormsModule, ReviewConfigElementsTemplateComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigElementEditComponent
    extends ObDialog<ReviewConfigElementEditComponent, { config: ReviewConfigElement }, ReviewConfigElement> {
    readonly #fb = inject(NonNullableFormBuilder);

    readonly #details = this.data.config.details;
    readonly criteriaCtrl = this.#fb.control<ReviewCriteria>(this.data.config.send_criteria);
    readonly scope = this.data.config.scope;
    readonly event: ElementTemplate = { name: this.#details?.event?.name };
    readonly session: ElementTemplate = { name: this.#details?.session?.name, startDate: this.#details?.session?.start_date };

    constructor() {
        super(DialogSize.LARGE);
    }

    close(): void {
        this.dialogRef.close();
    }

    save(): void {
        const criteria = this.criteriaCtrl.value;
        const updatedConfig = { ...this.data.config, send_criteria: criteria };

        if (criteria !== this.data.config.send_criteria) {
            return this.dialogRef.close(updatedConfig);
        }
        return this.dialogRef.close();
    }
}
