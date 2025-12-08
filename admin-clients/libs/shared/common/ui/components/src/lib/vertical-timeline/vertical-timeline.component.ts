import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { trigger, transition, style, animate } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ContentChild, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { TranslatePipe } from '@ngx-translate/core';

export interface TimelineElement {
    title: string;
    date?: string;
    status: TimelineElementStatus;
    description?: string;
    elements?: TimelineElement[];
    grouped?: boolean;
}

export enum TimelineElementStatus {
    ok = 'OK',
    error = 'ERROR',
    disabled = 'DISABLED'
}

@Component({
    imports: [CommonModule, TranslatePipe, DateTimePipe, MatIcon],
    selector: 'app-vertical-timeline',
    templateUrl: './vertical-timeline.component.html',
    styleUrls: ['./vertical-timeline.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('inAnimation', [
            transition(':enter', [
                style({ height: 0, opacity: 0 }),
                animate('300ms ease-out', style({ height: '*', opacity: 1 }))
            ])
        ])
    ]
})
export class VerticalTimelineComponent {
    @Input() timelineElements: TimelineElement[];
    @Output() readonly showMore = new EventEmitter<{ elem: TimelineElement; pos: number }>();
    @ContentChild('timelineElement') timelineElementTemplateRef?: TemplateRef<unknown>;

    readonly timelineElementStatus = TimelineElementStatus;
    readonly dateTimeFormats = DateTimeFormats;

    toggleTransitions(element: TimelineElement, position: number): void {
        this.showMore.emit({ elem: element, pos: position });
    }
}
