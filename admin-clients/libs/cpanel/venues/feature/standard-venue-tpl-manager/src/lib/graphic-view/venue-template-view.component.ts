import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    SeatStatus, StdVenueTplService, VENUE_MAP_SERVICE, VenueMapService, VenueTemplateView
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate, VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CommonModule } from '@angular/common';
import {
    AfterViewInit, booleanAttribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Inject, input, Input, OnDestroy, OnInit, Optional, Output,
    Renderer2, Signal, ViewChild
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject } from 'rxjs';
import { filter, map, take, takeUntil, delay } from 'rxjs/operators';
import { SvgElementType } from '../models/svg/svg-element-type.enum';
import { SVGEvent } from '../models/svg/svg-event.model';
import { VenueTemplateActionType, VenueTemplateSeatClickAction } from '../models/venue-template-action.model';
import { VenueTemplateEditorState } from '../models/venue-template-editor-state.enum';
import { StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateSelectionService } from '../services/standard-venue-template-selection.service';
import { VenueTemplateViewState } from './state/venue-template-view.state';
import { VenueTemplateSVGComponent } from './svg/venue-template-svg.component';
import { VenueTemplateViewTooltipDirective } from './tooltip/venue-template-view-tooltip.directive';
import { VenueTemplateViewService } from './venue-template-view.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        VenueTemplateSVGComponent,
        VenueTemplateViewTooltipDirective
    ],
    selector: 'app-venue-template-view',
    templateUrl: './venue-template-view.component.html',
    styleUrls: ['./venue-template-view.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [VenueTemplateViewService, VenueTemplateViewState]
})
export class VenueTemplateViewComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('venueTemplateSVGComponent', { read: VenueTemplateSVGComponent })
    private _venueTemplateSVGComponent: VenueTemplateSVGComponent;

    private _onDestroy: Subject<void> = new Subject<void>();
    private _mutationObserver: MutationObserver;
    private _venueTemplate: VenueTemplate;

    readonly $permanentSelection = input(false, { alias: 'permanentSelection', transform: booleanAttribute });

    @Output() $hasVisibleSeats: Signal<boolean>;
    venueTemplateStatus = VenueTemplateStatus;
    loading$: Observable<boolean>;
    loaded = false;
    svg$: Observable<string>;
    viewsPath: VenueTemplateView[];
    //this properties are for tricky purposes in the templates
    currentView: VenueTemplateView;
    minViewNameLength = 0;

    get venueTemplate(): VenueTemplate {
        return this._venueTemplate;
    }

    @Input()
    set venueTemplate(value: VenueTemplate) {
        const mustLoad = value?.graphic
            && (!this._venueTemplate || value.id !== this._venueTemplate.id || value.status !== this._venueTemplate.status);
        if (mustLoad || !value) {
            // reset steps
            this._venueTemplate = null;
            this.loaded = false;
            this.viewsPath = [];
        }
        // loading view start
        if (mustLoad) {
            this._venueTemplate = value;
            if (value.status === VenueTemplateStatus.active) {
                if (this.canLoad()) {
                    this.loadRootVenueContainer();
                } else {
                    this.waitAndInit();
                }
            }
        }
    }

    constructor(
        private _elementRef: ElementRef<HTMLElement>,
        private _renderer: Renderer2,
        private _changeDetectorRef: ChangeDetectorRef,
        private _msgDialogSrv: MessageDialogService,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _stdVenueTplSrv: StdVenueTplService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService,
        private _standardVenueTemplateBaseSrv: StandardVenueTemplateBaseService,
        private _standardVenueTemplateSelectionSrv: StandardVenueTemplateSelectionService,
        private _venueTemplateViewSrv: VenueTemplateViewService
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
        this.$hasVisibleSeats = toSignal(_venueTemplateViewSrv.hasVisibleSeats$());
    }

    ngOnInit(): void {
        this.loading$ = booleanOrMerge([
            this._venueTemplatesSrv.venueTpl.inProgress$(),
            this._venueTemplatesSrv.isVenueTemplateSaving$(),
            this._stdVenueTplSrv.isVenueTemplateViewLoading$(),
            this._stdVenueTplSrv.isVenueTemplateSVGLoading$(),
            this._venueMapSrv.isVenueMapLoading$(),
            this._venueMapSrv.isVenueMapSaving$()
        ]);
        this._stdVenueTplSrv.getVenueTemplateView$()
            .pipe(
                filter(container => container !== null),
                takeUntil(this._onDestroy)
            )
            .subscribe(container => {
                const lastView = this.viewsPath.length && this.viewsPath[this.viewsPath.length - 1];
                if (lastView?.id === container.id) {
                    if (lastView.url !== container.url) {
                        this.viewsPath[this.viewsPath.length - 1] = container;
                        this.loadCurrentVenueSVG();
                    }
                } else {
                    this.viewsPath.push(container);
                    this.loadCurrentVenueSVG();
                }
            });
        this.svg$ = this._stdVenueTplSrv.getVenueTemplateSVG$();
        this._venueTemplateViewSrv.getViewDataUpdated$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => {
                this.loaded = true;
                // markForCheck() fails, don't know why
                this._changeDetectorRef.detectChanges();
            });

        // Refit graphic venue template when relocating
        this._standardVenueTemplateBaseSrv.getCurrentState$()
            .pipe(
                map(state => state === VenueTemplateEditorState.relocation),
                delay(500),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => this.fitGraphicView());
    }

    ngAfterViewInit(): void {
        this._venueTemplateSVGComponent.interactiveElements$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(interactiveElements => this._venueTemplateViewSrv.setSVGTemplateElements(interactiveElements, this._renderer));
    }

    ngOnDestroy(): void {
        this.stopWaitingToInit();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    reloadSvg(): void {
        const currentView = this.viewsPath[this.viewsPath.length - 1];
        if (currentView) {
            this._stdVenueTplSrv.loadVenueTemplateView(this._venueTemplate.id, currentView.id);
        }
    }

    // fit

    fitGraphicView(): void {
        if (this._venueTemplate.graphic) {
            this._venueTemplateSVGComponent.fit();
        }
    }

    fitGraphicViewIfRequired(): void {
        if (this._venueTemplate.graphic) {
            this._venueTemplateSVGComponent.reFit();
        }
    }

    navigateToView(targetView: VenueTemplateView): void {
        if (this.viewsPath.includes(targetView)
            && this.viewsPath[this.viewsPath.length - 1].id !== targetView.id) {
            while (this.viewsPath[this.viewsPath.length - 1].id !== targetView.id) {
                this.viewsPath.pop();
            }
            this.loadCurrentVenueSVG();
        }
    }

    navigateToPrevView(): void {
        if (this.viewsPath.length > 1) {
            this.navigateToView(this.viewsPath[this.viewsPath.length - 2]);
        }
    }

    // interactions

    elementDoubleClick(event: SVGEvent): void {
        const element: SVGElement = this.getTemplateElementOf(event.element);
        if (this._venueTemplateViewSrv.getTemplateElementType(element) === SvgElementType.seat) {
            this._standardVenueTemplateBaseSrv.getVenueItems$().pipe(
                take(1),
                map(venueItems => venueItems.seats.get(+this._venueTemplateViewSrv.getTemplateElementId(element))),
                filter(seat => seat?.status === SeatStatus.sold
                    || seat?.status === SeatStatus.emitted
                    || seat?.status === SeatStatus.gift
                    || seat?.status === SeatStatus.booked
                )
            ).subscribe(seat => {
                this._standardVenueTemplateBaseSrv.emitAction({
                    type: VenueTemplateActionType.viewTicketDetail,
                    data: seat
                } as VenueTemplateSeatClickAction);
            });
        }
    }

    elementClick(event: SVGEvent): void {
        const venueElement: SVGElement = this.getTemplateElementOf(event.element);
        if (venueElement) {
            const elementType = this._venueTemplateViewSrv.getTemplateElementType(venueElement);
            if (elementType === SvgElementType.seat || elementType === SvgElementType.nnz) {
                this.selectElements([event.element], event.ctrlKey, event.shiftKey);
            } else if (elementType === SvgElementType.link) {
                this.navigateToContainer(this._venueTemplateViewSrv.getTemplateElementId(venueElement));
            }
        } else if (!event.ctrlKey && !event.shiftKey && !this.$permanentSelection()) {
            this._standardVenueTemplateSelectionSrv.unselectAll();
        }
    }

    elementsSurrounded(event: SVGEvent): void {
        this.selectElements(event.elements, event.ctrlKey, event.shiftKey);
    }

    selectElements(elements: SVGElement[], ctrlKey: boolean, shiftKey: boolean): void {
        if (!ctrlKey && !shiftKey && !this.$permanentSelection()) {
            this._standardVenueTemplateSelectionSrv.unselectAll();
        }
        const invertSelection = elements.length === 1 && (this.$permanentSelection() || (ctrlKey && !shiftKey));
        const svgSeats: SVGElement[] = [];
        const svgNNZs: SVGElement[] = [];
        elements.forEach(element => {
            const elementType = this._venueTemplateViewSrv.getTemplateElementType(element);
            if (elementType === SvgElementType.seat) {
                svgSeats.push(element);
            } else if (elementType === SvgElementType.nnz) {
                svgNNZs.push(element);
            }
        });
        if (svgSeats.length > 0) {
            this._standardVenueTemplateSelectionSrv.selectSeats(
                svgSeats.map(s => Number(this._venueTemplateViewSrv.getTemplateElementId(s))), invertSelection
            );
        }
        if (svgNNZs.length > 0) {
            this._standardVenueTemplateSelectionSrv.selectNNZs(
                svgNNZs.map(s => Number(this._venueTemplateViewSrv.getTemplateElementId(s))), invertSelection
            );
        }
    }

    private canLoad(): boolean {
        return this._elementRef.nativeElement.style.display !== 'none';
    }

    private waitAndInit(): void {
        if (!this._mutationObserver) {
            this._mutationObserver = new MutationObserver(() => {
                if (this.venueTemplate) { // no template no wait
                    if (this.canLoad()) {
                        this.stopWaitingToInit();
                        this.loadRootVenueContainer();
                    }
                } else {
                    this.stopWaitingToInit();
                }
            });
            this._mutationObserver.observe(this._elementRef.nativeElement, { attributes: true });
        }
    }

    private stopWaitingToInit(): void {
        if (this._mutationObserver) {
            this._mutationObserver.disconnect();
            this._mutationObserver = null;
        }
    }

    private loadRootVenueContainer(): void {
        this.loaded = false;
        this._stdVenueTplSrv.loadVenueTemplateRootView(this._venueTemplate.id);
    }

    private loadVenueContainer(containerId: number): void {
        this.loaded = false;
        this._stdVenueTplSrv.loadVenueTemplateView(this._venueTemplate.id, containerId);
    }

    private loadCurrentVenueSVG(): void {
        this.loaded = false;
        this.calcMinViewLength();
        this.currentView = this.viewsPath[this.viewsPath.length - 1];
        this._stdVenueTplSrv.loadVenueTemplateSVG(this.currentView.url);
    }

    private navigateToContainer(ref: string): void {
        const currentContainer = this.viewsPath[this.viewsPath.length - 1];
        if (currentContainer.links) {
            const targetLink = currentContainer.links.find(link => link.ref_id === ref);
            if (targetLink) {
                this.loadVenueContainer(targetLink.view_id);
            }
        }
    }

    private getTemplateElementOf(element: SVGElement): SVGElement {
        if (element !== null) {
            if (this._venueTemplateViewSrv.getTemplateElementType(element) && this._venueTemplateViewSrv.getTemplateElementId(element)) {
                return element;
            } else if (element.parentElement instanceof SVGElement) {
                return this.getTemplateElementOf(element.parentElement);
            }
        }
        return null;
    }

    private calcMinViewLength(): void {
        this.minViewNameLength = this.viewsPath
            .map(view => view.name?.length)
            .filter(length => !!length)
            .reduce((prev, current) => prev < current ? prev : current, 0);
    }
}
