import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';

@Component({
    selector: 'skeleton-card',
    imports: [CommonModule, IonicModule],
    templateUrl: './skeleton-card.component.html',
    styleUrls: ['./skeleton-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SkeletonCardComponent { }
