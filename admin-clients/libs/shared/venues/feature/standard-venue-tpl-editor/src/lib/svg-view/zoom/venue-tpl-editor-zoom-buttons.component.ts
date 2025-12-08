import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { Box } from '@svgdotjs/svg.js';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { VenueTplEditorSvgCoordinates } from '../../models/venue-tpl-editor-svg-coordinates.model';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe
    ],
    selector: 'app-venue-tpl-editor-zoom-buttons',
    templateUrl: './venue-tpl-editor-zoom-buttons.component.html',
    styleUrls: ['./venue-tpl-editor-zoom-buttons.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorZoomButtonsComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _scrollMargin = 8;
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private _svgSize: Box;
    private _viewportSize: { width: number; height: number };
    private _svgInitCoords: { width: number; height: number };
    private _zoomScale = 1;
    private _svgScale = 1;
    private _coords: VenueTplEditorSvgCoordinates = { width: 0, height: 0, top: 0, left: 0, scale: 1 };

    @Input()
    updateViewportSize(value: { width: number; height: number }): void {
        this._viewportSize = { width: value.width - this._scrollMargin / 2, height: value.height - this._scrollMargin };
        this.setWorkAreaInitSize();
    }

    ngOnInit(): void {
        this._domSrv.getSvgSvgElementViewBox$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(viewBox => {
                this._svgSize = new Box(viewBox);
                this.setWorkAreaInitSize();
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next();
        this._onDestroy.complete();
    }

    zoomIn(): void {
        if (this._zoomScale < 4) {
            this._zoomScale *= 1.25;
            this.updateCoords();
        }
    }

    zoomOut(): void {
        if (this._zoomScale > .65) {
            this._zoomScale /= 1.25;
            this.updateCoords();
        }
    }

    fit(): void {
        this._zoomScale = 1;
        this.updateCoords();
    }

    private setWorkAreaInitSize(): void {
        if (this._svgSize && this._viewportSize) {
            this._zoomScale = 1;
            const svgScale = this._svgSize.width / this._svgSize.height < this._viewportSize.width / this._viewportSize.height ?
                this._viewportSize.height / this._svgSize.height : this._viewportSize.width / this._svgSize.width;
            this._svgScale = Math.floor(svgScale * 100) / 100;
            this._svgInitCoords = {
                width: Math.min(this._viewportSize.width, this._svgSize.width * this._svgScale),
                height: Math.min(this._viewportSize.height, this._svgSize.height * this._svgScale)
            };
            this.updateCoords();
        }
    }

    private updateCoords(): void {
        this._coords.width = this._svgInitCoords.width * this._zoomScale;
        this._coords.height = this._svgInitCoords.height * this._zoomScale;
        this._coords.left = this._coords.width < this._viewportSize.width ? (this._viewportSize.width - this._coords.width) / 2 : 0;
        this._coords.scale = this._svgScale * this._zoomScale;
        this._domSrv.setWorkAreaCoordinates(this._coords);
    }
}
