import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';

@Component({
    imports: [
        CommonModule,
        FlexLayoutModule,
        MaterialModule
    ],
    selector: 'app-header-summary',
    templateUrl: './header-summary.component.html',
    styleUrls: ['./header-summary.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('show-hide', [
            state('hide', style({ height: 0 })),
            state('show', style({ height: '*' })),
            transition('hide <=> show', [animate('300ms ease-in-out')])
        ])
    ]
})
export class HeaderSummaryComponent {
    @Input()
    collapsed = false;

    @Input()
    collapsedLabel = '';

    constructor() { }

    changeState(): void {
        this.collapsed = !this.collapsed;
    }
}
