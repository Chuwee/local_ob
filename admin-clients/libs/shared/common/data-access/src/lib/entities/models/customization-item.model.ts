export enum CustomizationItemTag {
    logo = 'LOGO',
    tiny = 'TINY',
    favicon = 'FAVICON',
    reports = 'LOGO_REPORTS'
}

export enum CustomizationItemExtension {
    png = 'PNG',
    ico = 'ICO',
    svg = 'SVG',
    jpg = 'JPG'
}

export namespace CustomizationItemExtension {
    export function toEnum(value: string | null): CustomizationItemExtension | null {
        const lowercase = value.toLowerCase();
        if (lowercase.endsWith('.png')) return CustomizationItemExtension.png;
        if (lowercase.endsWith('.ico')) return CustomizationItemExtension.ico;
        if (lowercase.endsWith('.svg')) return CustomizationItemExtension.svg;
        if (lowercase.endsWith('.jpg') || lowercase.endsWith('.jpeg')) return CustomizationItemExtension.jpg;
        return null;
    }
    export function toContentType(value: string | null): string | null {
        const lowercase = value.toLowerCase();
        if (lowercase.endsWith('.png')) return 'image/png';
        if (lowercase.endsWith('.ico')) return 'image/x-icon';
        if (lowercase.endsWith('.svg')) return 'image/svg+xml';
        if (lowercase.endsWith('.jpg') || lowercase.endsWith('.jpeg')) return 'image/jpeg';
        return null;
    }
}

export interface CustomizationItem {
    tag: CustomizationItemTag;
    extension: CustomizationItemExtension | null;
    value: string | null;
}

