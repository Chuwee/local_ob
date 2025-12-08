import { ObFile } from '@admin-clients/shared/data-access/models';
import { Overlay, OverlayConfig, OverlayRef } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { Injectable, Injector, OnDestroy } from '@angular/core';
import { PreviewerComponent } from './previewer.component';

@Injectable({
    providedIn: 'root'
})
export class Previewer implements OnDestroy {
    private _opened: PreviewerComponent;

    constructor(private _overlay: Overlay,
        private _injector: Injector) {
    }

    open(file: ObFile): PreviewerComponent {

        if (this._opened) {
            throw Error('Previewer exists already. Previewer must be unique.');
        }

        const overlayRef = this.createOverlay();
        const previewer = this.attachPreviewer(overlayRef);
        previewer.file = file;

        this._opened = previewer;
        this._opened.onClose(() => this._opened = null);

        return previewer;
    }

    ngOnDestroy(): void {
        this._opened?.close();
    }

    private createOverlay(): OverlayRef {
        const overlayConfig = new OverlayConfig({
            positionStrategy: this._overlay.position().global(),
            hasBackdrop: true,
            disposeOnNavigation: true,
            backdropClass: 'previewer-backdrop'
        });
        return this._overlay.create(overlayConfig);
    }

    private attachPreviewer(overlay: OverlayRef): PreviewerComponent {
        const injector = Injector.create({
            parent: this._injector,
            providers: [{ provide: OverlayRef, useValue: overlay }]
        });

        const containerPortal = new ComponentPortal(PreviewerComponent,
            null, injector);
        const containerRef = overlay.attach<PreviewerComponent>(containerPortal);

        return containerRef.instance;
    }
}
