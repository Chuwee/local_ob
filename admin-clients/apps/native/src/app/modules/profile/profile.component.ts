import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
    selector: 'clients-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ProfileComponent { }
