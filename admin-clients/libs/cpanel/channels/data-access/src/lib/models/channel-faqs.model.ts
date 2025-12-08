export interface ChannelFaqs {
    key?: number;
    tags?: string[];
    values: {
        [language: string]: {
            title: string;
            content: string;
        };
    };
}

export interface FaqsListFilters {
    q?: string;
    tag?: string;
    language?: string;
}
