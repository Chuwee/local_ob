import { Clipboard, ClipboardModule } from '@angular/cdk/clipboard';
import { ChangeDetectionStrategy, Component, Input, inject } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { EphemeralMessageService } from '../ephemeral-message/ephemeral-message.service';
import { MessageType } from '../models/message-type.model';

@Component({
    imports: [MatIcon, MatIconButton, MatTooltip, TranslatePipe, ClipboardModule],
    selector: 'app-copy-text',
    templateUrl: './copy-text.component.html',
    styleUrls: ['./copy-text.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class CopyTextComponent {
    private readonly _translateSrv = inject(TranslateService);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _clipboard = inject(Clipboard);
    @Input() copyText: string | number;
    @Input() copyOk: string;
    @Input() copyKo: string;
    @Input() copyTooltip: string;

    copyToClipboard(event: MouseEvent): void {
        event.stopPropagation();
        const pending = this._clipboard.beginCopy(String(this.copyText));
        let remainingAttempts = 3;
        const attempt = (): void => {
            const result = pending.copy();
            if (!result && --remainingAttempts) {
                setTimeout(attempt);
            } else {
                this.showNotification(!!result);
                pending.destroy();
            }
        };
        attempt();
    }

    private showNotification(success: boolean): void {
        this._ephemeralSrv.show({
            type: success ? MessageType.success : MessageType.warn,
            msgKey: success ?
                this.copyOk || this._translateSrv.instant('ACTIONS.CLIPBOARD.OK')
                : this.copyKo || this._translateSrv.instant('ACTIONS.CLIPBOARD.KO')
        });
    }
}
