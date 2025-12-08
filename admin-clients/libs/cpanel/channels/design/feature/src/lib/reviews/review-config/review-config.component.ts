import { ChannelsService, ReviewCriteria, ReviewTimeUnit } from '@admin-clients/cpanel/channels/data-access';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatFormField, MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatOption, MatSelect } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { ReviewConfigCriteriaControlComponent } from './criteria-control/review-config-criteria-control.component';
import { ReviewConfigElementsComponent } from './elements/review-config-elements.component';

type ReviewConfigForm = FormGroup<{
    criteria: FormControl<ReviewCriteria>;
    time: FormGroup<{
        unit: FormControl<ReviewTimeUnit>;
        value: FormControl<number>;
    }>;
}>;

@Component({
    selector: 'ob-channel-review-config',
    templateUrl: './review-config.component.html',
    styleUrl: './review-config.component.scss',
    imports: [
        MatInput, MatSelect, MatOption, TranslatePipe, MatFormField, ReactiveFormsModule,
        MatProgressSpinner, ReviewConfigElementsComponent, ReviewConfigCriteriaControlComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ChannelReviewConfigComponent {
    readonly #channelsSrv = inject(ChannelsService);

    readonly $channelId = input.required<number>({ alias: 'channelId' });
    readonly $configForm = input.required<ReviewConfigForm>({ alias: 'configForm' });
    readonly $enabled = input<boolean>(true, { alias: 'enabled' });

    readonly $loading = toSignal(this.#channelsSrv.reviewConfig.loading$());
}
