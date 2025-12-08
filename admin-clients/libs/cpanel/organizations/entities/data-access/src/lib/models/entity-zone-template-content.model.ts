export interface ZoneTemplateContent {
    id: number;
    language?: string;
    type?: ZoneTemplateContentType;
    value?: string;
    subject?: string;
    labels?: {
        code: string;
        name: string;
    }[];
}

export enum ZoneTemplateContentType {
    parametrizedTemplate = 'PARAMETRIZED_TEMPLATE',
    template = 'TEMPLATE',
    profiledTemplate = 'PROFILED_TEMPLATE',
    text = 'TEXT'
}
