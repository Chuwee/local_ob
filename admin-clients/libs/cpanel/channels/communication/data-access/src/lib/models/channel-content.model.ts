export interface ChannelContent {
    id: number;
    language?: string;
    type?: ChannelContentType;
    subject?: string;
    value: string;
    audited?: boolean;
    labels?: {
        code: string;
        name: string;
    }[];
    profiled_content?: {
        id: number;
        name: string;
        value: string;
    }[];
}

export enum ChannelContentType {
    parametrizedTemplate = 'PARAMETRIZED_TEMPLATE',
    template = 'TEMPLATE',
    profiledTemplate = 'PROFILED_TEMPLATE',
    text = 'TEXT'
}
