export interface SvgElementWrapper {
    element: SVGElement;
    modifierGroup?: SVGElement;
    iconGroup?: SVGElement; // only available on seat elements
    selectionElement?: SVGElement;
}
