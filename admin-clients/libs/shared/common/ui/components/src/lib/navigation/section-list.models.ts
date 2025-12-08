export interface Section {
    id: string;
    icon: string;
    customIcon?: boolean;
    label: string;
    visible: boolean;
    link?: string[];
    isActive?: boolean; // allways overrided
    badge?: string;
    badgeClass?: string;
    subsections?: Subsection[];
}

export interface Subsection {
    id: string;
    label: string;
    visible: boolean;
    link?: string[];
    icon?: string;
}

export class SectionList extends Array<Section> {
    constructor() {
        super();
    }
}
