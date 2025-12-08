export interface EntityContent {
    id: number;
    type?: EntityContentType;
    subject?: string;
    value: string;
    language?: string;
    audited?: boolean;
    use_free_text?: boolean;
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

export type EntityContentType = 'PARAMETRIZED_TEMPLATE' | 'TEMPLATE' | 'TEXT';
export type EntityContentCategory = 'email';