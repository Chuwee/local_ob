import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelEvent, ChannelSession, PutReviewConfigElement, ReviewCriteria, ReviewScope
} from '@admin-clients/cpanel/channels/data-access';
import { WizardBarComponent } from '@admin-clients/cpanel/shared/ui/components';
import { DialogSize, ObDialog } from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal, viewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { ReviewConfigCriteriaControlComponent } from '../../criteria-control/review-config-criteria-control.component';
import { ReviewConfigEventsComponent } from '../../events/review-config-events.component';
import { ReviewConfigSessionsComponent } from '../../sessions/review-config-sessions.component';
import { ReviewConfigElementsTemplateComponent } from '../template/review-config-elements-template.component';

@Component({
    templateUrl: './review-config-element-create.component.html',
    imports: [
        MatDialogModule, MatIcon, MatButtonModule, MatRadioButton, MatRadioGroup, ReactiveFormsModule,
        ReviewConfigCriteriaControlComponent, TranslatePipe, MatTooltip, WizardBarComponent,
        ReviewConfigEventsComponent, ReviewConfigSessionsComponent, ReviewConfigElementsTemplateComponent
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigElementCreateComponent
    extends ObDialog<ReviewConfigElementCreateComponent, { channelId: number }, { scope: ReviewScope; data: PutReviewConfigElement }> {
    readonly #fb = inject(NonNullableFormBuilder);

    readonly $wizardBar = viewChild(WizardBarComponent);

    readonly step1 = 'CHANNELS.REVIEWS.DIALOG_CONFIG.STEP_1';
    readonly step2 = 'CHANNELS.REVIEWS.DIALOG_CONFIG.STEP_2';
    readonly $currentStep = signal<string>(this.step1);
    readonly $selectedSessions = signal<ChannelSession[]>([]);
    readonly $selectedEvents = signal<ChannelEvent[]>([]);
    readonly createForm = this.#fb.group({
        scope: this.#fb.control<ReviewScope>('EVENT'),
        criteria: this.#fb.control<ReviewCriteria>('ALWAYS')
    });

    readonly $scope = toSignal(this.createForm.get('scope').valueChanges, { initialValue: 'EVENT' });
    readonly $showSessionsTable = computed(() => this.$scope() === 'SESSION' && this.$selectedEvents().length === 1);
    readonly $sessionsElements = computed(() => this.$selectedSessions().map(el => ({ name: el.name, startDate: el.startDate })));
    readonly $eventsElements = computed(() => this.$selectedEvents().map(el => ({ name: el.event.name })));
    readonly $disabledNextStep = computed(() => this.$scope() === 'SESSION' ?
        this.$selectedSessions().length < 1 : this.$selectedEvents().length === 0);

    constructor() {
        super(DialogSize.LARGE);
        effect(() => {
            if (this.$selectedEvents()?.length === 0 && this.$scope() === 'SESSION') {
                this.createForm.reset();
            }
        });
    }

    save(): void {
        this.createForm.markAllAsTouched();
        if (this.createForm.valid) {
            const { scope, criteria } = this.createForm.getRawValue();
            const list = scope === 'EVENT'
                ? this.$selectedEvents().map(event => event.event.id)
                : this.$selectedSessions().map(session => session.id);
            const data = {
                send_criteria: criteria,
                scope_ids: list
            };

            this.dialogRef.close({ scope, data });
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
        }
    }

    next(): void {
        if (this.$currentStep() === this.step1) {
            this.$currentStep.set(this.step2);
            this.$wizardBar().nextStep();
        } else {
            this.save();
        }
    }

    previous(): void {
        this.$currentStep.set(this.step1);
        this.$wizardBar().previousStep();
    }

    setSessions(data: ChannelSession[]): void {
        this.$selectedSessions.set(data);
    }

    setEvents(data: ChannelEvent[]): void {
        this.$selectedEvents.set(data);
    }

    get shouldDisableEvent(): (value: IdName) => boolean {
        return (value: IdName) => this.createForm.get('scope').value === 'SESSION' && !!this.$selectedEvents().length
            && this.$selectedEvents().filter(item => item.id === value.id).length !== 1;
    }

}
