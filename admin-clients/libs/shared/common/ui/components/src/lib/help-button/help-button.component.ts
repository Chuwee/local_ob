import { booleanAttribute, ChangeDetectionStrategy, Component, HostListener, input, viewChild } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
    selector: 'app-help-button',
    template: `
        <div [matTooltip]="$message()">
            <button mat-icon-button type="button" class="ob-button help-button" [class.xsmall]="$isSmall()">
                <mat-icon class="ob-help-icon">help_outline</mat-icon>
            </button>
        </div>
    `,
    host: {
        style: 'cursor: help;'
    },
    imports: [MatIcon, MatIconButton, MatTooltip],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class HelpButtonComponent {
    $message = input.required<string>({ alias: 'message' });
    $isSmall = input(false, {
        alias: 'small',
        transform: booleanAttribute
    });

    $tooltip = viewChild(MatTooltip);

    @HostListener('click') onClick(): void {
        this.$tooltip().show(0);
    }
}
