export enum VenueTemplateElementInfoContents {
    badge = 'BADGE',
    textInfo = 'INFORMATION-TEXT',
    featuresList = 'FEATURES-LIST',
    view3d = '3D-VIEW',
    imageList = 'IMAGE-LIST',
    highlightedImage = 'HIGHLIGHTED-IMAGE',
    restriction = 'RESTRICTION'
}

export interface ElementInfoContentOption {
    value: VenueTemplateElementInfoContents;
    label: string;
    description: string;
    image: string;
    disabled: boolean;
    beta: boolean;
}
