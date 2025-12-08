import { inject, Injectable } from '@angular/core';
import { Circle, Container, Element, G, Image, Rect, SVG, Svg, Text } from '@svgdotjs/svg.js';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, shareReplay, take } from 'rxjs/operators';
import { SVGDefs } from './models/SVGDefs.enum';
import { VenueTplEditorSvgCoordinates } from './models/venue-tpl-editor-svg-coordinates.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';
import { roundDecimals } from './utils/geometry.utils';

export enum SVGParseError {
    svgMaxLengthRaised = 'svgMaxLengthRaised'
}

@Injectable()
export class VenueTplEditorDomService {

    private readonly _venueTplEdState = inject(VenueTplEditorState);

    constructor() { }

    getWorkAreaCoordinates$(): Observable<VenueTplEditorSvgCoordinates> {
        return this._venueTplEdState.workAreaCoordinates.getValue$();
    }

    setWorkAreaCoordinates(coords: VenueTplEditorSvgCoordinates): void {
        this._venueTplEdState.workAreaCoordinates.setValue(coords);
    }

    getSvgSvgElement$(): Observable<SVGSVGElement> {
        return this._venueTplEdState.svgSvgElement.getValue$();
    }

    /**
     * View box is a standard svg property, an array of 4 numbers, x, y, width and height.
     */
    getSvgSvgElementViewBox$(): Observable<number[]> {
        return this._venueTplEdState.svgSvgElement.getValue$()
            .pipe(
                filter(Boolean),
                map(mainSvg => {
                    const viewBox = new Svg(mainSvg.cloneNode(false) as SVGSVGElement).viewbox();
                    return [viewBox.x, viewBox.y, viewBox.width, viewBox.height];
                }),
                shareReplay({ refCount: true, bufferSize: 1 })
            );
    }

    setSvgSvgElement(value: SVGSVGElement): void {
        this._venueTplEdState.svgSvgElement.setValue(value);
    }

    // Element group

    groupElements(elements: SVGElement[]): SVGElement {
        let group: G;
        if (elements?.length) {
            group = new G();
            const parent = new Container(elements[0].parentNode);
            const selectedElements = elements.map(el => new Element(el));
            const selectedElementsOrdered = Array.from(parent.children()).filter(child => selectedElements.includes(child));
            let index = 0;
            selectedElementsOrdered.forEach(el => {
                index = parent.index(el);
                group.add(el);
            });
            parent.add(group, index);
            return group.node;
        } else {
            throw new Error('Error trying to group no elements');
        }
    }

    ungroupElements(elements: SVGElement[]): SVGElement[] {
        return elements.map(element => {
            if (element.tagName === SVGDefs.nodeTypes.group) {
                const groupChildren = Array.from(elements[0].children).map(child => child as SVGElement);
                const group = new G(element);
                group.ungroup(group.parent());
                groupChildren.forEach(child => this.optimizeTransform(child));
                return groupChildren;
            } else {
                return [];
            }
        })
            .flat()
            .filter(Boolean);
    }

    // Element order

    sendToFront(elements: SVGElement[]): void {
        const parentNode = elements[0].parentNode as SVGElement;
        const parent = new Container(parentNode);
        const selectedElements = elements.map(el => new Element(el));
        Array.from(parent.children()).forEach(child => {
            if (child instanceof Element && selectedElements.includes(child)) {
                parent.add(child);
            }
        });
    }

    sendToBack(elements: SVGElement[]): void {
        const parentNode = elements[0].parentNode;
        if (parentNode instanceof SVGElement) {
            const parent = new Container(parentNode);
            const selectedElements = elements.map(el => new Element(el));
            let index = 0;
            Array.from(parent.children()).forEach(child => {
                if (child instanceof Element && selectedElements.includes(child)) {
                    parent.add(child, index);
                    index++;
                }
            });
        }
    }

    moveUp(elements: SVGElement[]): void {
        elements.forEach(element => {
            const children = Array.from(element.parentElement.children);
            const itemIndex = children.indexOf(element);
            if (itemIndex < children.length - 1) {
                if (itemIndex === children.length - 2) {
                    element.parentElement.insertBefore(element, null);
                } else {
                    element.parentElement.insertBefore(element, children[itemIndex + 2]);
                }
            }
        });
    }

    moveDown(elements: SVGElement[]): void {
        elements.forEach(element => {
            const children = Array.from(element.parentElement.children);
            const itemIndex = children.indexOf(element);
            if (itemIndex > 0) {
                element.parentElement.insertBefore(element, children[itemIndex - 1]);
            }
        });
    }

    addShape(shapeType: 'rect' | 'text'): Observable<SVGElement> {
        return combineLatest([this.getSvgSvgElement$(), this.getSvgSvgElementViewBox$()])
            .pipe(
                take(1),
                map(([mainSVG, viewBox]) => {
                    const shape = shapeType === 'rect' ? this.createRect(viewBox) : this.createText(viewBox);
                    mainSVG.append(shape.node);
                    return shape.node;
                })
            );
    }

    addImage(mainSvg: SVGSVGElement, href: string, width: number, height: number): void {
        SVG(mainSvg).add(new Image({ ['xlink:href']: href, href, width, height, preserveAspectRatio: 'xMidYMid meet' }));
    }

    // Element transform simplification

