import { Session } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'session-card',
    imports: [CommonModule, IonicModule, TranslatePipe, DateTimePipe],
    templateUrl: './session-card.component.html',
    styleUrls: ['./session-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SessionCardComponent {
    @Input() readonly type?: 'session' | 'session-pack' = 'session';
    @Input() readonly session: Session;
    @Output() readonly tap = new EventEmitter<Session>();
    readonly dateTimeFormats = DateTimeFormats;

    onTap(): void {
        this.tap.emit(this.session);
    }

    get getFormattedDate(): string {
        return this.type === 'session' ? 'dd, DD/MM/YYYY - h:mm' : 'dd, DD/MM/YYYY';
    }
}
