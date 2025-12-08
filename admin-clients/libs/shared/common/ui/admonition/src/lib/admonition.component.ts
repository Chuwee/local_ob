import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';

@Component({
    selector: 'ob-admonition',
    imports: [CommonModule, MatIconModule],
    template: `
        <section class="admonition">
            <mat-icon  class="admonition-icon material-icons-outlined" *ngIf="icon">{{icon}}</mat-icon>
            <div class="admonition-content">
                <div class="admonition-title" *ngIf="title" [innerHTML]="title"></div>
                <div class="admonition-body" *ngIf="body" [innerHTML]="body"></div>
            </div>
        </section>`,
    styleUrls: ['./admonition.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdmonitionComponent {

    @Input({ required: false }) title: string;
    @Input({ required: false }) body: string;
    @Input({ required: false }) icon: string;

}
