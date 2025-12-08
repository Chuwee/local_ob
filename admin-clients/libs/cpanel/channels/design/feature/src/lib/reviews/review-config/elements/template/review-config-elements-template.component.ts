import { ReviewScope } from '@admin-clients/cpanel/channels/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { Component, ChangeDetectionStrategy, input, computed } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { TranslatePipe } from '@ngx-translate/core';

export type ElementTemplate = {
    name: string;
    startDate?: string;
    parentName?: string;
};

@Component({
    selector: 'ob-review-config-elements-template',
    template: `
            <div class="grid gap-3">
                <span class="mat-subtitle-2">
                    {{ $title() | translate }}
                </span>
                <div class="grid gap-2">
                    @for(item of $elements(); track $index){
                        <div class="summary-element">
                            @if($scope() === 'SESSION'){
                                <span class="session">
                                    {{item.name}} - {{item.startDate | dateTime : dateTimeFormats.shortDateTimeWithWeek}} -
                                </span>
                            }
                            <span class="event">{{item.parentName ?? item.name}}</span>
                        </div>
                    }
                </div>
                <mat-divider />
            </div>
    `,
    imports: [TranslatePipe, DateTimePipe, MatDivider],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewConfigElementsTemplateComponent {
    readonly $scope = input.required<ReviewScope>({ alias: 'scope' });
    readonly $events = input.required<ElementTemplate[]>({ alias: 'events' });
    readonly $sessions = input.required<ElementTemplate[]>({ alias: 'sessions' });

    readonly dateTimeFormats = DateTimeFormats;
    readonly $title = computed(() =>
        this.$scope() === 'EVENT' ? 'CHANNELS.REVIEWS.DIALOG_CONFIG.SCOPE.EVENT_TEXT' : 'CHANNELS.REVIEW_CONFIG.DIALOG.SCOPE.SESSION_TEXT'
    );

    readonly $elements = computed(() => {
        if (this.$scope() === 'EVENT') {
            return this.$events();
        } else {
            const eventName = this.$events()[0]?.name;
            return this.$sessions().map(element => {
                element.parentName = eventName;
                return element;
            });
        }
    });
}
