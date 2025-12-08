import { Metadata } from '@OneboxTM/utils-state';

export interface ChannelCustomResources {
    html_resources: {
        type: HtmlResourceTypes;
        language: string;
        content: string;
    }[];
    css_resources: {
        type: CssResourceTypes;
        content: string;
    }[];
}

export enum HtmlResourceTypes {
    headerHtml = 'HEADER_HTML',
    footerHtml = 'FOOTER_HTML'
}

export enum CssResourceTypes {
    customStyles = 'CUSTOM_STYLES_CSS'
}

export interface GetCustomAssetsResponse {
    data: CustomAssetElement[];
    metadata: Metadata;
}

export interface CustomAssetElement {
    filename: string;
    binary: string;
}
