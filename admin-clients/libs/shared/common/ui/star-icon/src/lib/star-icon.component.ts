import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'app-star-icon',
    template: `
        <mat-icon class="default-icon star"
            [class.default-icon-unchecked]="!$checked()">
            {{$checked() ? 'star' : 'star_outline'}}
        </mat-icon>
    `,
    styleUrls: ['./star-icon.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [MatIconModule]
})
export class StarIconComponent {
    readonly $checked = input.required<boolean>({ alias: 'checked' });
}