    optimizeTransform(element: SVGElement): void {
        const matrix = element.getAttribute(SVGDefs.attributes.transform);
        if (matrix) {
            if (matrix !== SVGDefs.attributeValues.neutralMatrixTransform) {
                const parts = matrix.split(/(\(|\)|,|\s)/gm);
                const resultTransformParts: string[] = [];
                parts.forEach(part => {
                    if (part) {
                        const numericPart = Number(part);
                        if (!isNaN(numericPart)) {
                            resultTransformParts.push(String(roundDecimals(numericPart, 2)));
                        } else {
                            resultTransformParts.push(part);
                        }
                    }
                });
                const resultTransform = resultTransformParts.join('');
                if (resultTransform !== SVGDefs.attributeValues.neutralMatrixTransform) {
                    element.setAttribute(SVGDefs.attributes.transform, resultTransform);
                } else {
                    element.removeAttribute(SVGDefs.attributes.transform);
                }
            } else {
                element.removeAttribute(SVGDefs.attributes.transform);
            }
        }
    }

    // Checks that element is a seat, and translates "translate transforms" to circle cx and cy values, keeps rotation,
    // removes scaling (warning)!, seat shapes must not have scale transformations.
    optimizeSeatTransform(element: Element, relElement: Element): void {
        const node = element.node;
        if (node.tagName === SVGDefs.nodeTypes.seat && node.classList.contains(SVGDefs.classes.interactive)) {
            if (!(element instanceof Circle)) {
                element = new Circle(element.node as SVGCircleElement);
            }
            const rBox = element.rbox(relElement);
            element.cx(roundDecimals(rBox.x + rBox.width / 2));
            element.cy(roundDecimals(rBox.y + rBox.height / 2));
            element.untransform();
        }
    }

    // SVG parse

    parseSVG(mainSVGElement: SVGElement): { svg?: string; error?: SVGParseError } {
        this.cleanRootNode(mainSVGElement);
        let svg = mainSVGElement.outerHTML;
        svg = svg.replace(/(\s{4})/gm, ' '); // changes 4 space tabulations to 1 space
        if (svg.length > SVGDefs.maxSVGLength) {
            return { error: SVGParseError.svgMaxLengthRaised };
        } else {
            return { svg };
        }
    }

    private cleanRootNode(rootNode: SVGElement): void {
        if (!rootNode.hasAttribute(SVGDefs.attributes.version)
            || rootNode.getAttribute(SVGDefs.attributes.version) !== SVGDefs.attributeValues.version) {
            rootNode.setAttribute(SVGDefs.attributes.version, SVGDefs.attributeValues.version);
        }
        if (rootNode.hasAttribute(SVGDefs.attributes.width)) {
            rootNode.removeAttribute(SVGDefs.attributes.width);
        }
        if (rootNode.hasAttribute(SVGDefs.attributes.height)) {
            rootNode.removeAttribute(SVGDefs.attributes.height);
        }
        this.removeSVGJSNamespace(rootNode);
        this.cleanNode(rootNode);
    }

    private cleanNode(node: SVGElement): SVGElement {
        // it shouldn't be required, but for if the flies
        this.removeSVGJSNamespace(node);
        const svgJSAttrPrefix = SVGDefs.attributes.svgjsNS + ':';
        Array.from(node.attributes).forEach(attribute => {
            if (attribute.name.includes(svgJSAttrPrefix)) {
                node.removeAttribute(attribute.name);
            }
        });
        if (node.hasAttribute(SVGDefs.attributes.class)) {
            if (node.classList.contains(SVGDefs.classes.dragMove)) {
                node.classList.remove(SVGDefs.classes.dragMove);
            }
            if (node.classList.length === 0) {
                node.removeAttribute(SVGDefs.attributes.class);
            }
        }
        Array.from(node.children).forEach(child => this.cleanNode(child as SVGElement));
        return node;
    }

    private removeSVGJSNamespace(node: SVGElement): void {
        // This namespace is setted by svgjs lib, and sometimes, more than one time
        while (node.hasAttribute(SVGDefs.attributes.namespaceDef + ':' + SVGDefs.attributes.svgjsNS)) {
            node.removeAttribute(SVGDefs.attributes.namespaceDef + ':' + SVGDefs.attributes.svgjsNS);
        }
    }

    private createRect(viewBox: number[]): Rect {
        const shapeSize = Math.round(Math.min(viewBox[2], viewBox[3]) / 3);
        const x = Math.round((viewBox[2] - shapeSize) / 2);
        const y = Math.round((viewBox[3] - shapeSize) / 2);
        const rect = new Rect({ width: shapeSize, height: shapeSize, x, y });
        rect.fill(SVGDefs.colors.defaultFill);
        rect.stroke({ color: SVGDefs.colors.defaultStroke, width: 1 });
        return rect;
    }

    private createText(viewBox: number[]): Text {
        const text = new Text();
        text.tspan('text');
        text.fill(SVGDefs.colors.text);
        text.stroke({ color: SVGDefs.colors.text, width: 0 });
        text.node.setAttribute('letter-spacing', '0');
        text.node.setAttribute('font-family', 'Arial');
        text.node.setAttribute('space', 'preserve');
        // wea, ma o meno
        const textSize = Math.round(viewBox[3] / 10);
        text.height(textSize);
        const span = text.children()[0];
        span.x(Math.round((viewBox[2] - textSize * 2) / 2));
        span.y(Math.round((viewBox[3] - textSize) / 2));
        return text;
    }
}
