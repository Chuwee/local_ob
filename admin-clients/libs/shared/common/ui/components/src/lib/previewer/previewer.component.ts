import { ObFile } from '@admin-clients/shared/data-access/models';
import { OverlayRef } from '@angular/cdk/overlay';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';

@Component({
    imports: [
        MatButtonModule,
        MatIconModule,
        MatToolbarModule
    ],
    selector: 'app-previewer',
    templateUrl: './previewer.component.html',
    styleUrls: ['./previewer.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PreviewerComponent {
    private _onCloseCallbacks: ((withEdition: boolean) => void)[] = [];

    file: ObFile;

    constructor(private _overlayRef: OverlayRef) { }

    resolvePath(): string {
        return this.file?.remote ? this.file.data : `data:${this.file.contentType};base64,${this.file.data}`;
    }

    close(withEdition = false): void {
        this._overlayRef.detach();
        this._overlayRef.dispose();
        this._onCloseCallbacks.forEach(callback => callback(withEdition));
    }

    onClose(callback: (withEdition: boolean) => void): void {
        this._onCloseCallbacks.push(callback);
    }
}
