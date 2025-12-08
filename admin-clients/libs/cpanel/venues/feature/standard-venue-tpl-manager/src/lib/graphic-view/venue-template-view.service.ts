import { NotNumberedZone, Seat, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { Injectable, OnDestroy, Renderer2 } from '@angular/core';
import { combineLatest, Observable, of, Subject, withLatestFrom } from 'rxjs';
import { map, take, takeUntil } from 'rxjs/operators';
import { VenueTemplateLabelGroup } from '../models/label-group/venue-template-label-group-list.model';
import { SvgElementType } from '../models/svg/svg-element-type.enum';
import { SvgElementWrapper } from '../models/svg/svg-element.wrapper.model';
import { getStatusIcon } from '../models/venue-template-icons';
import { VenueTemplateSelectionChange } from '../models/venue-template-selection-change.model';
import { StandardVenueTemplateState } from '../state/standard-venue-template.state';
import { getSeatIdentifierFunction } from '../utils/venue-item-managing-functions.utils';
import { SVGDefs } from './model/svf-defs.enum';
import { VenueTemplateViewState } from './state/venue-template-view.state';

@Injectable()
export class VenueTemplateViewService implements OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private static readonly WHITE_COLOR = '#FFFFFF';
    private static readonly BLACK_COLOR = '#000000';
    private static readonly FILTERED_OPACITY = '.25';
    private static readonly UNFILTERED_OPACITY = '1';
    private static readonly COLOR_BRIGHTNESS_FACTORS = [.33, .50, .17]; // rgb components brightness (rounded)
    private static readonly ICON_WIDTH = 100; // icons has to be squares of 100x100

    constructor(
        private _standardVenueTemplateState: StandardVenueTemplateState,
        private _venueTemplateViewSt: VenueTemplateViewState
    ) {
        this.setAppearanceUpdater();
        this.setSelectionUpdater();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    hasVisibleSeats$(): Observable<boolean> {
        return this._venueTemplateViewSt.getSvgSeats$().pipe(map(svgSeats => svgSeats !== null && svgSeats.size > 0));
    }

    setSVGTemplateElements(interactiveElements: SVGElement[], renderer: Renderer2): void {
        if (interactiveElements !== null) {
            const seatElementMap = new Map<string, SvgElementWrapper>();
            const nnzElementMap = new Map<string, SvgElementWrapper>();
            if (interactiveElements.length) {
                const rootSvgScale = this.getRootSvgOffsetAndScale(interactiveElements[0]);
                for (const element of interactiveElements) {
                    const elementId = this.getTemplateElementId(element);
                    if (elementId !== null) {
                        if (isNaN(Number(elementId))) { // links have alphanumeric id
                            element.setAttribute(SVGDefs.typeProperty, SvgElementType.link);
                        } else {
                            const elementWrp = { element } as SvgElementWrapper;
                            elementWrp.modifierGroup = renderer.createElement('g', 'svg');
                            elementWrp.modifierGroup.style.pointerEvents = 'none';
                            if (element.tagName === SVGDefs.seatElement) { // seats are always the same circle tag
                                element.setAttribute(SVGDefs.typeProperty, SvgElementType.seat);
                                element.style.stroke = VenueTemplateViewService.BLACK_COLOR;
                                elementWrp.iconGroup = VenueTemplateViewService.createSeatIconGroup(elementWrp, renderer);
                                elementWrp.modifierGroup.appendChild(elementWrp.iconGroup);
                                elementWrp.selectionElement = VenueTemplateViewService.createSeatSelectionElement(elementWrp, renderer);
                                seatElementMap.set(elementId, elementWrp);
                            } else { // the others are "not numbered zone"
                                element.setAttribute(SVGDefs.typeProperty, SvgElementType.nnz);
                                elementWrp.selectionElement = VenueTemplateViewService.createNNZSelectionElement(
                                    elementWrp, rootSvgScale.topOffset, rootSvgScale.leftOffset, rootSvgScale.scale, renderer
                                );
                                nnzElementMap.set(elementId, elementWrp);
                            }
                            elementWrp.modifierGroup.appendChild(elementWrp.selectionElement);
                            renderer.insertBefore(element.parentElement, elementWrp.modifierGroup, element);
                            renderer.insertBefore(element.parentElement, element, elementWrp.modifierGroup);
                        }
                    } else {
                        element.classList.remove(SVGDefs.interactiveClass);
                    }
                }
            }
            this._venueTemplateViewSt.setSvgSeats(seatElementMap);
            this._venueTemplateViewSt.setSvgNnz(nnzElementMap);
        } else {
            this._venueTemplateViewSt.setSvgSeats(null);
            this._venueTemplateViewSt.setSvgNnz(null);
        }
    }

    getTemplateElementId(element: Element): string {
        return element?.getAttribute(SVGDefs.idProperty);
    }

    getTemplateElementType(element: SVGElement): SvgElementType {
        return element?.getAttribute(SVGDefs.typeProperty) as SvgElementType;
    }

    getViewDataUpdated$(): Observable<void> {
        return this._venueTemplateViewSt.getViewUpdated$();
    }

    getTemplateElementItem(element: SVGElement): Observable<Seat | NotNumberedZone> {
        const elementType = this.getTemplateElementType(element);
        if (elementType === SvgElementType.seat || elementType === SvgElementType.nnz) {
            const elementId = this.getTemplateElementId(element);
            return this._standardVenueTemplateState.getVenueItems$()
                .pipe(
                    take(1),
                    map(value => {
                        if (elementType === SvgElementType.seat) {
                            return value.seats.get(Number(elementId));
                        } else {
                            return value.nnzs.get(Number(elementId));
                        }
                    })
                );
        } else {
            return of(null);
        }
    }

    // PRIVATE

    private setAppearanceUpdater(): void {
        combineLatest([
            this._standardVenueTemplateState.getSelectedLabelGroup$(),
            this._venueTemplateViewSt.getSvgSeats$(),
            this._venueTemplateViewSt.getSvgNnz$(),
            this._standardVenueTemplateState.getVenueItems$(),
            this._standardVenueTemplateState.getFilteredVenueItems$(),
            this._standardVenueTemplateState.getModifiedItems$() // only works as trigger
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([
                selectedLabelGroup,
                svgSeats,
                svgNNZs,
                venueItems,
                filteredVenueItems
            ]) => {
                let viewUpdated = false;
                if (venueItems.seats && svgSeats && selectedLabelGroup) {
                    this.updateSeatsColor(selectedLabelGroup, venueItems.seats, svgSeats);
                    this.updateSeatIcons(svgSeats, venueItems.seats);
                    this.updateSeatFilterStates(svgSeats, filteredVenueItems.seats);
                    viewUpdated = true;
                }
                if (svgNNZs) {
                    this.updateNNZFilterStates(svgNNZs, filteredVenueItems.nnzs);
                    viewUpdated = true;
                }
                if (viewUpdated) {
                    this._venueTemplateViewSt.setViewUpdated();
                }
            });
    }

    private updateSeatsColor(
        selectedLabelGroup: VenueTemplateLabelGroup,
        seats: Map<number, Seat>,
        svgSeats: Map<string, SvgElementWrapper>
    ): void {
        if (selectedLabelGroup != null) {
            if (svgSeats) {
                const colorMap: Map<string, string> = new Map<string, string>();
                selectedLabelGroup.labels?.forEach(label => colorMap.set(String(label.id), label.color));
                const getValueFunction: (seat: Seat) => string = getSeatIdentifierFunction(selectedLabelGroup.id);
                const notFoundSeats = new Set<string>();
                svgSeats.forEach(seatElementGroup => {
                    const seat = seats.get(Number(this.getTemplateElementId(seatElementGroup.element)));
                    if (seat) {
                        seatElementGroup.element.style.fill = colorMap.get(getValueFunction(seat)) || VenueTemplateViewService.WHITE_COLOR;
                    } else {
                        notFoundSeats.add(this.getTemplateElementId(seatElementGroup.element));
                    }
                });
                if (notFoundSeats.size) {
                    console.error('seats not available to update aspect: ', { seatIds: Array.from(notFoundSeats).join() });
                }
            }
        }
    }

    // SEAT ICONS, ICONS CAN CONTAIN COLORED ELEMENTS (like sold icon)

    private updateSeatIcons(svgSeats: Map<string, SvgElementWrapper>, seats: Map<number, Seat>): void {
        if (svgSeats !== null) {
            svgSeats.forEach(seatElement => {
                if (seatElement) {
                    const seat = seats.get(Number(this.getTemplateElementId(seatElement.element)));
                    if (seat) {
                        const seatIcon = getStatusIcon(seat.status);
                        if (!seatIcon || seatIcon.skipOnGraphicView) {
                            seatElement.iconGroup.innerHTML = null;
                        } else {
                            seatElement.iconGroup.innerHTML = seatIcon.icon;
                            // color set
                            if (!seatIcon.complete) {
                                const colorBrightness = seatElement.element.style.fill
                                    .slice(4, seatElement.element.style.fill.length - 1)// converts rgb(XX,XX,XX) to XX,XX,XX
                                    .split(', ') // converts 'R, G, B' in ['R','G','B']
                                    .map(value => Number(value))
                                    .map((value, index) => value * VenueTemplateViewService.COLOR_BRIGHTNESS_FACTORS[index])
                                    .reduce(((result, currentValue) => result + currentValue), 0);
                                // In a scale of 0 to 255 of brightness, the seat colors over 63 will have black icons
                                seatElement.iconGroup.style.fill
                                    = colorBrightness > 63 ? VenueTemplateViewService.BLACK_COLOR : VenueTemplateViewService.WHITE_COLOR;
                            }
                        }
                    }
                }
            });
        }
    }

    private updateSeatFilterStates(svgSeats: Map<string, SvgElementWrapper>, filteredSeats: Set<number>): void {
        if (svgSeats !== null) {
            filteredSeats = filteredSeats || new Set<number>();
            svgSeats.forEach(seatElement => {
                if (filteredSeats.has(Number(this.getTemplateElementId(seatElement.element)))) {
                    seatElement.element.style.opacity = VenueTemplateViewService.FILTERED_OPACITY;
                    seatElement.iconGroup.style.opacity = VenueTemplateViewService.FILTERED_OPACITY;
                    seatElement.element.classList.add(SVGDefs.filteredClass);
                } else {
                    seatElement.element.style.opacity = VenueTemplateViewService.UNFILTERED_OPACITY;
                    seatElement.iconGroup.style.opacity = VenueTemplateViewService.UNFILTERED_OPACITY;
                    seatElement.element.classList.remove(SVGDefs.filteredClass);
                }
            });
        }
    }

    private updateNNZFilterStates(svgNNZs: Map<string, SvgElementWrapper>, filteredNNZs: Set<number>): void {
        if (svgNNZs !== null) {
            filteredNNZs = filteredNNZs || new Set<number>();
            svgNNZs.forEach(nnzElement => {
                if (filteredNNZs.has(Number(this.getTemplateElementId(nnzElement.element)))) {
                    nnzElement.element.style.opacity = VenueTemplateViewService.FILTERED_OPACITY;
                    nnzElement.element.classList.add(SVGDefs.filteredClass);
                } else {
                    nnzElement.element.style.opacity = VenueTemplateViewService.UNFILTERED_OPACITY;
                    nnzElement.element.classList.remove(SVGDefs.filteredClass);
                }
            });
        }
    }

    private setSelectionUpdater(): void {
        this._standardVenueTemplateState.getSelectionQueue$()
            .pipe(
                withLatestFrom(this._venueTemplateViewSt.getSvgSeats$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([selectionChange, svgSeats]) =>
                this.updateElementsSelection(selectionChange, svgSeats, VenueTemplateItemType.seat)
            );
        this._standardVenueTemplateState.getSelectionQueue$()
            .pipe(
                withLatestFrom(this._venueTemplateViewSt.getSvgNnz$()),
                takeUntil(this._onDestroy)
            )
            .subscribe(([selectionChange, svgSeats]) =>
                this.updateElementsSelection(selectionChange, svgSeats, VenueTemplateItemType.notNumberedZone)
            );
        combineLatest([
            this._venueTemplateViewSt.getSvgSeats$(),
            this._venueTemplateViewSt.getSvgNnz$()
        ])
            .pipe(
                withLatestFrom(
                    this._standardVenueTemplateState.getSelectedVenueItems$(),
                    this._standardVenueTemplateState.getVenueItems$()
                ),
                takeUntil(this._onDestroy)
            )
            .subscribe(([[svgSeats, svgNnz], selectedItemIds, venueItems]) => {
                this.globalUpdateElementsSelection(venueItems.seats, selectedItemIds.seats, svgSeats, VenueTemplateItemType.seat);
                this.globalUpdateElementsSelection(venueItems.nnzs, selectedItemIds.nnzs, svgNnz, VenueTemplateItemType.notNumberedZone);
            });
    }

    private globalUpdateElementsSelection(
        items: Map<number, Seat | NotNumberedZone>,
        selectedItemIds: Set<number>,
        svgElements: Map<string, SvgElementWrapper>,
        itemType: VenueTemplateItemType
    ): void {
        if (selectedItemIds?.size) {
            this.updateElementsSelection(
                {
                    select: true,
                    items: Array.from(selectedItemIds.keys()).map(id => items.get(id))
                },
                svgElements,
                itemType);
        }
    }

    private updateElementsSelection(
        selectionChange: VenueTemplateSelectionChange,
        svgElements: Map<string, SvgElementWrapper>,
        type: VenueTemplateItemType
    ): void {
        if (selectionChange && svgElements?.size) {
            const newElementVisibility = selectionChange.select ? 'visible' : 'hidden';
            if (selectionChange.items?.length) {
                selectionChange.items.forEach(item => {
                    if (item.itemType === type && svgElements.has(item.id.toString())) {
                        svgElements.get(item.id.toString()).selectionElement.style.visibility = newElementVisibility;
                    }
                });
            } else {
                svgElements.forEach(elementWrp => elementWrp.selectionElement.style.visibility = newElementVisibility);
            }
        }
    }

    // Retrieve root data

    private getRootSvgOffsetAndScale(element: SVGElement): { scale: number; leftOffset: number; topOffset: number } {
        const result = {
            scale: null,
            leftOffset: 0,
            topOffset: 0
        };
        const rootSvgElement = this.getRootElement(element);
        const rootSvgRect = rootSvgElement.getBoundingClientRect();
        result.topOffset = rootSvgRect.top;
        result.leftOffset = rootSvgRect.left;
        const viewBoxParts = rootSvgElement.getAttribute('viewBox').split(' ');
        const viewBoxWidth = Number(viewBoxParts[2]);
        const viewBoxHeight = Number(viewBoxParts[3]);
        const rootWidth = Number(rootSvgElement.style.width.replace('px', ''));
        const rootHeight = Number(rootSvgElement.style.height.replace('px', ''));
        if (viewBoxWidth / viewBoxHeight > rootWidth / rootHeight) {
            result.scale = viewBoxWidth / rootWidth;
            result.topOffset += (rootSvgRect.height - (rootSvgRect.width * viewBoxHeight / viewBoxWidth)) / 2;
        } else {
            result.scale = viewBoxHeight / rootHeight;
            result.leftOffset += (rootSvgRect.width - (rootSvgRect.height * viewBoxWidth / viewBoxHeight)) / 2;
        }
        return result;
    }

    private getRootElement(element: SVGElement): SVGElement {
        if (element) {
            if (element.parentElement instanceof SVGElement) {
                return this.getRootElement(element.parentElement);
            }
        }
        return element;
    }

    // CONTENT CREATION

    // the icons must have 100px of width and height, to calc the required scale it has to divide the circle diameter by 100,
    // to get the diameter it doubles the radius
    private static createSeatIconGroup(elementWrp: SvgElementWrapper, renderer: Renderer2): SVGElement {
        const circle = elementWrp.element as SVGCircleElement;
        const radius = circle.r.baseVal.value + .5;
        const scale = (radius * 2) / VenueTemplateViewService.ICON_WIDTH;
        const svgSeatIcon: SVGElement = renderer.createElement('g', 'svg');
        let transform = circle.getAttribute('transform');

        if (transform) {
            transform = transform + ',';
        } else {
            transform = '';
        }

        svgSeatIcon.setAttribute('transform',
            `${transform}` +
            `translate(${circle.cx.baseVal.value - radius}, ${circle.cy.baseVal.value - radius}),` +
            ` scale(${scale}, ${scale})`
        );
        return svgSeatIcon;
    }

    private static createSeatSelectionElement(elementWrp: SvgElementWrapper, renderer: Renderer2): SVGElement {
        const circle = elementWrp.element as SVGCircleElement;
        const radius = circle.r.baseVal.value + 1;
        const svgSelection: SVGElement = renderer.createElement('rect', 'svg');
        const transform = circle.getAttribute('transform');
        if (transform) {
            svgSelection.setAttribute('transform', circle.getAttribute('transform'));
        }
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'x', circle.cx.baseVal.value - radius);
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'y', circle.cy.baseVal.value - radius);
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'width', radius * 2);
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'height', radius * 2);
        svgSelection.classList.add(SVGDefs.selectionRectClass);
        svgSelection.style.visibility = 'hidden';
        return svgSelection;
    }

    private static createNNZSelectionElement(
        elementWrp: SvgElementWrapper,
        svgTopOffset: number,
        svgLeftOffset: number,
        svgScale: number,
        renderer: Renderer2
    ): SVGElement {
        const elementBoundingRect = elementWrp.element.getBoundingClientRect();
        const svgSelection: SVGElement = renderer.createElement('rect', 'svg');
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'x', ((elementBoundingRect.left - svgLeftOffset) * svgScale) - 1);
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'y', ((elementBoundingRect.top - svgTopOffset) * svgScale) - 1);
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'width', (elementBoundingRect.width * svgScale) + 2);
        VenueTemplateViewService.setSVGElementAttribute(svgSelection, 'height', (elementBoundingRect.height * svgScale) + 2);
        // it increases 1 px in each side to show the rectangle outside the selected elements
        svgSelection.classList.add(SVGDefs.selectionRectClass);
        svgSelection.style.visibility = 'hidden';
        return svgSelection;
    }

    private static setSVGElementAttribute(svgElement: SVGElement, property: string, value: number): void {
        if (!isNaN(value)) {
            svgElement.setAttribute(property, value + 'px');
        }
    }
}
